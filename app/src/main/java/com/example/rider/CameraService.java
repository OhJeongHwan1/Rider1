package com.example.rider;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class CameraService extends Service {
    private static final int ONGOING_NOTIFICATION_ID = 1;
    ///////////////////////////////
    PreviewView previewView;
    private ImageAnalysis imageAnalysis;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    ProcessCameraProvider processCameraProvider;
    ////////////////////////////////
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, DriveActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle("Camera Service")
                .setContentText("Running in the background")
                .setSmallIcon(R.drawable.warning)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);

        // 카메라 작업 수행
        // 카메라 구동을 위해 startCameraX() 등의 함수를 여기에 적용해야 합니다.
        ProcessCameraProvider cameraProvider = null;
        try {
            cameraProvider = cameraProviderFuture.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startCameraX(cameraProvider);

        return START_STICKY;
    }
    private void startCameraX(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        cameraProvider.unbindAll();

        //프리뷰 매핑
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT) // BACK: 후면 FRONT: 전면
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        // 카메라 시작
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);

    }
}