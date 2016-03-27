package org.qii.weiciyuan.dao.unread;

import org.qii.weiciyuan.bean.ClearUnreadBean;
import org.qii.weiciyuan.bean.UnreadBean;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * User: qii
 * Date: 12-9-26
 */
public class ClearUnreadDao {

    public static final String STATUS = "app_message";
    public static final String FOLLOWER = "follower";
    public static final String CMT = "cmt";
    public static final String DM = "dm";
    public static final String MENTION_STATUS = "mention_status";
    public static final String MENTION_CMT = "mention_cmt";


    /**
     * first check server unread status,if unread count is the same,reset unread count
     */
    public static boolean clearMentionStatusUnread(String token, UnreadBean unreadBean, String accountId){
        return clearUnread(token, unreadBean, accountId, ClearUnreadDao.MENTION_STATUS);
    }

    public static boolean clearMentionCommentUnread(String token, UnreadBean unreadBean, String accountId){
        return clearUnread(token, unreadBean, accountId, ClearUnreadDao.MENTION_CMT);
    }

    public static boolean clearCommentUnread(String token, UnreadBean unreadBean, String accountId) {
        return clearUnread(token, unreadBean, accountId, ClearUnreadDao.CMT);
    }


    private static boolean clearUnread(String token, UnreadBean unreadBean, String accountId, String type) {
        int count = unreadBean.getMention_status();

        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<UnreadBean> call = service.getUnReadCount(token, accountId);

        UnreadBean currentCount = null;
        try {
            Response<UnreadBean> response  = call.execute();
            currentCount = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (currentCount == null) {
            return false;
        }
        //already reset or have new unread message
        if (count != currentCount.getMention_status()) {
            return false;
        }


        return clearUnread(token,type);
    }

    public static boolean clearUnread(String token, String type) {

        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<ClearUnreadBean> call2 = service.clearUnread(token, type);
        try {
            Response<ClearUnreadBean> response2 = call2.execute();
            ClearUnreadBean bean2 = response2.body();
            return bean2.result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
