package net.reisshie.vkgroupreader;

import com.vk.sdk.VKSdk;

/**
 * Created by Alexey on 18.12.2016.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        VKSdk.initialize(this);
    }
}
