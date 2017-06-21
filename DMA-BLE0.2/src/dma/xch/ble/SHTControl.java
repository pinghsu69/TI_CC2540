package dma.xch.ble;

import java.util.UUID;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class SHTControl extends Activity 
{
	
	static RefreshHandler m_RefreshHandler;
	static final int Message_ShtRead = 1;
	static final int Message_ShtWrite = 2;
	static final int Message_ShtNotify = 3;
	
	boolean m_bwriteok = false;
	boolean m_breadok = false;
	
	private ImageButton mIb_title_back;
	private TextView mTv_title_view;
	
	private ImageView mIv_temp,mIv_humi;
	private TextView mTv_temp,mTv_humi;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub	
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sht_control);
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
		mTv_title_view.setText("úÿùÒ∂»ΩÈ√Ê");
		
		mIv_temp=(ImageView) this.findViewById(R.id.iv_temp);
		mIv_humi=(ImageView) this.findViewById(R.id.iv_humi);
		
		mTv_temp=(TextView) this.findViewById(R.id.tv_temp);
		mTv_humi=(TextView) this.findViewById(R.id.tv_humi);
		
		AbsoluteLayout.LayoutParams mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (105*ShareData.densitydpi*ShareData.width_scale),(int) (242*ShareData.densitydpi*ShareData.height_scale));
		mTv_temp.setLayoutParams(mLayout_view);
		mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (223*ShareData.densitydpi*ShareData.width_scale),(int) (242*ShareData.densitydpi*ShareData.height_scale));
		mTv_humi.setLayoutParams(mLayout_view);
		
		m_RefreshHandler = new RefreshHandler();
		
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		MainActivity.m_bShtControlShow= true;
        MainActivity.setCharacteristicNotification(MainActivity.gattCharacteristic_SHT10, MainActivity.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID, true);
		super.onResume();
	}
	
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		MainActivity.m_bShtControlShow = false;	
		MainActivity.LedBleDisconnect();
		super.finish();
	}


	
	public static void ParseMessage(int message)
	{
		Message msg = new Message();
		msg.arg1 = message;
		msg.what = 1;
		// Log.v("ParseMessage", "message = " + msg.arg1);
		SHTControl.m_RefreshHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	class RefreshHandler extends Handler 
	{
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) 
		{
			// TODO Auto-generated method stub
			//int value=0;
			if (msg.what == 1) 
			{
				switch (msg.arg1) 
				{
				case Message_ShtRead:
					m_breadok = true;
					break;	
				case Message_ShtWrite:
					m_bwriteok = true;

					break;
				case Message_ShtNotify:
					
					byte[] value = MainActivity.gattCharacteristic_SHT10.getValue();
					String str="";
					String tem=""+(char)value[4]+(char)value[5]+(char)value[6]+(char)value[7];
					String humi=""+(char)value[13]+(char)value[14]+(char)value[15]+(char)value[16];
					
					mTv_temp.setText(tem+" °„C");
					mTv_humi.setText(humi+" %");
					
					//130 185 100
					
					AbsoluteLayout.LayoutParams mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) ((int) (110*Float.parseFloat(tem)/100)*ShareData.densitydpi*ShareData.height_scale),(int) (88*ShareData.densitydpi*ShareData.width_scale),(int) ((195+(float)(11*(100-Float.parseFloat(tem)))/10)*ShareData.densitydpi*ShareData.height_scale));
					mIv_temp.setLayoutParams(mLayout_view);
					
					mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) ((int) (110*Float.parseFloat(humi)/100)*ShareData.densitydpi*ShareData.height_scale),(int) (203*ShareData.densitydpi*ShareData.width_scale),(int) ((195+(float)(11*(100-Float.parseFloat(humi)))/10)*ShareData.densitydpi*ShareData.height_scale));
					mIv_humi.setLayoutParams(mLayout_view);
					
					
					for(int i=0;i<value.length;i++)
					{
						str+=(char)value[i];
					}
					
					Log.e("notify---", "str="+str);
					Log.e("notify---", "tem="+tem);
					Log.e("notify---", "humi="+humi);
					
					//float tem=value[0]+(float)value[1]/10;
					//float humi=value[2]+(float)value[3]/10;
					
					//Log.e("notify---", "tem="+tem);
					//Log.e("notify---", "humi="+humi);
					break;
					
				case ShareData.Message_BLE_DisConnectOk:
					finish();
					break;
				}
			}
			// super.handleMessage(msg);
		}
	}
}
