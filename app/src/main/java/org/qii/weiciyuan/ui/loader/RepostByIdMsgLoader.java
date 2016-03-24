package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.bean.RepostListBean;
import org.qii.weiciyuan.support.error.WeiboException;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.settinghelper.SettingUtility;
import org.qii.weiciyuan.support.utils.TimeUtility;

import java.io.IOException;
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
public class RepostByIdMsgLoader extends AbstractAsyncNetRequestTaskLoader<RepostListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String sinceId;
    private String maxId;
    private String id;
    private String count;

    public RepostByIdMsgLoader(Context context, String id, String token, String sinceId,
            String maxId) {
        super(context);
        this.token = token;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.id = id;
        this.count = SettingUtility.getMsgCount();
    }

    public RepostListBean loadData() throws WeiboException {
        RepostListBean result = null;

        lock.lock();

        try {
            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<RepostListBean> call = service.getRepostList(token,id,sinceId,maxId,count,"","");
            Response<RepostListBean> response = call.execute();

            result = response.body();

            if (result != null && result.getItemList().size() > 0) {
                List<MessageBean> msgList = result.getItemList();
                Iterator<MessageBean> iterator = msgList.iterator();
                while (iterator.hasNext()) {
                    MessageBean msg = iterator.next();
                    if (msg.getUser() == null) {
                        iterator.remove();
                    } else {
                        msg.getListViewSpannableString();
                        TimeUtility.dealMills(msg);
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


