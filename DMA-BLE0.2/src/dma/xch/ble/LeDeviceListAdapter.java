package dma.xch.ble;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeDeviceListAdapter extends BaseAdapter {

	// Adapter for holding devices found through scanning.
		
	private ArrayList<BleDeviceList> m_BleDevices;
		
	private LayoutInflater mInflator;
	private Activity mContext;

	public LeDeviceListAdapter(Activity c) 
	{
		super();
		mContext = c;	
		m_BleDevices = new ArrayList<BleDeviceList>();
		mInflator = mContext.getLayoutInflater();
	}

	public void addDevice(BleDeviceList ble) 
	{
		if (ble == null)
			return;

		for (int i = 0; i < m_BleDevices.size(); i++) 
		{
			String btAddress = m_BleDevices.get(i).BleDevice.getAddress();
			if (btAddress.equals(ble.BleDevice.getAddress())) 
			{
				//这个设备之前已经添加过了，更新即可
				m_BleDevices.add(i + 1, ble);
				m_BleDevices.remove(i);
				return;
			}
		}
		m_BleDevices.add(ble);
	}
	
	public BleDeviceList getDevice(int index)
	{
		return m_BleDevices.get(index);
	}

	public void clear() {
		m_BleDevices.clear();
	}

	@Override
	public int getCount() {
		return m_BleDevices.size();
	}
	

	@SuppressLint("NewApi")
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {		
		// General ListView optimization code.	
		ViewHolder viewHolder;
		if (view == null) 
		{		
			viewHolder = new ViewHolder();
			Log.v("BLE", "i = " + i + ", view = " + view);
			view = mInflator.inflate(R.layout.listitem, null);	
			viewHolder.deviceImage = (ImageView) view.findViewById(R.id.iv_head);
			viewHolder.deviceName = (TextView) view.findViewById(R.id.textView_DeviceName);
			viewHolder.deviceMac = (TextView) view.findViewById(R.id.textView_DeviceMac);
			viewHolder.devicetxRSSI = (TextView) view.findViewById(R.id.textView_Rssi);
			viewHolder.deviceIvRssI=(ImageView) view.findViewById(R.id.iv_rssi);
			view.setTag(viewHolder);
		} 
		else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//viewHolder.deviceName.setText(m_BleDevices.get(i).BleDevice.getName());
		viewHolder.deviceMac.setText(m_BleDevices.get(i).BleDevice.getAddress());
		//viewHolder.devicetxRSSI.setText(String.format("%d", m_BleDevices.get(i).rssi));
		viewHolder.devicetxRSSI.setText(String.format("%d", m_BleDevices.get(i).rssi)+"db ");
		
		if(m_BleDevices.get(i).rssi>-33)
		{
			viewHolder.deviceIvRssI.setBackgroundResource(R.drawable.signal_01_03);
		}
		else if(m_BleDevices.get(i).rssi>-66&&m_BleDevices.get(i).rssi<=-33)
		{
			viewHolder.deviceIvRssI.setBackgroundResource(R.drawable.signal_02_03);
		}
		else if(m_BleDevices.get(i).rssi>-99&&m_BleDevices.get(i).rssi<=-66)
		{
			viewHolder.deviceIvRssI.setBackgroundResource(R.drawable.signal_03_03);
		}
		else
		{
			viewHolder.deviceIvRssI.setBackgroundResource(R.drawable.signal_04_03);
		}
		
		
		// view.setBackgroundColor(Color.argb(255-iAlpha,255-iRed, 0xFF-iGreen,
		// 0xFF-iBlue));
		
		if(i%2==0)
		{
			view.setBackgroundColor(Color.argb(255, 229, 230, 230));
		}
		else
		{
			view.setBackgroundColor(Color.argb(255, 245, 245, 245));
		}
		
		
		if (!m_BleDevices.get(i).BleDevice.getName().contains("DMATEK"))// set color
		{
			//view.setBackgroundColor(Color.argb(25, 0, 0, 255));
			viewHolder.deviceName.setText("Unknow");
			viewHolder.deviceImage.setBackgroundResource(R.drawable.title_unknow);
		} 
		else 
		{
			//view.setBackgroundColor(Color.argb(25, 0, 255, 0));
			
			if(m_BleDevices.get(i).BleDevice.getName().contains("LED"))
			{
				viewHolder.deviceName.setText("LED");
				viewHolder.deviceImage.setBackgroundResource(R.drawable.title_led);
			}
			else if(m_BleDevices.get(i).BleDevice.getName().contains("HUMAN"))
			{
				viewHolder.deviceName.setText("人體感應");
				viewHolder.deviceImage.setBackgroundResource(R.drawable.title_person);
			}
			else if(m_BleDevices.get(i).BleDevice.getName().contains("TEM"))
			{
				viewHolder.deviceName.setText("溫濕度");
				viewHolder.deviceImage.setBackgroundResource(R.drawable.title_sht);
			}
			else if(m_BleDevices.get(i).BleDevice.getName().contains("GSENSOR"))
			{
				viewHolder.deviceName.setText("重力感應");
				viewHolder.deviceImage.setBackgroundResource(R.drawable.title_gsensors);
			}
			else if(m_BleDevices.get(i).BleDevice.getName().contains("PWM"))
			{
				viewHolder.deviceName.setText("PWM");
				viewHolder.deviceImage.setBackgroundResource(R.drawable.title_pwm);
			}
			else if(m_BleDevices.get(i).BleDevice.getName().contains("RELAY"))
			{
				viewHolder.deviceName.setText("繼電器");
				viewHolder.deviceImage.setBackgroundResource(R.drawable.title_relay);
			}
			else
			{
				viewHolder.deviceImage.setBackgroundResource(R.drawable.title_unknow);
				viewHolder.deviceName.setText("DMA-Unknow");
			}
		}
		return view;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	class ViewHolder
	{
		ImageView deviceImage;
		TextView deviceName;
		TextView deviceMac;
		TextView devicetxRSSI;
		ImageView deviceIvRssI;
	}
}

class BleDeviceList 
{
	BluetoothDevice BleDevice;
	int  rssi;
	byte[] scanRecord = new byte[62];	//蓝牙扫描返回的数据包最大长度为62，31个广播包和31个返回数据包		
}
