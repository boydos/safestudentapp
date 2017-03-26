package ec3bd.virginia.edu.safestudentapp;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.zxing.Result;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends ActionBarActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String student_id;
    private String scanner_name;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, false);

        Location location = locationManager.getLastKnownLocation(provider);

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public void QrScanner(View view){

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

        EditText name = (EditText) findViewById(R.id.name);
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

        this.student_id = rawResult.getText();
    }

    public void postData() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("localhost:8000/api/v1/event/create");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("student_id", this.student_id));
            nameValuePairs.add(new BasicNameValuePair("scanner_name", this.scanner_name));
            nameValuePairs.add(new BasicNameValuePair("latitude", ""+this.latitude));
            nameValuePairs.add(new BasicNameValuePair("longitude", ""+this.longitude));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse respopnse = httpClient.execute(httpPost);
        } catch (ClientProtocolException e){
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

}
