package org.qii.weiciyuan.ui.topic;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.TopicBean;
import org.qii.weiciyuan.bean.UserBean;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.utils.GlobalContext;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: qii
 * Date: 12-11-18
 */
public class UserTopicListFragment extends ListFragment {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> result = new ArrayList<String>();

    private UserBean userBean;


    public static UserTopicListFragment newInstance(UserBean userBean, ArrayList<String> topicList) {

        Bundle args = new Bundle();
        args.putParcelable("userBean", userBean);
        args.putStringArrayList("topicList", topicList);
        UserTopicListFragment fragment = new UserTopicListFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userBean = getArguments().getParcelable("userBean");
        result = getArguments().getStringArrayList("topicList");
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                result);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = result.get(position);
                String q;
                if (str.startsWith("#") && str.endsWith("#")) {
                    q = str.substring(1, str.length() - 1);
                } else {
                    q = str;
                }
                Intent intent = new Intent(getActivity(), SearchTopicByNameActivity.class);
                intent.putExtra("q", q);
                startActivity(intent);
            }
        });
        if (result == null || result.size() == 0) {
            refresh();
        }
    }

    private void refresh() {
        WeiBoService service = RetrofitUtils.createWeiBoService();
        String token = GlobalContext.getInstance().getSpecialToken();
        String uid =  userBean.getId();

        Call<List<TopicBean>> call = service.getTopicList(token, uid);
        call.enqueue(new Callback<List<TopicBean>>() {
            @Override
            public void onResponse(Call<List<TopicBean>> call, Response<List<TopicBean>> response) {
                if(response.isSuccessful()) {

                    List<TopicBean> value = response.body();

                    if (value != null) {
                        ArrayList<String> msgList = new ArrayList<String>();
                        for (TopicBean b : value) {
                            msgList.add(b.hotword);
                        }
                        result.clear();
                        result.addAll(msgList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<TopicBean>> call, Throwable t) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (userBean.getId().equals(GlobalContext.getInstance().getCurrentAccountId())) {
            inflater.inflate(R.menu.actionbar_menu_usertopiclistfragment, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_topic:
                FollowTopicDialog dialog = FollowTopicDialog.newInstance();
                dialog.setTargetFragment(this, 1);
                dialog.show(getFragmentManager(), "");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addTopic(String keyWord) {

        WeiBoService service = RetrofitUtils.createWeiBoService();
        String token = GlobalContext.getInstance().getSpecialToken();

        Call<TopicBean> call = service.followTopic(token, keyWord);
        call.enqueue(new Callback<TopicBean>() {
            @Override
            public void onResponse(Call<TopicBean> call, Response<TopicBean> response) {
                if(response.isSuccessful()) {
                    TopicBean user = response.body();

                    if (user!=null) {
                        Toast.makeText(getActivity(), getString(R.string.follow_topic_successfully),
                                Toast.LENGTH_SHORT).show();
                        refresh();

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.follow_topic_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TopicBean> call, Throwable t) {
            }
        });

    }
}
