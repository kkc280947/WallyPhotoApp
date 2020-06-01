package r.c.wallyphotoapp.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.WallyApplication;
import r.c.wallyphotoapp.repository.ImageFileRepository;
import retrofit2.Response;

import static r.c.wallyphotoapp.utils.WallyConstants.ACTION_DOWNLOAD;
import static r.c.wallyphotoapp.utils.WallyConstants.ACTION_FAIL;
import static r.c.wallyphotoapp.utils.WallyConstants.ACTION_UPDATE;
import static r.c.wallyphotoapp.utils.WallyConstants.KEY_PROGRESS;
import static r.c.wallyphotoapp.utils.WallyConstants.NOTIFICATION_CHANNEL_ID;
import static r.c.wallyphotoapp.utils.WallyConstants.NOTIFICATION_CHANNEL_NAME;
import static r.c.wallyphotoapp.utils.WallyConstants.NOTIFICATION_ID;

public class ImageDownloadService extends IntentService {

    public static final String KEY_URL = "url";
    public static final String KEY_FILE_NAME = "filename";
    @Inject
    ImageFileRepository imageFileRepository;

    public ImageDownloadService() {
        super("service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        ((WallyApplication)getApplication()).getAppComponent().inject(this);
        createNotification();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        createNotification();
        startDownload(intent.getStringExtra(KEY_URL),intent.getStringExtra(KEY_FILE_NAME));
    }

    private void createNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(getString(R.string.download))
                .setContentText(getString(R.string.text_downloading_image))
                .setAutoCancel(true);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void startDownload(String url, String fileName) {
        Response<ResponseBody> response;
        try {
            response = imageFileRepository.downloadFile(url);
            ResponseBody fileData = response.body();
            if(response.isSuccessful() && fileData!=null){
                downloadImage(fileData,fileName);
            }else {
                onDownloadFail();
            }
        } catch (IOException e) {
            e.printStackTrace();
            onDownloadFail();
        }
    }

    private void downloadImage(ResponseBody body, String fileName) {
        int count;
        byte[] data = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                , fileName +".jpeg");
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outputFile);
            long total = 0;
            while ((count = inputStream.read(data)) != -1) {
                total += count;
                int progress = (int) ((double) (total * 100) / (double) fileSize);
                updateNotification(progress);
                sendUpdateProgress(progress);
                outputStream.write(data, 0, count);
            }
            onDownloadComplete();
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            onDownloadFail();
        }
    }

    private void updateNotification(int currentProgress) {
        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void sendUpdateProgress(int progress) {
        Intent intent = new Intent(ACTION_UPDATE);
        intent.putExtra(KEY_PROGRESS, progress);
        sendBroadcast(intent);
    }

    private void sendDownloadAction() {
        Intent intent = new Intent(ACTION_DOWNLOAD);
        sendBroadcast(intent);
    }

    private void onDownloadComplete() {
        sendDownloadAction();
        notificationManager.cancel(NOTIFICATION_ID);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(getString(R.string.notification_text_download_complete));
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void onDownloadFail(){
        sendBroadcast(new Intent(ACTION_FAIL));
        notificationManager.cancel(NOTIFICATION_ID);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(getString(R.string.notification_download_fail));
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }
}
