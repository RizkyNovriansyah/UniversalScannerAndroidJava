package com.barcodev.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private static final int PERMISSION_REQUEST_CODE = 200;
    TextView maindata;
    TextView data1, data2, data3, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView scannerText = findViewById(R.id.textScanner);
        data1 = findViewById(R.id.data1);
        data2 = findViewById(R.id.data2);
        data3 = findViewById(R.id.data3);
        maindata = findViewById(R.id.maindata);
        info = findViewById(R.id.info);
        maindata.setText("");
        data1.setText("");
        data2.setText("");
        data3.setText("");
        info.setText("");

        if (checkPermission()) {
            CodeScannerView scannerView = findViewById(R.id.scanner_view);
            mCodeScanner = new CodeScanner(this, scannerView);
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            maindata.setText("Loading -- (data qr:"+result.getText()+") -- Wait");
                            data1.setText("");
                            data2.setText("");
                            data3.setText("");
                            info.setText("");
                            Toast.makeText(getApplicationContext(), "proses...", Toast.LENGTH_SHORT).show();
                            reqData(result.getText()+"");
                        }
                    });
                }
            });
            scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                    maindata.setText("");
                    data1 .setText("");
                }
            });
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private String reqData(final String qr_data){
        String data = "";
        String url = BaseActivity.scanner;
        System.out.println("Sended : "+qr_data);
        System.out.println("Request : "+url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        info.setVisibility(View.VISIBLE);
                        System.out.println("Request Data : "+response);

                        try {
                            JSONObject object = new JSONObject (response);
                            maindata.setText("(data qr:"+qr_data+")");

                            /*
                            info.setText("");
                            data1.setText("");
                            data2.setText("");
                            data3.setText("");
                            */

                            /* JSONObject data = object.getJSONObject("data");
                            if (data != null){
                                String nama = data.getString("nama");
                                String no_telp = data.getString("no_telp");
                                String seat_number = data.getString("seat_number");
                                maindata.setText(nama+" (username:"+qr_data+") / "+no_telp );
                                info.setText("SEAT : "+seat_number+" -- "+message);
                            }*/

                            info.setText("-- Berhasil");
                        } catch (JSONException e) {
                            System.out.println("Request Data JSONException : "+e);
                            e.printStackTrace();
                            info.setText("-- Terjadi Kesalahan");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Error : "+volleyError);
                        Toast.makeText(getApplicationContext(), "Tidak Terhubung", Toast.LENGTH_SHORT).show();
                        maindata.setText("(data qr:"+qr_data+")");
                        info.setText("Tidak Terhubung dengan server");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("username", qr_data);
                return params;
            }
        };
        BaseActivity.getQueueVolley(getApplicationContext()).add(stringRequest);

        return data;
    }
}