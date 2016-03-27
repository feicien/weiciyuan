package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.UserListBean;
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
 * Date: 13-5-12
 */
public class FanUserLoader extends AbstractAsyncNetRequestTaskLoader<UserListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String uid;
    private String cursor;
    private String count;

    public FanUserLoader(Context context, String token, String uid, String cursor) {
        super(context);
        this.token = token;
        this.uid = uid;
        this.cursor = cursor;
        this.count = SettingUtility.getMsgCount();
    }

    public UserListBean loadData() throws WeiboException {
        UserListBean result = null;
        lock.lock();

        try {
            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<UserListBean> call = service.getFollowerList(token, uid, count,cursor);
            Response<UserListBean> response = call.execute();
            result = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;
    }
}

