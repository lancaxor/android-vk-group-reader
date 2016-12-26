package net.reisshie.vkgroupreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiCommunityArray;

import net.reisshie.vkgroupreader.Api.ApiWorker;
import net.reisshie.vkgroupreader.Db.Entity.Group;
import net.reisshie.vkgroupreader.Interface.ApiCallbackInterface;
import net.reisshie.vkgroupreader.Interface.ClickCallback;
import net.reisshie.vkgroupreader.tools.Tools;

import java.util.List;

public class GroupListActivity extends AppCompatActivity {

    String newGroupId = "";
    ApiWorker api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.api = new ApiWorker(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.showGroups();
    }

    protected void showGroups() {
        List<Group> groups = (new Group(this)).getGroups();
        TextView container = (TextView) this.findViewById(R.id.tw_group_list);
        if(groups.size() > 0) {
            container.setText("");  // clear
        }
        String row = "";
        for(Group group: groups) {
            row = String.valueOf(group.getId()) + ". "
                    + group.getTitle() + " ("
                    + String.valueOf(group.getVkId()) + ")"
                    + " (" + (group.isEnabled() ? "Enabled" : "Disabled") + ")\n\n";
            container.append(row);
        }
    }

    /**
     * Show "AddGroup" dialog and prompt for input
     */
    protected void showAddGroupDialog() {

        final EditText input = new EditText(this);
        AlertDialog.Builder builder = Tools.prompt("Type new group ID:", this, new ClickCallback() {
            @Override
            public void onClick(DialogInterface dialog, int which, EditText input) {
                newGroupId = input.getText().toString();
                addGroupToDatabase(newGroupId);
            }
        }, new ClickCallback() {
            @Override
            public void onClick(DialogInterface dialog, int which, EditText input) {
                dialog.cancel();

            }
        });
        builder.show();
    }

    /**
     * Show "RemoveGroup" dialog and prompt for input
     */
    protected void showRemoveGroupDialog() {

        AlertDialog.Builder builder = Tools.prompt("Type ID of group to remove:", this, new ClickCallback() {
            @Override
            public void onClick(DialogInterface dialog, int which, EditText input) {
                newGroupId = input.getText().toString();
                removeGroupFromDatabase(newGroupId);
            }
        }, new ClickCallback() {
            @Override
            public void onClick(DialogInterface dialog, int which, EditText input) {
                dialog.cancel();

            }
        });
        builder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_group_list, menu);
        return true;
    }

    protected void addGroupToDatabase(String groupId) {
        Group group = new Group(this);
        try {
            group.setVkId(Long.parseLong(groupId));
        } catch (Exception e) {
            // wrong user input
        }

        group.load(group.COLUMN_VK_ID);
        if(group.getEntityId() != null && group.getEntityId() != 0) {
            return; // group already in the DataBase;
        }

        ApiCallbackInterface callback = this.getLoadGroupCallback();
        this.api.setCallback(callback);
        this.api.getGroup(groupId);
    }

    protected void removeGroupFromDatabase(String groupId) {
        Group group = new Group(this);
        group.setEntityId(Long.parseLong(groupId));
        group.load();
        if(group.getEntityId() == null) {
            Toast.makeText(this, "Group with ID '" + groupId + "' was not found in DataBase!", Toast.LENGTH_LONG).show();
            return;
        }
        group.remove(groupId);
        Toast.makeText(this, "Group '" + group.getTitle() + "' was removed from database!", Toast.LENGTH_LONG).show();
        this.showGroups();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mgl_add_group: // Add group
                this.showAddGroupDialog();
                break;
            case R.id.mgl_remove_group:
                this.showRemoveGroupDialog();   // remove group
                break;
            default:
                Toast.makeText(this, "Unknown Action", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get callback for LoadGroup
     * @return ApiCallbackInterface
     */
    protected ApiCallbackInterface getLoadGroupCallback() {
        final GroupListActivity self = this;
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

                Toast.makeText(self, "Group '" + community.name + "' was successfully added to list!", Toast.LENGTH_SHORT).show();
                self.showGroups();
            }

            @Override
            public void onFail(VKError result) {
                Toast.makeText(self, "Error while adding group to list: " + result.errorMessage, Toast.LENGTH_LONG).show();
            }
        };
        return callback;
    }
}
