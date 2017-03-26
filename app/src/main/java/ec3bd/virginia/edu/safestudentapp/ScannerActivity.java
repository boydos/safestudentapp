package ec3bd.virginia.edu.safestudentapp;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String student_id;
    private String scanner_name;
    private double latitude = 0;
    private double longitude = 0;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        Bundle bundle = getIntent().getExtras();
        scanner_name = bundle.getString("name");

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.

        int backCameraId = -1, frontCameraId = -1;
        for(int i = 0; i< Camera.getNumberOfCameras(); i++){
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
        mScannerView.startCamera(camera);
    }

    @Override
    public void onPause(){
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        GPSTracker gps = new GPSTracker(this);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        Log.e("handler", rawResult.getText());
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
        image = new ImageView(this);
        //Test for success
        this.student_id = rawResult.getText();
        this.sendData();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(image);
        //builder.setMessage(rawResult.getText());
        final AlertDialog alert1 = builder.create();
        alert1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert1.show();

        // If you would like to resume scanning, call this method below:
        final ScannerActivity thisproxy = this;
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(thisproxy);
                alert1.dismiss();
            }
        },750L);

        // Get the student id


        //Send data through to server

    }

    public void sendData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://safestudent.herokuapp.com/api/v1/event/create";
        final String name = this.scanner_name;
        final String lat = ""+this.latitude;
        final String lon = ""+this.longitude;
        final String student_id = this.student_id;
        Map<String, String>  params = new HashMap<String, String>();
        params.put("uuid", student_id);
        params.put("scanner_name", name);
        params.put("latitude", lat);
        params.put("longitude", lon);
        JSONObject json = new JSONObject(params);
        Log.d("json", json.toString());
        CustomRequest postRequest = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("resp", response.toString());
                        try {
                            String status = response.getString("status");
                            Log.d("status",status);
                            if (status.equals("success"))
                                image.setImageResource(R.drawable.check);
                            else
                                image.setImageResource(R.drawable.x_icon);
                        }catch (JSONException e){}
                        //Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {};
        queue.add(postRequest);
    }
}
