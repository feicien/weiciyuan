package org.qii.weiciyuan.ui.basefragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.ListBean;
import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.ui.actionmenu.StatusSingleChoiceModeListener;
import org.qii.weiciyuan.ui.adapter.StatusListAdapter;
import org.qii.weiciyuan.ui.interfaces.IRemoveItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: qii
 * Date: 12-7-29
 */
public abstract class AbstractMessageTimeLineFragment<T extends ListBean<MessageBean, ?>>
        extends AbstractTimeLineFragment<T> implements IRemoveItem {


    protected void showNewMsgToastMessage(ListBean<MessageBean, ?> newValue) {
        if (newValue != null && getActivity() != null) {
            if (newValue.getSize() == 0) {
                Toast.makeText(getActivity(), getString(R.string.no_new_message),
                        Toast.LENGTH_SHORT).show();
            } else if (newValue.getSize() > 0) {
                Toast.makeText(getActivity(),
                        getString(R.string.total) + newValue.getSize() + getString(
                                R.string.new_messages), Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void clearAndReplaceValue(ListBean<MessageBean, ?> value) {
        getList().getItemList().clear();
        getList().getItemList().addAll(value.getItemList());
        getList().setTotal_number(value.getTotal_number());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemLongClickListener(onItemLongClickListener);
    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener
            = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (position - getListView().getHeaderViewsCount() < getList().getSize()
                    && position - getListView().getHeaderViewsCount() >= 0
                    && timeLineAdapter.getItem(position - getListView().getHeaderViewsCount())
                    != null) {
                MessageBean msg = getList().getItemList()
                        .get(position - getListView().getHeaderViewsCount());
                StatusSingleChoiceModeListener choiceModeListener
                        = new StatusSingleChoiceModeListener(getListView(),
                        (StatusListAdapter) timeLineAdapter, AbstractMessageTimeLineFragment.this,
                        msg);
                if (actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }

                getListView().setItemChecked(position, true);
                getAdapter().notifyDataSetChanged();
                actionMode = getActivity().startActionMode(choiceModeListener);
                return true;
            }
            return false;
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * fix android bug,long press a item in the first tab's listview, rotate screen, the item
     * background is still blue(it is checked),
     * but if you test on other tabs' lstview, the item is not checked
     */
    @Override
    public void onResume() {
        super.onResume();
        clearActionMode();
    }

    @Override
    protected void buildListAdapter() {
        timeLineAdapter = new StatusListAdapter(this, getList().getItemList(), getListView(), true);
        getListView().setAdapter(timeLineAdapter);
    }

    @Override
    public void removeItem(int position) {
        clearActionMode();
        removeStatus(GlobalContext.getInstance().getSpecialToken(), getList().getItemList().get(position).getId(), position);
    }

    @Override
    public void removeCancel() {
        clearActionMode();
    }



    private void removeStatus(String token, String id, final int positon){

        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<MessageBean> call = service.destroyStatus(token, id);
        call.enqueue(new Callback<MessageBean>() {
            @Override
            public void onResponse(Call<MessageBean> call, Response<MessageBean> response) {
                if(response.isSuccessful() && response.body() != null){
                    ((StatusListAdapter) timeLineAdapter).removeItem(positon);
                }
            }

            @Override
            public void onFailure(Call<MessageBean> call, Throwable t) {

            }
        });

    }
}
