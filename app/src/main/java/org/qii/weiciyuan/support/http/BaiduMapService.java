package org.qii.weiciyuan.support.http;

import org.qii.weiciyuan.bean.BaiduMapBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * FIXME
 *
 * doc: http://developer.baidu.com/map/webservice-geocoding.htm
 * @author feicien (ithcheng@gmail.com)
 * @since 2016-03-25 12:04
 */
public interface BaiduMapService {

     String BASE_URL = "http://api.map.baidu.com/";

//    &location=%f,%f
//
//    lat, long_fix);
    @GET("geocoder/v2/?ak=AAacde37a912803101fe91fb2de38c30&coordtype=wgs84ll&output=json&pois=0")
    Call<BaiduMapBean> getAddress(@Query("location") String location);



}
