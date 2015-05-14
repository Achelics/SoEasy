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
	//����HashMap���169��ͼƬ������ֵ�������飬�����趨�ö�Ӧ������
	@SuppressLint("UseSparseArrays")
	HashMap<Integer, int[]> map = new HashMap<Integer, int[]>();
	private int[] highIndex;//�����ѡͼƬ��ȡ�����ɫ����ռ����������
	//�����ӦͼƬ������
	Map<Integer, Integer> imageIndex = new HashMap<Integer, Integer>();
	//����Ҫɫ�ز�ֵ�����ޣ�����
	public final int MAX_VALUE = 8;
	public final int MIN_VALUE = -8;
	
	//����Ҫ��ȡ��ͼƬֵ�����䴫�ݹ�ȥ
	private ImageView return_image;
	private ImageView match_start;
	private Bitmap bitmap;
	private Bitmap changeBitmap;
	//ѡ���Ӧ��ֵ���д���
	private int flag;
	protected ImageView runImage;
	
	//��ȡ���飬��ͼƬ���д���
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
        //��ȡ��ѡͼƬ����������λ��
        getIndex();
        //��ȡԴͼƬ����������λ��
        getDataIndex();
        //�Ƚϵõ���Ӧ��ͼƬ����ֵ
        getMatchIndex();
        
        txtValue = (TextView)findViewById(R.id.txt_show);
        //��SeekBar��ֵ�������ֵ��Ȼ����иı�
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
				txtValue.setText("�ݴ�ֵ�� " + String.valueOf(seekBar.getProgress()));
				//��ȡ��ѡͼƬ����������λ��
		        getIndex();
		        //��ȡԴͼƬ����������λ��
		        getDataIndex();
			    //�Ƚϵõ���Ӧ��ͼƬ����ֵ
		        getMatchIndex();
			}
        	
        });
        
        //�����ã���ʾ��ǰ��ͼƬǰ�������ֵ
        Log.i(TAG, imageIndex.toString());
        match_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(GetImageActivity.this, ReturnListActivity.class);
//				it.putExtra("bitmap", getBitmap(bitmap,gValue));
				it.putExtra("bitmap", changeBitmap);
				Bundle bundle = new Bundle();
				//��Mapֵ���ݵ���Ӧ�Ľ��浱��
				SerializableMap tmpmap=new SerializableMap();
				tmpmap.setMap(imageIndex); 
				bundle.putSerializable("imageIndex", tmpmap);  
				bundle.putInt("flag", flag);
				it.putExtras(bundle);
				startActivity(it);
			}
		});   
	}
	//��ȡ���պ��ֱ��ͼ����ѡ��ռ������ǰ����
	public String getIndex() {
		 HistogramFilter hfilter = new HistogramFilter(flag);
//		 float[] candidateData = hfilter.filter(getBitmap(bitmap,gValue));
		 float[] candidateData = hfilter.filter(changeBitmap);
		 //����һ���±�����
		 highIndex = new int[3];
		 //��ȡ��ѡͼƬ�ĳ���
		 int length = candidateData.length;
		 float[] imageTemp = new float[length];
		 //����ѡͼƬ��ֵ��ֵ����ʱͼƬ��
		 System.arraycopy(candidateData, 0, imageTemp, 0, length);
		 //��ȡ��ӦͼƬ����ȡ��ֵ
		 java.util.Arrays.sort(candidateData); 
		 //�õ���ǰͼƬ���ֵ��ǰ����
		 for(int i=length-1; i>length-4; i--){
			 for(int j=0; j<length; j++){
				 if(candidateData[i]==imageTemp[j]){
					 //��ǰ����ֵ��ֵ��������
					 highIndex[length-1-i] = j;
				 }
			 }
		 }
		return highIndex.toString();
		 
	}
	//��ȡͼƬԴ���±�ֵ
	public void getDataIndex(){
		InputStream inputStream = getResources().openRawResource(R.raw.high_data_index);
		//���ı��ڵ����ݱ���ַ�����
		String string = TxtReader.getString(inputStream);
		//�ָ�169��ͼƬ��ǰ��������ֵ��ÿһ���ַ���Ϊһ��ͼƬ��ǰ��������ֵ������
		String indexData[] = string.split("@");
		//����һ��ͼƬ��ֱ��ͼ���飨�ַ����ͣ�
		String[] indexInt = null;
		int[] scIndex = null;
		
		for(int i=0; i<indexData.length; i++){
			indexInt = indexData[i].split(" ");
			//��������ֵ�����鳤��
			scIndex = new int[indexInt.length];
			//����Ӧ��������и�ֵ
			for(int j=0; j<scIndex.length; j++){
				scIndex[j] = Integer.parseInt(indexInt[j]);
			}
			map.put(i, scIndex);
		}
	}
	
	//�Ƚϳ�Դͼ�ʹ�ƥ��ͼ֮����ߵ��Դͼ��Ȼ���ȡ��Ӧ������
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
                // ������ص���ɫ    
                int color = pix[w * i + j];    
                int R = ((color & 0x00FF0000) >> 16);    
                int G = ((color & 0x0000FF00) >> 8);    
                int B = color & 0x000000FF;    
                if(((R)<200 && (R)>0 ) && ((G)<256 && (G)>g_value)
						&& ((B)<256 && (B)>g_value)){ //ȥ��������
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
