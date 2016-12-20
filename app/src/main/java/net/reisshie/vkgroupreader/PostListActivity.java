package net.reisshie.vkgroupreader;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PostListActivity extends ListActivity {

    ArrayList<String> postList = new ArrayList<String>();
    ArrayAdapter<String> postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_list_activity);
        this.postAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                this.postList
        );
        this.setListAdapter(this.postAdapter);

        for(int i = 0; i < 10; i++) {
            this.addItem("new item: " + i);
        }

    }

    protected void appendList(List<String> items) {
        this.postList.addAll(items);
        this.postAdapter.notifyDataSetChanged();
    }

    protected void addItem(String item) {
//        this.postList.add(item);
//        this.postAdapter.notifyDataSetChanged();
        this.postAdapter.add(item);
    }
}
