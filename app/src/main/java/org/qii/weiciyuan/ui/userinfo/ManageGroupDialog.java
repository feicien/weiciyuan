package org.qii.weiciyuan.ui.userinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.GroupBean;
import org.qii.weiciyuan.bean.GroupListBean;
import org.qii.weiciyuan.bean.GroupUserBean;
import org.qii.weiciyuan.bean.UserBean;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.ui.friendgroup.ManageGroupActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: qii
 * Date: 12-11-5
 */
public class ManageGroupDialog extends DialogFragment {

    private String[] valueArray;
    private boolean[] selectedArray;

    private ArrayList<String> currentList = new ArrayList<String>();
    private ArrayList<String> addList = new ArrayList<String>();
    private ArrayList<String> removeList = new ArrayList<String>();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("valueArray", valueArray);
        outState.putBooleanArray("selectedArray", selectedArray);
        outState.putStringArrayList("currentList", currentList);
        outState.putStringArrayList("addList", addList);
        outState.putStringArrayList("removeList", removeList);
    }


    public static ManageGroupDialog newInstance(GroupListBean group, String uid) {

        Bundle args = new Bundle();
        args.putParcelable("group", group);
        args.putString("uid", uid);
        ManageGroupDialog fragment = new ManageGroupDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            valueArray = savedInstanceState.getStringArray("valueArray");
            selectedArray = savedInstanceState.getBooleanArray("selectedArray");
            currentList = savedInstanceState.getStringArrayList("currentList");
            addList = savedInstanceState.getStringArrayList("addList");
            removeList = savedInstanceState.getStringArrayList("removeList");
        }

        final GroupListBean group = getArguments().getParcelable("group");
        final String uid = getArguments().getString("uid");

        final List<GroupBean> list = group.getLists();
        selectedArray = new boolean[list.size()];

        List<String> name = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            name.add(list.get(i).getName());
        }

        valueArray = name.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View customTitle = getActivity().getLayoutInflater().inflate(R.layout.managegroupdialog_title_layout, null);

        ImageView setting = (ImageView) customTitle.findViewById(R.id.title_button);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ManageGroupActivity.class));
            }
        });

        builder.setMultiChoiceItems(valueArray, selectedArray,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        String id = group.getLists().get(which).getIdstr();
                        if (isChecked) {
                            if (!currentList.contains(id)) {
                                addList.add(id);
                            }
                        } else {
                            if (currentList.contains(id)) {
                                removeList.add(group.getLists().get(which).getIdstr());
                            }
                        }
                    }
                })
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        modifyGroupMember(uid, addList, removeList);
                    }
                })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setCustomTitle(customTitle);


        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GroupListBean group = getArguments().getParcelable("group");
        String uid = getArguments().getString("uid");

        groupList(group, uid);
    }

    private void groupList(final GroupListBean group, String uid) {
        String token = GlobalContext.getInstance().getSpecialToken();
        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<List<GroupUserBean>> call = service.getInfo(token, uid);
        call.enqueue(new Callback<List<GroupUserBean>>() {
            @Override
            public void onResponse(Call<List<GroupUserBean>> call, Response<List<GroupUserBean>> response) {
                List<GroupUserBean> value = response.body();
                if (value != null && value.size() > 0) {
                    GroupUserBean user = value.get(0);
                    List<String> ids = new ArrayList<>();
                    for (GroupBean b : user.lists) {
                        ids.add(b.getIdstr());
                    }


                    if (ids.size() > 0) {
                        currentList.clear();
                        currentList.addAll(ids);
                        int length = valueArray.length;
                        for (String id : ids) {
                            for (int i = 0; i < length; i++) {
                                if (group.getLists().get(i).getIdstr().equals(id)) {
                                    selectedArray[i] = true;
                                    ((AlertDialog) getDialog()).getListView().setItemChecked(i, true);
                                }
                            }
                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<List<GroupUserBean>> call, Throwable t) {

            }
        });

    }


    private void modifyGroupMember(String uid, List<String> add, List<String> remove) {

        String token = GlobalContext.getInstance().getSpecialToken();
        for (String id : add) {
            addMember(token, uid, id);
        }
        for (String id : remove) {
            deleteMember(token, uid, id);
        }

        Toast.makeText(GlobalContext.getInstance(),
                GlobalContext.getInstance().getString(R.string.modify_successfully),
                Toast.LENGTH_SHORT).show();


    }

    private void deleteMember(String token, String uid, String id) {
        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<UserBean> call = service.deleteMember(token, uid, id);
        call.enqueue(new Callback<UserBean>() {
            @Override
            public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                if (response.isSuccessful() && response.body() != null) {

                }
            }

            @Override
            public void onFailure(Call<UserBean> call, Throwable t) {

            }
        });
    }

    private void addMember(String token, String uid, String id) {
        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<UserBean> call = service.addMember(token, uid, id);
        call.enqueue(new Callback<UserBean>() {
            @Override
            public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                if (response.isSuccessful() && response.body() != null) {

                }
            }

            @Override
            public void onFailure(Call<UserBean> call, Throwable t) {

            }
        });
    }
}