package com.example.rider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.impl.ImageAnalysisConfig;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
//import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class DriveActivity extends AppCompatActivity {
    private Socket mSocket;
    private static final String TAG = "DriveActivity";
    //private static final String SERVER_URL = "http://[flask-server-ip]:[port]";
    private static final String SERVER_URL = "http://172.20.10.2:5000";
    // private static final String SERVER_URL = "http://192.168.0.12:5000";

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    private ImageAnalysis imageAnalysis;
    ProcessCameraProvider processCameraProvider;
    int lensFacing = CameraSelector.LENS_FACING_FRONT;
    private Executor executor = Executors.newSingleThreadExecutor();


    // fragment 변수
    private face fragmentFace;
    private ready fragmentReady;
    private driving fragmentDriving;
    private warning fragmentWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);


        // 상태바를 안보이도록 합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 화면 켜진 상태를 유지합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        previewView = findViewById(R.id.PreviewView);
        // 카메라 프로바이더 획득
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        // 카메라 시작
        cameraProviderFuture.addListener(() -> {
            try {
                mSocket = IO.socket(SERVER_URL);
                mSocket.connect();
                System.out.println("connect flask");
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
                Log.i(TAG, "HI");
                mSocket.emit("hello", "world");

                mSocket.on("hi", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String msg = (String)args[0];
                        System.out.println(args[0]);
                        if(msg == "this is flask"){ Log.i(TAG, "cONNECTED TO SERVER" + "HI FLASK");
                        }
                        else {Log.i(TAG, "cONNECTED TO SERVER" + "not FLASK");}
                    }
                });

                // processCameraProvider = ProcessCameraProvider.getInstance(this).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to connect to server: " + SERVER_URL);
                return;
            }

//            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Log.d(TAG, "Connected to server: " + SERVER_URL);
//                }
//            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Log.d(TAG, "Disconnected from server");
//                }
//            });


        }, getExecutor());


        fragmentFace = new face();
        fragmentReady = new ready();
        fragmentDriving = new driving();
        fragmentWarning = new warning();

        //프래그먼트 매니저 획득
        FragmentManager fragmentManager = getSupportFragmentManager();
        //프래그먼트 Transaction 획득
        //프래그먼트를 올리거나 교체하는 작업을 Transaction이라고 합니다.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //프래그먼트를 FrameLayout의 자식으로 등록해줍니다.
        fragmentTransaction.add(R.id.fragmentFrame,fragmentFace);
        //commit을 하면 자식으로 등록된 프래그먼트가 화면에 보여집니다.
        fragmentTransaction.commit();

    }
    public void onClick_face(View view){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, fragmentReady);
        fragmentTransaction.commit();
    }
    public void onClick_ready(View view){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, fragmentDriving);
        fragmentTransaction.commit();
        sendData("start");
    }
    public void onClick_drive(View view){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, fragmentWarning);
        fragmentTransaction.commit();
        sendData("warning");
    }
    public void onClick_nodrive(View view){
        onBackPressed();
        sendData("return");
    }
    public void onClick_warning(View view){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, fragmentDriving);
        fragmentTransaction.commit();
        sendData("safe");
        // 돌아올 때 타이밍이 엇나가 액티비티가 다시 생성되었을 때 돌아오기 위함.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendData("safe");
            }
        }, 1300);
    }
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        cameraProvider.unbindAll();

        //프리뷰 매핑
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT) // BACK: 후면 FRONT: 전면
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // 이미지 분석 매핑
        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @ExperimentalGetImage
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                try {
//                    Image image = imageProxy.getImage();
//                     //Image.Plane[] planes = imageProxy.getImage().getPlanes();
//                    // 프레임을 JPEG 형식으로 인코딩하여 ByteArrayOutputStream에 저장
//                     ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//                     //ByteBuffer buffer = planes[0].getBuffer();
//                     byte[] bytes = new byte[buffer.capacity()];
//                     buffer.get(bytes);


                    ByteBuffer yBuffer = imageProxy.getPlanes()[0].getBuffer(); // Y
                    ByteBuffer vuBuffer = imageProxy.getPlanes()[2].getBuffer(); // VU

                    int ySize = yBuffer.remaining();
                    int vuSize = vuBuffer.remaining();

                    byte[] nv21 = new byte[ySize + vuSize];

                    yBuffer.get(nv21, 0, ySize);
                    vuBuffer.get(nv21, ySize, vuSize);

                    YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, imageProxy.getWidth(), imageProxy.getHeight(), null);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 50, out);
                    byte[] imageBytes = out.toByteArray();


                    // 바이트 배열을 JPEG 형식으로 인코딩
                    // byte 배열로부터 bitmap 객체를 생성한 후, compress() 메소드를 사용하여 jpeg형식으로 압축
                    Bitmap bitmapImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    if (bitmapImage == null) {
                        Log.e(TAG, "Failed to decode byte array into Bitmap");
                        return;
                    }

                    // Matrix 객체를 사용하여 이미지 회전 적용
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), matrix, true);

                    // 회전된 이미지를 JPEG로 압축
                    ByteArrayOutputStream rotatedOut = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, rotatedOut);

//                     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                     bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

                    // Base64 인코딩
                    String base64Data = Base64.encodeToString(rotatedOut.toByteArray(), Base64.DEFAULT);

                    // 바이트 배열을 Base64 문자열로 인코딩
                    // String base64Data = Base64.encodeToString(bytes, Base64.DEFAULT);
                    // 인코딩된 데이터의 크기가 커지는 문제가 있음.

                    // JSON 데이터 생성
                    JSONObject json = new JSONObject();
                    json.put("imageData", base64Data);
                    // key: imageData  value: base64Data 문자열

                    // 서버로 데이터 전송
                    mSocket.emit("preview", json.toString());


                    //mSocket.emit("preview", json);


                    imageProxy.close();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        // DisplayManager를 사용하여 디바이스 방향 감지
        // DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);

        // 카메라 시작
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

    }

    // 메세지 전송 메소드
    public void sendData(String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = "/data";
                String message = data;
                byte[] messageData = message.getBytes(StandardCharsets.UTF_8);

                Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
                nodeListTask.addOnSuccessListener(new OnSuccessListener<List<Node>>() {
                    @Override
                    public void onSuccess(List<Node> nodes) {
                        for (Node node : nodes) {
                            Task<Integer> sendMessageTask = Wearable.getMessageClient(DriveActivity.this)
                                    .sendMessage(node.getId(), path, messageData);

                            sendMessageTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer integer) {
                                    Log.d("from android", "성공적 전송: " + message);
                                }
                            });

                            sendMessageTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.e("from android", "Failed to send data: " + exception);
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }
    private void enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            PictureInPictureParams params = new PictureInPictureParams.Builder()
                    .setAspectRatio(new Rational(10,22))
                    .build();
            enterPictureInPictureMode(params);
        } else {
            Toast.makeText(this, "소형 플레이어로 실행", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClick_enterPipMode(View view) {
        enterPipMode();
    }
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        }

        View pipButton = findViewById(R.id.pipButton);
        View frag = findViewById(R.id.fragmentFrame);
        if (isInPictureInPictureMode) {
            // PiP 모드로 전환될 때
            pipButton.setVisibility(View.GONE);
            frag.setVisibility(View.GONE);
        } else {
            // 정상 모드로 돌아올 때
            pipButton.setVisibility(View.VISIBLE);
            frag.setVisibility(View.VISIBLE);
        }
    }
}