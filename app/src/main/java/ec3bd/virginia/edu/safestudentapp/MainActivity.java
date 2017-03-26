package ec3bd.virginia.edu.safestudentapp;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void QrScanner(View view){
        Intent intent = new Intent(this, ScannerActivity.class);
        EditText editText = (EditText) findViewById(R.id.name);
        String name = editText.getText().toString();
        intent.putExtra("name", "testName");
        startActivity(intent);
    }



}
