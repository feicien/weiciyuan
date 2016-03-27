package org.qii.weiciyuan.ui.topic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.TopicBean;
import org.qii.weiciyuan.bean.TopicResultListBean;
import org.qii.weiciyuan.bean.android.AsyncTaskLoaderResult;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.ui.basefragment.AbstractMessageTimeLineFragment;
import org.qii.weiciyuan.ui.browser.BrowserWeiboMsgActivity;
import org.qii.weiciyuan.ui.loader.SearchTopicByNameLoader;
import org.qii.weiciyuan.ui.send.WriteWeiboActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: qii
 * Date: 12-9-26
 */
public class SearchTopicByNameFragment
        extends AbstractMessageTimeLineFragment<TopicResultListBean> {

    private String q;

    //page 0 and page 1 data is same
    private int page = 1;

    private TopicResultListBean bean = new TopicResultListBean();


    @Override
    public TopicResultListBean getList() {
        return bean;
    }


    public static SearchTopicByNameFragment newInstance(String q) {

        Bundle args = new Bundle();
        args.putString("q", q);
        SearchTopicByNameFragment fragment = new SearchTopicByNameFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page", page);
        outState.putParcelable("bean", bean);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        q = getArguments().getString("q");
        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                getPullToRefreshListView().setRefreshing();
                loadNewMsg();
                break;
            case SCREEN_ROTATE:
                //nothing
                refreshLayout(bean);
                break;
            case ACTIVITY_DESTROY_AND_CREATE:

                page = savedInstanceState.getInt("page");
                getList()
                        .addNewData((TopicResultListBean) savedInstanceState.getParcelable("bean"));
                getAdapter().notifyDataSetChanged();
                refreshLayout(getList());
                break;
        }
    }

    @Override
    protected void newMsgLoaderSuccessCallback(TopicResultListBean newValue, Bundle loaderArgs) {
        if (newValue != null && getActivity() != null && newValue.getSize() > 0) {
            getList().addNewData(newValue);
            getAdapter().notifyDataSetChanged();
            getListView().setSelectionAfterHeaderView();
            buildActionBatSubtitle();
        }
    }

    @Override
    protected void oldMsgLoaderSuccessCallback(TopicResultListBean newValue) {
        if (newValue != null && newValue.getSize() > 0) {
            getList().addOldData(newValue);
            page++;
            buildActionBatSubtitle();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_menu_searchtopicbynamefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_write:
                Intent intent = new Intent(getActivity(), WriteWeiboActivity.class);
                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
                intent.putExtra("account", GlobalContext.getInstance().getAccountBean());
                intent.putExtra("content", "#" + q + "#");
                startActivity(intent);
                break;

            case R.id.menu_refresh:
                pullToRefreshListView.setRefreshing();
                loadNewMsg();
                break;
            case R.id.menu_follow_topic:
                followTopic();
                break;
            case R.id.menu_unfollow_topic:
                unFollowTopic();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void listViewItemClick(AdapterView parent, View view, int position, long id) {
        startActivity(BrowserWeiboMsgActivity.newIntent(bean.getItemList().get(position),
                GlobalContext.getInstance().getSpecialToken()));
    }

    private void buildActionBatSubtitle() {
        int newSize = bean.getTotal_number();
        String number = bean.getSize() + "/" + newSize;
        getActivity().getActionBar().setSubtitle(number);
    }


    private void followTopic(){

        WeiBoService service = RetrofitUtils.createWeiBoService();
        String token = GlobalContext.getInstance().getSpecialToken();

        Call<TopicBean> call = service.followTopic(token, q);
        call.enqueue(new Callback<TopicBean>() {
            @Override
            public void onResponse(Call<TopicBean> call, Response<TopicBean> response) {
                if(response.isSuccessful()) {
                    TopicBean user = response.body();

                    if (user!=null) {
                        Toast.makeText(getActivity(), getString(R.string.follow_topic_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.follow_topic_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TopicBean> call, Throwable t) {
            }
        });

    }


    private void unFollowTopic(){

        isFollowTopic();

    }
    private void isFollowTopic(){

        WeiBoService service = RetrofitUtils.createWeiBoService();
        String token = GlobalContext.getInstance().getSpecialToken();

        Call<TopicBean> call = service.isFollowTopic(token, q);
        call.enqueue(new Callback<TopicBean>() {
            @Override
            public void onResponse(Call<TopicBean> call, Response<TopicBean> response) {
                if(response.isSuccessful()) {
                    TopicBean user = response.body();

                    if (user!=null && user.is_follow) {

                        unFollow(user.trend_id);

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.unfollow_topic_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TopicBean> call, Throwable t) {
            }
        });

    }

    private void unFollow(String trend_id){

        WeiBoService service = RetrofitUtils.createWeiBoService();
        String token = GlobalContext.getInstance().getSpecialToken();

        Call<TopicBean> call = service.unFollowTopic(token, trend_id);
        call.enqueue(new Callback<TopicBean>() {
            @Override
            public void onResponse(Call<TopicBean> call, Response<TopicBean> response) {
                if(response.isSuccessful()) {
                    TopicBean user = response.body();

                    if (user!=null && user.result) {
                        Toast.makeText(getActivity(), getString(R.string.unfollow_topic_successfully),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.unfollow_topic_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TopicBean> call, Throwable t) {
            }
        });

    }

    @Override
    protected Loader<AsyncTaskLoaderResult<TopicResultListBean>> onCreateNewMsgLoader(int id,
            Bundle args) {
        String token = GlobalContext.getInstance().getSpecialToken();
        String word = this.q;
        page = 1;
        return new SearchTopicByNameLoader(getActivity(), token, word, String.valueOf(page));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<TopicResultListBean>> onCreateOldMsgLoader(int id,
            Bundle args) {
        String token = GlobalContext.getInstance().getSpecialToken();
        String word = this.q;
        return new SearchTopicByNameLoader(getActivity(), token, word, String.valueOf(page + 1));
    }
}
