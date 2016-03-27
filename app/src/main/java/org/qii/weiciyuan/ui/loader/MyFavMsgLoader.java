package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.FavBean;
import org.qii.weiciyuan.bean.FavListBean;
import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.support.error.WeiboException;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.settinghelper.SettingUtility;
import org.qii.weiciyuan.support.utils.TimeUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Response;

/**
 * User: qii
 * Date: 13-5-15
 */
public class MyFavMsgLoader extends AbstractAsyncNetRequestTaskLoader<FavListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String page;
    private String count;

    public MyFavMsgLoader(Context context, String token, String page) {
        super(context);
        this.token = token;
        this.page = page;
        this.count = SettingUtility.getMsgCount();
    }

    public FavListBean loadData() throws WeiboException {
        FavListBean result = null;
        lock.lock();

        try {
            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<FavListBean> call = service.getGSONMsgList(token, count, page);
            Response<FavListBean> response = call.execute();
            result = response.body();

            if (result != null) {
                List<MessageBean> msgList = new ArrayList<MessageBean>();
                int size = result.getFavorites().size();
                for (int i = 0; i < size; i++) {
                    msgList.add(result.getFavorites().get(i).getStatus());
                }

                Iterator<FavBean> iterator = result.getFavorites().iterator();

                while (iterator.hasNext()) {

                    FavBean msg = iterator.next();
                    if (msg.getStatus().getUser() == null) {
                        iterator.remove();
                    } else {
                        msg.getStatus().getListViewSpannableString();
                        TimeUtility.dealMills(msg.getStatus());
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return result;
    }
}

