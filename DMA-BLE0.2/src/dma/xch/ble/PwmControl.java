package dma.xch.ble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class PwmControl extends Activity implements  OnSeekBarChangeListener 
{
	
	static RefreshHandler m_RefreshHandler;
	static final int Message_PwmRead = 1;
	static final int Message_PwmWrite = 2;
	
	
	boolean m_bwriteok = false;
	boolean m_breadok = false;
	
	private ImageButton mIb_title_back;
	private TextView mTv_title_view;
	
	private SeekBar mSb_v,mSb_c;
	private TextView mTv_v_values,mTv_c_values;
	
	
	private int int_v,int_c;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pwm_control);
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
		mTv_title_view.setText("PWM½éÃæ");
		
		//mIb_v_set=(ImageButton) this.findViewById(R.id.ib_v_set);
		//mIb_c_set=(ImageButton) this.findViewById(R.id.ib_c_set);
		
		//mIb_v_set.setOnTouchListener(this);
		//mIb_c_set.setOnTouchListener(this);
		
		mSb_v=(SeekBar) this.findViewById(R.id.sb_v);
		mSb_c=(SeekBar) this.findViewById(R.id.sb_c);
		
		mSb_v.setOnSeekBarChangeListener(this);
		mSb_c.setOnSeekBarChangeListener(this);
		
		
		mTv_v_values=(TextView) this.findViewById(R.id.tv_v);
		mTv_c_values=(TextView) this.findViewById(R.id.tv_c);
		
		
		AbsoluteLayout.LayoutParams mLayout_view=new AbsoluteLayout.LayoutParams( (int) (220*ShareData.densitydpi*ShareData.width_scale),AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (70*ShareData.densitydpi*ShareData.width_scale),(int) (200*ShareData.densitydpi*ShareData.height_scale));
		mSb_c.setLayoutParams(mLayout_view);
		mLayout_view=new AbsoluteLayout.LayoutParams( (int) (220*ShareData.densitydpi*ShareData.width_scale),AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (70*ShareData.densitydpi*ShareData.width_scale),(int) (350*ShareData.densitydpi*ShareData.height_scale));
		mSb_v.setLayoutParams(mLayout_view);
		mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (160*ShareData.densitydpi*ShareData.width_scale),(int) (158*ShareData.densitydpi*ShareData.height_scale));
		mTv_c_values.setLayoutParams(mLayout_view);
		mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (160*ShareData.densitydpi*ShareData.width_scale),(int) (307*ShareData.densitydpi*ShareData.height_scale));
		mTv_v_values.setLayoutParams(mLayout_view);
		
		
		m_RefreshHandler = new RefreshHandler();
	
		//MainActivity.WriteCharX(MainActivity.gattCharacteristic_Led, value);
		
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		MainActivity.m_bPwmControlShow = true;
        getLedStatus();
		super.onResume();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		MainActivity.m_bPwmControlShow = false;
		
		MainActivity.LedBleDisconnect();
		super.finish();
	}


	
	private void getLedStatus()
	{
		MainActivity.ReadCharX(MainActivity.gattCharacteristic_PWM);
	}
	

	
	public static void ParseMessage(int message) {
		Message msg = new Message();
		msg.arg1 = message;
		msg.what = 1;
		// Log.v("ParseMessage", "message = " + msg.arg1);
		PwmControl.m_RefreshHandler.sendMessage(msg);
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
				case Message_PwmRead:
					int value;
					value = MainActivity.gattCharacteristic_PWM.getValue()[0];
					//m_TextView_LedStatus.setText(str);
					m_breadok = true;
					break;	
				case Message_PwmWrite:
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

	

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) 
	{
		// TODO Auto-generated method stub
		if(seekBar==mSb_v)
		{
			int_v=progress;
			mTv_v_values.setText(""+int_v*100);
			
			
		}
		else if(seekBar==mSb_c)
		{
			int_c=progress;
			mTv_c_values.setText(""+int_c*10);
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) 
	{
		// TODO Auto-generated method stub
		Log.e("", "---------------");
		byte[] values=new byte[5];
		
		if(seekBar==mSb_v)
		{
			values[0]=0x01;
			values[1]=(byte) (((int_v*100)&0xff00)>>8);
			values[2]=(byte) ((int_v*100)&0xff);
			
			MainActivity.WriteCharX(MainActivity.gattCharacteristic_PWM, values);
			
			
		}
		else if(seekBar==mSb_c)
		{
			values[0]=0x02;
			values[3]=(byte) (((int_c*10)&0xff00)>>8);
			values[4]=(byte) ((int_c*10)&0xff);
			
			MainActivity.WriteCharX(MainActivity.gattCharacteristic_PWM, values);
		}
	}
}
