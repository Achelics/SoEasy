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
	
	//定义相应的图片路径
	private static String TEST_IMAGE;
	private static final String FILE_NAME = "share_lzq.jpg";
	//定义Menu菜单选项的ItemId
	final static int ONE = Menu.FIRST;
	final static int TWO = Menu.FIRST+1;
	//定义要获取的图片
	private Bitmap bitmap;
	//获取图片资源的ID
	ImageData imageData = new ImageData();
	private ImageView runImage;
	//选择图片的标志位
	private int flag;
	TranslateAnimation left, right;
	Animation up, down;
	//定义拍照传递过来的图片值，并且存储到Map中
	Map<Integer, Integer> imageIndex;
	//定义一个要匹配图片的长度
	public static int matchLength;
	/*本地图片的位置*/
	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_return_list);
		runImage = (ImageView) findViewById(R.id.run_image);
		runAnimation();
		//初始化分享功能
		ShareSDK.initSDK(this);
		/**获取传过来的数据*/
		Intent intent=getIntent();
        if(intent!=null){
        	bitmap = intent.getParcelableExtra("bitmap");
        	Bundle bGet = intent.getExtras();
        	flag = bGet.getInt("flag");
        	if(flag == 1){
        		//获取由前一界面传递过来的图片值
            	SerializableMap serializableMap = (SerializableMap) bGet
        				.get("imageIndex");
            	imageIndex = serializableMap.getMap();
            	matchLength = imageIndex.size();
        	}
       }
        HistogramFilter hfilter = new HistogramFilter(flag);
		float[] candidateData = hfilter.filter(bitmap);
		//获取所有图片的直方图数据
		InputStream inputStream = null;
		if(flag == 0){
			inputStream = getResources().openRawResource(R.raw.source_data);  
		}else{
			inputStream = getResources().openRawResource(R.raw.no_source_data);
		}
		    
		String string = TxtReader.getString(inputStream);
		
		//分割169张图片的直方图，每一个字符串为一张图片的直方图数据
		String imageData[] = string.split("@");
		
		//定义一张图片的直方图数组（字符串型）
		String[] imageFloat = null;
		
		//定义一张图片的直方图数组（float型）
		float[] filterData = null;
		//定义HashMap存放169张图片的float型数组，并且设定好对应的索引
		HashMap<Integer, float[]> map = new HashMap<Integer, float[]>();
		
		for(int i=0; i<imageData.length; i++){
			imageFloat = imageData[i].split(" ");
			//声明直方图数值的长度
			filterData = new float[imageFloat.length];
			for(int j=0; j<imageFloat.length; j++){
				filterData[j] = Float.parseFloat(imageFloat[j].trim());
			}
			//将图片的直方图数据保存到map中
			map.put(i, filterData);
		}
		if(flag == 1) {
			//获取图片下标
			HashMap<Double, Integer> mapIndex = new HashMap<Double, Integer>();
			//获取候选图片与数据库中的巴氏系数
			double similaritys[] = new double[matchLength];
			double Reverse[] = new double[matchLength];
			//暂时保存图片的一个数据
			double[] mixedData = new double[candidateData.length];
			int i=0;
			//先将169张图片的长度：imageData.length 改变为 imageIndex.size()
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
			//获取最大值图片下标
			Arrays.sort(Reverse);
			//获取相似度最高的图片下标，即索引
			int[] indexValue = new int[7];
			for(int index=0; index<indexValue.length; index++){
				indexValue[index] = mapIndex.get(Reverse[index]);
			}
			//显示图片的值
			getReturnList(this,indexValue);
		}else if(flag == 0){
			//获取候选图片与数据库中的巴氏系数
			double similaritys[] = new double[imageData.length];
			double Reverse[] = new double[imageData.length];
			//暂时保存图片的一个数据
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
			
			//获取图片下标
			HashMap<Double, Integer> mapIndex = new HashMap<Double, Integer>();
			for(int index=0; index<Reverse.length; index++){
				mapIndex.put(Reverse[index],index);
			}
			//获取最大值图片下标
			Arrays.sort(Reverse);
			//获取相似度最高的图片下标，即索引
			int[] indexValue = new int[7];
			for(int index=0; index<indexValue.length; index++){
				
				indexValue[index] = mapIndex.get(Reverse[index]);
			}
			//显示图片的值
			getReturnList(this,indexValue);
		}
		
		/**初始化图片路径*/
		initImagePath();
	}
	
	@SuppressLint("ShowToast")
	private void getReturnList(Context activity,int[] indexValue) {	
		final double list_screenWidth;//listview图片宽度
		final double list_screenHeigth;//listview图片高度
		
		/**设置图片的宽和高*/
		DisplayMetrics dm = new DisplayMetrics();
		dm = activity.getResources().getDisplayMetrics();
		list_screenWidth = (dm.widthPixels)/3;
		list_screenHeigth = list_screenWidth / 0.618;
		
		ListView lvReturn = (ListView)findViewById(R.id.lv_return_list);
		
		ArrayList<HashMap<String, Object>> listItem=new ArrayList<HashMap<String, Object>>();  // ListView的数据源，这里是一个HashMap的列表
		
		for(int i=0;i<indexValue.length;i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			//获取品牌
	    	map.put("brand", imageData.brand[indexValue[i]]);
	    	//获取品牌对应的型号
	    	map.put("item_no",imageData.itemNum[indexValue[i]]);	
	    	//获取品牌男装和女装
	    	map.put("sex",imageData.sex[indexValue[i]]+"装");
	    	map.put("image_src",imageData.imageRaw[indexValue[i]]);
	    	
	    	listItem.add(map);
		}
		
		//获取ListView的SimpleAdapter
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
	
	//获取一键分享
	private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://tyut.edu.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我分享了一件衣服");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(ReturnListActivity.TEST_IMAGE);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://tyut.edu.cn");
        oks.setFilePath(ReturnListActivity.TEST_IMAGE);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("衣服不错哦");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://tyut.edu.cn");

        // 启动分享GUI
        oks.show(this);
   }
	//创建菜单按钮
	//创建Menu菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ONE, 0, "分享").setIcon(R.drawable.one_share);  //设置文字与图标
		menu.add(0, TWO, 0, "收藏").setIcon(R.drawable.get_image);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	 //选项菜单被关闭事件，菜单被关闭有三种情形，menu按钮被再次点击、
		//back按钮被点击或者用户选择了某一个菜单项
	public void onOptionsMenuClosed(Menu menu) {
	}
	@Override
	//菜单被显示之前的事件 
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;  
	}
	//点击Menu菜单选项响应事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 1:
			//分享
			showShare();
			break;
		case 2:
			MyToast.toast(ReturnListActivity.this, "你已收藏");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**初始化图片地址*/
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
