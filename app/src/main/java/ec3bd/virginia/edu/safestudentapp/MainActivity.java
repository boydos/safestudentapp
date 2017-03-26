package ec3bd.virginia.edu.safestudentapp;

import android.content.Context;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.Result;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ZXingScannerView mScannerView;
    private String student_id;
    private String scanner_name;
    private double latitude = 0;
    private double longitude = 0;


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
        for(int i=0;i< Camera.getNumberOfCameras();i++){
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
        mScannerView.startCamera(camera);     // Start camera

        LayoutInflater factory = getLayoutInflater();
        View mainText = factory.inflate(R.layout.activity_main, null);
        EditText name = (EditText) mainText.findViewById(R.id.name);
        this.scanner_name = name.getText().toString();




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

        // Get the student id
        this.student_id = rawResult.getText();

        //Send data through to server
        this.sendData();
    }

    public void sendData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://safestudent.herokuapp.com/api/v1/event/create";
        final String name = this.scanner_name;
        final String lat = ""+this.latitude;
        final String lon = ""+this.longitude;
        final String student_id = this.student_id;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
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
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("student_id", student_id);
                params.put("scanner_name", name);
                params.put("latitude", lat);
                params.put("longitude", lon);

                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
