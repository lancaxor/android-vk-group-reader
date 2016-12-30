package net.reisshie.vkgroupreader.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiGroups;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiCommunityArray;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKPostArray;

import net.reisshie.vkgroupreader.Interface.ApiCallbackInterface;
import net.reisshie.vkgroupreader.tools.Pager;

import org.json.JSONException;

public class ApiWorker {

    private Context context;
    private TextView successContainer;
    private TextView errorContainer;
    private boolean isError = false;

    public void setCallback(ApiCallbackInterface callback) {
        this.callback = callback;
    }

    private ApiCallbackInterface callback;

    public boolean isError() {
        return isError;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private String lastErrorMessage = "";

    public ApiWorker(Context context) {
        this.context = context;
    }

    public ApiWorker setSuccessContainer(TextView container) {
        this.successContainer = container;
        return this;
    }

    public ApiWorker setErrorContainer(TextView container) {
        this.errorContainer = container;
        return this;
    }

    public void sendRequest(VKRequest request, final ApiCallbackInterface callback) {
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                callback.onFail(error);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onComplete(VKResponse response) {
                callback.onSuccess(response);
            }
        });
    }

    public void getGroup(String groupId) {
        VKRequest request = VKApi.groups().getById(VKParameters.from(VKApiConst.GROUP_ID, groupId));
        final ApiWorker self = this;
        this.sendRequest(request, new ApiCallbackInterface() {
            @Override
            public void onSuccess(VKResponse result) {
                self.callback.onSuccess(result);
            }

            @Override
            public void onFail(VKError result) {
                self.callback.onFail(result);
            }
        });
    }

    public void getGroupPosts(String groupId, Pager pager) {

        String fields = "text,id,date";
        String wallId = "-" + groupId;
        VKRequest request = VKApi.wall().get(VKParameters.from(
                VKApiConst.OWNER_ID, wallId,
                VKApiConst.FIELDS, fields,
                VKApiConst.COUNT, pager.getLimit(),
                VKApiConst.OFFSET, pager.getOffset()
        ));
        final ApiWorker self = this;

        this.sendRequest(request, this.callback);
    }

    public void getGroupPostsAsync(String groupId, Pager pager) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }
        }.execute();
    }
}
