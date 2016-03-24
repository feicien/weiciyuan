package org.qii.weiciyuan.ui.search;

import android.app.Activity;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.AtUserBean;
import org.qii.weiciyuan.support.database.AtUsersDBTask;
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
 * Date: 12-10-8
 */
public class AtUserFragment extends ListFragment {

    private ArrayAdapter<String> adapter;

    private List<String> result = new ArrayList<String>();
    private List<AtUserBean> atList = new ArrayList<AtUserBean>();

    private String token;

    public static AtUserFragment newInstance(String token) {
        AtUserFragment fragment = new AtUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        fragment.setArguments(bundle);
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
        token = getArguments().getString("token");
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                result);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("name", "@" + atList.get(position).getNickname() + " ");
                getActivity().setResult(Activity.RESULT_OK, intent);
                AtUsersDBTask.add(atList.get(position),
                        GlobalContext.getInstance().getCurrentAccountId());
                getActivity().finish();
            }
        });

        atList = AtUsersDBTask.get(GlobalContext.getInstance().getCurrentAccountId());
        for (AtUserBean b : atList) {
            result.add(b.getNickname());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_menu_atuserfragment, menu);
        SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.at_other));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    atUser(newText);
                } else {

                    atList.clear();
                    result.clear();
                    atList = AtUsersDBTask.get(GlobalContext.getInstance().getCurrentAccountId());
                    for (AtUserBean b : atList) {
                        result.add(b.getNickname());
                    }
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        searchView.requestFocus();
    }


    private void atUser(final String q){
        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<List<AtUserBean>> call = service.searchATUser(token, q,"10","0","2");
        call.enqueue(new Callback<List<AtUserBean>>() {
            @Override
            public void onResponse(Call<List<AtUserBean>> call, Response<List<AtUserBean>> response) {

                if (response.isSuccessful()) {

                    List<AtUserBean> atUserBeans = response.body();
                    if (atUserBeans == null || atUserBeans.size() == 0) {
                        result.clear();
                        atList.clear();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    result.clear();
                    for (AtUserBean b : atUserBeans) {
                        if (b.getRemark().contains(q)) {
                            result.add(b.getNickname() + "(" + b.getRemark() + ")");
                        } else {
                            result.add(b.getNickname());
                        }
                    }
                    atList = atUserBeans;
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<AtUserBean>> call, Throwable t) {

            }
        });
    }
}
