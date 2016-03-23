package org.qii.weiciyuan.ui.actionmenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.CommentBean;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.ui.browser.BrowserWeiboMsgActivity;
import org.qii.weiciyuan.ui.send.WriteReplyToCommentActivity;

/**
 * User: qii
 * Date: 12-12-6
 */
public class CommentFloatingMenu extends DialogFragment {

    public static CommentFloatingMenu newInstance(CommentBean bean) {

        Bundle args = new Bundle();
        args.putParcelable("bean", bean);
        CommentFloatingMenu fragment = new CommentFloatingMenu();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final CommentBean bean = getArguments().getParcelable("bean");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(bean.getUser().getScreen_name());
        String[] str = {getString(R.string.view), getString(R.string.reply_to_comment)};
        builder.setItems(str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch (which) {
                    case 0:
                        startActivity(BrowserWeiboMsgActivity
                                .newIntent(bean.getStatus(),
                                        GlobalContext.getInstance().getSpecialToken()));
                        break;
                    case 1:
                        intent = new Intent(getActivity(), WriteReplyToCommentActivity.class);
                        intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
                        intent.putExtra("msg", bean);
                        getActivity().startActivity(intent);
                        break;
                }
            }
        });

        return builder.create();
    }

}
