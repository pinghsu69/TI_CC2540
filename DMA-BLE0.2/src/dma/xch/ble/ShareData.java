package dma.xch.ble;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class ShareData 
{
	
	public static final int Message_BLE_ConnectOK = 1;
	public static final int Message_BLE_GetServiceOk = 2;
	public static final int Message_BLE_GetServiceErr = 3;
	public static final int Message_BLE_DisConnectOk = 4;
	public static final int Message_BLE_UPDATESCAN=5;
	public static final int Message_BLE_UPDATESCANEND=6;
	
	
	
	public static int screen_width = 800;
	public static int screen_height = 480;
	
	public static float screen_width_d = 800;
	public static float screen_height_d = 480;
	
	
	
	public static final int STANDARD_SCREEN_WIDTH = 360;
	public static final int STANDARD_SCREEN_HEIGHT = 640;
	
	public static final int STANDARD_SCREEN_WIDTH_H = 640;
	public static final int STANDARD_SCREEN_HEIGHT_H = 360;
	
	public static float densitydpi=1;
	public static float width_scale=1;
	public static float height_scale=1;
	public static int screen_orient=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	
	public static int currentapiVersion=9;
	public static boolean istimeflag=false;

	
	

    /**
     * 
     * @param i 延时的毫秒值
     */
    public static void delay(int i) 
    {
    		// TODO Auto-generated method stub
    	try 
    	{
    		Thread.sleep(i);
    	} catch (InterruptedException e) 
    	{
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    		
    }
    
    
	
  //获取屏幕方向
    public static int ScreenOrient(Activity activity)
    {
    int orient = activity.getRequestedOrientation();
    if(orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
    //宽>高为横屏,反正为竖屏
    WindowManager windowManager = activity.getWindowManager();
    Display display = windowManager.getDefaultDisplay();
    int screenWidth = display.getWidth();
    int screenHeight = display.getHeight();
    orient = screenWidth < screenHeight ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }
    return orient;
    }
	
    public static void getSrceenSize(Activity activity) 
   	{
   		// TODO Auto-generated method stub
   		//获取屏幕大小
           Display display = activity.getWindowManager().getDefaultDisplay();
            
           ShareData.screen_width = display.getWidth();
           ShareData.screen_height = display.getHeight();
          
           
           Log.e("Share", "screen_width="+screen_width);
           Log.e("Share", "screen_height="+screen_height);
           
           
           
           
           DisplayMetrics dm = new DisplayMetrics();  
           dm = activity.getResources().getDisplayMetrics();  
           int densityDPI = dm.densityDpi;
           Log.e("Share", "densityDPI="+densityDPI);
           
           ShareData.densitydpi=(float)densityDPI/160;
           screen_width_d=(float)screen_width/densitydpi;
           screen_height_d=(float)screen_height/densitydpi;
           
           Log.e("Share", "screen_width_d="+screen_width_d);
           Log.e("Share", "screen_height_d="+screen_height_d);
           
           if(ShareData.screen_orient==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
           {
           ShareData.width_scale=(float)ShareData.screen_width_d/ShareData.STANDARD_SCREEN_WIDTH;
           ShareData.height_scale=(float)ShareData.screen_height_d/ShareData.STANDARD_SCREEN_HEIGHT;
           }
           else
           {
           	ShareData.width_scale=(float)ShareData.screen_width_d/ShareData.STANDARD_SCREEN_WIDTH_H;
            ShareData.height_scale=(float)ShareData.screen_height_d/ShareData.STANDARD_SCREEN_HEIGHT_H;	
           }
           
           
           
           
   	}
    
    public static float getStatusHeight(Activity activity)
    {
    	Class<?> c = null;
    	Object obj = null;
    	java.lang.reflect.Field field = null;
    	int x = 0, sbar = 0;
    	try {
    	    c = Class.forName("com.android.internal.R$dimen");
    	    obj = c.newInstance();
    	    field = c.getField("status_bar_height");
    	    x = Integer.parseInt(field.get(obj).toString());
    	    sbar = activity.getResources().getDimensionPixelSize(x);
    	    
    	    
    	} catch (Exception e1) 
    	{
    	    Log.e("","get status bar height fail");
    	    e1.printStackTrace();
    	    return 0;
    	}  
    	
    	 Log.e("Share","status_bar_height="+sbar);
    	return sbar;
    }
	
	
	
    public static  float ChangeHeight(float y) {
		// TODO Auto-generated method stub
		y=(int) (y*ShareData.height_scale);
		return y;
	}

    public static float ChangeWidth(float x) 
	{
		// TODO Auto-generated method stub
		x=(int) (x*ShareData.width_scale);
		return x;
	}

    public static float ChangeHeight(int y)
	{
		// TODO Auto-generated method stub
		y=(int) (y*ShareData.height_scale);
		return y;
	}

    public static float ChangeWidth(int x) 
	{
		// TODO Auto-generated method stub
		x=(int) (x*ShareData.width_scale);
		return x;
	}
    
    public static Bitmap ChangeBitmap(Bitmap mBitmap)
	{
		Matrix matrix=new Matrix();
		matrix.postScale(ShareData.width_scale*ShareData.densitydpi, ShareData.height_scale*ShareData.densitydpi);
		Bitmap tmp=mBitmap;
		return mBitmap=Bitmap.createBitmap(mBitmap, 0, 0, tmp.getWidth(), tmp.getHeight(),matrix,true);
		
	}
    
    
    public static Toast mToast;  
    public static void showToast(Activity actvity,String text) {    
        if(mToast == null) {    
            mToast = Toast.makeText(actvity, text, Toast.LENGTH_SHORT);    
        } else {    
            mToast.setText(text);      
            mToast.setDuration(Toast.LENGTH_SHORT);    
        }    
        mToast.show();    
    }    
        
    public static void cancelToast() {    
            if (mToast != null) {    
                mToast.cancel();    
            }    
        }    
	

}
