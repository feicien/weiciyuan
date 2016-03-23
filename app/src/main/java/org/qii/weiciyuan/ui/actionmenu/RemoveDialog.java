package org.qii.weiciyuan.ui.actionmenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.ui.interfaces.IRemoveItem;

/**
 * User: qii
 * Date: 12-9-11
 */
public class RemoveDialog extends DialogFragment {

    public static RemoveDialog newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        RemoveDialog fragment = new RemoveDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final int positon = getArguments().getInt("position");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.askdelete))
                .setMessage(getString(R.string.askdeletemessage))
                .setPositiveButton(getString(R.string.delete),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IRemoveItem iRemoveItem = (IRemoveItem) getTargetFragment();
                                iRemoveItem.removeItem(positon);
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IRemoveItem iRemoveItem = (IRemoveItem) getTargetFragment();
                                iRemoveItem.removeCancel();
                            }
                        });

        return builder.create();
    }
}
