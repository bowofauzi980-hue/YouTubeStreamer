package com.example.youtubestreamer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class StreamService extends Service {

    private static final String CHANNEL_ID = "LiveStreamChannel";
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        // Menjaga proses HP tidak tidur agar live streaming tidak terputus
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YouTubeStreamer::WakeLock");
            wakeLock.acquire();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        // Notifikasi permanen di bar atas HP (syarat wajib Android untuk Foreground Service)
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("YouTube Live Streaming")
                .setContentText("Siaran 24/7 sedang aktif di latar belakang...")
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1, notification);

        // LOGIKA UTAMA (FFmpeg / RTMP streaming lokal) berjalan di sini

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Melepaskan pengunci CPU saat live dihentikan
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Live Streaming Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
