package org.qii.weiciyuan.ui.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.support.utils.Utility;
import org.qii.weiciyuan.support.utils.WebBrowserSelector;
import org.qii.weiciyuan.ui.userinfo.UserInfoActivity;

/**
 * User: qii
 * Date: 13-3-26
 */
public class LongClickLinkDialog extends DialogFragment {

    public static LongClickLinkDialog newInstance(Uri uri) {

        Bundle args = new Bundle();
        args.putParcelable("uri", uri);
        LongClickLinkDialog fragment = new LongClickLinkDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Uri uri = getArguments().getParcelable("uri");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] strangerItems = {getString(R.string.open), getString(R.string.copy)};

        builder.setTitle(getStringContent(uri))
                .setItems(strangerItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Context context = getActivity();
                                if (uri.getScheme().startsWith("http")) {
                                    String url = uri.toString();
                                    if (Utility.isWeiboAccountIdLink(url)) {
                                        Intent intent = new Intent(context, UserInfoActivity.class);
                                        intent.putExtra("id",
                                                Utility.getIdFromWeiboAccountLink(url));
                                        context.startActivity(intent);
                                    } else if (Utility.isWeiboAccountDomainLink(url)) {
                                        Intent intent = new Intent(context, UserInfoActivity.class);
                                        intent.putExtra("domain",
                                                Utility.getDomainFromWeiboAccountLink(url));
                                        context.startActivity(intent);
                                    } else {
                                        WebBrowserSelector.openLink(context, uri);
                                    }
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    intent.putExtra(Browser.EXTRA_APPLICATION_ID,
                                            context.getPackageName());
                                    context.startActivity(intent);
                                }
                                break;
                            case 1:
                                ClipboardManager cm = (ClipboardManager) getActivity()
                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setPrimaryClip(
                                        ClipData.newPlainText("sinaweibo", getStringContent(uri)));
                                Toast.makeText(GlobalContext.getInstance(), String.format(
                                        GlobalContext.getInstance().getString(R.string.have_copied),
                                        getStringContent(uri)), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });

        return builder.create();
    }

    private String getStringContent(Uri uri) {
        String d = uri.toString();
        String newValue = "";
        if (d.startsWith("org.qii.weiciyuan")) {
            int index = d.lastIndexOf("/");
            newValue = d.substring(index + 1);
        } else if (d.startsWith("http")) {
            newValue = d;
        }
        return newValue;
    }
}