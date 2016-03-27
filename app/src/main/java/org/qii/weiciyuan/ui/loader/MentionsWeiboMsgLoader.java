package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.bean.MessageListBean;
import org.qii.weiciyuan.dao.unread.ClearUnreadDao;
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
 * Date: 13-4-14
 */
public class MentionsWeiboMsgLoader extends AbstractAsyncNetRequestTaskLoader<MessageListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String sinceId;
    private String maxId;
    private String accountId;
    private String count;

    public MentionsWeiboMsgLoader(Context context, String accountId, String token, String sinceId,
            String maxId) {
        super(context);
        this.token = token;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.accountId = accountId;
        this.count = SettingUtility.getMsgCount();
    }

    public MessageListBean loadData() throws WeiboException {
        MessageListBean result = null;
        lock.lock();

        try {
            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<MessageListBean> call = service.getStatusesMetionList(token, sinceId,maxId,count);
            Response<MessageListBean> response = call.execute();
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

            ClearUnreadDao.clearUnread(token, ClearUnreadDao.MENTION_STATUS);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return result;
    }
}

