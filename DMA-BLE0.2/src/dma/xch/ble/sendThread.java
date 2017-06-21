package dma.xch.ble;


import android.app.Activity;
import android.util.Log;

public class sendThread extends Thread 
{


	
	private int time_int;
	private int count=0;

	public sendThread(int time_int,int count) 
	{
		// TODO Auto-generated constructor stub
		Log.v("onProgressUpdate", "sendThread init");
		this.time_int = time_int;
		this.count=count;

	}

	

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		Log.e("onProgressUpdate", "sendThread++++++");
		while (ShareData.istimeflag) 
		{
			if(count>0)
			{
				ShareData.delay(time_int);
				count--;
				
				MainActivity.ParseMessage(ShareData.Message_BLE_UPDATESCAN);
			}
			else
			{
				
				MainActivity.ParseMessage(ShareData.Message_BLE_UPDATESCANEND);
				
				break;
			}
			
			//开始使用
			
		}
		Log.e("onProgressUpdate", "sendThread----");
	}
}
