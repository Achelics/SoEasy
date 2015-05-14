package com.lzq.soeasy.db;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ResourceData {
	
	public static String databasename = "soeasy.db";
	
	public static String urlPath = "http://202.207.247.45:8001/cnsoft_test/cnsoft.php";

	//判断是否有网络连接 
	public static boolean isNetworkConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {  
	            return mNetworkInfo.isAvailable();  
	        }  
	    }
		return false;  
	    
	}
}
