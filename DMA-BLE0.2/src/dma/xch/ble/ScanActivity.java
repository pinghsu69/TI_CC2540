package dma.xch.ble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScanActivity extends Activity 
{

	private ImageButton mIb_title_back;
	private TextView mTv_title_view;
	
	private ImageButton mIb_scan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scan);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		mIb_title_back = (ImageButton) this.findViewById(R.id.back);
		mIb_title_back.setVisibility(View.GONE);
		/*
		 * mIb_title_back.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
		 * Auto-generated method stub finish(); return false; } });
		 */

		mTv_title_view = (TextView) this.findViewById(R.id.title);
		mTv_title_view.setText("主介面");
		
		
		RelativeLayout.LayoutParams mLayout_view=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLayout_view.topMargin=(int) (130*ShareData.densitydpi*ShareData.height_scale);//90+40 转px，加比例放缩
		mLayout_view.leftMargin=(int) (130*ShareData.densitydpi*ShareData.width_scale);
		mIb_scan=(ImageButton) this.findViewById(R.id.ib_scan);
		
		mIb_scan.setLayoutParams(mLayout_view);
		
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		ShareData.getSrceenSize(this);
        ShareData.getStatusHeight(this);
	}
	
	public void OnTouchScan(View view)
	{
		Intent intent=new Intent();
   	 	intent.setClass(ScanActivity.this, MainActivity.class);
   	 	startActivityForResult(intent, 1);
   	 	
	}

}
