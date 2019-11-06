package com.ahnsafety.ex76bluetoothtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ClientActivity extends AppCompatActivity {

    static final UUID BT_UUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public  static final int REQ_ENABLE=10;
    public  static final int REQ_DISCOVERY=20;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;

    DataInputStream dis;
    DataOutputStream dos;

    ClientThread clientThread;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        getSupportActionBar().setTitle("CLIENT");

        tv= findViewById(R.id.tv);

        //블루투스 관리자 객체 소환
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter==null){
            Toast.makeText(this, "이 기기에는 블루투스가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(bluetoothAdapter.isEnabled()){
            //서버 블루투스 장치 탐색 및 리스트 보기를 해주는 액티비티 실행하기
            discoveryBluetoothDevices();
        }else{
            Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQ_ENABLE);
        }

    }//onCreate method..

    //주변의 블루투스장치들을 탐색하여
    //리스트로 보여주는 액티비티를 실행하는 메소드
    void discoveryBluetoothDevices(){
        Intent intent= new Intent(this, BTDevicesListActivity.class);
        startActivityForResult(intent, REQ_DISCOVERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_ENABLE:
                if(resultCode == RESULT_CANCELED){
                    Toast.makeText(this, "사용불가", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    //서버 블루투스 장치 탐색 및 리스트 보기
                    discoveryBluetoothDevices();
                }
                break;
            case REQ_DISCOVERY:
                //결과가 OK면.
                if(requestCode==RESULT_OK){
                    //연결할 블루투스장치의 mac주소를 얻어 왔다.
                    String deviceAddress= data.getStringExtra("Adress");

                    //이 주소를 통해 Socket 연결작업 실행
                    //하는 별도의 Thread객체 생성 및 실행
                    clientThread= new ClientThread(deviceAddress);
                    clientThread.start();

                }

                break;
        }
    }

    ////////////inner class//////////////
    class ClientThread extends Thread{

        String address;

        public ClientThread(String address) {
            this.address = address;
        }

        @Override
        public void run() {
            //소켄생성하기 위해 Bluetooth 장치 객체 얻어오기(서버와 연결)
            BluetoothDevice device =bluetoothAdapter.getRemoteDevice(address);


            try {
                //디바이스를 통해 소켓연결
                socket= device.createInsecureRfcommSocketToServiceRecord(BT_UUID);
                socket.connect();//연결시도

                //연결이 성공되었다고 메세지
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText("서버에 접속되었습니다.");
                    }
                });

                dis= new DataInputStream(socket.getInputStream());
                dos= new DataOutputStream(socket.getOutputStream());

                //원하는 통신작업 수행...


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }//ClientThread..

}//ClientActivity class..
