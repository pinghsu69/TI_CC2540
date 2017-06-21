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

public class RelayControl extends Activity 
{
	
	static RefreshHandler m_RefreshHandler;
	static final int Message_RelayRead = 1;
	static final int Message_RelayWrite = 2;
	
	
	boolean m_brelayon = false;
	boolean m_bwriteok = false;
	boolean m_breadok = false;
	
	private Switch mSw_relay;
	
	private ImageButton mIb_title_back;
	private TextView mTv_title_view;
	
	private ImageView mIv_relay;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.relay_control);
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
		mTv_title_view.setText("RelayΩÈ√Ê");
		
		
		
		m_RefreshHandler = new RefreshHandler();
	
		
		
		mSw_relay=(Switch) this.findViewById(R.id.sw_relay);
		mIv_relay=(ImageView) this.findViewById(R.id.iv_relay);
		
		
		mSw_relay.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				// TODO Auto-generated method stub
				
				byte[] value = new byte[1];
				
				if(m_brelayon)
				{
					if(!isChecked)
					{
					mIv_relay.setBackgroundResource(R.drawable.ui_relay_close);
					value[0] = 0x00;
					m_brelayon = false;
					MainActivity.WriteCharX(MainActivity.gattCharacteristic_REALY, value);
					}
				}
				else
				{
					if(isChecked)
					{
					mIv_relay.setBackgroundResource(R.drawable.ui_relay_opening);
					value[0] = 0x01;
					m_brelayon = true;
					MainActivity.WriteCharX(MainActivity.gattCharacteristic_REALY, value);
					}
				}			
				
				
				
			}
		});
		
		
		AbsoluteLayout.LayoutParams mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (138*ShareData.densitydpi*ShareData.width_scale),(int) (440*ShareData.densitydpi*ShareData.height_scale));
		mSw_relay.setLayoutParams(mLayout_view);
		
		

		
		//m_Button_GetStatus.callOnClick();
		//MainActivity.m_bLedControlShow = true;
		
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		MainActivity.m_bRelayControlShow = true;
		getRelayStatus();
		super.onResume();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		MainActivity.m_bRelayControlShow = false;
		
		MainActivity.LedBleDisconnect();
		super.finish();
	}


	
	private void getRelayStatus()
	{
		MainActivity.ReadCharX(MainActivity.gattCharacteristic_REALY);
	}
	

	
	public static void ParseMessage(int message) {
		Message msg = new Message();
		msg.arg1 = message;
		msg.what = 1;
		// Log.v("ParseMessage", "message = " + msg.arg1);
		RelayControl.m_RefreshHandler.sendMessage(msg);
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
				case Message_RelayRead:
					int value;
					value = MainActivity.gattCharacteristic_REALY.getValue()[0];
					if((value & 1) == 0)
					{
						mIv_relay.setBackgroundResource(R.drawable.ui_relay_opening);
						m_brelayon= false;
						mSw_relay.setChecked(false);
					}
					else
					{
						mIv_relay.setBackgroundResource(R.drawable.ui_relay_close);
						m_brelayon = true;
						mSw_relay.setChecked(true);
					}
					
					//m_TextView_LedStatus.setText(str);
					m_breadok = true;
					break;	
				case Message_RelayWrite:
					m_bwriteok = true;
					getRelayStatus();
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
