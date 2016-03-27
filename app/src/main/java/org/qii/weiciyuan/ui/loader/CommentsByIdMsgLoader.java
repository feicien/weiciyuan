package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.CommentBean;
import org.qii.weiciyuan.bean.CommentListBean;
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
public class CommentsByIdMsgLoader extends AbstractAsyncNetRequestTaskLoader<CommentListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String sinceId;
    private String maxId;
    private String id;
    private String count;

    public CommentsByIdMsgLoader(Context context, String id, String token, String sinceId,
            String maxId) {
        super(context);
        this.token = token;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.id = id;
        this.count = SettingUtility.getMsgCount();
    }

    public CommentListBean loadData() throws WeiboException {
        CommentListBean result = null;
        lock.lock();

        try {

            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<CommentListBean> call = service.getCommentList(token,id,sinceId,maxId,count);
            Response<CommentListBean> response = call.execute();

            result = response.body();

            if (result != null && result.getItemList().size() > 0) {
                List<CommentBean> msgList = result.getItemList();
                Iterator<CommentBean> iterator = msgList.iterator();
                while (iterator.hasNext()) {

                    CommentBean msg = iterator.next();
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

