package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.bean.MessageListBean;
import org.qii.weiciyuan.support.error.WeiboException;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.utils.TimeLineUtility;
import org.qii.weiciyuan.support.utils.TimeUtility;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Response;

/**
 * User: qii
 * Date: 13-5-12
 */
public class StatusesByIdLoader extends AbstractAsyncNetRequestTaskLoader<MessageListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String sinceId;
    private String maxId;
    private String screenName;
    private String uid;
    private String count;

    public StatusesByIdLoader(Context context, String uid, String screenName, String token,
            String sinceId, String maxId) {
        super(context);
        this.token = token;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.uid = uid;
        this.screenName = screenName;
    }

    public StatusesByIdLoader(Context context, String uid, String screenName, String token,
            String sinceId, String maxId, String count) {
        this(context, uid, screenName, token, sinceId, maxId);
        this.count = count;
    }

    public MessageListBean loadData() throws WeiboException {

        MessageListBean result = null;

        lock.lock();

        try {

            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<MessageListBean> call = service.getUserTimelineList(token, uid,sinceId,maxId,count,screenName);
            Response<MessageListBean> response = call.execute();
            result = response.body();

            if (result != null && result.getSize() > 0) {
                for (MessageBean b : result.getItemList()) {
                    TimeUtility.dealMills(b);
                    TimeLineUtility.addJustHighLightLinks(b);
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


