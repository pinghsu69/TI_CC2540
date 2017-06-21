package dma.xch.ble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class LedControl extends Activity 
{
	
	static RefreshHandler m_RefreshHandler;
	static final int Message_LedRead = 1;
	static final int Message_LedWrite = 2;
	
	
	boolean m_bled1on = false;
	boolean m_bled2on = false;
	
	boolean m_bwriteok = false;
	boolean m_breadok = false;
	
	private ImageView mIv_led1,mIv_led2;
	private Switch mSw_led1,mSw_led2;
	
	private ImageButton mIb_title_back;
	private TextView mTv_title_view;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.led_control);
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
		mTv_title_view.setText("LEDΩÈ√Ê");
		
		
		
		m_RefreshHandler = new RefreshHandler();
	
		
		mIv_led1=(ImageView) this.findViewById(R.id.led_1_view);
		mIv_led2=(ImageView) this.findViewById(R.id.led_2_view);
		
		mSw_led1=(Switch) this.findViewById(R.id.led_1_switch);
		mSw_led2=(Switch) this.findViewById(R.id.led_2_switch);
		
		AbsoluteLayout.LayoutParams mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (60*ShareData.densitydpi*ShareData.width_scale),(int) (456*ShareData.densitydpi*ShareData.height_scale));
		mSw_led1.setLayoutParams(mLayout_view);
		
		mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (180*ShareData.densitydpi*ShareData.width_scale),(int) (456*ShareData.densitydpi*ShareData.height_scale));
		mSw_led2.setLayoutParams(mLayout_view);
		
		mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (70*ShareData.densitydpi*ShareData.width_scale),(int) (204*ShareData.densitydpi*ShareData.height_scale));
		mIv_led1.setLayoutParams(mLayout_view);
		
		mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (204*ShareData.densitydpi*ShareData.width_scale),(int) (204*ShareData.densitydpi*ShareData.height_scale));
		mIv_led2.setLayoutParams(mLayout_view);
		
		
		mSw_led1.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				// TODO Auto-generated method stub
				
				byte[] value = new byte[1];
				
				if(m_bled1on)
				{
					if(!isChecked)
					{
					mIv_led1.setBackgroundResource(R.drawable.led_off);
					value[0] = 0x10;
					m_bled1on = false;
					MainActivity.WriteCharX(MainActivity.gattCharacteristic_Led, value);
					}
				}
				else
				{
					if(isChecked)
					{
					mIv_led1.setBackgroundResource(R.drawable.led_on);
					value[0] = 0x11;
					mSw_led1.setChecked(true);
					m_bled1on = true;
					MainActivity.WriteCharX(MainActivity.gattCharacteristic_Led, value);
					}
				}			
				
				
				
			}
		});
		
		mSw_led2.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				// TODO Auto-generated method stub
				byte[] value = new byte[1];
				
				if(m_bled2on)
				{
					if(!isChecked)
					{
					mIv_led2.setBackgroundResource(R.drawable.led_off);
					value[0] = 0x20;
					m_bled2on = false;
					MainActivity.WriteCharX(MainActivity.gattCharacteristic_Led, value);
					}
				}
				else
				{
					if(isChecked)
					{
					mIv_led2.setBackgroundResource(R.drawable.led_on);
					value[0] = 0x21;
					mSw_led2.setChecked(true);
					m_bled2on = true;
					MainActivity.WriteCharX(MainActivity.gattCharacteristic_Led, value);
					}
				}			
			}
		});
		

		
		//m_Button_GetStatus.callOnClick();
		//MainActivity.m_bLedControlShow = true;
		
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		MainActivity.m_bLedControlShow = true;
        getLedStatus();
		super.onResume();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		MainActivity.m_bLedControlShow = false;
		
		MainActivity.LedBleDisconnect();
		super.finish();
	}


	
	private void getLedStatus()
	{
		MainActivity.ReadCharX(MainActivity.gattCharacteristic_Led);
	}
	

	
	public static void ParseMessage(int message) {
		Message msg = new Message();
		msg.arg1 = message;
		msg.what = 1;
		// Log.v("ParseMessage", "message = " + msg.arg1);
		LedControl.m_RefreshHandler.sendMessage(msg);
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
				case Message_LedRead:
					int value;
					value = MainActivity.gattCharacteristic_Led.getValue()[0];
					if((value & 1) == 0)
					{
						mIv_led1.setBackgroundResource(R.drawable.led_off);
						m_bled1on = false;
						mSw_led1.setChecked(false);
					}
					else
					{
						mIv_led1.setBackgroundResource(R.drawable.led_on);
						m_bled1on = true;
						mSw_led1.setChecked(true);
					}
					
					if((value & 2) == 0)
					{
						mIv_led2.setBackgroundResource(R.drawable.led_off);
						m_bled2on = false;
						mSw_led2.setChecked(false);
					}
					else
					{
						mIv_led2.setBackgroundResource(R.drawable.led_on);
						m_bled2on = true;
						mSw_led2.setChecked(true);
					}
					//m_TextView_LedStatus.setText(str);
					m_breadok = true;
					break;	
				case Message_LedWrite:
					m_bwriteok = true;
					getLedStatus();
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
