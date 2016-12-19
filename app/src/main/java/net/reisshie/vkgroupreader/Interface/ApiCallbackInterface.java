package net.reisshie.vkgroupreader.Interface;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKResponse;

/**
 * Created by Alexey on 19.12.2016.
 */

public interface ApiCallbackInterface {
    void onSuccess(VKResponse result);
    void onFail(VKError result);
}
