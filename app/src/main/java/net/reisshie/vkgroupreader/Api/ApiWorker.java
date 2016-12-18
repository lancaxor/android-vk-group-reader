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

/**
 * Created by Alexey on 18.12.2016.
 */

public class ApiWorker {

    private Context context;
    private TextView successContainer;
    private TextView errorContainer;

    interface ApiCallbackInterface {
        void onSuccess(String result);
        void onFail(String result);
    }

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
                callback.onFail(error.errorMessage);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onComplete(VKResponse response) {
                callback.onSuccess(response.responseString);
            }
        });
    }

    public void getGroup(String groupId) {
        VKRequest request = VKApi.groups().getById(VKParameters.from(VKApiConst.GROUP_ID, groupId));
        this.sendRequest(request, new ApiCallbackInterface() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(context, "Success, result: " + result, Toast.LENGTH_LONG).show();
                if(successContainer != null) {
                    successContainer.setText("Success, result: " + result);
                }
            }

            @Override
            public void onFail(String result) {
                Toast.makeText(context, "Error, message: " + result, Toast.LENGTH_LONG).show();
                if(errorContainer != null) {
                    errorContainer.setText("Error, message: " + result);
                }
            }
        });
    }
}
