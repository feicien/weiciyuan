package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.DMListBean;
import org.qii.weiciyuan.support.error.WeiboException;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.settinghelper.SettingUtility;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Response;

/**
 * User: qii
 * Date: 13-5-15
 */
public class DMConversationLoader extends AbstractAsyncNetRequestTaskLoader<DMListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String uid;
    private String page;
    private String count;

    public DMConversationLoader(Context context, String token, String uid, String page) {
        super(context);
        this.token = token;
        this.uid = uid;
        this.page = page;
        this.count = SettingUtility.getMsgCount();
    }

    public DMListBean loadData() throws WeiboException {

        DMListBean result = null;
        lock.lock();

        try {
            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<DMListBean> call = service.getConversationList(token, uid, page, count);
            Response<DMListBean> response = call.execute();
            result = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;

    }
}
