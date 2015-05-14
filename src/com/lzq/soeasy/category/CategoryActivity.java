package com.lzq.soeasy.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.lzq.soeasy.R;
import com.lzq.soeasy.returnimage.CategoryReturnActivity;

public class CategoryActivity extends Activity implements OnItemClickListener{
	
	private ImageView runImage;
	TranslateAnimation left, right;
	Animation up, down;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		//��ʼ��GridViewS
		initGridView();
		runImage = (ImageView) findViewById(R.id.run_image);
//		runAnimation();
	}
	
	private void initGridView(){
		//��ȡGridViewֵ
		GridView categoryGrid = (GridView) findViewById(R.id.grid_category);
		int[] brandValues = {R.drawable.whitehouse,R.drawable.jackjones,R.drawable.only,R.drawable.selected,R.drawable.veromoda,R.drawable.zara};
	
		List<HashMap<String, Object>> grdImageItem = new ArrayList<HashMap<String, Object>>();  
	 	for(int i=0; i<brandValues.length; i++){		 		
	 		  HashMap<String, Object> map = new HashMap<String, Object>(); 
		      map.put("brand", brandValues[i]);//���ͼ����Դ��ID  
		      grdImageItem.add(map); 
	 	}
	 	SimpleAdapter saImageItems = new SimpleAdapter(this, //ûʲô����  
				grdImageItem,//������Դ   
                R.layout.category_grid_item,//night_item��XMLʵ��  
                //��̬������ImageItem��Ӧ������          
                new String[]{"brand"},   
                //ImageItem��XML�ļ������һ��ImageView,����TextView ID  
                new int[] {R.id.imgView}  );  
				//��Ӳ�����ʾ  
	 	categoryGrid.setAdapter(saImageItems);  
				//�����Ϣ����  
	 	
	 	categoryGrid.setOnItemClickListener(this);
	}
	
	//�����Ӧ��Ʒ�ƺţ������Ӧ��Ʒ��Ԥ��
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
		Intent it = new Intent(CategoryActivity.this, CategoryReturnActivity.class);
		Bundle bundle=new Bundle();
		bundle.putInt("brandId",arg2);
		it.putExtras(bundle);
		startActivity(it);
	}

	public void runAnimation() {
		down = AnimationUtils.loadAnimation(CategoryActivity.this,
				R.anim.bg_del_down);
		up = AnimationUtils.loadAnimation(CategoryActivity.this,
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
