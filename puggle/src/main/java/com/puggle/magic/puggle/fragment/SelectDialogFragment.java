package com.puggle.magic.puggle.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.puggle.magic.puggle.R;
import com.puggle.magic.puggle.activity.LockScreenActivity;

/**
 * Created by jaeeo99 on 2018. 3. 16..
 */

public class SelectDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.select_title)
                .setItems(R.array.select_items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LockScreenActivity lockScreenActivity = (LockScreenActivity)getActivity();
                        String flag = lockScreenActivity.getFlag();
                        SharedPreferences sharedPref = getActivity().getBaseContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        switch(which) {
                            case 0:
                                lockScreenActivity.requestContact(0);
                                break;
                            case 1:
                                editor.putInt(flag, which);
                                editor.putString(flag + "_ACTION", "잠금해제");
                                editor.commit();
                                break;
                            default:
                                Log.d("dialog", "" + which);
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
