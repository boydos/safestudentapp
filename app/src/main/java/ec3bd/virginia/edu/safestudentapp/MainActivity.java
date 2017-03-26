package ec3bd.virginia.edu.safestudentapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

//import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GPSTracker gps = new GPSTracker(this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        LatLng myLocation = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(myLocation)
                .title("My current location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

    public void QrScanner(View view){
        Intent intent = new Intent(this, ScannerActivity.class);
        EditText editText = (EditText) findViewById(R.id.name);
        String name = editText.getText().toString();
        intent.putExtra("name", name);
        startActivity(intent);
    }



}
