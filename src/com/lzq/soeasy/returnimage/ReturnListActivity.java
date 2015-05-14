package com.lzq.soeasy.returnimage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lzq.soeasy.R;
import com.lzq.soeasy.getimage.SerializableMap;
import com.lzq.soeasy.imagedata.ImageData;
import com.lzq.soeasy.imagedata.TxtReader;
import com.lzq.soeasy.photo.HistogramFilter;
import com.lzq.soeasy.util.MyToast;

public class ReturnListActivity extends Activity {
	
	//������Ӧ��ͼƬ·��
	private static String TEST_IMAGE;
	private static final String FILE_NAME = "share_lzq.jpg";
	//����Menu�˵�ѡ���ItemId
	final static int ONE = Menu.FIRST;
	final static int TWO = Menu.FIRST+1;
	//����Ҫ��ȡ��ͼƬ
	private Bitmap bitmap;
	//��ȡͼƬ��Դ��ID
	ImageData imageData = new ImageData();
	private ImageView runImage;
	//ѡ��ͼƬ�ı�־λ
	private int flag;
	TranslateAnimation left, right;
	Animation up, down;
	//�������մ��ݹ�����ͼƬֵ�����Ҵ洢��Map��
	Map<Integer, Integer> imageIndex;
	//����һ��Ҫƥ��ͼƬ�ĳ���
	public static int matchLength;
	/*����ͼƬ��λ��*/
	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_return_list);
		runImage = (ImageView) findViewById(R.id.run_image);
		runAnimation();
		//��ʼ��������
		ShareSDK.initSDK(this);
		/**��ȡ������������*/
		Intent intent=getIntent();
        if(intent!=null){
        	bitmap = intent.getParcelableExtra("bitmap");
        	Bundle bGet = intent.getExtras();
        	flag = bGet.getInt("flag");
        	if(flag == 1){
        		//��ȡ��ǰһ���洫�ݹ�����ͼƬֵ
            	SerializableMap serializableMap = (SerializableMap) bGet
        				.get("imageIndex");
            	imageIndex = serializableMap.getMap();
            	matchLength = imageIndex.size();
        	}
       }
        HistogramFilter hfilter = new HistogramFilter(flag);
		float[] candidateData = hfilter.filter(bitmap);
		//��ȡ����ͼƬ��ֱ��ͼ����
		InputStream inputStream = null;
		if(flag == 0){
			inputStream = getResources().openRawResource(R.raw.source_data);  
		}else{
			inputStream = getResources().openRawResource(R.raw.no_source_data);
		}
		    
		String string = TxtReader.getString(inputStream);
		
		//�ָ�169��ͼƬ��ֱ��ͼ��ÿһ���ַ���Ϊһ��ͼƬ��ֱ��ͼ����
		String imageData[] = string.split("@");
		
		//����һ��ͼƬ��ֱ��ͼ���飨�ַ����ͣ�
		String[] imageFloat = null;
		
		//����һ��ͼƬ��ֱ��ͼ���飨float�ͣ�
		float[] filterData = null;
		//����HashMap���169��ͼƬ��float�����飬�����趨�ö�Ӧ������
		HashMap<Integer, float[]> map = new HashMap<Integer, float[]>();
		
		for(int i=0; i<imageData.length; i++){
			imageFloat = imageData[i].split(" ");
			//����ֱ��ͼ��ֵ�ĳ���
			filterData = new float[imageFloat.length];
			for(int j=0; j<imageFloat.length; j++){
				filterData[j] = Float.parseFloat(imageFloat[j].trim());
			}
			//��ͼƬ��ֱ��ͼ���ݱ��浽map��
			map.put(i, filterData);
		}
		if(flag == 1) {
			//��ȡͼƬ�±�
			HashMap<Double, Integer> mapIndex = new HashMap<Double, Integer>();
			//��ȡ��ѡͼƬ�����ݿ��еİ���ϵ��
			double similaritys[] = new double[matchLength];
			double Reverse[] = new double[matchLength];
			//��ʱ����ͼƬ��һ������
			double[] mixedData = new double[candidateData.length];
			int i=0;
			//�Ƚ�169��ͼƬ�ĳ��ȣ�imageData.length �ı�Ϊ imageIndex.size()
			while(i<matchLength){
				int j=0;
				while(j<candidateData.length){
					mixedData[j] = Math.sqrt(map.get(imageIndex.get(i))[j]* candidateData[j]);
					similaritys[i] += mixedData[j];
					j++;
				}
				Reverse[i] = 1-similaritys[i];
				mapIndex.put(Reverse[i],imageIndex.get(i));
				i++;
			}
			//��ȡ���ֵͼƬ�±�
			Arrays.sort(Reverse);
			//��ȡ���ƶ���ߵ�ͼƬ�±꣬������
			int[] indexValue = new int[7];
			for(int index=0; index<indexValue.length; index++){
				indexValue[index] = mapIndex.get(Reverse[index]);
			}
			//��ʾͼƬ��ֵ
			getReturnList(this,indexValue);
		}else if(flag == 0){
			//��ȡ��ѡͼƬ�����ݿ��еİ���ϵ��
			double similaritys[] = new double[imageData.length];
			double Reverse[] = new double[imageData.length];
			//��ʱ����ͼƬ��һ������
			double[] mixedData = new double[candidateData.length];
			int i=0;
			while(i<imageData.length){
				int j=0;
				while(j<candidateData.length){
					mixedData[j] = Math.sqrt( map.get(i)[j]* candidateData[j]);
					similaritys[i] += mixedData[j];
					j++;
				}
				Reverse[i] = 1-similaritys[i];
				i++;
			}
			
			//��ȡͼƬ�±�
			HashMap<Double, Integer> mapIndex = new HashMap<Double, Integer>();
			for(int index=0; index<Reverse.length; index++){
				mapIndex.put(Reverse[index],index);
			}
			//��ȡ���ֵͼƬ�±�
			Arrays.sort(Reverse);
			//��ȡ���ƶ���ߵ�ͼƬ�±꣬������
			int[] indexValue = new int[7];
			for(int index=0; index<indexValue.length; index++){
				
				indexValue[index] = mapIndex.get(Reverse[index]);
			}
			//��ʾͼƬ��ֵ
			getReturnList(this,indexValue);
		}
		
		/**��ʼ��ͼƬ·��*/
		initImagePath();
	}
	
	@SuppressLint("ShowToast")
	private void getReturnList(Context activity,int[] indexValue) {	
		final double list_screenWidth;//listviewͼƬ���
		final double list_screenHeigth;//listviewͼƬ�߶�
		
		/**����ͼƬ�Ŀ�͸�*/
		DisplayMetrics dm = new DisplayMetrics();
		dm = activity.getResources().getDisplayMetrics();
		list_screenWidth = (dm.widthPixels)/3;
		list_screenHeigth = list_screenWidth / 0.618;
		
		ListView lvReturn = (ListView)findViewById(R.id.lv_return_list);
		
		ArrayList<HashMap<String, Object>> listItem=new ArrayList<HashMap<String, Object>>();  // ListView������Դ��������һ��HashMap���б�
		
		for(int i=0;i<indexValue.length;i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			//��ȡƷ��
	    	map.put("brand", imageData.brand[indexValue[i]]);
	    	//��ȡƷ�ƶ�Ӧ���ͺ�
	    	map.put("item_no",imageData.itemNum[indexValue[i]]);	
	    	//��ȡƷ����װ��Ůװ
	    	map.put("sex",imageData.sex[indexValue[i]]+"װ");
	    	map.put("image_src",imageData.imageRaw[indexValue[i]]);
	    	
	    	listItem.add(map);
		}
		
		//��ȡListView��SimpleAdapter
		SimpleAdapter listItemAdapter = new SimpleAdapter(activity, listItem, R.layout.return_list_item,
				new String[]{"image_src","sex","brand","item_no"}, new int[]{R.id.imv_src,R.id.txt_sex,R.id.txt_brand,R.id.txt_item_no});
		
		listItemAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				   if(view instanceof ImageView ){  
				        ImageView i = (ImageView)view;  
				        i.setImageResource((Integer)data);
						LayoutParams para = i.getLayoutParams();
				 		para.width = (int) list_screenWidth;
					    para.height = (int)list_screenHeigth;
						i.setLayoutParams(para);	
			        return true;  
			    } 
				return false;
			}}); 
		
		lvReturn.setAdapter(listItemAdapter);
		listItemAdapter.notifyDataSetChanged();
	}
	
	public void runAnimation() {
		down = AnimationUtils.loadAnimation(ReturnListActivity.this,
				R.anim.bg_del_down);
		up = AnimationUtils.loadAnimation(ReturnListActivity.this,
				R.anim.bg_del_up);
		down.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
			}
		});

		right = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f,
				Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f);
		left = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 0f);
		right.setDuration(25000);
		left.setDuration(25000);
		right.setFillAfter(true);
		left.setFillAfter(true);

		right.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				runImage.startAnimation(left);
			}
		});
		left.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				runImage.startAnimation(right);
			}
		});
		runImage.startAnimation(right);
	}
	
	//��ȡһ������
	private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //�ر�sso��Ȩ
        oks.disableSSOWhenAuthorize();
        
        // ����ʱNotification��ͼ�������
        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
        oks.setTitle(getString(R.string.share));
        // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
        oks.setTitleUrl("http://tyut.edu.cn");
        // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
        oks.setText("�ҷ�����һ���·�");
        // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
        oks.setImagePath(ReturnListActivity.TEST_IMAGE);
        // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
        oks.setUrl("http://tyut.edu.cn");
        oks.setFilePath(ReturnListActivity.TEST_IMAGE);
        // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
        oks.setComment("�·�����Ŷ");
        // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
        oks.setSite(getString(R.string.app_name));
        // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
        oks.setSiteUrl("http://tyut.edu.cn");

        // ��������GUI
        oks.show(this);
   }
	//�����˵���ť
	//����Menu�˵�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ONE, 0, "����").setIcon(R.drawable.one_share);  //����������ͼ��
		menu.add(0, TWO, 0, "�ղ�").setIcon(R.drawable.get_image);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	 //ѡ��˵����ر��¼����˵����ر����������Σ�menu��ť���ٴε����
		//back��ť����������û�ѡ����ĳһ���˵���
	public void onOptionsMenuClosed(Menu menu) {
	}
	@Override
	//�˵�����ʾ֮ǰ���¼� 
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;  
	}
	//���Menu�˵�ѡ����Ӧ�¼�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 1:
			//����
			showShare();
			break;
		case 2:
			MyToast.toast(ReturnListActivity.this, "�����ղ�");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**��ʼ��ͼƬ��ַ*/
	private void initImagePath() {
		try {
			String cachePath = cn.sharesdk.framework.utils.R.getCachePath(this, null);
			TEST_IMAGE = cachePath + FILE_NAME;
			File file = new File(TEST_IMAGE);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = bitmap;
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch(Throwable t) {
			t.printStackTrace();
			TEST_IMAGE = null;
		}
	}
	
}
