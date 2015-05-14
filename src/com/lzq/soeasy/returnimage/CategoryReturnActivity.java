package com.lzq.soeasy.returnimage;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.lzq.soeasy.R;
import com.lzq.soeasy.imagedata.ImageData;

public class CategoryReturnActivity extends Activity{
	
	private ImageView runImage;
	TranslateAnimation left, right;
	Animation up, down;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_return_list);
		Bundle bundle =getIntent().getExtras();	
        int brandId=bundle.getInt("brandId");	
        //品牌预览
		getReturnList(this,brandId);
		runImage = (ImageView) findViewById(R.id.run_image);
		runAnimation();
	}
	
	private void getReturnList(Context activity,int brandId) {	
		ImageData imageData = new ImageData();
		final double list_screenWidth;//listview图片宽度
		final double list_screenHeigth;//listview图片高度
		
		/**设置图片的宽和高*/
		DisplayMetrics dm = new DisplayMetrics();
		dm = activity.getResources().getDisplayMetrics();
		list_screenWidth = (dm.widthPixels)/3;
		list_screenHeigth = list_screenWidth / 0.618;
		
		ListView lvReturn = (ListView)findViewById(R.id.lv_return_list);
		
		ArrayList<HashMap<String, Object>> listItem=new ArrayList<HashMap<String, Object>>();  // ListView的数据源，这里是一个HashMap的列表
		switch(brandId){
		case 0:
			for(int i=0;i<imageData.basichouse.length;i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			//获取品牌
	    	map.put("brand", imageData.brand[i]);
	    	map.put("sex","女装");
	    	//获取品牌对应的型号
	    	map.put("item_no",imageData.itemNum[i]);	
	    	map.put("image_src",imageData.basichouse[i]);
	    	listItem.add(map);
		}
			break;
		case 1:
			for(int i=0;i<imageData.jackjones.length;i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				//获取品牌
		    	map.put("brand", imageData.brand[i+13]);
		    	map.put("sex","男装");
		    	//获取品牌对应的型号
		    	map.put("item_no",imageData.itemNum[i+13]);	
		    	map.put("image_src",imageData.jackjones[i]);
		    	listItem.add(map);
			}
			break;
		case 2:
			for(int i=0;i<imageData.only.length;i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				//获取品牌
		    	map.put("brand", imageData.brand[i+55]);
		    	map.put("sex","女装");
		    	//获取品牌对应的型号
		    	map.put("item_no",imageData.itemNum[i+55]);	
		    	map.put("image_src",imageData.only[i]);
		    	listItem.add(map);
			}
			break;
		case 3:
			for(int i=0;i<imageData.selected.length;i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				//获取品牌
		    	map.put("brand", imageData.brand[i+81]);
		    	map.put("sex","男装");
		    	//获取品牌对应的型号
		    	map.put("item_no",imageData.itemNum[i+81]);	
		    	map.put("image_src",imageData.selected[i]);
		    	listItem.add(map);
			}
			break;
		case 4:
			for(int i=0;i<imageData.veromode.length;i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				//获取品牌
		    	map.put("brand", imageData.brand[i+102]);
		    	map.put("sex","女装");
		    	//获取品牌对应的型号
		    	map.put("item_no",imageData.itemNum[i+102]);	
		    	map.put("image_src",imageData.veromode[i]);
		    	listItem.add(map);
			}
			break;
		case 5:
			for(int i=0;i<imageData.zara.length;i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				//获取品牌
		    	map.put("brand", imageData.brand[i+132]);
		    	map.put("sex","男装");
		    	//获取品牌对应的型号
		    	map.put("item_no",imageData.itemNum[i+132]);	
		    	map.put("image_src",imageData.zara[i]);
		    	listItem.add(map);
			}
			break;
		default:
			break;
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
		down = AnimationUtils.loadAnimation(CategoryReturnActivity.this,
				R.anim.bg_del_down);
		up = AnimationUtils.loadAnimation(CategoryReturnActivity.this,
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
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				runImage.startAnimation(right);
			}
		});
		runImage.startAnimation(right);
	}
}
