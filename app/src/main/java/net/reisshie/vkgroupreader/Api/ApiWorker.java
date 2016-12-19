package net.reisshie.vkgroupreader.Api;

import android.content.Context;
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
        this.sendRequest(request, new ApiCallbackInterface() {
            @Override
            public void onSuccess(VKResponse result) {
                Toast.makeText(context, "Group info loaded!", Toast.LENGTH_SHORT).show();
                VKApiCommunityArray communityArray = new VKApiCommunityArray();
                try {
                    communityArray.parse(result.json);
                } catch (JSONException e) {
                    return;
                }
                if(communityArray.size() == 0) {
                    Toast.makeText(context, "Group Not Found", Toast.LENGTH_LONG).show();
                    if(errorContainer != null) {
                        errorContainer.setText("Group Not Found!");
                    }
                    return;
                }
                VKApiCommunity community = communityArray.get(0);
                if(successContainer != null) {
                    successContainer.setText("Group '" + community.name + "' was loaded successfully!");
                }
            }

            @Override
            public void onFail(VKError result) {
                Toast.makeText(context, "Load group info failed!", Toast.LENGTH_SHORT).show();
                if(errorContainer != null) {
                    errorContainer.setText("Error (" + result.errorCode + "), message: " + result.errorMessage + "; reason: " + result.errorReason);
                }
            }
        });
    }

    public void getGroupPosts(String groupId, Pager pager) {
        String wallGroupId = "-" + groupId;
        String fields = "text,id,date";
        VKRequest request = VKApi.wall().get(VKParameters.from(
                VKApiConst.OWNER_ID, wallGroupId,
                VKApiConst.FIELDS, fields
        ));

        this.sendRequest(request, new ApiCallbackInterface() {
            @Override
            public void onSuccess(VKResponse response) {
                isError = false;
                String result = "";
                String separator = "\n-----------------\n";
                VKPostArray postArray = new VKPostArray();
                try {
                    postArray.parse(response.json);
                } catch (JSONException exception) {
                    isError = true;
                    successContainer.append("Error while parsing text!");
                    return;
                }

                for(VKApiPost post: postArray) {
                    result += (separator + post.text);
                }
                successContainer.append(result);
            }

            @Override
            public void onFail(VKError result) {
                isError = true;
            }
        });
    }
}
