package org.qii.weiciyuan.ui.browser;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.qii.weiciyuan.bean.BaiduMapBean;
import org.qii.weiciyuan.bean.GeoBean;
import org.qii.weiciyuan.bean.MapBean;
import org.qii.weiciyuan.dao.location.GoogleGeoCoderDao;
import org.qii.weiciyuan.support.asyncdrawable.TaskCache;
import org.qii.weiciyuan.support.file.FileLocationMethod;
import org.qii.weiciyuan.support.file.FileManager;
import org.qii.weiciyuan.support.http.BaiduMapService;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.imageutility.ImageUtility;
import org.qii.weiciyuan.support.lib.MyAsyncTask;
import org.qii.weiciyuan.support.utils.GlobalContext;
import org.qii.weiciyuan.support.utils.Utility;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * User: qii
 * Date: 13-1-25
 */
public class GetWeiboLocationInfoTask extends MyAsyncTask<Void, String, Bitmap> {

    private Activity activity;
    private TextView location;
    private ImageView mapView;

    private GeoBean geoBean;

    public GetWeiboLocationInfoTask(Activity activity, GeoBean geoBean, ImageView mapView,
            TextView location) {
        this.geoBean = geoBean;
        this.activity = activity;
        this.mapView = mapView;
        this.location = location;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        location.setVisibility(View.VISIBLE);
        location.setText(String.valueOf(geoBean.getLat() + "," + geoBean.getLon()));
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        if (Utility.isGPSLocationCorrect(geoBean)) {
            String gpsLocationString = new GoogleGeoCoderDao(activity, geoBean).get();

            try {
                if (TextUtils.isEmpty(gpsLocationString)) {


                    BaiduMapService service = RetrofitUtils.createBaiduMapService();
                    Call<BaiduMapBean> call = service.getAddress(geoBean.getLat() + "," + geoBean.getLon());
                    Response<BaiduMapBean> response = call.execute();
                    String result = response.body().result.formatted_address;

                    publishProgress(result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        String token = GlobalContext.getInstance().getSpecialToken();

        String coordinates = String.valueOf(geoBean.getLat()) + "," + String.valueOf(geoBean.getLon());

        WeiBoService service = RetrofitUtils.createWeiBoService();
        Call<MapBean> call = service.getMap(token,coordinates, "14","600*380");
        try {
            Response<MapBean> response = call.execute();

            MapBean bean = response.body();
            String mapUrl = bean.image_url;

            String filePath = FileManager.getFilePathFromUrl(mapUrl, FileLocationMethod.map);

            boolean downloaded = TaskCache
                    .waitForPictureDownload(mapUrl, null, filePath, FileLocationMethod.map);

            if (!downloaded) {
                return null;
            }

            Bitmap bitmap = ImageUtility
                    .readNormalPic(FileManager.getFilePathFromUrl(mapUrl, FileLocationMethod.map), -1,
                            -1);

            return bitmap;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (!TextUtils.isEmpty(values[0])) {
            location.setVisibility(View.VISIBLE);
            location.setText(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Bitmap s) {
        mapView.setImageBitmap(s);
        super.onPostExecute(s);
    }
}
