package org.qii.weiciyuan.ui.browser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.qii.weiciyuan.R;

/**
 * User: qii
 * Date: 12-11-28
 */
public class RemoveWeiboMsgDialog extends DialogFragment {

    public static interface IRemove {
        public void removeMsg(String id);
    }

//    private String id;

    public static RemoveWeiboMsgDialog newInstance(String id) {

        Bundle args = new Bundle();
        args.putString("id", id);
        RemoveWeiboMsgDialog fragment = new RemoveWeiboMsgDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String id = getArguments().getString("id");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.askdelete))
                .setMessage(getString(R.string.askdeletemessage))
                .setPositiveButton(getString(R.string.delete),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IRemove IRemove = (IRemove) getActivity();
                                IRemove.removeMsg(id);
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
