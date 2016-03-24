package org.qii.weiciyuan.support.http;


import org.qii.weiciyuan.bean.AtUserBean;
import org.qii.weiciyuan.bean.ClearUnreadBean;
import org.qii.weiciyuan.bean.CommentBean;
import org.qii.weiciyuan.bean.CommentListBean;
import org.qii.weiciyuan.bean.DMListBean;
import org.qii.weiciyuan.bean.DMUserListBean;
import org.qii.weiciyuan.bean.EmotionBean;
import org.qii.weiciyuan.bean.FavBean;
import org.qii.weiciyuan.bean.FavListBean;
import org.qii.weiciyuan.bean.GroupBean;
import org.qii.weiciyuan.bean.GroupListBean;
import org.qii.weiciyuan.bean.GroupUserBean;
import org.qii.weiciyuan.bean.MapBean;
import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.bean.MessageListBean;
import org.qii.weiciyuan.bean.QueryIdBean;
import org.qii.weiciyuan.bean.RepostListBean;
import org.qii.weiciyuan.bean.ResultBean;
import org.qii.weiciyuan.bean.SearchStatusListBean;
import org.qii.weiciyuan.bean.ShareListBean;
import org.qii.weiciyuan.bean.ShortUrlBean;
import org.qii.weiciyuan.bean.TopicBean;
import org.qii.weiciyuan.bean.TopicResultListBean;
import org.qii.weiciyuan.bean.UnreadBean;
import org.qii.weiciyuan.bean.UserBean;
import org.qii.weiciyuan.bean.UserListBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * FIXME
 *
 * @author feicien (ithcheng@gmail.com)
 * @since 2016-03-22 19:37
 */
public interface WeiBoService {


    @GET("direct_messages/conversation.json")
    Call<DMListBean> getConversationList(@Query("access_token") String token,
                                         @Query("uid") String uid,
                                         @Query("page") String page,
                                         @Query("count") String count);


    @GET("direct_messages/user_list.json")
    Call<DMUserListBean> getUserList(@Query("access_token") String token,
                                     @Query("count") String count,
                                     @Query("cursor") String cursor);


    /**
     * http://open.weibo.com/wiki/2/direct_messages/new
     */
    @POST("direct_messages/new.json")
    Call<ResultBean> send(@Field("access_token") String token,
                          @Field("text") String text,
                          @Field("uid") String uid);

    /**
     * 获取分组列表
     */
    @GET("friendships/groups.json")
    Call<GroupListBean> getGroup(@Query("access_token") String token);


    /**
     * 创建分组
     * http://open.weibo.com/wiki/2/friendships/groups/create
     */
    @POST("friendships/groups/create.json")
    Call<GroupBean> create(@Field("access_token") String token,
                           @Field("name") String name);

    /**
     * 更新分组
     * http://open.weibo.com/wiki/2/friendships/groups/update
     */
    @POST("friendships/groups/update.json")
    Call<GroupBean> update(@Field("access_token") String token,
                           @Field("name") String name,
                           @Field("list_id") String idStr);

    /**
     * 删除分组
     * http://open.weibo.com/wiki/2/friendships/groups/destroy
     * suggest use idstr
     */
    @POST("friendships/groups/destroy.json")
    Call<ResultBean> destroy(@Field("access_token") String token,
                             @Field("list_id") String idStr);


    @GET("friendships/groups/listed.json")
    Call<List<GroupUserBean>> getInfo(@Query("access_token") String token,
                                      @Query("uids") String uids);


    /**
     * 删除评论
     */
    @POST("comments/destroy.json")
    Call<CommentBean> destroyComment(@Field("access_token") String token,
                                     @Field("cid") String cid);


    /**
     * 删除微博
     */
    @POST("statuses/destroy.json")
    Call<MessageBean> destroyStatus(@Field("access_token") String token,
                                    @Field("id") String id);


    @GET("emotions.json")
    Call<List<EmotionBean>> getEmotions(@Query("access_token") String token,
                                        @Query("type") String type,
                                        @Query("language") String language);


    /**
     * 收藏
     */
    @POST("favorites/create.json")
    Call<FavBean> favIt(@Field("access_token") String token,
                        @Field("id") String id);

    /**
     * 删除收藏
     */
    @POST("favorites/destroy.json")
    Call<FavBean> unFavIt(@Field("access_token") String token,
                          @Field("id") String id);


    @GET("favorites.json")
    Call<FavListBean> getGSONMsgList(@Query("access_token") String token,
                                     @Query("count") String count,
                                     @Query("page") String page);


    @POST("friendships/groups/members/add.json")
    Call<UserBean> addMember(@Field("access_token") String token,
                             @Field("uid") String uid,
                             @Field("list_id") String list_id);

    @POST("friendships/groups/members/destroy.json")
    Call<UserBean> deleteMember(@Field("access_token") String token,
                                @Field("uid") String uid,
                                @Field("list_id") String list_id);


    @GET("account/get_uid.json")
    Call<String> getUserUID(@Query("access_token") String token);

//    String url = URLHelper.UID;
//    Map<String, String> map = new HashMap<String, String>();
//    map.put("access_token", access_token);
//    String uidJson = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);
//
//    String uid = "";
//
//    try {
//        JSONObject jsonObject = new JSONObject(uidJson);
//        uid = jsonObject.optString("uid");
//    } catch (JSONException e) {
//        AppLogger.e(e.getMessage());
//    }


    @GET("users/show.json")
    Call<UserBean> getOAuthUserInfo(@Query("access_token") String token,
                                    @Query("uid") String uid);

//        String url = URLHelper.USER_SHOW;
//        String result = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);
//
//        Gson gson = new Gson();
//        UserBean user = new UserBean();
//        try {
//            user = gson.fromJson(result, UserBean.class);
//        } catch (JsonSyntaxException e) {
//            AppLogger.e(result);
//        }
//        return user;
//    }


    @GET("location/base/get_map_image.json")
    Call<MapBean> getMap(@Query("access_token") String token,
                         @Query("center_coordinate") String center_coordinate,
                         @Query("zoom") String zoom,
                         @Query("size") String size);


//        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);
//        String mapUrl = "";
//        try {
//            JSONObject jsonObject = new JSONObject(jsonData);
//            JSONArray array = jsonObject.optJSONArray("map");
//            jsonObject = array.getJSONObject(0);
//            mapUrl = jsonObject.getString("image_url");
//        } catch (JSONException e) {
//
//        }
//
//        if (TextUtils.isEmpty(mapUrl)) {
//            return null;
//        }


    @POST("friendships/followers/destroy.json")
    Call<UserBean> removeFan(@Field("access_token") String token,
                             @Field("uid") String uid);


    @POST("friendships/create.json")
    Call<UserBean> followFan(@Field("access_token") String token,
                             @Field("uid") String uid,
                             @Field("screen_name") String screen_name);

    @POST("friendships/destroy.json")
    Call<UserBean> unFollowFan(@Field("access_token") String token,
                               @Field("uid") String uid,
                               @Field("screen_name") String screen_name);


    @GET("comments/show.json")
    Call<CommentListBean> getCommentList(@Query("access_token") String token,
                                         @Query("id") String id,
                                         @Query("since_id") String since_id,
                                         @Query("max_id") String max_id,
                                         @Query("count") String count,
                                         @Query("page") String page,
                                         @Query("filter_by_author") String filter_by_author);


    @GET("statuses/repost_timeline.json")
    Call<RepostListBean> getRepostList(@Query("access_token") String token,
                                       @Query("id") String id,
                                       @Query("since_id") String since_id,
                                       @Query("max_id") String max_id,
                                       @Query("count") String count,
                                       @Query("page") String page,
                                       @Query("filter_by_author") String filter_by_author);


    @GET("statuses/show.json")
    Call<MessageBean> getMsg(@Query("access_token") String access_token,
                             @Query("id") String id);


    @GET("users/domain_show.json")
    Call<UserBean> getUserDomainShow(@Query("access_token") String token,
                                     @Query("domain") String domain);

    @GET("users/show.json")
    Call<UserBean> getUserShow(@Query("access_token") String token,
                               @Query("uid") String uid,
                               @Query("screen_name") String screen_name);

//        String url = (!TextUtils.isEmpty(domain) ? URLHelper.USER_DOMAIN_SHOW : URLHelper.USER_SHOW);


    @POST("trends/follow.json")
    Call<TopicBean> followTopic(@Field("access_token") String token,
                                @Field("trend_name") String trend_name);


    @GET("trends/is_follow.json")
    Call<TopicBean> isFollowTopic(@Query("access_token") String token,
                                  @Query("trend_name") String trend_name);


    @POST("trends/destroy.json")
    Call<TopicBean> unFollowTopic(@Field("access_token") String token,
                                  @Field("trend_id") String trend_id);


    @GET("trends.json")
    Call<List<TopicBean>> getTopicList(@Query("access_token") String token,
                                       @Query("uid") String uid);
//                                       @Query("count") String count,
//                                       @Query("page") String page);



    @GET("search/topics.json")
    Call<TopicResultListBean> searchTopic(@Query("access_token") String token,
                                          @Query("q") String q,
                                          @Query("count") String count,
                                          @Query("page") String page);



    //    private String count = "10";
//    private String type = "0";
//    private String range = "2";
    @GET("search/suggestions/at_users.json")
    Call<List<AtUserBean>> searchATUser(@Query("access_token") String token,
                                         @Query("q") String q,
                                         @Query("count") String count,
                                         @Query("type") String type,
                                         @Query("range") String range);




    @GET("search/users.json")
    Call<UserListBean> searchUserList(@Query("access_token") String token,
                                      @Query("q") String q,
                                      @Query("count") String count,
                                      @Query("page") String page);

//        this.count = SettingUtility.getMsgCount();
//        String url = URLHelper.USERS_SEARCH;



    @GET("search/statuses.json")
    Call<SearchStatusListBean> searchStatusList(@Query("access_token") String token,
                                             @Query("q") String q,
                                             @Query("count") String count,
                                             @Query("page") String page);




    @GET("statuses/user_timeline.json")
    Call<MessageListBean> getUserTimelineList(@Query("access_token") String token,
                                            @Query("uid") String uid,
                                            @Query("since_id") String since_id,
                                            @Query("max_id") String max_id,
                                            @Query("count") String count,
                                            @Query("screen_name") String screen_name);




    @GET("short_url/share/statuses.json")
    Call<ShareListBean> shortUrlShareStatus(@Query("access_token") String token,
                                            @Query("count") String count,
                                            @Query("max_id") String max_id,
                                            @Query("url_short") String url_short);



    @GET("short_url/share/counts.json")
    Call<ShortUrlBean> shortUrlShareCount(@Query("access_token") String token,
                                          @Query("url_short") String url_short);



    //        map.put("type", "1");
//        map.put("isBase62", "1");
    @GET("statuses/queryid.json")
    Call<QueryIdBean> queryId(@Query("access_token") String token,
                              @Query("mid") String mid,
                              @Query("type") String type,
                              @Query("isBase62") String isBase62);




    @POST("friendships/remark/update.json")
    Call<UserBean> updateRemark(@Field("access_token") String token,
                                @Field("uid") String uid,
                                @Field("remark") String remark);



    @GET("friendships/friends.json")
    Call<UserListBean> getFriendList(@Query("access_token") String token,
                                     @Query("uid") String uid,
                                     @Query("count") String count,
                                     @Query("cursor") String cursor);
//                                     @Query("trim_status") String trim_status,
//                                     @Query("screen_name") String screen_name);



    @GET("friendships/followers.json")
    Call<UserListBean> getFollowerList(@Query("access_token") String token,
                                       @Query("uid") String uid,
                                       @Query("count") String count,
                                       @Query("cursor") String cursor);

//        map.put("trim_status", trim_status);
//        map.put("screen_name", screen_name);




    @GET("remind/unread_count.json")
    Call<UnreadBean> getUnReadCount(@Query("access_token") String token,
                              @Query("uid") String uid);


    @GET("remind/set_count.json")
    Call<ClearUnreadBean> clearUnread(@Query("access_token") String token,
                                      @Query("type") String type);



    @POST("comments/create.json")
    Call<CommentBean> createComment(@Field("access_token") String token,
                                     @Field("id") String id,
                                     @Field("comment") String comment,
                                     @Field("comment_ori") String comment_ori);



    @POST("comments/reply.json")
    Call<CommentBean> replyComment(@Field("access_token") String token,
                                   @Field("id") String id,
                                   @Field("cid") String cid,
                                   @Field("comment") String comment);




    @POST("statuses/repost.json")
    Call<MessageBean> sendNewMsg(@Field("access_token") String token,
                                 @Field("id") String id,
                                 @Field("status") String status,
                                 @Field("is_comment") String is_comment);



    @GET("comments/to_me.json")
    Call<CommentListBean> getCommentToMe(@Query("access_token") String token,
                                         @Query("since_id") String since_id,
                                         @Query("max_id") String max_id,
                                         @Query("count") String count);




    @GET("comments/mentions.json")
    Call<CommentListBean> getMentionToMe(@Query("access_token") String token,
                                         @Query("since_id") String since_id,
                                         @Query("max_id") String max_id,
                                         @Query("count") String count);



}
