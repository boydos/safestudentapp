package ec3bd.virginia.edu.safestudentapp;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        mScannerView.stopCamera();           // Stop camera on pause
        setContentView(R.layout.activity_main);
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
        CheckBox checkin = (CheckBox)findViewById(R.id.checkBox3);
        CheckBox checkout = (CheckBox)findViewById(R.id.checkBox);
        


        Log.e("handler", rawResult.getText());
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

}
