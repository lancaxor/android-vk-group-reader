package net.reisshie.vkgroupreader;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKSdk;

import net.reisshie.vkgroupreader.Api.ApiWorker;
import net.reisshie.vkgroupreader.tools.Pager;

public class MainActivity extends AppCompatActivity {

    private Button btnLoadGroup;
    private Button btnSaveGroup;
    private EditText etGroupId;
    private TextView twGroupInfo;
    private Pager pager;
    private Context context;
    private ApiWorker apiWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        this.apiWorker = new ApiWorker(this);
        this.initTools();
        this.saveWidgets();
        this.apiWorker.setErrorContainer(this.twGroupInfo).setSuccessContainer(this.twGroupInfo);
        this.initWidgets();
    }

    protected void saveWidgets() {
        this.btnLoadGroup = (Button) this.findViewById(R.id.btn_load_group);
        this.btnSaveGroup = (Button) this.findViewById(R.id.btn_save_group);
        this.etGroupId = (EditText) this.findViewById(R.id.et_enter_group);
        this.twGroupInfo = (TextView) this.findViewById(R.id.tw_group_info);
    }

    protected void initWidgets() {
        final MainActivity self = this;

        btnLoadGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredId = etGroupId.getText().toString();
                Toast.makeText(getApplicationContext(), "Load Group by id: " + enteredId, Toast.LENGTH_LONG).show();
                self.apiWorker.getGroup(enteredId);
                self.apiWorker.getGroupPosts(enteredId, self.pager);
            }
        });
    }

    protected void initTools() {
        this.pager = new Pager();
    }
}
