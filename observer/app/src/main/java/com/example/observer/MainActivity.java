package com.example.observer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.observer.model.Image;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private final String SERVER_PROTOCOL = "SERVER_PROTOCOL";       // SERVER_PROTOCOL
    private final String SERVER_IP =  "SERVER_IP";                  //  SERVER_IP
    private final int SERVER_PORT = 1111;                           // SERVER_PORT
    private static NetworkService networkService;

    private final double TARGET_FRAME = 1;

    Camera camera = null;
    SurfaceHolder  holder = null;
    VideoView videoView = null;
    TextView textView = null;

    int width = 640; // 640;
    int height = 360; //360;

    // connect Test
    private static TextView receiveText;

    long currentTime = System.currentTimeMillis();
    long lastThreadRunTime = System.currentTimeMillis();

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        currentTime = System.currentTimeMillis();

        if( currentTime - lastThreadRunTime >= 1000 / TARGET_FRAME ){
            lastThreadRunTime = System.currentTimeMillis();

            new Thread() {
                @Override
                public void run() {
                    sendImage( data, width, height, new Date() );
                }
            }.start();
        }

        textView.setHighlightColor(Color.BLACK);
        textView.setTextColor(Color.WHITE);
        textView.setShadowLayer(2.0f, 1.0f, 1.0f, Color.BLACK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService(SERVER_PROTOCOL, SERVER_IP, SERVER_PORT);
        networkService = ApplicationController.getInstance().getNetworkService();

        videoView = (VideoView)findViewById(R.id.videoView);
        textView = (TextView)findViewById(R.id.textView);
        receiveText = (TextView)findViewById(R.id.receiveText);

        // camera 기본 설정들
        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        params.setPreviewFpsRange(10000, 30000);  // frame 설정 ( 1000 배율 min, max)
        params.setPreviewSize(width, height); // width, height pixer 1280*720
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        params.setPreviewFormat(ImageFormat.NV21);
        //params.setPreviewFormat(ImageFormat.FLEX_RGB_888);

        //params.setPreviewFrameRate(1);   // frame 설정
        //params.setPreviewFpsRange(1,1);

        camera.setDisplayOrientation(90); // 기본 가로모드
        camera.setParameters(params);

        holder = videoView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.unlock();
            camera.reconnect();
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.setPreviewCallback(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    public static void sendImage( byte[] previewImage, int width, int height, Date imageDate ){
        long sendStartTime = System.currentTimeMillis();

        //Restaurant POST
        //int[] rgbImage = convertYUV420_NV21toRGB8888( previewImage, width, height ); // GBR value -> 3초 정도 딜레이
        //int[] rgbImage = decodeYUV420SP( previewImage, width, height ); // 3초 정도 딜레이
        int[] rgbImage = {1,2,3,4,5,6,7,8,9,10}; // 0.05초 정도
        Image image = new Image(rgbImage, width, height, imageDate);

        Call<Image> postCall = networkService.post_image(image);
        postCall.enqueue(new Callback<Image>() {
            @Override
            public void onResponse(Call<Image> call, Response<Image> response) {
                if( response.isSuccessful()) {
                    receiveText.setText( "전송 시간 : " + (System.currentTimeMillis() - sendStartTime) + " milli seconds");
                } else {
                    int StatusCode = response.code();
                    receiveText.setText( "Fail!!! " + "Status Code :" + StatusCode );
                    Log.e(ApplicationController.TAG, "Status Code : " + StatusCode);
                }
            }
            @Override
            public void onFailure(Call<Image> call, Throwable t) {
                Log.e(ApplicationController.TAG, "Fail Message : " + t.getMessage());
            }
        });
    }


    /**
     * Converts YUV420 NV21 to RGB8888
     *
     * @param data byte array on YUV420 NV21 format.
     * @param width pixels width
     * @param height pixels height
     * @return a RGB8888 pixels int array. Where each int is a pixels ARGB.
     */
    public static int[] convertYUV420_NV21toRGB8888(byte [] data, int width, int height) {
        int size = width*height;
        int offset = size;
        int[] pixels = new int[size];
        int u, v, y1, y2, y3, y4;

        // i percorre os Y and the final pixels
        // k percorre os pixles U e V
        for(int i=0, k=0; i < size; i+=2, k+=2) {
            y1 = data[i  ]&0xff;
            y2 = data[i+1]&0xff;
            y3 = data[width+i  ]&0xff;
            y4 = data[width+i+1]&0xff;

            u = data[offset+k  ]&0xff;
            v = data[offset+k+1]&0xff;
            u = u-128;
            v = v-128;

            pixels[i  ] = convertYUVtoRGB(y1, u, v);
            pixels[i+1] = convertYUVtoRGB(y2, u, v);
            pixels[width+i  ] = convertYUVtoRGB(y3, u, v);
            pixels[width+i+1] = convertYUVtoRGB(y4, u, v);

            if (i!=0 && (i+2)%width==0)
                i+=width;
        }

        return pixels;
    }

    private static int convertYUVtoRGB(int y, int u, int v) {
        int r,g,b;

        r = y + (int)(1.402f*v);
        g = y - (int)(0.344f*u +0.714f*v);
        b = y + (int)(1.772f*u);
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;
        //return 0xff000000 | (b<<16) | (g<<8) | r;

        return 0x00000000 | (b<<16) | (g<<8) | r;
    }



    // 출처: https://d-sik.tistory.com/entry/OpenCV-카메라-이미지bitmap-NDK로-넘기고-처리한-이미지-받아오기 [SIK]
    static public int[] decodeYUV420SP(byte[] yuv420sp,int width,int height) {
        final int frameSize = width * height;
        int[] rgb = new int[frameSize];

        for (int j =0, yp =0; j < height; j++) {
            int uvp = frameSize + (j >>1) * width, u =0, v =0;
            for (int i =0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) -16;
                if (y <0) y =0;
                if ((i &1) ==0) {
                    v = (0xff & yuv420sp[uvp++]) -128;
                    u = (0xff & yuv420sp[uvp++]) -128;
                }

                int y1192 =1192 * y;
                int r = (y1192 +1634 * v);
                int g = (y1192 -833 * v -400 * u);
                int b = (y1192 +2066 * u);

                if (r <0) r =0;else if (r >262143) r =262143;
                if (g <0) g =0;else if (g >262143) g =262143;
                if (b <0) b =0;else if (b >262143) b =262143;

                rgb[yp] =0xff000000 | ((r <<6) &0xff0000) | ((g >>2) &0xff00) | ((b >>10) &0xff);
            }
        }

        return rgb;
    }
}