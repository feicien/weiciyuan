package org.qii.weiciyuan.support.http;

import org.qii.weiciyuan.support.file.FileDownloaderHttpHelper;

public class HttpUtility {

    private static HttpUtility httpUtility = new HttpUtility();

    private HttpUtility() {
    }

    public static HttpUtility getInstance() {
        return httpUtility;
    }


    public boolean executeDownloadTask(String url, String path,
            FileDownloaderHttpHelper.DownloadListener downloadListener) {
        return !Thread.currentThread().isInterrupted() && new JavaHttpUtility()
                .doGetSaveFile(url, path, downloadListener);
    }


}

