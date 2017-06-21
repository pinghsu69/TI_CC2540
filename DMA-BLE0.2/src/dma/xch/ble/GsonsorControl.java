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

public class GsonsorControl extends Activity 
{
	
	static RefreshHandler m_RefreshHandler;
	static final int Message_GsensorRead = 1;
	static final int Message_GsensorWrite = 2;
	static final int Message_GsensorNotify = 3;
	
	
	boolean m_bwriteok = false;
	boolean m_breadok = false;
	
	private ImageButton mIb_title_back;
	private TextView mTv_title_view;
	
	
	private TextView mTv_x,mTv_y,mTv_z;
	private ImageView mIv_x,mIv_y,mIv_z;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub	
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gsensor_control);
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
		mTv_title_view.setText("÷ÿ¡¶ΩÈ√Ê");
		
		mTv_x=(TextView) this.findViewById(R.id.tv_x);
		mTv_y=(TextView) this.findViewById(R.id.tv_y);
		mTv_z=(TextView) this.findViewById(R.id.tv_z);
		
		mIv_x=(ImageView) this.findViewById(R.id.iv_x);
		mIv_y=(ImageView) this.findViewById(R.id.iv_y);
		mIv_z=(ImageView) this.findViewById(R.id.iv_z);
		
		
		AbsoluteLayout.LayoutParams mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (95*ShareData.densitydpi*ShareData.width_scale),(int) (385*ShareData.densitydpi*ShareData.height_scale));
		mTv_x.setLayoutParams(mLayout_view);
		mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (173*ShareData.densitydpi*ShareData.width_scale),(int) (385*ShareData.densitydpi*ShareData.height_scale));
		mTv_y.setLayoutParams(mLayout_view);
		mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (251*ShareData.densitydpi*ShareData.width_scale),(int) (385*ShareData.densitydpi*ShareData.height_scale));
		mTv_z.setLayoutParams(mLayout_view);
		
		
		
		
		m_RefreshHandler = new RefreshHandler();
	

		
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		MainActivity.m_bGsensorsControlShow = true;
        getPersonStatus();
        MainActivity.setCharacteristicNotification(MainActivity.gattCharacteristic_GSENSORS, MainActivity.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID, true);
		super.onResume();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		MainActivity.m_bGsensorsControlShow = false;
		
		MainActivity.LedBleDisconnect();
		super.finish();
	}


	
	private void getPersonStatus()
	{
		MainActivity.ReadCharX(MainActivity.gattCharacteristic_GSENSORS);
	}
	

	
	public static void ParseMessage(int message)
	{
		Message msg = new Message();
		msg.arg1 = message;
		msg.what = 1;
		// Log.v("ParseMessage", "message = " + msg.arg1);
		GsonsorControl.m_RefreshHandler.sendMessage(msg);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("HandlerLeak")
	class RefreshHandler extends Handler 
	{
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) 
		{
			// TODO Auto-generated method stub
			if (msg.what == 1) 
			{
				switch (msg.arg1) 
				{
				case Message_GsensorRead:
					
					//value = MainActivity.gattCharacteristic_PERSON.getValue()[0];
					//Log.e("read---", "vales"+value);
					m_breadok = true;
					
					break;	
				case Message_GsensorWrite:
					m_bwriteok = true;

					break;
				
				case Message_GsensorNotify:
					byte[] value= MainActivity.gattCharacteristic_GSENSORS.getValue();
					Log.e("read---", "vales"+Integer.toHexString(value[0]));
					Log.e("read---", "vales"+Integer.toHexString(value[1]));
					
					
					Log.e("read---", "vales5"+Integer.toHexString(value[5]));
					Log.e("read---", "vales6"+Integer.toHexString(value[6]));
					
					int x=(int)value[5]*256+value[6];
					int y=(int)value[8]*256+value[9];
					int z=(int)value[11]*256+value[12];
					
					
					if(value[4]==0)
					{
						x=0-x;
					}
					if(value[7]==0)
					{
						y=0-y;
					}
					if(value[10]==0)
					{
						y=0-y;
					}
					
					mTv_x.setText(""+x);
					mTv_y.setText(""+y);
					mTv_z.setText(""+z);
					

					AbsoluteLayout.LayoutParams mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (92*ShareData.densitydpi*ShareData.width_scale),(int) ((300-(160*(x+512)/1024))*ShareData.densitydpi*ShareData.height_scale));
					mIv_x.setLayoutParams(mLayout_view);
					mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (169*ShareData.densitydpi*ShareData.width_scale),(int) ((300-(160*(y+512)/1024))*ShareData.densitydpi*ShareData.height_scale));
					mIv_y.setLayoutParams(mLayout_view);
					mLayout_view=new AbsoluteLayout.LayoutParams( AbsoluteLayout.LayoutParams.WRAP_CONTENT,AbsoluteLayout.LayoutParams.WRAP_CONTENT,(int) (250*ShareData.densitydpi*ShareData.width_scale),(int) ((300-(160*(z+512)/1024))*ShareData.densitydpi*ShareData.height_scale));
					mIv_z.setLayoutParams(mLayout_view);
					
					
					

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
