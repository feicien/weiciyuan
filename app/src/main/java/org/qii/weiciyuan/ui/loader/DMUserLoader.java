package org.qii.weiciyuan.ui.loader;

import android.content.Context;

import org.qii.weiciyuan.bean.DMUserBean;
import org.qii.weiciyuan.bean.DMUserListBean;
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
public class DMUserLoader extends AbstractAsyncNetRequestTaskLoader<DMUserListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String cursor;
    private String count = "0";

    public DMUserLoader(Context context, String token, String cursor) {
        super(context);
        this.token = token;
        this.cursor = cursor;
        this.count = SettingUtility.getMsgCount();
    }

    public DMUserListBean loadData() throws WeiboException {

        DMUserListBean result = null;
        lock.lock();

        try {

            WeiBoService service = RetrofitUtils.createWeiBoService();
            Call<DMUserListBean> call = service.getUserList(token, count, cursor);
            Response<DMUserListBean> response = call.execute();
            result = response.body();

            for (DMUserBean b : result.getItemList()) {
                if (!b.isMiddleUnreadItem()) {
                    b.getListViewSpannableString();
                    b.getListviewItemShowTime();
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

