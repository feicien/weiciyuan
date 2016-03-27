package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.bean.ShareListBean;
import org.qii.weiciyuan.support.error.WeiboException;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.settinghelper.SettingUtility;
import org.qii.weiciyuan.support.utils.TimeUtility;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Response;

/**
 * User: qii
 * Date: 13-5-15
 */
public class BrowserShareMsgLoader extends AbstractAsyncNetRequestTaskLoader<ShareListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String maxId;
    private String url;
    private String count;

    public BrowserShareMsgLoader(Context context, String token, String url, String maxId) {
        super(context);
        this.token = token;
        this.maxId = maxId;
        this.url = url;
        this.count = SettingUtility.getMsgCount();
    }

    public ShareListBean loadData() throws WeiboException {
        ShareListBean result = null;

        lock.lock();

        try {
            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<ShareListBean> call = service.shortUrlShareStatus(token,count,maxId,url);
            Response<ShareListBean> response = call.execute();

            result = response.body();

            if (result != null) {
                Iterator<MessageBean> iterator = result.getItemList().iterator();
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



