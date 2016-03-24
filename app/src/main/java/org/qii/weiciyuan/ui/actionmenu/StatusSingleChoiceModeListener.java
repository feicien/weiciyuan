package org.qii.weiciyuan.ui.actionmenu;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.FavBean;
import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.support.utils.Utility;
import org.qii.weiciyuan.ui.basefragment.AbstractTimeLineFragment;
import org.qii.weiciyuan.ui.browser.BrowserWeiboMsgFragment;
import org.qii.weiciyuan.ui.send.WriteCommentActivity;
import org.qii.weiciyuan.ui.send.WriteRepostActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: qii
 * Date: 12-9-9
 */
public class StatusSingleChoiceModeListener implements ActionMode.Callback {

    private ListView listView;
    private BaseAdapter adapter;
    private Fragment fragment;
    private ActionMode mode;
    private MessageBean bean;
    private ShareActionProvider mShareActionProvider;

    public void finish() {
        if (mode != null) {
            mode.finish();
        }
    }

    public StatusSingleChoiceModeListener(ListView listView, BaseAdapter adapter, Fragment fragment,
            MessageBean bean) {
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
        if (bean.getUser().getId().equals(GlobalContext.getInstance().getCurrentAccountId())) {
            inflater.inflate(R.menu.contextual_menu_fragment_status_listview_myself, menu);
        } else {
            inflater.inflate(R.menu.contextual_menu_fragment_status_listview, menu);
        }

        mode.setTitle(bean.getUser().getScreen_name());


        MenuItem item = menu.findItem(R.id.menu_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        if (fragment.getActivity() != null) {
            Utility.setShareIntent(fragment.getActivity(), mShareActionProvider, bean);
        }
        mShareActionProvider.setOnShareTargetSelectedListener(
                new ShareActionProvider.OnShareTargetSelectedListener() {
                    @Override
                    public boolean onShareTargetSelected(ShareActionProvider source,
                            Intent intent) {
                        finish();
                        return false;
                    }
                });
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (listView.getCheckedItemCount() == 0) {
            return true;
        }

        Intent intent;
        long[] ids = listView.getCheckedItemIds();
        switch (item.getItemId()) {
            case R.id.menu_repost:
                intent = new Intent(getActivity(), WriteRepostActivity.class);
                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
                intent.putExtra("id", String.valueOf(ids[0]));
                intent.putExtra("msg", bean);
                getActivity().startActivity(intent);
                listView.clearChoices();
                mode.finish();
                break;
            case R.id.menu_comment:
                intent = new Intent(getActivity(), WriteCommentActivity.class);
                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
                intent.putExtra("id", String.valueOf(ids[0]));
                intent.putExtra("msg", bean);
                getActivity().startActivity(intent);
                listView.clearChoices();
                mode.finish();

                break;
            case R.id.menu_fav:
                fav(GlobalContext.getInstance().getSpecialToken(), bean.getId());
                listView.clearChoices();
                mode.finish();
                break;
            case R.id.menu_unfav:
                unFav(GlobalContext.getInstance().getSpecialToken(), bean.getId());
                listView.clearChoices();
                mode.finish();
                break;
            case R.id.menu_remove:

                int position = listView.getCheckedItemPosition() - listView.getHeaderViewsCount();
                RemoveDialog dialog = RemoveDialog.newInstance(position);
                dialog.setTargetFragment(fragment, 0);
                dialog.show(fragment.getFragmentManager(), "");

                break;
            case R.id.menu_share:
                if (fragment.getActivity() != null) {
                    Utility.setShareIntent(fragment.getActivity(), mShareActionProvider, bean);
                }
                mShareActionProvider.setOnShareTargetSelectedListener(
                        new ShareActionProvider.OnShareTargetSelectedListener() {
                            @Override
                            public boolean onShareTargetSelected(ShareActionProvider source,
                                    Intent intent) {
                                finish();
                                return false;
                            }
                        });
                break;
            case R.id.menu_copy:
                ClipboardManager cm = (ClipboardManager) fragment.getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("sinaweibo", bean.getText()));
                Toast.makeText(fragment.getActivity(),
                        fragment.getString(R.string.copy_successfully), Toast.LENGTH_SHORT).show();
                mode.finish();
                break;
        }
        return true;
    }

    private void unFav(String token, String id) {
        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<FavBean> call = service.unFavIt(token, id);
        call.enqueue(new Callback<FavBean>() {
            @Override
            public void onResponse(Call<FavBean> call, Response<FavBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(GlobalContext.getInstance(),
                            GlobalContext.getInstance().getString(R.string.un_fav_successfully),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavBean> call, Throwable t) {

            }
        });

    }

    private void fav(String token, String id) {
        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<FavBean> call = service.favIt(token, id);
        call.enqueue(new Callback<FavBean>() {
            @Override
            public void onResponse(Call<FavBean> call, Response<FavBean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(GlobalContext.getInstance(),
                            GlobalContext.getInstance().getString(R.string.fav_successfully),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavBean> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.mode = null;
        listView.clearChoices();
        adapter.notifyDataSetChanged();
        if (fragment instanceof AbstractTimeLineFragment) {
            ((AbstractTimeLineFragment) fragment).setActionMode(null);
        }

        if (fragment instanceof BrowserWeiboMsgFragment) {
            ((BrowserWeiboMsgFragment) fragment).setActionMode(null);
        }
    }
}