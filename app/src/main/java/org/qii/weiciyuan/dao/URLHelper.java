package org.qii.weiciyuan.dao;

import org.qii.weiciyuan.support.utils.Utility;

/**
 * User: qii
 * Date: 12-7-28
 */
public class URLHelper {
    //base url
    private static final String URL_SINA_WEIBO = "https://api.weibo.com/2/";

    //login
    public static final String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";

    public static final String APP_KEY = Utility.rot47("`_edd``d`b");


    public static final String DIRECT_URL = Utility.rot47("9EEADi^^2A:]H6:3@]4@>^@2FE9a^5672F=E]9E>=");


    //main timeline
    public static final String FRIENDS_TIMELINE = URL_SINA_WEIBO + "statuses/friends_timeline.json";
    public static final String COMMENTS_MENTIONS_TIMELINE = URL_SINA_WEIBO + "comments/mentions.json";
    public static final String STATUSES_MENTIONS_TIMELINE = URL_SINA_WEIBO + "statuses/mentions.json";
    public static final String COMMENTS_TO_ME_TIMELINE = URL_SINA_WEIBO + "comments/to_me.json";
    public static final String COMMENTS_BY_ME_TIMELINE = URL_SINA_WEIBO + "comments/by_me.json";
    public static final String BILATERAL_TIMELINE = URL_SINA_WEIBO + "statuses/bilateral_timeline.json";
    public static final String TIMELINE_RE_CMT_COUNT = URL_SINA_WEIBO + "statuses/count.json";


    //group timeline
    public static final String FRIENDSGROUP_TIMELINE = URL_SINA_WEIBO + "friendships/groups/timeline.json";


    //send weibo
    public static final String STATUSES_UPDATE = URL_SINA_WEIBO + "statuses/update.json";
    public static final String STATUSES_UPLOAD = URL_SINA_WEIBO + "statuses/upload.json";



    public static final String DISABLE_COMMENT = "0";
    public static final String ENABLE_COMMENT = "1";
    public static final String ENABLE_ORI_COMMENT = "2";
    public static final String ENABLE_COMMENT_ALL = "3";



    public static final String BAIDU_GEO_CODER_MAP
            = "http://api.map.baidu.com/geocoder/v2/?ak=AAacde37a912803101fe91fb2de38c30&coordtype=wgs84ll&output=json&pois=0&location=%f,%f";

    /**
     * black magic
     */




    //edit my profile
    public static final String MYPROFILE_EDIT = URL_SINA_WEIBO + "account/profile/basic_update.json";
    public static final String AVATAR_UPLOAD = URL_SINA_WEIBO + "account/avatar/upload.json";
}
