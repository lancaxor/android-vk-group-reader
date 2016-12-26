package net.reisshie.vkgroupreader.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputType;
import android.widget.EditText;

import net.reisshie.vkgroupreader.Interface.ClickCallback;

/**
 * Created by Alexey on 25.12.2016.
 */

public class Tools {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static AlertDialog.Builder prompt(String text, Context context, final ClickCallback okCallback, ClickCallback cancelCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(text);

        // Set up the input
        final EditText input = new EditText(context);

        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                okCallback.onClick(dialog, which, input);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder;
    }
}
