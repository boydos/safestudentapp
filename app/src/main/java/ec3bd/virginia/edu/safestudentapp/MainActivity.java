package ec3bd.virginia.edu.safestudentapp;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private int orientation;
    Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();
        int orientation = display.getOrientation();
    }

    public void QrScanner(View view){

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.

        int backCameraId = -1, frontCameraId = -1;
        for(int i=0;i<Camera.getNumberOfCameras();i++){
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i,cameraInfo);
            if(cameraInfo.facing== Camera.CameraInfo.CAMERA_FACING_BACK) {
                backCameraId = i;
            }
            if(cameraInfo.facing== Camera.CameraInfo.CAMERA_FACING_FRONT){
                frontCameraId = i;
            }
        }
        int camera = backCameraId;
        if(backCameraId == -1)
            camera = frontCameraId;
        mScannerView.startCamera(camera);         // Start camera

    }

    @Override
    public void onPause() {
        super.onPause();
        int orientation_ = display.getOrientation();
        if(mScannerView != null && orientation == orientation_) { //if you're in the scannerview and the pause isn't due to a orientation change, quit
            mScannerView.stopCamera();           // Stop camera on pause
            setContentView(R.layout.activity_main);
        }
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//        mScannerView.startCamera();          // Start camera on resume
//    }

    @Override
    public void handleResult(Result rawResult) {
        // request to server with info from scan + gps + settings/name of device


        Log.e("handler", rawResult.getText());
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
        ImageView image = new ImageView(this);
        //Test for success
        image.setImageResource(R.drawable.check);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(image);
        //builder.setMessage(rawResult.getText());
        final AlertDialog alert1 = builder.create();
        alert1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert1.show();

        // If you would like to resume scanning, call this method below:
        final MainActivity thisproxy = this;
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(thisproxy);
            }
        },250L);
        Handler alertHandler = new Handler();
        alertHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert1.dismiss();
            }
        },1000L);

    }

}
