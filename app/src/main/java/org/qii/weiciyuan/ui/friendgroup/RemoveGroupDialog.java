package org.qii.weiciyuan.ui.friendgroup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.qii.weiciyuan.R;

import java.util.ArrayList;

/**
 * User: qii
 * Date: 13-2-15
 */
public class RemoveGroupDialog extends DialogFragment {

    public static RemoveGroupDialog newInstance(ArrayList<String> checkedNames) {

        Bundle args = new Bundle();
        args.putStringArrayList("checkedNames", checkedNames);
        RemoveGroupDialog fragment = new RemoveGroupDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList<String> checkedNames = getArguments().getStringArrayList("checkedNames");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.remove_group))
                .setMessage(getString(R.string.remove_group_content))
                .setPositiveButton(getString(R.string.delete),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ManageGroupActivity.ManageGroupFragment fragment
                                        = (ManageGroupActivity.ManageGroupFragment) getTargetFragment();
                                fragment.removeGroup(checkedNames);
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }
}
