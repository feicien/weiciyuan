package org.qii.weiciyuan.othercomponent;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.EmotionBean;
import org.qii.weiciyuan.support.database.DatabaseManager;
import org.qii.weiciyuan.support.error.WeiboException;
import org.qii.weiciyuan.support.file.FileLocationMethod;
import org.qii.weiciyuan.support.file.FileManager;
import org.qii.weiciyuan.support.http.HttpUtility;
import org.qii.weiciyuan.support.http.RetrofitUtils;
import org.qii.weiciyuan.support.http.WeiBoService;
import org.qii.weiciyuan.support.lib.MyAsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * User: qii
 * Date: 12-9-24
 */
@Deprecated
public class DownloadEmotionsService extends Service {
    private String token;

    private DownloadTask task;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        token = intent.getStringExtra("token");

        if (task == null) {
            task = new DownloadTask();
            task.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
        return super.onStartCommand(intent, flags, startId);

    }


    class DownloadTask extends MyAsyncTask<Void, Integer, Void> {

        Notification notification;
        final int NOTIFICATION_ID = 2;
        WeiboException e;
        int size;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Notification.Builder builder = new Notification.Builder(DownloadEmotionsService.this)
                    .setTicker(getString(R.string.download_emotions))
                    .setContentTitle(getString(R.string.weibo_emotions))
                    .setContentText(getString(R.string.background_downloading))
                    .setProgress(0, 100, false)
                    .setSmallIcon(R.drawable.download_light);
            notification = builder.getNotification();
            startForeground(NOTIFICATION_ID, notification);
        }

        @Override
        protected Void doInBackground(Void... params) {
            int now = 0;
            try {

                WeiBoService service = RetrofitUtils.createWeiBoService();
                Call<List<EmotionBean>> call = service.getEmotions(token, "", "");
                Response<List<EmotionBean>> response = call.execute();
                List<EmotionBean> list = response.body();

                List<EmotionBean> needList = new ArrayList<EmotionBean>();

                for (EmotionBean bean : list) {
                    if (TextUtils.isEmpty(bean.getCategory())) {
                        needList.add(bean);
                    }
                }

                size = needList.size();

                DatabaseManager.getInstance().addEmotions(needList);

                for (EmotionBean bean : needList) {
                    String url = bean.getUrl();
                    String path = FileManager.getFilePathFromUrl(url, FileLocationMethod.emotion);

                    HttpUtility.getInstance().executeDownloadTask(url, path, null);
                    now++;
                    publishProgress(now);
                }


            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {

            if (values.length > 0) {
                int data = values[0];

                Notification.Builder builder = new Notification.Builder(DownloadEmotionsService.this)
                        .setTicker(getString(R.string.download_emotions))
                        .setContentTitle(getString(R.string.weibo_emotions))
                        .setContentText(getString(R.string.background_downloading))
                        .setProgress(size, data, false)
                        .setNumber(size - data)
                        .setSmallIcon(R.drawable.download_light);
                notification = builder.getNotification();

                startForeground(NOTIFICATION_ID, notification);

            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stopForeground(true);
            stopSelf();
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            stopForeground(true);
            stopSelf();
        }
    }


}
