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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class PersonControl extends Activity 
{
	
	static RefreshHandler m_RefreshHandler;
	static final int Message_PersonRead = 1;
	static final int Message_PersonWrite = 2;
	static final int Message_PersonNotify = 3;
	
	
	boolean m_bwriteok = false;
	boolean m_breadok = false;
	
	private ImageView mIv_person;
	private ImageButton mIb_title_back;
	private TextView mTv_title_view;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub	
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.person_control);
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
		mTv_title_view.setText("»ÀÛwΩÈ√Ê");
		
		m_RefreshHandler = new RefreshHandler();
		mIv_person=(ImageView) this.findViewById(R.id.imageView1);

		
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		MainActivity.m_bPersonControlShow = true;
        getPersonStatus();
        MainActivity.setCharacteristicNotification(MainActivity.gattCharacteristic_PERSON, MainActivity.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID, true);
		super.onResume();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		MainActivity.m_bPersonControlShow = false;
		
		MainActivity.LedBleDisconnect();
		super.finish();
	}


	
	private void getPersonStatus()
	{
		MainActivity.ReadCharX(MainActivity.gattCharacteristic_PERSON);
	}
	

	
	public static void ParseMessage(int message)
	{
		Message msg = new Message();
		msg.arg1 = message;
		msg.what = 1;
		// Log.v("ParseMessage", "message = " + msg.arg1);
		PersonControl.m_RefreshHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	class RefreshHandler extends Handler 
	{
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) 
		{
			// TODO Auto-generated method stub
			int value=0;
			if (msg.what == 1) 
			{
				switch (msg.arg1) 
				{
				case Message_PersonRead:
					value = MainActivity.gattCharacteristic_PERSON.getValue()[0];
					Log.e("read---", "vales"+value);
					if((value & 1) == 0)
					{
						//mIv_person.setVisibility(View.GONE);
						mIv_person.setBackgroundResource(R.drawable.person_bg_no);
					}
					else
					{
						//mIv_person.setVisibility(View.VISIBLE);
						mIv_person.setBackgroundResource(R.drawable.person_bg_yes);
					}
					m_breadok = true;
					break;	
				case Message_PersonWrite:
					m_bwriteok = true;

					break;
				case Message_PersonNotify:
					value = MainActivity.gattCharacteristic_PERSON.getValue()[0];
					Log.e("read---", "vales"+value);
					if((value & 1) == 0)
					{
						//mIv_person.setVisibility(View.GONE);
						mIv_person.setBackgroundResource(R.drawable.person_bg_no);
					}
					else
					{
						//mIv_person.setVisibility(View.VISIBLE);
						mIv_person.setBackgroundResource(R.drawable.person_bg_yes);
					}

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
