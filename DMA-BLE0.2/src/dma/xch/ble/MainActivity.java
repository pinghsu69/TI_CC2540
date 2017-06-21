package dma.xch.ble;


import java.util.List;
import java.util.UUID;



import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements  OnItemClickListener 
{

	public static String UUID_LED 		= "0000fff1-0000-1000-8000-00805f9b34fb";
	public static String UUID_RELAY 	= "0000fff2-0000-1000-8000-00805f9b34fb";
	public static String UUID_PWM 		= "0000fff3-0000-1000-8000-00805f9b34fb";
	public static String UUID_GSENSORS 	= "0000fff6-0000-1000-8000-00805f9b34fb";
	public static String UUID_PERSON 	= "0000fff4-0000-1000-8000-00805f9b34fb";
	public static String UUID_SHT 		= "0000fff5-0000-1000-8000-00805f9b34fb";
	
	
	// 搜索BLE终端
	private BluetoothAdapter mBluetoothAdapter;
	boolean m_bStartScan = false;
	
	private ListView mLv_setting_content;
	private LeDeviceListAdapter mLeDeviceListAdapter = null;
	
	static BluetoothGatt mBluetoothGatt = null;
	static BluetoothDevice	mBluetoothDevice = null;
	
	static BluetoothGattCharacteristic gattCharacteristic_Led = null;
	static BluetoothGattCharacteristic gattCharacteristic_PERSON = null;
	static BluetoothGattCharacteristic gattCharacteristic_SHT10 = null;
	static BluetoothGattCharacteristic gattCharacteristic_GSENSORS= null;
	static BluetoothGattCharacteristic gattCharacteristic_PWM = null;
	static BluetoothGattCharacteristic gattCharacteristic_REALY= null;
	
	
	static RefreshHandler m_RefreshHandler;
	
	
	static boolean m_bLedControlShow = false;
	static boolean m_bRelayControlShow = false;
	static boolean m_bShtControlShow = false;
	static boolean m_bGsensorsControlShow = false;
	static boolean m_bPwmControlShow = false;
	static boolean m_bPersonControlShow = false;
	
	
	
	private ImageButton mIb_title_back;
	private TextView mTv_title_view;
	private RoundProgressBar mPb_pb;
	
	public static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	
	int progress=0;
	
	private Button mBt_scan;
	
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		
		mIb_title_back = (ImageButton) this.findViewById(R.id.back);
		mIb_title_back.setOnTouchListener(new OnTouchListener() 
		 {
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				// TODO Auto-generated method stub
				finish();
				return false;
			}
		});
		  

		mTv_title_view = (TextView) this.findViewById(R.id.title);
		mTv_title_view.setText("搜索介面");
		
		initFrame();
		
		
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();		
		
		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "設備不支援藍牙", Toast.LENGTH_SHORT).show();
			finish();
			return;
		} else {
			Log.v("BLE", "mBluetoothAdapter = " + mBluetoothAdapter);
		}
		
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
		    Toast.makeText(this, "設備不支援藍牙4.0", Toast.LENGTH_SHORT).show();
		    finish();
		    return;
		}
		
		if(!mBluetoothAdapter.enable())
		{
			Toast.makeText(this, "打開藍牙失敗", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		mBt_scan=(Button) this.findViewById(R.id.bt_ble);
		mBt_scan.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				
				if(!m_bStartScan)
				{
				mBt_scan.setText("掃描中，點擊停止");
				//mBt_scan.setEnabled(false);
				StartScan(true);
				}
				else
				{
					StartScan(false);
					mBt_scan.setText("點擊搜索");
				}
				
			}
		});
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		ShareData.getSrceenSize(this);
        ShareData.getStatusHeight(this);
		//�?��扫描BLE
		mBt_scan.setText("掃描中，點擊停止");
		//mBt_scan.setEnabled(false);
		StartScan(true);
		
		
	    //mPb_pb.setAlpha(1);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//停止扫描BLE
		StartScan(false);
		super.onDestroy();
	}
	
	
	private void initFrame() 
	{
		// TODO Auto-generated method stub
		m_RefreshHandler = new RefreshHandler();
		mLv_setting_content = (ListView) this.findViewById(R.id.lv_content);
		mLeDeviceListAdapter = new LeDeviceListAdapter(this);
		mLv_setting_content.setAdapter(mLeDeviceListAdapter);
		
		mLv_setting_content.setOnItemClickListener(this);
		mPb_pb=(RoundProgressBar) this.findViewById(R.id.progressBar1);
		
	}


	@SuppressLint("NewApi")
	void StartScan(boolean start)
	{
		if(start)
		{
			if(!m_bStartScan)
			{
				mLeDeviceListAdapter.clear();
				mBluetoothAdapter.startLeScan(mLeScanCallback);
				m_bStartScan = true;
				//mPb_pb.setVisibility(View.VISIBLE);
				progress=0;
				ShareData.istimeflag=true;
				
				sendThread st = new sendThread(200,100);// 1s 100s
				st.start();
				
				
			}
		}
		else
		{
			if(m_bStartScan)
			{
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				m_bStartScan = false;
				ShareData.istimeflag=false;
				//mPb_pb.setVisibility(View.INVISIBLE);
				//mPb_pb.setProgress(0);
			}
		}
	}
	// 蓝牙设备扫描的回调函
		@SuppressLint("NewApi")
		private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) 
			{			
				final BleDeviceList bdl = new BleDeviceList();
				bdl.BleDevice = device;
				bdl.rssi = rssi;
				System.arraycopy(scanRecord, 0, bdl.scanRecord, 0, bdl.scanRecord.length);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mLeDeviceListAdapter.addDevice(bdl);
						mLeDeviceListAdapter.notifyDataSetChanged();
					}
				});
				
			}
		};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment 
	{

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}



	@SuppressLint("NewApi")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
	{
		// TODO Auto-generated method stub
		//如果正在扫描，先停止扫描
		StartScan(false);
		
		
				
		//获取远端蓝牙的信�?
		String addr = mLeDeviceListAdapter.getDevice(position).BleDevice.getAddress();
		String name = mLeDeviceListAdapter.getDevice(position).BleDevice.getName();
		if(!name.contains("DMATEK"))
		{
			setTitle("設備非DMATEK設備，不支持連接");
			return;
		}
		
		
		mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(addr);
		if (mBluetoothDevice == null) {
			setTitle("獲取遠端藍牙信息錯誤，連接失敗！");
			return ;
		}
		
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		if(mBluetoothGatt == null)
		{
			mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mGattCallback);
			if (mBluetoothGatt == null) {
				setTitle("連接失敗");
				return ;
			}
		}
		else
		{
			mBluetoothGatt.connect();
		}
		setTitle("連接遠端藍牙...");

	}
	
	
	
	// Implements callback methods for GATT events that the app cares about. For
		// example,
		// connection change and services discovered.	
		@SuppressLint("NewApi")
		private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
		{
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status,
					int newState) {
				if (newState == BluetoothProfile.STATE_CONNECTED) 
				{
					//连接设备成功
					Log.v("BLE", "BLE Device Connect OK!");				
				//	setTitle("连接设备成功，开始获取服�?..");
					MainActivity.ParseMessage(ShareData.Message_BLE_ConnectOK);
				//	mBluetoothGatt.discoverServices();
				} 
				else if (newState == BluetoothProfile.STATE_DISCONNECTED) 
				{
					//断开连接
					Log.v("BLE", "BLE Device DisConnect !");
					
					//close和disconnect的区别在于，close除了断开连接外，还会释放掉所有资源，导致不可以直接在后面的操作中用gatt对象的connect直接连接，�?disconnect并不释放资源，所以，�?��的资源还保存�?��就可以用Gatt的connect进行�?��恢复连接
					mBluetoothGatt.close();
					mBluetoothGatt = null;
					
					if(m_bLedControlShow)
						LedControl.ParseMessage(ShareData.Message_BLE_DisConnectOk);
					
					if(m_bGsensorsControlShow)
						GsonsorControl.ParseMessage(ShareData.Message_BLE_DisConnectOk);
					
					if(m_bPersonControlShow)
						PersonControl.ParseMessage(ShareData.Message_BLE_DisConnectOk);
					
					if(m_bRelayControlShow)
						RelayControl.ParseMessage(ShareData.Message_BLE_DisConnectOk);
					if(m_bShtControlShow)
						SHTControl.ParseMessage(ShareData.Message_BLE_DisConnectOk);
					if(m_bPwmControlShow)
						PwmControl.ParseMessage(ShareData.Message_BLE_DisConnectOk);
					
					
					MainActivity.ParseMessage(ShareData.Message_BLE_DisConnectOk);

				}
			}

			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				if (status == BluetoothGatt.GATT_SUCCESS) 
				{
					//查找服务成功
					List<BluetoothGattService> gattServices = gatt.getServices();
					if (gattServices == null)
						return;
					
					for (BluetoothGattService gattService : gattServices) 
					{
						// -----Service的字段信�?----//
						int type = gattService.getType();
						Log.e("BLE", "-->service type:" + type);
						Log.e("BLE", "-->includedServices size:"
								+ gattService.getIncludedServices().size());
						Log.e("BLE", "-->service uuid:" + gattService.getUuid());

						// -----Characteristics的字段信�?----//
						List<BluetoothGattCharacteristic> gattCharacteristics = gattService
								.getCharacteristics();
						
						for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) 
						{
							
							//查找到匹配dmatek_led的UUID
							if (gattCharacteristic.getUuid().toString().equals(UUID_LED)) 
							{
								gattCharacteristic_Led = gattCharacteristic;
								gattCharacteristic_REALY=null;
								gattCharacteristic_GSENSORS=null;
								gattCharacteristic_PERSON=null;
								gattCharacteristic_PWM=null;
								gattCharacteristic_SHT10=null;
								break;
							}
							else if(gattCharacteristic.getUuid().toString().equals(UUID_RELAY))
							{
								gattCharacteristic_REALY = gattCharacteristic;
								
								gattCharacteristic_Led = null;
								gattCharacteristic_GSENSORS=null;
								gattCharacteristic_PERSON=null;
								gattCharacteristic_PWM=null;
								gattCharacteristic_SHT10=null;
								break;
							}
							else if(gattCharacteristic.getUuid().toString().equals(UUID_GSENSORS))
							{
								gattCharacteristic_GSENSORS = gattCharacteristic;
								
								gattCharacteristic_Led = null;
								gattCharacteristic_REALY=null;
								gattCharacteristic_PERSON=null;
								gattCharacteristic_PWM=null;
								gattCharacteristic_SHT10=null;
								
								break;
							}
							else if(gattCharacteristic.getUuid().toString().equals(UUID_PERSON))
							{
								gattCharacteristic_PERSON = gattCharacteristic;
								
								gattCharacteristic_Led = null;
								gattCharacteristic_GSENSORS=null;
								gattCharacteristic_REALY=null;
								gattCharacteristic_PWM=null;
								gattCharacteristic_SHT10=null;
								
								break;
							}
							else if(gattCharacteristic.getUuid().toString().equals(UUID_PWM))
							{
								gattCharacteristic_PWM = gattCharacteristic;
								
								gattCharacteristic_Led = null;
								gattCharacteristic_GSENSORS=null;
								gattCharacteristic_PERSON=null;
								gattCharacteristic_REALY=null;
								gattCharacteristic_SHT10=null;
								
								break;
							}
							else if(gattCharacteristic.getUuid().toString().equals(UUID_SHT))
							{
								gattCharacteristic_SHT10 = gattCharacteristic;
								
								gattCharacteristic_Led = null;
								gattCharacteristic_GSENSORS=null;
								gattCharacteristic_PERSON=null;
								gattCharacteristic_PWM=null;
								gattCharacteristic_REALY=null;
								
								break;
							}
							
							
							Log.e("BLE", "---->char uuid:" + gattCharacteristic.getUuid());

							int permission = gattCharacteristic.getPermissions();
							Log.e("BLE",
									"---->char permission:" + permission);

							int property = gattCharacteristic.getProperties();
							Log.e("BLE",
									"---->char property:" + property);

							byte[] data = gattCharacteristic.getValue();
							if (data != null && data.length > 0) {
								Log.e("BLE", "---->char value:" + new String(data));
							}
							
							// -----Descriptors的字段信�?----//
							List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
							for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) 
							{
								Log.e("BLE", "-------->desc uuid:" + gattDescriptor.getUuid());
								int descPermission = gattDescriptor.getPermissions();
								Log.e("BLE", "-------->desc permission:" + descPermission);							
								byte[] desData = gattDescriptor.getValue();
								if (desData != null && desData.length > 0) {
									Log.e("BLE", "-------->desc value:" + new String(desData));
								}
							}
						}
					}//
					
					
					MainActivity.ParseMessage(ShareData.Message_BLE_GetServiceOk);				
				} else {
					//查找服务失败
					MainActivity.ParseMessage(ShareData.Message_BLE_GetServiceErr);
				}
			}

			//蓝牙读的回调函数
			@Override
			public void onCharacteristicRead(BluetoothGatt gatt,
					BluetoothGattCharacteristic characteristic, int status)
			{
				Log.v("BLE", "onCharacteristicRead, gatt = " + gatt + ", characteristic = " + characteristic + ", status = " + status);
				Log.v("BLE", "onCharacteristicRead, characteristic.getValue().len = " + characteristic.getValue().length + ", value = " + characteristic.getValue()[0]);
				
				if(characteristic == gattCharacteristic_Led)
				{
					LedControl.ParseMessage(LedControl.Message_LedRead);
				}
				else if(characteristic == gattCharacteristic_PERSON)
				{
					PersonControl.ParseMessage(PersonControl.Message_PersonRead);
				}
				else if(characteristic == gattCharacteristic_GSENSORS&&m_bGsensorsControlShow)
				{
					GsonsorControl.ParseMessage(GsonsorControl.Message_GsensorRead);
				}
				else if(characteristic == gattCharacteristic_REALY&&m_bRelayControlShow)
				{

					RelayControl.ParseMessage(RelayControl.Message_RelayRead);
				}
				else if(characteristic == gattCharacteristic_SHT10&&m_bShtControlShow)
				{

					SHTControl.ParseMessage(SHTControl.Message_ShtRead);
				}
				else if(characteristic == gattCharacteristic_PWM&&m_bPwmControlShow)
				{
					PwmControl.ParseMessage(PwmControl.Message_PwmRead);
				}
				
			}
			
			//蓝牙写的回调函数
			@Override
			public void onCharacteristicWrite(BluetoothGatt gatt,
					BluetoothGattCharacteristic characteristic, int status) 
			{
				Log.v("BLE", "onCharacteristicWrite, gatt = " + gatt + ", characteristic = " + characteristic + ", status = " + status);
				if(characteristic == gattCharacteristic_Led&&m_bLedControlShow)
				{
					LedControl.ParseMessage(LedControl.Message_LedWrite);
				}
				else if(characteristic == gattCharacteristic_PWM&&m_bPwmControlShow)
				{
					PwmControl.ParseMessage(PwmControl.Message_PwmWrite);
				}
				else if(characteristic == gattCharacteristic_REALY&&m_bRelayControlShow)
				{
					RelayControl.ParseMessage(RelayControl.Message_RelayWrite);
				}
					
				// TODO Auto-generated method stub
				super.onCharacteristicWrite(gatt, characteristic, status);
			}

			@Override
			public void onCharacteristicChanged(BluetoothGatt gatt,
					BluetoothGattCharacteristic characteristic) 
			{
				
				Log.v("BLE", "onCharacteristicChanged, gatt = " + gatt + ", characteristic = " + characteristic);
				if(characteristic == gattCharacteristic_PERSON&&m_bPersonControlShow)
				{
					//写notify
					PersonControl.ParseMessage(PersonControl.Message_PersonRead);
				}
				else if(characteristic == gattCharacteristic_SHT10&&m_bShtControlShow)
				{
					SHTControl.ParseMessage(SHTControl.Message_ShtNotify);
				}
				else if(characteristic == gattCharacteristic_GSENSORS&&m_bGsensorsControlShow)
				{
					//写notify
					GsonsorControl.ParseMessage(GsonsorControl.Message_GsensorNotify);
				}
			}
		};
	   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		@SuppressLint({ "InlinedApi", "NewApi" })
		public static void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,UUID uuid,boolean enabled) 
		{
			if (mBluetoothGatt == null) 
			{
				Log.w("BLE", "BluetoothAdapter not initialized");
				return;
			}
			
			if (enabled == true) {
				Log.i("BLE", "Enable Notification");
				mBluetoothGatt.setCharacteristicNotification(characteristic, true);
				BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
				descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				mBluetoothGatt.writeDescriptor(descriptor);			
			} else {
				Log.i("BLE", "Disable Notification");
				mBluetoothGatt.setCharacteristicNotification(characteristic, false);
				BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
				descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
				mBluetoothGatt.writeDescriptor(descriptor);
			}
		

			// mBluetoothGatt.setCharacteristicNotification(characteristic,
			// enabled);
		}
	
	
		
		@SuppressLint("NewApi")
		static public void WriteCharX(BluetoothGattCharacteristic GattCharacteristic, byte[] writeValue) {
			Log.i("BLE", "writeCharX = " + GattCharacteristic);
			if (GattCharacteristic != null&&mBluetoothGatt!=null) 
			{
				GattCharacteristic.setValue(writeValue);			
				mBluetoothGatt.writeCharacteristic(GattCharacteristic);
			}
		}

		@SuppressLint("NewApi")
		static public void ReadCharX(BluetoothGattCharacteristic GattCharacteristic) 
		{
			Log.i("BLE", "GattCharacteristic = " + GattCharacteristic);		
			if (GattCharacteristic != null) 
			{			
				mBluetoothGatt.readCharacteristic(GattCharacteristic);
			}
		};
		
		@SuppressLint("NewApi")
		static public void LedBleDisconnect()
		{
			if(mBluetoothGatt != null)
			{
				mBluetoothGatt.disconnect();
			}
		}
		
		public static void ParseMessage(int message) 
		{
			Message msg = new Message();
			msg.arg1 = message;
			msg.what = 1;
			// Log.v("ParseMessage", "message = " + msg.arg1);
			MainActivity.m_RefreshHandler.sendMessage(msg);
		}
		
		public void setTitle(String title)
		{
			//mTv_title_view.setText(title);
			//Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
			ShareData.showToast(this, title);
		}
		
		
		@SuppressLint("HandlerLeak")
		class RefreshHandler extends Handler {
			@SuppressLint("NewApi")
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what == 1) 
				{
					switch (msg.arg1) 
					{
					case ShareData.Message_BLE_ConnectOK:
						setTitle("連接藍牙成功，開始獲取服務...");
						gattCharacteristic_Led = null;
						mBluetoothGatt.discoverServices();
						break;	
					case ShareData.Message_BLE_GetServiceOk:
						if(gattCharacteristic_Led != null)
						{
							setTitle("找到lED服務，啟動介面");
							
							Intent intent = new Intent();
							intent.setClass(MainActivity.this, LedControl.class);					
							startActivity(intent);
							
							Log.v("BLE", "Start Led Control Activity!");					
						}
						else if(gattCharacteristic_REALY != null)
						{
							setTitle("找到Relay服务，啟動介面");
							
							Intent intent = new Intent();
							intent.setClass(MainActivity.this, RelayControl.class);					
							startActivity(intent);
							
							Log.v("BLE", "Start Relay Control Activity!");					
						}
						else if(gattCharacteristic_PERSON != null)
						{
							setTitle("找到人體服务，啟動介面");
							
							Intent intent = new Intent();
							intent.setClass(MainActivity.this, PersonControl.class);					
							startActivity(intent);
							
							Log.v("BLE", "Start Person Control Activity!");					
						}
						else if(gattCharacteristic_PWM != null)
						{
							setTitle("找到PWM服務，啟動介面");
							
							Intent intent = new Intent();
							intent.setClass(MainActivity.this, PwmControl.class);					
							startActivity(intent);
							
							Log.v("BLE", "Start pwm Control Activity!");					
						}
						else if(gattCharacteristic_GSENSORS != null)
						{
							setTitle("找到Gsensor服務，啟動介面");
							
							Intent intent = new Intent();
							intent.setClass(MainActivity.this, GsonsorControl.class);					
							startActivity(intent);
							
							Log.v("BLE", "Start Gsonsor Control Activity!");					
						}
						else if(gattCharacteristic_SHT10 != null)
						{
							setTitle("找到溫濕度服務，啟動介面");
							
							Intent intent = new Intent();
							intent.setClass(MainActivity.this, SHTControl.class);					
							startActivity(intent);
							
							Log.v("BLE", "Start sht10 Control Activity!");					
						}
						break;
					case ShareData.Message_BLE_DisConnectOk:
						setTitle("和遠端藍牙斷開連接，掃描中...");
						StartScan(true);
						break;
						
					case ShareData.Message_BLE_UPDATESCAN:
						progress++;
						mPb_pb.setProgress(progress);
						break;
					case ShareData.Message_BLE_UPDATESCANEND:
						mPb_pb.setProgress(100);
						StartScan(false);
						mBt_scan.setText("點擊搜索");
						break;
					}
				}
				// super.handleMessage(msg);
			}
		}
		
		
	
	

}
