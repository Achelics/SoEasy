package com.lzq.soeasy.getimage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.lzq.soeasy.R;
import com.lzq.soeasy.imagedata.TxtReader;
import com.lzq.soeasy.photo.HistogramFilter;
import com.lzq.soeasy.returnimage.ReturnListActivity;

@SuppressLint({ "ShowToast", "UseSparseArrays" })
public class GetImageActivity extends Activity {
	
	public static String TAG = "GetImageActivity";
	//定义HashMap存放169张图片的索引值整型数组，并且设定好对应的索引
	@SuppressLint("UseSparseArrays")
	HashMap<Integer, int[]> map = new HashMap<Integer, int[]>();
	private int[] highIndex;//定义候选图片获取的最高色素所占比例的索引
	//定义对应图片的索引
	Map<Integer, Integer> imageIndex = new HashMap<Integer, Integer>();
	//定义要色素差值的上限，下限
	public final int MAX_VALUE = 8;
	public final int MIN_VALUE = -8;
	
	//定义要获取的图片值，将其传递过去
	private ImageView return_image;
	private ImageView match_start;
	private Bitmap bitmap;
	private Bitmap changeBitmap;
	//选择对应的值进行传递
	private int flag;
	protected ImageView runImage;
	
	//获取滑块，对图片进行处理
	private SeekBar sbChange;
	private TextView txtValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_image);
		
		return_image = (ImageView) findViewById(R.id.return_image);
		match_start = (ImageView) findViewById(R.id.match_start);
		Intent intent=getIntent();
        if(intent!=null){
        	bitmap = intent.getParcelableExtra("bitmap");
        	changeBitmap = bitmap;
        	Bundle bGet = intent.getExtras();
        	flag = bGet.getInt("flag");
            return_image.setImageBitmap(changeBitmap);
        }
        runImage = (ImageView) findViewById(R.id.run_image);
        //获取候选图片的最大点所在位置
        getIndex();
        //获取源图片的最大点所在位置
        getDataIndex();
        //比较得到相应的图片索引值
        getMatchIndex();
        
        txtValue = (TextView)findViewById(R.id.txt_show);
        //将SeekBar的值设置最大值，然后进行改变
        sbChange = (SeekBar)findViewById(R.id.sb_g);
        sbChange.setMax(255);
        sbChange.setProgress(255);
        sbChange.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				//show the bitmap
				changeBitmap = getBitmap(bitmap,seekBar.getProgress());
				return_image.setImageBitmap(changeBitmap);
				txtValue.setText("容错值： " + String.valueOf(seekBar.getProgress()));
				//获取候选图片的最大点所在位置
		        getIndex();
		        //获取源图片的最大点所在位置
		        getDataIndex();
			    //比较得到相应的图片索引值
		        getMatchIndex();
			}
        	
        });
        
        //测试用，显示当前的图片前三个最大值
        Log.i(TAG, imageIndex.toString());
        match_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(GetImageActivity.this, ReturnListActivity.class);
//				it.putExtra("bitmap", getBitmap(bitmap,gValue));
				it.putExtra("bitmap", changeBitmap);
				Bundle bundle = new Bundle();
				//将Map值传递到对应的界面当中
				SerializableMap tmpmap=new SerializableMap();
				tmpmap.setMap(imageIndex); 
				bundle.putSerializable("imageIndex", tmpmap);  
				bundle.putInt("flag", flag);
				it.putExtras(bundle);
				startActivity(it);
			}
		});   
	}
	//获取拍照后的直方图，并选择占比最大的前三项
	public String getIndex() {
		 HistogramFilter hfilter = new HistogramFilter(flag);
//		 float[] candidateData = hfilter.filter(getBitmap(bitmap,gValue));
		 float[] candidateData = hfilter.filter(changeBitmap);
		 //定义一个下标索引
		 highIndex = new int[3];
		 //获取候选图片的长度
		 int length = candidateData.length;
		 float[] imageTemp = new float[length];
		 //将候选图片的值赋值到临时图片中
		 System.arraycopy(candidateData, 0, imageTemp, 0, length);
		 //获取相应图片所获取的值
		 java.util.Arrays.sort(candidateData); 
		 //得到当前图片最大值的前三项
		 for(int i=length-1; i>length-4; i--){
			 for(int j=0; j<length; j++){
				 if(candidateData[i]==imageTemp[j]){
					 //将前三的值赋值到数组中
					 highIndex[length-1-i] = j;
				 }
			 }
		 }
		return highIndex.toString();
		 
	}
	//获取图片源的下标值
	public void getDataIndex(){
		InputStream inputStream = getResources().openRawResource(R.raw.high_data_index);
		//将文本内的内容变成字符串型
		String string = TxtReader.getString(inputStream);
		//分割169张图片的前三个索引值，每一个字符串为一张图片的前三个像素值的索引
		String indexData[] = string.split("@");
		//定义一张图片的直方图数组（字符串型）
		String[] indexInt = null;
		int[] scIndex = null;
		
		for(int i=0; i<indexData.length; i++){
			indexInt = indexData[i].split(" ");
			//声明索引值的数组长度
			scIndex = new int[indexInt.length];
			//对相应的数组进行赋值
			for(int j=0; j<scIndex.length; j++){
				scIndex[j] = Integer.parseInt(indexInt[j]);
			}
			map.put(i, scIndex);
		}
	}
	
	//比较出源图和待匹配图之间最高点的源图，然后获取相应的索引
	public void getMatchIndex() {
		int j=0;
		for(int i=0; i<map.size(); i++){
			if((map.get(i)[0]-highIndex[0]<MAX_VALUE && map.get(i)[0]-highIndex[0]>MIN_VALUE) 
					|| (map.get(i)[1]-highIndex[1]<MAX_VALUE && map.get(i)[1]-highIndex[1]>MIN_VALUE) 
					|| (map.get(i)[2]-highIndex[2]<MAX_VALUE && map.get(i)[2]-highIndex[2]>MIN_VALUE)){
				imageIndex.put(j, i);
				j++;
			}
		}
	}
	
	private  Bitmap getBitmap(Bitmap src, int g_value){
		int w=src.getWidth();
		int h=src.getHeight();  
        int[] pix = new int[w * h];  
        src.getPixels(pix, 0, w, 0, 0, w, h);  
        int alpha=0xFF<<24;  
        for (int i = 0; i < h; i++) {    
            for (int j = 0; j < w; j++) {    
                // 获得像素的颜色    
                int color = pix[w * i + j];    
                int R = ((color & 0x00FF0000) >> 16);    
                int G = ((color & 0x0000FF00) >> 8);    
                int B = color & 0x000000FF;    
                if(((R)<200 && (R)>0 ) && ((G)<256 && (G)>g_value)
						&& ((B)<256 && (B)>g_value)){ //去除背景；
					color = alpha | (color << 16) | (color << 8) | color;    
					pix[w * i + j] = Color.WHITE;  
                }
            }  
        } 
        Bitmap result=Bitmap.createBitmap(w, h, Config.RGB_565);  
        result.setPixels(pix, 0, w, 0, 0,w, h);
        
        return result;
	}
}
