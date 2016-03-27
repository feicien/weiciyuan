package org.qii.weiciyuan.ui.friendgroup;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.GroupBean;
import org.qii.weiciyuan.bean.GroupListBean;
import org.qii.weiciyuan.bean.ResultBean;
import org.qii.weiciyuan.support.database.GroupDBTask;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.support.utils.ThemeUtility;
import org.qii.weiciyuan.support.utils.Utility;
import org.qii.weiciyuan.ui.interfaces.AbstractAppActivity;
import org.qii.weiciyuan.ui.preference.SettingActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: qii
 * Date: 13-2-14
 */
public class ManageGroupActivity extends AbstractAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setTitle(getString(R.string.friend_group));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new ManageGroupFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return false;
    }

    public static class ManageGroupFragment extends ListFragment {

        private GroupAdapter adapter;
        private GroupListBean group;
        private List<String> name;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            setRetainInstance(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            name = new ArrayList<String>();
            adapter = new GroupAdapter();
            getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            getListView().setMultiChoiceModeListener(new GroupMultiChoiceModeListener());
            setListAdapter(adapter);
            group = GlobalContext.getInstance().getGroup();
            if (group != null) {
                final List<GroupBean> list = group.getLists();

                for (int i = 0; i < list.size(); i++) {
                    name.add(list.get(i).getName());
                }
                adapter.notifyDataSetChanged();
            }
        }

        private void refreshListData() {
            if (group != null) {
                name.clear();
                final List<GroupBean> list = group.getLists();

                for (int i = 0; i < list.size(); i++) {
                    name.add(list.get(i).getName());
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.actionbar_menu_managegroupfragment, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_add:
                    AddGroupDialog dialog = new AddGroupDialog();
                    dialog.setTargetFragment(ManageGroupFragment.this, 0);
                    dialog.show(getFragmentManager(), "");
                    break;
            }

            return true;
        }

        public void addGroup(String name) {
            final String token = GlobalContext.getInstance().getSpecialToken();

            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<GroupBean> call = service.create(token, name);
            call.enqueue(new Callback<GroupBean>() {
                @Override
                public void onResponse(Call<GroupBean> call, Response<GroupBean> response) {
                    GroupBean groupBean = response.body();

                    if (Utility.isAllNotNull(groupBean)) {
                        refreshGroup(token);
                    }
                }

                @Override
                public void onFailure(Call<GroupBean> call, Throwable t) {

                }
            });
        }

        public void modifyGroupName(String idstr, String name) {
            final String token = GlobalContext.getInstance().getSpecialToken();

            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<GroupBean> call = service.update(token, name, idstr);
            call.enqueue(new Callback<GroupBean>() {
                @Override
                public void onResponse(Call<GroupBean> call, Response<GroupBean> response) {
                    GroupBean groupBean = response.body();

                    if (Utility.isAllNotNull(groupBean)) {
                        refreshGroup(token);
                    }
                }

                @Override
                public void onFailure(Call<GroupBean> call, Throwable t) {

                }
            });
        }

        public void removeGroup(List<String> groupNames) {
            final String token = GlobalContext.getInstance().getSpecialToken();

            for (String idStr : groupNames) {
                WeiBoService service = RetrofitUtils.createWeiBoService();
                Call<ResultBean> call = service.destroy(token, idStr);
                call.enqueue(new Callback<ResultBean>() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                        ResultBean body = response.body();
                        if (Utility.isAllNotNull(body)) {
                            refreshGroup(token);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {

                    }
                });

            }

        }

        class GroupAdapter extends BaseAdapter {

            int checkedBG;
            int defaultBG;

            public GroupAdapter() {
                defaultBG = getResources().getColor(R.color.transparent);
                checkedBG = ThemeUtility
                        .getColor(getActivity(), R.attr.listview_checked_color);
            }

            @Override
            public int getCount() {
                return name.size();
            }

            @Override
            public String getItem(int position) {
                return name.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.managegroupactivity_list_item_layout, parent, false);
                TextView tv = (TextView) view;
                tv.setBackgroundColor(defaultBG);
                if (getListView().getCheckedItemPositions().get(position)) {
                    tv.setBackgroundColor(checkedBG);
                }
                tv.setText(name.get(position));
                return view;
            }
        }

        class GroupMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {

            MenuItem modify;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.contextual_menu_managegroupfragment, menu);
                modify = menu.findItem(R.id.menu_modify_group_name);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                SparseBooleanArray positions = null;
                ArrayList<String> checkedIdstrs = null;
                switch (item.getItemId()) {
                    case R.id.menu_modify_group_name:
                        positions = getListView().getCheckedItemPositions();
                        checkedIdstrs = new ArrayList<String>();
                        String oriName = null;
                        for (int i = 0; i < positions.size(); i++) {
                            if (positions.get(positions.keyAt(i))) {
                                oriName = group.getLists().get(positions.keyAt(i)).getName();
                                checkedIdstrs
                                        .add(group.getLists().get(positions.keyAt(i)).getIdstr());
                            }
                        }
                        ModifyGroupDialog modifyGroupDialog = ModifyGroupDialog.newInstance(oriName, checkedIdstrs.get(0));
                        modifyGroupDialog.setTargetFragment(ManageGroupFragment.this, 0);
                        modifyGroupDialog.show(getFragmentManager(), "");
                        mode.finish();
                        return true;
                    case R.id.menu_remove:
                        positions = getListView().getCheckedItemPositions();
                        checkedIdstrs = new ArrayList<String>();
                        for (int i = 0; i < positions.size(); i++) {
                            if (positions.get(positions.keyAt(i))) {
                                checkedIdstrs
                                        .add(group.getLists().get(positions.keyAt(i)).getIdstr());
                            }
                        }
                        RemoveGroupDialog removeGroupDialog = RemoveGroupDialog.newInstance(checkedIdstrs);
                        removeGroupDialog.setTargetFragment(ManageGroupFragment.this, 0);
                        removeGroupDialog.show(getFragmentManager(), "");
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                  boolean checked) {
                if (getListView().getCheckedItemCount() > 1) {
                    modify.setVisible(false);
                } else {
                    modify.setVisible(true);
                }
                mode.setTitle(String.format(getString(R.string.have_selected),
                        String.valueOf(getListView().getCheckedItemCount())));
                adapter.notifyDataSetChanged();
            }
        }


        private void refreshGroup(String token) {
            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<GroupListBean> call = service.getGroup(token);
            call.enqueue(new Callback<GroupListBean>() {
                @Override
                public void onResponse(Call<GroupListBean> call, Response<GroupListBean> response) {
                    GroupListBean groupListBean = response.body();

                    GroupDBTask.update(groupListBean, GlobalContext.getInstance().getCurrentAccountId());
                    GlobalContext.getInstance().setGroup(groupListBean);
                    group = groupListBean;
                    refreshListData();
                }

                @Override
                public void onFailure(Call<GroupListBean> call, Throwable t) {

                }
            });
        }


    }
}