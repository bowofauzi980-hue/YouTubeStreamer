package com.example.youtubestreamer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etRtmpUrl, etStreamKey;
    private Button btnMulaiLive;
    private boolean isStreaming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etRtmpUrl = findViewById(R.id.etRtmpUrl);
        etStreamKey = findViewById(R.id.etStreamKey);
        btnMulaiLive = findViewById(R.id.btnMulaiLive);

        btnMulaiLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStreaming) {
                    mulaiStreaming();
                } else {
                    hentikanStreaming();
                }
            }
        });
    }

    private void mulaiStreaming() {
        String rtmp = etRtmpUrl.getText().toString().trim();
        String key = etStreamKey.getText().toString().trim();

        if (key.isEmpty()) {
            Toast.makeText(this, "Masukkan Stream Key YouTube Anda dulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Menjalankan Foreground Service agar live tetap berjalan di latar belakang
        Intent serviceIntent = new Intent(this, StreamService.class);
        serviceIntent.putExtra("RTMP_URL", rtmp);
        serviceIntent.putExtra("STREAM_KEY", key);

        ContextCompat.startForegroundService(this, serviceIntent);

        isStreaming = true;
        btnMulaiLive.setText("HENTIKAN LIVE");
        btnMulaiLive.setBackgroundColor(0xFF888888); // Berubah jadi abu-abu saat live
        Toast.makeText(this, "Live Streaming Dimulai!", Toast.LENGTH_SHORT).show();
    }

    private void hentikanStreaming() {
        Intent serviceIntent = new Intent(this, StreamService.class);
        stopService(serviceIntent);

        isStreaming = false;
        btnMulaiLive.setText("MULAI LIVE");
        btnMulaiLive.setBackgroundColor(0xFFE50914); // Kembali jadi merah
        Toast.makeText(this, "Live Streaming Dihentikan.", Toast.LENGTH_SHORT).show();
    }
}
