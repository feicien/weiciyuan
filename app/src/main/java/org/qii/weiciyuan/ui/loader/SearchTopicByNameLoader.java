package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.bean.TopicResultListBean;
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
 * Date: 13-5-12
 */
public class SearchTopicByNameLoader
        extends AbstractAsyncNetRequestTaskLoader<TopicResultListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String searchWord;
    private String page;
    private String count;

    public SearchTopicByNameLoader(Context context, String token, String searchWord, String page) {
        super(context);
        this.token = token;
        this.searchWord = searchWord;
        this.page = page;
        this.count = SettingUtility.getMsgCount();
    }

    public TopicResultListBean loadData() throws WeiboException {

        TopicResultListBean result = null;
        lock.lock();

        try {

            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<TopicResultListBean> call = service.searchTopic(token, searchWord,count, page);
            Response<TopicResultListBean> response = call.execute();
            result = response.body();

            if (result != null && result.getStatuses() != null && result.getStatuses().size() > 0) {
                List<MessageBean> msgList = result.getStatuses();
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
