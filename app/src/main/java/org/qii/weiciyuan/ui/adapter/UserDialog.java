package org.qii.weiciyuan.ui.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.UserBean;
import org.qii.weiciyuan.support.database.FilterDBTask;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.ui.send.WriteWeiboActivity;
import org.qii.weiciyuan.ui.userinfo.ManageGroupDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: qii
 * Date: 13-3-10
 */
public class UserDialog extends DialogFragment {

    private UserBean user;

    public static UserDialog newInstance(UserBean user) {

        Bundle args = new Bundle();
        args.putParcelable("user",user);
        UserDialog fragment = new UserDialog();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        user = getArguments().getParcelable("user");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] friendItems = {getString(R.string.at_him), getString(R.string.manage_group),
                getString(R.string.add_to_app_filter), getString(R.string.unfollow_him)};
        CharSequence[] strangerItems = {getString(R.string.at_him), getString(R.string.follow_him),
                getString(R.string.add_to_app_filter)};
        if (user.isFollowing()) {
            builder.setTitle(user.getScreen_name())
                    .setItems(friendItems, new FriendOnClicker());
        } else {
            builder.setTitle(user.getScreen_name())
                    .setItems(strangerItems, new StrangerOnClick());
        }

        return builder.create();
    }

    private class StrangerOnClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
                    intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
                    intent.putExtra("content", "@" + user.getScreen_name());
                    intent.putExtra("account", GlobalContext.getInstance().getAccountBean());
                    startActivity(intent);
                    break;
                case 1:
                    follow();
                    break;
                case 2:
                    FilterDBTask.addFilterKeyword(FilterDBTask.TYPE_USER, user.getScreen_name());
                    FilterDBTask.addFilterKeyword(FilterDBTask.TYPE_KEYWORD, user.getScreen_name());
                    Toast.makeText(getActivity(), getString(R.string.filter_successfully),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private class FriendOnClicker implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:
                    Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
                    intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
                    intent.putExtra("content", "@" + user.getScreen_name());
                    intent.putExtra("account", GlobalContext.getInstance().getAccountBean());
                    startActivity(intent);
                    break;
                case 1:
                    ManageGroupDialog manageGroupDialog = ManageGroupDialog.newInstance(GlobalContext.getInstance().getGroup(), user.getId());
                    manageGroupDialog.show(getFragmentManager(), "");
                    break;
                case 2:
                    FilterDBTask.addFilterKeyword(FilterDBTask.TYPE_USER, user.getScreen_name());
                    FilterDBTask.addFilterKeyword(FilterDBTask.TYPE_KEYWORD, user.getScreen_name());
                    Toast.makeText(getActivity(), getString(R.string.filter_successfully),
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    unFollow();
                    break;
            }
        }
    }


    private void follow(){
        String token = GlobalContext.getInstance().getSpecialToken();
        String uid = user.getId();
        String screen_name = user.getScreen_name();

        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<UserBean> call = service.followFan(token, uid, screen_name);
        call.enqueue(new Callback<UserBean>() {
            @Override
            public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(GlobalContext.getInstance(),
                            GlobalContext.getInstance().getString(R.string.follow_successfully),
                            Toast.LENGTH_SHORT).show();
                    user.setFollowing(true);
                }
            }

            @Override
            public void onFailure(Call<UserBean> call, Throwable t) {

            }
        });
    }

    private void unFollow(){
        String token = GlobalContext.getInstance().getSpecialToken();
        String uid = user.getId();
        String screen_name = user.getScreen_name();

        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<UserBean> call = service.unFollowFan(token, uid, screen_name);
        call.enqueue(new Callback<UserBean>() {
            @Override
            public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(GlobalContext.getInstance(),
                            GlobalContext.getInstance().getString(R.string.unfollow_successfully),
                            Toast.LENGTH_SHORT).show();
                    user.setFollowing(false);
                }
            }

            @Override
            public void onFailure(Call<UserBean> call, Throwable t) {

            }
        });
    }

}
