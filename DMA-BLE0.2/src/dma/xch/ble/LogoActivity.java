package dma.xch.ble;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class LogoActivity extends Activity 
{
	
	 private Handler mHandler; 

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState); 
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);		
        this.setContentView(R.layout.ly_logo);
        
        ShareData.getSrceenSize(this);
        ShareData.getStatusHeight(this);
         
        mHandler = new Handler();   
        mHandler.postDelayed(update, 3000);  
        
    }
    
    private Runnable update = new Runnable() 
    {   
        public void run() 
        {   
        	 Intent intent=new Intent();
        	 intent.setClass(LogoActivity.this, ScanActivity.class);
        	 startActivity(intent);
        	 LogoActivity.this.finish();
        }   
    };  
    
    
    
  
}