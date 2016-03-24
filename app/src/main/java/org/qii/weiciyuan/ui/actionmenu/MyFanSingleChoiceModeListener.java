package org.qii.weiciyuan.ui.actionmenu;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.UserBean;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.ui.adapter.UserListAdapter;
import org.qii.weiciyuan.ui.basefragment.AbstractUserListFragment;
import org.qii.weiciyuan.ui.send.WriteWeiboActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: qii
 * Date: 12-10-9
 */
public class MyFanSingleChoiceModeListener implements ActionMode.Callback {
    private ListView listView;
    private UserListAdapter adapter;
    private Fragment fragment;
    private ActionMode mode;
    private UserBean bean;


    public void finish() {
        if (mode != null) {
            mode.finish();
        }

    }

    public MyFanSingleChoiceModeListener(ListView listView, UserListAdapter adapter,
            Fragment fragment, UserBean bean) {
        this.listView = listView;
        this.fragment = fragment;
        this.adapter = adapter;
        this.bean = bean;
    }

    private Activity getActivity() {
        return fragment.getActivity();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (this.mode == null) {
            this.mode = mode;
        }

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        menu.clear();
        inflater.inflate(R.menu.contextual_menu_myfansinglechoicemodelistener, menu);
        mode.setTitle(bean.getScreen_name());
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_at:
                Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
                intent.putExtra("content", "@" + bean.getScreen_name());
                intent.putExtra("account", GlobalContext.getInstance().getAccountBean());
                getActivity().startActivity(intent);
                listView.clearChoices();
                mode.finish();
                break;
            case R.id.menu_follow:
                follow();
                listView.clearChoices();
                mode.finish();
                break;
            case R.id.menu_unfollow:
                unFollow();
                listView.clearChoices();
                mode.finish();
                break;
            case R.id.menu_remove_fan:
                removeFan();
                listView.clearChoices();
                mode.finish();
                break;
        }
        return true;
    }

    private void follow(){
        String token = GlobalContext.getInstance().getSpecialToken();
        String uid = bean.getId();
        String screen_name = bean.getScreen_name();

        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<UserBean> call = service.followFan(token, uid, screen_name);
        call.enqueue(new Callback<UserBean>() {
            @Override
            public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.follow_successfully),
                            Toast.LENGTH_SHORT).show();
                    adapter.update(bean, response.body());
                }
            }

            @Override
            public void onFailure(Call<UserBean> call, Throwable t) {

            }
        });
    }

    private void unFollow(){
        String token = GlobalContext.getInstance().getSpecialToken();
        String uid = bean.getId();
        String screen_name = bean.getScreen_name();

        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<UserBean> call = service.unFollowFan(token, uid, screen_name);
        call.enqueue(new Callback<UserBean>() {
            @Override
            public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.unfollow_successfully),
                            Toast.LENGTH_SHORT).show();
                    adapter.update(bean, response.body());
                }
            }

            @Override
            public void onFailure(Call<UserBean> call, Throwable t) {

            }
        });
    }

    private void removeFan(){
        String token = GlobalContext.getInstance().getSpecialToken();
        String uid = bean.getId();
        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<UserBean> call = service.removeFan(token, uid);
        call.enqueue(new Callback<UserBean>() {
            @Override
            public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.remove_fan_successfully),
                    Toast.LENGTH_SHORT).show();
                    adapter.removeItem(bean);
                }
            }

            @Override
            public void onFailure(Call<UserBean> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.mode = null;
        listView.clearChoices();
        adapter.notifyDataSetChanged();
        ((AbstractUserListFragment) fragment).setmActionMode(null);
    }
}

