package com.AmoSmartRF.bluetooth.le;

// 阿莫单片机淘宝店店主  完成编码
// http://amomcu.taobao.com/

import org.apache.http.HttpException;



import com.lin.bluetooth.le.R;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.AmoSmartRF.bluetooth.le.HexToDecimal;

public class StartActivity extends Activity implements OnClickListener {

	
	
	private final static String TAG = "StartActivity"; // StartActivity.class.getSimpleName();
		
	private final String ACTION_NAME_RSSI = "AMOMCU_RSSI";  	// 其他文件广播的定义必须一致
	private final String ACTION_CONNECT = "AMOMCU_CONNECT"; 	// 其他文件广播的定义必须一致
		
	public static final int REFRESH = 0x000001;  
	
	static Button btn=null;

	
    // 根据rssi 值计算距离， 只是参考作用， 不准确---amomcu
	static final int rssibufferSize = 10;
	int[] rssibuffer = new int[rssibufferSize];
	int rssibufferIndex = 0;
	boolean rssiUsedFalg = false;	
	
	static byte keyValue_save = 0;
	
	static Handler mHandler = new Handler();
	Handler mHandler_Msg = new Handler();
	static Handler mHandler_relay = new Handler();

	// adc 采样数据， 分别为AIN4与AIN5的通道数据，也就是p0.4与p0.5的管脚输入的adc数据
	static byte[] adc1_value = new byte[2];
	static byte[] adc2_value = new byte[2];
	static byte[] adc3_value = new byte[2];
	static byte[] adc4_value = new byte[2];
	static byte[] adc5_value = new byte[2];	
	static byte[] adc6_value = new byte[2];
	static byte[] adc7_value = new byte[2];
	static byte[] adc8_value = new byte[2];
	static byte[] adc9_value = new byte[2];
	static byte[] adc10_value = new byte[2];
	static byte[] adc11_value = new byte[2];
	static byte[] adc12_value = new byte[2];
	static byte[] adc13_value = new byte[2];
	
	static double[] voldata=new double[13];
	static String[] voldata2=new String[13];
	
	static TextView start_txt_ADC4ADC5 = null;
	static TextView ADC01=null;
	static TextView ADC02=null;
	static TextView ADC03=null;
	static TextView ADC04=null;
	static TextView ADC05=null;
	static TextView ADC06=null;
	static TextView ADC07=null;
	static TextView ADC08=null;
	static TextView ADC09=null;
	static TextView ADC10=null;
	static TextView ADC11=null;
	static TextView ADC12=null;
	static TextView ADC13=null;
	static TextView ADCALL=null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		getActionBar().setTitle("AmoSmartRF蓝牙APP v1.1 20150324");

		this.btn=(Button)super.findViewById(R.id.btn);
		btn.setOnClickListener(new BtnListener());
		
		ADC01=(TextView) findViewById(R.id.adc01);
		ADC02=(TextView) findViewById(R.id.adc02);
		ADC03=(TextView) findViewById(R.id.adc03);
		ADC04=(TextView) findViewById(R.id.adc04);
		ADC05=(TextView) findViewById(R.id.adc05);
		ADC06=(TextView) findViewById(R.id.adc06);
		ADC07=(TextView) findViewById(R.id.adc07);
		ADC08=(TextView) findViewById(R.id.adc08);
		ADC09=(TextView) findViewById(R.id.adc09);
		ADC10=(TextView) findViewById(R.id.adc10);
		ADC11=(TextView) findViewById(R.id.adc11);
		ADC12=(TextView) findViewById(R.id.adc12);
		ADC13=(TextView) findViewById(R.id.adc13);
		ADCALL=(TextView) findViewById(R.id.adcAll);

		adc1_value[0] = 0;
		adc1_value[1] = 0;
		
		adc2_value[0] = 0;
		adc2_value[1] = 0;
		
		adc3_value[0] = 0;
		adc3_value[1] = 0;

		
		adc4_value[0] = 0;
		adc4_value[1] = 0;

		adc5_value[0] = 0;
		adc5_value[1] = 0;
		
		adc6_value[0] = 0;
		adc6_value[1] = 0;
		
		adc7_value[0] = 0;
		adc7_value[1] = 0;
		
		adc8_value[0] = 0;
		adc8_value[1] = 0;

		adc9_value[0] = 0;
		adc9_value[1] = 0;
		
		adc10_value[0] = 0;
		adc10_value[1] = 0;
		
		adc11_value[0] = 0;
		adc11_value[1] = 0;
		
		adc12_value[0] = 0;
		adc12_value[1] = 0;
		
		adc13_value[0] = 0;
		adc13_value[1] = 0;
		
		registerBoradcastReceiver();				
		
		new MyThread().start();		
	}
	
	
	
	// 接收 rssi 的广播
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
				
			if (action.equals(ACTION_NAME_RSSI)) {
				int rssi = intent.getIntExtra("RSSI", 0);

				// 以下这些参数我 amomcu 自己设置的， 不太具有参考意义，
				//实际上我的本意就是根据rssi的信号前度计算以下距离， 
				//以便达到定位目的， 但这个方法并不准  ---amomcu---------20150411
				
				int rssi_avg = 0;
				int distance_cm_min = 10; // 距离cm -30dbm
				int distance_cm_max_near = 1500; // 距离cm -90dbm
				int distance_cm_max_middle = 5000; // 距离cm -90dbm
				int distance_cm_max_far = 10000; // 距离cm -90dbm
				int near = -72;
				int middle = -80;
				int far = -88;
				double distance = 0.0f;

				if (true) {
					rssibuffer[rssibufferIndex] = rssi;
					rssibufferIndex++;

					if (rssibufferIndex == rssibufferSize)
						rssiUsedFalg = true;

					rssibufferIndex = rssibufferIndex % rssibufferSize;

					if (rssiUsedFalg == true) {
						int rssi_sum = 0;
						for (int i = 0; i < rssibufferSize; i++) {
							rssi_sum += rssibuffer[i];
						}

						rssi_avg = rssi_sum / rssibufferSize;

						if (-rssi_avg < 35)
							rssi_avg = -35;

						if (-rssi_avg < -near) {
							distance = distance_cm_min
									+ ((-rssi_avg - 35) / (double) (-near - 35))
									* distance_cm_max_near;
						} else if (-rssi_avg < -middle) {
							distance = distance_cm_min
									+ ((-rssi_avg - 35) / (double) (-middle - 35))
									* distance_cm_max_middle;
						} else {
							distance = distance_cm_min
									+ ((-rssi_avg - 35) / (double) (-far - 35))
									* distance_cm_max_far;
						}
					}
				}
				//接受的信号强度指数RSSI
				getActionBar().setTitle(
						"RSSI: " + rssi_avg + " dbm" + ", " + "距离: "
								+ (int) distance + " cm");
			
				
			}
			else if(action.equals(ACTION_CONNECT)){
				int status = intent.getIntExtra("CONNECT_STATUC", 0);
				if(status == 0)	{				
					getActionBar().setTitle("已断开连接");
					//finish();
				}
				else{
					getActionBar().setTitle("已连接设备");
				}
			}
		}
	};	
	
	public void registerBoradcastReceiver(){
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME_RSSI);
		//注册广播      
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}	
	
	// 按键事件
	public void onClick(View v) {
		switch (v.getId()) {
		
		}
	}

	public static synchronized void onCharacteristicRead(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		// Log.i(TAG, "onCharacteristicRead str = " + str);

		if (DeviceScanActivity.gattCharacteristic_char1
				.equals(characteristic)) {//char1
			byte[] adc01_value = new byte[2];
			adc01_value = characteristic.getValue();
			adc1_value[0] = adc01_value[0];
			adc1_value[1] = adc01_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_char2
				.equals(characteristic)) {//char1
			byte[] adc02_value = new byte[2];
			adc02_value = characteristic.getValue();
			adc2_value[0] = adc02_value[0];
			adc2_value[1] = adc02_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_char3
				.equals(characteristic)) {//char1
			byte[] adc03_value = new byte[2];
			adc03_value = characteristic.getValue();
			adc3_value[0] = adc03_value[0];
			adc3_value[1] = adc03_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_char4
				.equals(characteristic)) {//char1
			byte[] adc04_value = new byte[2];
			adc04_value = characteristic.getValue();
			adc4_value[0] = adc04_value[0];
			adc4_value[1] = adc04_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_char5
				.equals(characteristic)) {//char1
			byte[] adc05_value = new byte[2];
			adc05_value = characteristic.getValue();
			adc5_value[0] = adc05_value[0];
			adc5_value[1] = adc05_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_char6
				.equals(characteristic)) {//char1
			byte[] adc06_value = new byte[2];
			adc06_value = characteristic.getValue();
			adc6_value[0] = adc06_value[0];
			adc6_value[1] = adc06_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_char7
				.equals(characteristic)) {//char1
			byte[] adc07_value = new byte[2];
			adc07_value = characteristic.getValue();
			adc7_value[0] = adc07_value[0];
			adc7_value[1] = adc07_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_char8
				.equals(characteristic)) {//char1
			byte[] adc08_value = new byte[2];
			adc08_value = characteristic.getValue();
			adc8_value[0] = adc08_value[0];
			adc8_value[1] = adc08_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_char9
				.equals(characteristic)) {//char1
			byte[] adc09_value = new byte[2];
			adc09_value = characteristic.getValue();
			adc9_value[0] = adc09_value[0];
			adc9_value[1] = adc09_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_charA
				.equals(characteristic)) {//char1
			byte[] adc010_value = new byte[2];
			adc010_value = characteristic.getValue();
			adc10_value[0] = adc010_value[0];
			adc10_value[1] = adc010_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_charB
				.equals(characteristic)) {//char1
			byte[] adc011_value = new byte[2];
			adc011_value = characteristic.getValue();
			adc11_value[0] = adc011_value[0];
			adc11_value[1] = adc011_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_charC
				.equals(characteristic)) {//char1
			byte[] adc012_value = new byte[2];
			adc012_value = characteristic.getValue();
			adc12_value[0] = adc012_value[0];
			adc12_value[1] = adc012_value[1];	
		}
		else if (DeviceScanActivity.gattCharacteristic_charD
				.equals(characteristic)) {//char1
			byte[] adc013_value = new byte[2];
			adc013_value = characteristic.getValue();
			adc13_value[0] = adc013_value[0];
			adc13_value[1] = adc013_value[1];	
		}
		 else {
			return;
		}

		mHandler.post(new Runnable() {
			@Override
			public synchronized void run() {
				
				
				//显示adc01第一节电压的值
				String current_adc1 = Utils.bytesToHexString(adc1_value);
				double adc1=Utils.test16To10(current_adc1);						
				String adc1b=Utils.achange(adc1);
				voldata[0]=Double.parseDouble(adc1b);
				voldata2[0]=adc1b;
				String adc1a="第一节电压："+adc1b+"V";
				ADC01.setText(adc1a);
				
				String current_adc2 = Utils.bytesToHexString(adc2_value);
				double adc2=Utils.test16To10(current_adc2);
				String adc2b=Utils.achange(adc2);
				voldata[1]=Double.parseDouble(adc2b);
				voldata2[1]=adc2b;
				String adc2a="第二节电压："+adc2b+"V";
				ADC02.setText(adc2a);
				
				String current_adc3 = Utils.bytesToHexString(adc3_value);
				double adc3=Utils.test16To10(current_adc3);
				String adc3b=Utils.achange(adc3);
				voldata[2]=Double.parseDouble(adc3b);
				voldata2[2]=adc3b;
				String adc3a="第三节电压："+adc3b+"V";
				ADC03.setText(adc3a);
				
				String current_adc4 = Utils.bytesToHexString(adc4_value);
				double adc4=Utils.test16To10(current_adc4);
				String adc4b=Utils.achange(adc4);
				voldata[3]=Double.parseDouble(adc4b);
				voldata2[3]=adc4b;
				String adc4a="第四节电压："+adc4b+"V";
				ADC04.setText(adc4a);
				
				String current_adc5 = Utils.bytesToHexString(adc5_value);
				double adc5=Utils.test16To10(current_adc5);
				String adc5b=Utils.achange(adc5);
				voldata[4]=Double.parseDouble(adc5b);
				voldata2[4]=adc5b;
				String adc5a="第五节电压："+adc5b+"V";
				ADC05.setText(adc5a);
				
				String current_adc6 = Utils.bytesToHexString(adc6_value);
				double adc6=Utils.test16To10(current_adc6);
				String adc6b=Utils.achange(adc6);
				voldata[5]=Double.parseDouble(adc6b);
				voldata2[5]=adc6b;
				String adc6a="第六节电压："+adc6b+"V";
				ADC06.setText(adc6a);
				
				String current_adc7 = Utils.bytesToHexString(adc7_value);
				double adc7=Utils.test16To10(current_adc7);
				String adc7b=Utils.achange(adc7);
				voldata[6]=Double.parseDouble(adc7b);
				voldata2[6]=adc7b;
				String adc7a="第七节电压："+adc7b+"V";
				ADC07.setText(adc7a);
				
				String current_adc8 = Utils.bytesToHexString(adc8_value);
				double adc8=Utils.test16To10(current_adc8);
				String adc8b=Utils.achange(adc8);
				voldata[7]=Double.parseDouble(adc8b);
				voldata2[7]=adc8b;
				String adc8a="第八节电压："+adc8b+"V";
				ADC08.setText(adc8a);
				
				String current_adc9 = Utils.bytesToHexString(adc9_value);
				double adc9=Utils.test16To10(current_adc9);
				String adc9b=Utils.achange(adc9);
				voldata[8]=Double.parseDouble(adc9b);
				voldata2[8]=adc9b;
				String adc9a="第九节电压："+adc9b+"V";
				ADC09.setText(adc9a);
				
				String current_adc10 = Utils.bytesToHexString(adc10_value);
				double adc10=Utils.test16To10(current_adc10);
				String adc10b=Utils.achange(adc10);
				voldata[9]=Double.parseDouble(adc10b);
				voldata2[9]=adc10b;
				String adc10a="第十节电压："+adc10b+"V";
				ADC10.setText(adc10a);
				
				String current_adc11 = Utils.bytesToHexString(adc11_value);
				double adc11=Utils.test16To10(current_adc11);
				String adc11b=Utils.achange(adc11);
				voldata[10]=Double.parseDouble(adc11b);
				voldata2[10]=adc11b;
				String adc11a="第十一节电压："+adc11b+"V";
				ADC11.setText(adc11a);
				
				String current_adc12 = Utils.bytesToHexString(adc12_value);
				double adc12=Utils.test16To10(current_adc12);
				String adc12b=Utils.achange(adc12);
				voldata[11]=Double.parseDouble(adc12b);
				voldata2[11]=adc12b;
				String adc12a="第十二节电压："+adc12b+"V";
				ADC12.setText(adc12a);
				
				String current_adc13 = Utils.bytesToHexString(adc13_value);
				double adc13=Utils.test16To10(current_adc13);
				String adc13b=Utils.achange(adc13);
				voldata[12]=Double.parseDouble(adc13b);
				voldata2[12]=adc13b;
				String adc13a="第十三节电压："+adc13b+"V";
				ADC13.setText(adc13a);
				
				double dataAll=0;
				for(int i=0;i<13;i++)
				{
					dataAll+=voldata[i];
				}
				String dataAll2 = String.format("%.3f",dataAll);
				String adcAll="电池电压："+dataAll2+"V";
				ADCALL.setText(adcAll);
			}
		});
	}

	// 线程， 发送消息
	public class MyThread extends Thread {  
        public void run() {
        	int count = 0;
            while (!Thread.currentThread().isInterrupted()) {
//            	Message msg = null;
//        		msg.what = REFRESH;  
//              mHandler.sendMessage(msg);

            	if(count == 0)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char1);
                }
            	
            	else if(count == 1)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char2);
            	}
            	else if(count == 2)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char3);
            	}
            	else if(count == 3)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char4);
            	}
            	else if(count == 4)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char5);
            	}
            	else if(count == 5)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char6);
            	}
            	else if(count == 6)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char7);
            	}
            	else if(count == 7)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char8);
            	}
            	else if(count == 8)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char9);
            	}
            	else if(count == 9)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_charA);
            	}else if(count == 10)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_charB);
            	}else if(count == 11)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_charC);
            	}else if(count == 12)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_charD);
            	}
            	
            	count++;            	
            	//count %= 2;            	
            	if(count==13){
            		count=0;
            	}
            	try {  
                    Thread.sleep(500);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }
        	} 
        }
    }	
	
	private class BtnListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putStringArray("voldata2", voldata2);
			intent.putExtras(bundle);
			intent.setClass(StartActivity.this, MainActivity.class);
			StartActivity.this.startActivity(intent);
		}
		
	}
	
	
}
