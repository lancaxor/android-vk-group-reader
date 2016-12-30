package net.reisshie.vkgroupreader.Adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.widget.TextView;

import net.reisshie.vkgroupreader.Db.Entity.Post;
import net.reisshie.vkgroupreader.R;
import net.reisshie.vkgroupreader.tools.Pager;

import java.util.List;

/**
 * Created by Alexey on 28.12.2016.
 */

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    private List<Post> posts;
    private Pager pager;
    private Context context;
    Boolean parity = true;

    public PostListAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    public void clear() {
        this.posts.clear();
        this.notifyDataSetChanged();
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return this.posts.size();
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view, this.parity);
        this.parity = !this.parity;
        return holder;
    }

    public void loadData(Pager pager) {
        this.posts = (new Post(this.context)).getNewPosts(pager);
        this.notifyDataSetChanged();
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = this.posts.get(position);
        String header = String.valueOf(position + 1) + ". " + post.getDate().toString() + " | " + post.getGroup().getTitle();
        String resultText = header + "\n\n" + post.getText();
        holder.postText.setText(resultText);
    }

    /**
     * Called by RecyclerView when it starts observing this Adapter.
     * <p>
     * Keep in mind that same adapter may be observed by multiple RecyclerViews.
     *
     * @param recyclerView The RecyclerView instance which started observing this adapter.
     * @see #onDetachedFromRecyclerView(RecyclerView)
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView postText;

        public ViewHolder (View itemView, Boolean parity) {
            super(itemView);
            this.postText = (TextView) itemView.findViewById(R.id.post_list_item_text);
            itemView.setBackgroundColor(parity ? 0xFFFFFFFF : 0xFFFAFAFA);
        }
    }
}