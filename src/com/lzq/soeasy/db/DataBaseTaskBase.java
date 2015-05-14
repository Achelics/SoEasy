package com.lzq.soeasy.db;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class DataBaseTaskBase extends AsyncTask<SQLiteDatabase, Integer,String>{
	
	/*定义要跳转的Activity界面*/
	private Activity activity;
	/*定义对应的数据库*/
	private SQLiteDatabase mDatabase;
	/*一个良好的用户体验progressbar*/
	private ProgressBar mProgressBar;
	/*定义一个泛型，将数据保存到Sqlite数据库中*/
	private ArrayList<NameValuePair> listparams;
	
	public DataBaseTaskBase(Activity activity,ProgressBar mProgressBar) {

		this.mProgressBar=mProgressBar;
        this.activity=activity;
	}
	
	@Override
	protected String doInBackground(SQLiteDatabase... params) {
		publishProgress(0);//将会调用onProgressUpdate(Integer... progress)方法  	    	
		mDatabase=params[0]; 
		listparams=new ArrayList<NameValuePair>();
		
		//创建cosoft表
		String DeleteTableCnsoft="DROP TABLE IF EXISTS cnsoft";
		mDatabase.execSQL(DeleteTableCnsoft);
		String CreatTableCnsoft="CREATE TABLE IF NOT EXISTS cnsoft" + "(id INTEGER PRIMARY KEY,brand VARCHAR,item_no VARCHAR)";		
		//执行sql语句
		mDatabase.execSQL(CreatTableCnsoft);
		
		 //获取cnsoft数据    
        listparams.clear();
        listparams.add(new BasicNameValuePair("type","cnsoft"));
        
        try {
        	String json=getStringFromUrl(ResourceData.urlPath,listparams);
        	JSONObject object = new JSONObject(json);
        	JSONArray array =object.getJSONArray("cnsoft");
        	for(int i = 0; i < array.length(); i++){
        		JSONObject jsonObject = (JSONObject)array.get(i); 	
        		//ContentValues以键值对的形式存放数据
        		ContentValues cv = new ContentValues();
        		cv.put("id", jsonObject.getInt("id"));
        		cv.put("brand", jsonObject.getString("brand"));
        		cv.put("item_no", jsonObject.getString("item_no"));
        		//插入ContentValues中的数据
				mDatabase.insert("cnsoft", null, cv);
        	}
        }catch (ClientProtocolException e) {
			e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } catch (JSONException e) {
	    	e.printStackTrace();
	    }
		
        publishProgress(100);//将会调用onProgressUpdate(Integer... progress)方法  
		return null;
	}
	
	@Override
	 protected void onProgressUpdate(Integer... progress) {
		//在调用publishProgress之后被调用，在ui线程执行 
		if(mProgressBar!=null){
			//更新进度条的进度
			 mProgressBar.setProgress(progress[0]);
		}
	}
	
	@Override
	protected void onPostExecute(String  result) {
		//进度条加载完之后执行
		if(activity!=null){
			Toast.makeText( activity, "数据更新完成", 2000).show();
			activity.finish();
		}
		super.onPostExecute(result);
	}
	
	private String getStringFromUrl(String url,List<NameValuePair> listparams) throws ClientProtocolException, IOException {
		/*通过post方式获取php资源文件*/
		HttpPost  httpRequest = new HttpPost(url);
		HttpClient client = new DefaultHttpClient();

		HttpEntity httpEntity = new UrlEncodedFormEntity(listparams);
		httpRequest.setEntity(httpEntity);
		HttpResponse response = client.execute(httpRequest);
		return retrieveInputStream(response.getEntity());
	}

	private String retrieveInputStream(HttpEntity entity) {

		@SuppressWarnings("unused")
		Long l = entity.getContentLength();		
		int length = (int) entity.getContentLength();		
		if (length < 0) 
			length = 10000;
		StringBuffer stringBuffer = new StringBuffer(length);
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(entity.getContent(), HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return stringBuffer.toString();
	}
	
}


