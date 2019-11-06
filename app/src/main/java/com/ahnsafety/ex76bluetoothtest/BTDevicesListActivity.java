package com.ahnsafety.ex76bluetoothtest;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BTDevicesListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> deviceList = new ArrayList<>();

    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> devices;

    DiscoveryResultReceiver discoveryResultReceiver;

    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btdevices_list);

        listView= findViewById(R.id.listview);
        adapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        //액티비티에 적용된 다이얼로그 테마시에
        //다이얼로그의 아웃사이드를 터치했을때
        //다이얼로그가 cancel되지 않도록
        setFinishOnTouchOutside(false);

        //블루투스 관리자 소환
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        //이미 페어링이 되어 있는 블루투스 장치의 리스트를 얻어오기
        devices= bluetoothAdapter.getBondedDevices();
        //Set<> 이기에 중복데이터가 저장되지 않음
        for(BluetoothDevice device : devices){
            String name= device.getName();
            String address= device.getAddress();
            deviceList.add(name+"\n"+address);
        }

        //탐색시작전에
        //블루투스장치 찾았다!! 라는 방송(Broadcast)을
        //수신하기 위해 Receiver를 생성 및 등록
        discoveryResultReceiver= new DiscoveryResultReceiver();

        intentFilter= new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryResultReceiver,intentFilter);

        intentFilter= new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryResultReceiver,intentFilter);

        //주변 블루투스장 탐색시작
        bluetoothAdapter.startDiscovery();

        //리스트뷰의 아이템을 클릭하는 것을 듣는 리스너
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String s= deviceList.get(position);
                String[] ss=s.split("\n");
                String address= ss[1];//BT mac주소만 얻어내기

                //ClientActivity에게 결과 address를
                //전달해 주기 위해...
                Intent intent= getIntent();
                intent.putExtra("Address",address);

                //이 결과가  Ok라고 설정
                setResult(RESULT_OK, intent);

                //블루투스 장치를 선택했으므로
                //리스트를 보여주는 이 액티비티는 종료
                finish();

            }
        });

    }//onCreate method..

    @Override
    protected void onPause() {
        unregisterReceiver(discoveryResultReceiver); //속도를 잡기위해서
        super.onPause();
    }

    ////////inner class ////////
    //블루투스 탐색 결과
    class  DiscoveryResultReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            String action= intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                //찾아온 장치에 대한 정보 얻어오기
                BluetoothDevice device= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device.getName()+"\n"+device.getAddress());
                adapter.notifyDataSetChanged();

            }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                Toast.makeText(context, "블루투스 장치 탐색을 완료했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ////////////////////////////
}
