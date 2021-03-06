package com.AmoSmartRF.bluetooth.le;

// 阿莫单片机淘宝店店主  完成编码
// http://amomcu.taobao.com/

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

public class StartActivity2 extends Activity implements OnClickListener {

	private final static String TAG = "StartActivity"; // StartActivity.class.getSimpleName();
		
	private final String ACTION_NAME_RSSI = "AMOMCU_RSSI";  	// 其他文件广播的定义必须一致
	private final String ACTION_CONNECT = "AMOMCU_CONNECT"; 	// 其他文件广播的定义必须一致
		
	public static final int REFRESH = 0x000001;  
	
	// SmartRF 开发板的按键值定义
	final static int BLE_KEY_UP = 1;
	final static int BLE_KEY_DOWN = 16;
	final static int BLE_KEY_LEFT = 8;
	final static int BLE_KEY_RIGHT = 2;
	final static int BLE_KEY_CENTER = 4;
	final static int BLE_KEY_S1 = 32;
	final static int BLE_KEY_RELEASE = 0;
			
	
//	final static int  AMQ_CTRL_START    =         0;
//	final static int  AMQ_CTRL_PAUSE     =        1;
//	final static int  AMQ_CTRL_STOP       =      2;

	
    // 根据rssi 值计算距离， 只是参考作用， 不准确---amomcu
	static final int rssibufferSize = 10;
	int[] rssibuffer = new int[rssibufferSize];
	int rssibufferIndex = 0;
	boolean rssiUsedFalg = false;	
	
	static byte keyValue_save = 0;
	
	static Handler mHandler = new Handler();
	Handler mHandler_Msg = new Handler();
	static Handler mHandler_relay = new Handler();

	static EditText start_edit_SetDeviceName = null;

	// 设备名称
	static boolean DeviceNameFlag = false;	
	static String DeviceName = null;
	
	// dht11 传感器数据， 包含温度与湿度
	static byte[] dht11_Sensor = new byte[4];

//	static TextView start_txt_temperature = null;

	// adc 采样数据， 分别为AIN4与AIN5的通道数据，也就是p0.4与p0.5的管脚输入的adc数据
	static byte[] adc4_value = new byte[2];
	static byte[] adc5_value = new byte[2];
	static TextView start_txt_ADC4ADC5 = null;
//	static double iWeight_g = 0.0; 

	// 读取adc数据的按键定义
	static Button start_button_Read_ADC0ADC1 = null;

	// 读取pwm值的按键定义
	static Button start_button_SetPwm = null;
	
    //按键值
	byte[] ledx_value = new byte[1];
	
	//继电器值
	byte[] relay_value = new byte[1];
		
	static TextView board_info_log = null;
	
	
	// 系统信息信息
	static TextView start_text_system_Info = null;	
	//static String system_Info;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		getActionBar().setTitle("AmoSmartRF蓝牙APP v1.1 20150324");

		// Intent intent = getIntent();
		// String value = intent.getStringExtra("mac_addr");

		findViewById(R.id.start_button_SetDeviceName).setOnClickListener(this);
//		findViewById(R.id.start_button_Read_ADC4ADC5).setOnClickListener(this);
		findViewById(R.id.start_button_SetPwm).setOnClickListener(this);

		board_info_log = (TextView) findViewById(R.id.board_info_log);
		start_edit_SetDeviceName = (EditText) findViewById(R.id.start_edit_SetDeviceName);
//		start_txt_temperature = (TextView) findViewById(R.id.start_txt_temperature);
//		start_txt_ADC4ADC5 = (TextView) findViewById(R.id.start_txt_ADC0ADC1);

//		start_button_Read_ADC0ADC1 = (Button) findViewById(R.id.start_button_Read_ADC4ADC5);
		start_button_SetPwm = (Button) findViewById(R.id.start_button_SetPwm);

		adc4_value[0] = 0;
		adc4_value[1] = 0;

		adc5_value[0] = 0;
		adc5_value[1] = 0;

		dht11_Sensor[0] = 0;
		dht11_Sensor[1] = 0;
		dht11_Sensor[2] = 0;
		dht11_Sensor[3] = 0;
		
		registerBoradcastReceiver();

		UpdateDeviceName();

		((Switch) findViewById(R.id.led1_switch))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Log.i(TAG, "led1_switch isChecked = " + isChecked);
				if (isChecked) {
					ledx_value[0] = 0x11;
				} else {
					ledx_value[0] = 0x10;
				}
				DeviceScanActivity.WriteCharX(
						DeviceScanActivity.gattCharacteristic_char1,
						ledx_value);				
			}
		});

		((Switch) findViewById(R.id.led2_switch))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Log.i(TAG, "led2_switch isChecked = " + isChecked);
				if (isChecked) {
					ledx_value[0] = 0x21;
				} else {
					ledx_value[0] = 0x20;
				}
				DeviceScanActivity.WriteCharX(
						DeviceScanActivity.gattCharacteristic_char1,
						ledx_value);
			}
		});
		
		((Switch) findViewById(R.id.led3_switch))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Log.i(TAG, "led3_switch isChecked = " + isChecked);
				if (isChecked) {
					ledx_value[0] = 0x41;
				} else {
					ledx_value[0] = 0x40;
				}
				DeviceScanActivity.WriteCharX(
						DeviceScanActivity.gattCharacteristic_char1,
						ledx_value);
			}
		});
		// 继电器开关操作
		((Switch) findViewById(R.id.relay_switch))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Log.i(TAG, "relay_switch isChecked = " + isChecked);
				if (isChecked) {
					relay_value[0] = 0x44;
				} else {
					relay_value[0] = 0x43;
				}

				DeviceScanActivity.WriteCharX(
						DeviceScanActivity.gattCharacteristic_char1,
						relay_value);
			}
		});
				
		
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
		case R.id.start_button_SetDeviceName: {						
			TextView start_edit_SetDeviceName = (TextView) this
					.findViewById(R.id.start_edit_SetDeviceName);
			if (start_edit_SetDeviceName.length() > 0) {
				String str = start_edit_SetDeviceName.getText().toString();
				DeviceScanActivity.WriteCharX(
						DeviceScanActivity.gattCharacteristic_char7,
						str.getBytes());
			} else {
				Toast.makeText(this, "请输入设备名称 ：BLE4.0-Device",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
		/*case R.id.start_button_Read_ADC4ADC5: {
			DeviceScanActivity
					.ReadCharX(DeviceScanActivity.gattCharacteristic_char9);
			break;
		}*/
		case R.id.start_button_SetPwm: {
			TextView start_edit_SetPWM = (TextView) this
					.findViewById(R.id.start_txt_SetPWM);
			if (start_edit_SetPWM.length() > 0
					&& start_edit_SetPWM.length() <= 8) {
				String pwm = start_edit_SetPWM.getText().toString();
				if (Utils.isHexChar(pwm) == true) {
					byte[] PwmValue = new byte[4];
					PwmValue = Utils.hexStringToBytes(pwm);
					DeviceScanActivity.WriteCharX(
							DeviceScanActivity.gattCharacteristic_charA,
							PwmValue);
				} else {
					Toast.makeText(this, "请输入4位十六进制数据如 ：102030F5",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "请输入4位十六进制数据如 ：102030F5",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
		}
	}

	public static synchronized void onCharacteristicRead(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		// Log.i(TAG, "onCharacteristicRead str = " + str);

		/*if (DeviceScanActivity.gattCharacteristic_keydata.equals(characteristic)) {// 按键
			byte[] key_value = new byte[1];
			key_value = characteristic.getValue();
			Log.i(TAG, "key_value[0] = " + key_value[0]);			
			keyValue_save = key_value[0];			
		} else*/ if (DeviceScanActivity.gattCharacteristic_char5.equals(characteristic)) {

		} else if (DeviceScanActivity.gattCharacteristic_char6
				.equals(characteristic)) {
			// Log.i(TAG, "onCharacteristicRead str = " + str);
			int i = characteristic.getValue().length;

			dht11_Sensor = characteristic.getValue();
			Log.i(TAG, "dht11_Sensor[2] = " + dht11_Sensor[2]);
		} else if (DeviceScanActivity.gattCharacteristic_char7
				.equals(characteristic)) {
			int i = characteristic.getValue().length;
			DeviceName = Utils.bytesToString(characteristic.getValue());
			DeviceNameFlag = true;
			Log.i(TAG, "DeviceName = " + DeviceName);
		} else if (DeviceScanActivity.gattCharacteristic_char9
				.equals(characteristic)) {// adc0 adc1 数据
			byte[] adc4_adc5_value = new byte[4];
			adc4_adc5_value = characteristic.getValue();
			adc4_value[0] = adc4_adc5_value[0];
			adc4_value[1] = adc4_adc5_value[1];
			adc5_value[0] = adc4_adc5_value[2];
			adc5_value[1] = adc4_adc5_value[3];
//			byte[] weight_g = new byte[4];
//			weight_g = characteristic.getValue();
//			int weight = Utils.byteArrayToInt(weight_g, 0);	
//			iWeight_g = (double)weight;
//			iWeight_g /= 10.0;
//			
//			Log.i(TAG, "iWeight_g = " + iWeight_g + "g");			
		} else {
			return;
		}

		mHandler.post(new Runnable() {
			@Override
			public synchronized void run() {
				// 显示设备名称
				if(DeviceNameFlag == true)
				{
					DeviceNameFlag = false;
					start_edit_SetDeviceName.setText(DeviceName);
				}

				// 显示当前温湿度
		/*		String current_temperature = "当前温度：" + dht11_Sensor[2] + "."
						+ dht11_Sensor[3] + "℃";
				String current_humitidy = "当前湿度：" + dht11_Sensor[0] + "."
						+ dht11_Sensor[1] + "%";
				start_txt_temperature.setText(current_temperature + "\r\n"
						+ current_humitidy);*/

				// 显示当前adc0 adc1的值
				String current_adc4 = "当前ADC4：0x"
						+ Utils.bytesToHexString(adc4_value);
				String current_adc5 = "当前ADC5：0x"
						+ Utils.bytesToHexString(adc5_value);
				start_txt_ADC4ADC5
						.setText(current_adc4 + "\r\n" + current_adc5);
				
//				start_txt_ADC0ADC1.setText("重量：" + String.format("%.1f",iWeight_g) + "克");				
				
			    // 显示按键状态
				switch(keyValue_save)
				{
				case BLE_KEY_UP:
					board_info_log.setText("按键信息: BLE_KEY_UP");
					break;
				case BLE_KEY_DOWN:
					board_info_log.setText("按键信息: BLE_KEY_DOWN");
					break;
				case BLE_KEY_LEFT:
					board_info_log.setText("按键信息: BLE_KEY_LEFT");
					break;
				case BLE_KEY_RIGHT:
					board_info_log.setText("按键信息: BLE_KEY_RIGHT");
					break;
				case BLE_KEY_CENTER:
					board_info_log.setText("按键信息: BLE_KEY_CENTER");
					break;
				case BLE_KEY_S1:
					board_info_log.setText("按键信息: BLE_KEY_S1");
					break;
				case BLE_KEY_RELEASE:
					board_info_log.setText("按键信息: BLE_KEY_RELEASE");
					break;
				}					
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
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char6);	
            	}
            	else if(count == 1)
            	{
            		DeviceScanActivity.ReadCharX(DeviceScanActivity.gattCharacteristic_char9);
            	}
            	
            	count++;            	
            	count %= 2;            	
            	
            	try {  
                    Thread.sleep(1000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }
        	} 
        }
    }	
	
	
	@SuppressWarnings("unused")
	private void UpdateDeviceName() {
		DeviceScanActivity
				.ReadCharX(DeviceScanActivity.gattCharacteristic_char7);
	}

	private void SetTemperatureNotifyUpdate(boolean enable) {
		DeviceScanActivity.setCharacteristicNotification(
				DeviceScanActivity.gattCharacteristic_char6, enable);
	}

	// @Override
	// protected void onResume() {
	// Log.i(TAG, "---> onResume");
	// super.onResume();
	// }
	//
	// @Override
	// protected void onPause() {
	// Log.i(TAG, "---> onPause");
	// super.onPause();
	// }
	//
	@Override
	protected void onStop() {
		Log.i(TAG, "---> onStop");
		SetTemperatureNotifyUpdate(false);

		super.onStop();
	}
	//
	//
	// @Override
	// protected void onDestroy() {
	// Log.i(TAG, "---> onDestroy");
	// super.onDestroy();
	// SetTemperatureNotifyUpdate(false);
	// }
	//
}
