package net.reisshie.vkgroupreader.Interface;

import android.content.DialogInterface;
import android.widget.EditText;

/**
 * Created by Alexey on 26.12.2016.
 */

public interface ClickCallback {
    void onClick(DialogInterface dialog, int which, EditText input);
}
