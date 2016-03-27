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



    public static final String DISABLE_COMMENT = "0";
    public static final String ENABLE_COMMENT = "1";
    public static final String ENABLE_ORI_COMMENT = "2";
    public static final String ENABLE_COMMENT_ALL = "3";



}
