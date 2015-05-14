package com.lzq.soeasy.updatesplash;

import com.lzq.soeasy.R;
import com.lzq.soeasy.db.DBHelper;
import com.lzq.soeasy.db.DataBaseTaskBase;
import com.lzq.soeasy.db.ResourceData;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class UpdateDataActivity extends Activity {
	private SQLiteDatabase mDatabase;
	private ImageView runImage;
	TranslateAnimation left, right;
	Animation up, down;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_data);
		
		ProgressBar pbGetData=(ProgressBar)findViewById(R.id.pb_getdata);
		DBHelper mDBHelper = new DBHelper(this,ResourceData.databasename);  
		
 			mDatabase=mDBHelper.getWritableDatabase();
          	DataBaseTaskBase mDataBaseTaskBase=new DataBaseTaskBase(UpdateDataActivity.this,pbGetData);
        	mDataBaseTaskBase.execute(mDatabase);	
        	
        	runImage = (ImageView) findViewById(R.id.run_image);
    		runAnimation();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
	  	    finish();
			return true;
	}
		return false;
	}
	
	public void runAnimation() {
		down = AnimationUtils.loadAnimation(UpdateDataActivity.this,
				R.anim.bg_del_down);
		up = AnimationUtils.loadAnimation(UpdateDataActivity.this,
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
