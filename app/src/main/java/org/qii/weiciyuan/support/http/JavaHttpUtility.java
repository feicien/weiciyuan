package org.qii.weiciyuan.support.http;

import android.text.TextUtils;

import org.qii.weiciyuan.BuildConfig;
import org.qii.weiciyuan.support.debug.AppLogger;
import org.qii.weiciyuan.support.file.FileDownloaderHttpHelper;
import org.qii.weiciyuan.support.file.FileManager;
import org.qii.weiciyuan.support.imageutility.ImageUtility;
import org.qii.weiciyuan.support.utils.Utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * User: qii
 * Date: 12-12-19
 */
public class JavaHttpUtility {

    private static final int DOWNLOAD_CONNECT_TIMEOUT = 15 * 1000;
    private static final int DOWNLOAD_READ_TIMEOUT = 60 * 1000;

    public class NullHostNameVerifier implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };

    public JavaHttpUtility() {

        //allow Android to use an untrusted certificate for SSL/HTTPS connection
        //so that when you debug app, you can use Fiddler http://fiddler2.com to logs all HTTPS traffic
        try {
            if (BuildConfig.DEBUG) {
                HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            }
        } catch (Exception e) {
        }
    }



    private static Proxy getProxy() {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort)) {
            return new Proxy(java.net.Proxy.Type.HTTP,
                    new InetSocketAddress(proxyHost, Integer.valueOf(proxyPort)));
        } else {
            return null;
        }
    }



    public boolean doGetSaveFile(String urlStr, String path,
            FileDownloaderHttpHelper.DownloadListener downloadListener) {

        File file = FileManager.createNewFileInSDCard(path);
        if (file == null) {
            return false;
        }

        boolean result = false;

        BufferedOutputStream out = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(urlStr);
            AppLogger.d("download request=" + urlStr);
            Proxy proxy = getProxy();
            if (proxy != null) {
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(DOWNLOAD_CONNECT_TIMEOUT);
            urlConnection.setReadTimeout(DOWNLOAD_READ_TIMEOUT);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();

            int status = urlConnection.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK) {
                return false;
            }

            int bytetotal = (int) urlConnection.getContentLength();
            int bytesum = 0;
            int byteread = 0;
            out = new BufferedOutputStream(new FileOutputStream(file));

            InputStream is = urlConnection.getInputStream();
            String content_encode = urlConnection.getContentEncoding();
            if (!TextUtils.isEmpty(content_encode) &&
                    content_encode.equals("gzip")) {
                is = new GZIPInputStream(is);
            }
            in = new BufferedInputStream(is);

            final Thread thread = Thread.currentThread();
            byte[] buffer = new byte[1444];
            while ((byteread = in.read(buffer)) != -1) {
                if (thread.isInterrupted()) {
                    if (((float) bytesum / (float) bytetotal) < 0.8f) {
                        file.delete();
                        throw new InterruptedIOException();
                    }
                }

                bytesum += byteread;
                out.write(buffer, 0, byteread);
                if (downloadListener != null && bytetotal > 0) {
                    downloadListener.pushProgress(bytesum, bytetotal);
                }
            }
            if (downloadListener != null) {
                downloadListener.completed();
            }
            AppLogger.v("download request= " + urlStr + " download finished");
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            AppLogger.v("download request= " + urlStr + " download failed");
        } finally {
            Utility.closeSilently(in);
            Utility.closeSilently(out);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result && ImageUtility.isThisBitmapCanRead(path);
    }




}



