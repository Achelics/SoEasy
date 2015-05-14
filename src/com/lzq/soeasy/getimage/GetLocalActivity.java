package com.lzq.soeasy.getimage;

import com.lzq.soeasy.R;
import com.lzq.soeasy.returnimage.ReturnListActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class GetLocalActivity extends Activity {
	private ImageView return_image;
	private ImageView match_start;
	private Bitmap bitmap;
	//选择对应的值进行传递
	private int flag;
	protected ImageView runImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_local);
		
		return_image = (ImageView) findViewById(R.id.return_image);
		match_start = (ImageView) findViewById(R.id.match_start);
		Intent intent=getIntent();
        if(intent!=null){
        	bitmap =intent.getParcelableExtra("bitmap");
        	Bundle bGet = intent.getExtras();
        	flag = bGet.getInt("flag");
            return_image.setImageBitmap(bitmap);
            
            runImage = (ImageView) findViewById(R.id.run_image);
        }
        
        match_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(GetLocalActivity.this, ReturnListActivity.class);
				Bundle bundle = new Bundle();
				it.putExtra("bitmap", bitmap);
				bundle.putInt("flag", flag);
				it.putExtras(bundle);
				startActivity(it);
			}
		});       
        
	}
}
