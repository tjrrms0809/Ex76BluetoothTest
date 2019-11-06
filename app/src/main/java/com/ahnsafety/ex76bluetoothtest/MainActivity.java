package com.ahnsafety.ex76bluetoothtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //마시멜로우부터 동적퍼미션 필요
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case  10:
                if(grantResults[0]==PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "클라이언트로서 새로운 장치를 탐색하는 기능을 제한 합니다.\n기존에 페어링된 장치가 있다면 접속이 가능함", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void clickServer(View view) {
        Intent intent = new Intent(this,ServerActivity.class);
        startActivity(intent);

    }

    public void clickClient(View view) {
        startActivity(new Intent(this,ClientActivity.class));
    }
}
