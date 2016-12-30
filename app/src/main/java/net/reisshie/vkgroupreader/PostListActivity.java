package net.reisshie.vkgroupreader;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiCommunityArray;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKPostArray;

import net.reisshie.vkgroupreader.Adapter.PostListAdapter;
import net.reisshie.vkgroupreader.Api.ApiWorker;
import net.reisshie.vkgroupreader.Db.DbManager;
import net.reisshie.vkgroupreader.Db.Entity.Group;
import net.reisshie.vkgroupreader.Db.Entity.Post;
import net.reisshie.vkgroupreader.Interface.ApiCallbackInterface;
import net.reisshie.vkgroupreader.tools.Pager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity {

    List<Post> posts = new ArrayList<Post>();
    ArrayList<String> postList = new ArrayList<String>();
    Pager pager;
    ApiWorker api;
    DbManager db;

    /// api
    List<String> groupIdsToLoad;
    String currentGroupId;
    int maxPages = 3;
    int currentPage = 0;

    /// recycler
    private RecyclerView recyclerView;
    private PostListAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    /// end recycler

    private boolean currentIsViewed = false;


    protected void initVariables() {
        this.api = new ApiWorker(this);
        this.pager = new Pager();
        this.pager.setStrict(true);
        this.pager.setCurrentPage(0);
        this.pager.setPageSize(300);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initModels();
        this.initVariables();
        setContentView(R.layout.post_list_activity);
        this.initRecycler(savedInstanceState);
        this.refreshList();
    }

    protected void initRecycler(Bundle savedInstanceState) {
        this.recyclerView = (RecyclerView) findViewById(R.id.post_list_recycler_view);
        this.recyclerView.setHasFixedSize(true);    // performance up
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerAdapter = new PostListAdapter(this.posts, this);
        this.recyclerView.setAdapter(this.recyclerAdapter);
    }

    protected void initModels() {
        (new Group(this)).install();
        (new Post(this)).install();
    }

    public void refreshList() {
        this.refreshList(true);
    }
    public void refreshList(boolean clear) {
        if(clear) {
            this.recyclerAdapter.clear();
        }
        this.recyclerAdapter.loadData(this.pager);
        this.recyclerView.scrollToPosition(0);
    }


    /**
     * Add list of items to ListView
     * @param items
     */
    protected void appendList(List<String> items, boolean clearBeforeInsert) {
        if(clearBeforeInsert) {
            this.postList.clear();
        }
        this.postList.addAll(items);
    }
    protected void appendList(List<String> items) {
        this.appendList(items, false);
    }

    /**
     * Add item to ListView
     * @param item
     */
    protected void addItem(String item) {
        this.postList.add(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_post_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mpl_menu_action_settings: // Settings
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                break;
            case R.id.mpl_menu_download_posts:  // Download posts
                this.loadSavePosts();
                Toast.makeText(this, "Downloading posts...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mpl_menu_refresh_list:    // Refresh list
                this.refreshList();
                break;
            case R.id.mpl_menu_manage_groups:  // Manage Groups
                this.openManageGroupsActivity();
                break;
            case R.id.mpl_clear_posts:      // clear Posts DB
                this.recyclerAdapter.clear();
                break;
            case R.id.mpl_toggle_viewed:
                (new Post(this)).toggleViewedAll(this.currentIsViewed);
                this.currentIsViewed = !this.currentIsViewed;
                break;
            default:
                Toast.makeText(this, "Unknown Action", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openManageGroupsActivity() {
        Intent intent = new Intent(this, GroupListActivity.class);
        this.startActivity(intent);
    }

    ////// API //////


    public void loadSavePosts() {
        this.loadSavePosts(true);
    }

    public void loadSavePosts(boolean first) {

        if (first) {
            List<Group> groups = new ArrayList<Group>();
            this.groupIdsToLoad = new ArrayList<String>();
            Pager groupsPager = new Pager();    // separate pager for groups
            groupsPager.setStrict(true)
                    .setCurrentPage(0)
                    .setPageSize(100);
            groups = (new Group(this)).getEnabledGroups(this.pager);

            this.api.setCallback(this.getLoadPostsCallback());
            for (Group group : groups) {
//                this.api.getGroupPosts(group.getVkId().toString(), this.pager);
                this.groupIdsToLoad.add(group.getVkId());
            }
        }
        if (this.currentPage < this.maxPages) {
            this.pager.setCurrentPage(this.currentPage);
            this.currentPage++;
            this.api.getGroupPosts(this.currentGroupId, this.pager); // load next page
        } else {
            if (this.groupIdsToLoad.size() > 0) {
                this.currentGroupId = this.groupIdsToLoad.get(0);
                this.groupIdsToLoad.remove(0);
                this.currentPage = 0;
                this.pager.setCurrentPage(0);
                this.api.getGroupPosts(this.currentGroupId, this.pager);
            }
        }
    }

    /**
     * Get callback for LoadGroup
     * @return ApiCallbackInterface
     */
    protected ApiCallbackInterface getLoadGroupCallback() {
        final PostListActivity self = this;
        ApiCallbackInterface callback = new ApiCallbackInterface() {
            @Override
            public void onSuccess(VKResponse result) {
                VKApiCommunityArray communityArray = (VKApiCommunityArray)result.parsedModel;
                if(communityArray.getCount() == 0) {
                    return;
                }
                VKApiCommunity community = communityArray.get(0);
                Group dbGroup = new Group(self.getApplicationContext());
                dbGroup.setTitle(community.name);
                dbGroup.setEnabled(true);
                dbGroup.setVkId(Long.valueOf(community.id));
                dbGroup.save();
            }

            @Override
            public void onFail(VKError result) { }
        };
        return callback;
    }

    /**
     * Get callback for LoadPosts
     * @return
     */
    protected ApiCallbackInterface getLoadPostsCallback() {
        final PostListActivity self = this;
        ApiCallbackInterface callback = new ApiCallbackInterface() {
            @Override
            public void onSuccess(VKResponse result) {
                VKPostArray postArray = new VKPostArray();
                try {
                    postArray.parse(result.json);
                } catch (JSONException exception) {
                    Toast.makeText(self, "Exception: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<String> data = new ArrayList<String>();

                for(VKApiPost post: postArray) {
                    if(post.text.trim().length() > 0) {
                        Post dbPost = new Post(self);
                        dbPost.setVkId(Long.valueOf(post.getId()));
                        dbPost.loadByVkId();
                        if(dbPost.getEntityId() == null || dbPost.getEntityId() == 0L) {  // new post

                            Group group = new Group(self);
                            Long vkId = (post.reply_owner_id == 0L ? Long.valueOf(post.from_id) : Long.valueOf(post.reply_owner_id));
                            if(vkId < 0) {
                                vkId *= -1;
                            }
                            group.setVkId(vkId);
                            group.load(group.COLUMN_VK_ID);
                            Log.d("ALEX_D", "Group ID: " + String.valueOf(vkId) + ", groupName: " + group.getTitle());
                            Log.d("ALEX_D", "Post ID: " + String.valueOf(post.getId()) + ", post: " + post.text.substring(0, post.text.length() < 100 ? post.text.length() : 99));

                            dbPost.setText(post.text.trim());
                            dbPost.setTimestamp(post.date);
                            dbPost.setIsViewed(false);
                            dbPost.setGroup(group);
                            dbPost.save();
                        }
                    }
                }

                if(pager.getCurrentPage() < maxPages) {
                    Toast.makeText(self, "Loading page " + self.currentPage + "for group '" + self.currentGroupId + "'...", Toast.LENGTH_SHORT).show();
                    loadSavePosts(false);
                } else {
                    if (groupIdsToLoad.size() > 0) {
                        Toast.makeText(self, "Loading next group with ID = '" + self.currentGroupId + "'...", Toast.LENGTH_SHORT).show();
                        loadSavePosts(false);
                    } else {
                        Toast.makeText(self, "Posts was downloaded successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                self.appendList(data);
                self.refreshList();
            }

            @Override
            public void onFail(VKError result) {
                Toast.makeText(self, "getLoadPostsCallback exception: " + result.errorReason + "; message: " + result.errorMessage + "; code: " + String.valueOf(result.errorCode), Toast.LENGTH_LONG).show();
            }

        };
        return callback;
    }

}
