package com.lzq.soeasy.home;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.lzq.soeasy.R;
import com.lzq.soeasy.category.CategoryActivity;
import com.lzq.soeasy.db.ResourceData;
import com.lzq.soeasy.getimage.GetImageActivity;
import com.lzq.soeasy.getimage.GetLocalActivity;
import com.lzq.soeasy.info.InfoActivity;
import com.lzq.soeasy.photo.CompressPhoto;
import com.lzq.soeasy.updatesplash.UpdateDataActivity;
import com.lzq.soeasy.util.MyToast;

public class HomeActivity extends Activity implements OnClickListener{
	
	//data shows
	private static final String TAG = "HomeActivity";
	private static final int CHOOSE_SMALL_PICTURE = 0;
	private static final int TAKE_SMALL_PICTURE = 1;
	private static final int CROP_SMALL_PICTURE = 2;
	private Uri imageUri;//to store the big bitmap
	@SuppressLint("SdCardPath")
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	
	private ImageView dataUpdate; //数据更新
	private ImageView information;//信息回收
	private ImageView brandView;//品牌预览
	private ImageView direct_shot;//直接搜索
	private Bitmap myBitmap;
	private ImageView runImage;
	TranslateAnimation left, right;
	Animation up, down;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//初始化四个匹配按钮
		initView();
		
		//instantiate
		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
	}
	
	private void initView(){
		dataUpdate=(ImageView)findViewById(R.id.data_update);
		information=(ImageView)findViewById(R.id.id_information);
		brandView=(ImageView)findViewById(R.id.brand_view);
		direct_shot=(ImageView)findViewById(R.id.direct_shot);
		
		dataUpdate.setOnClickListener(this);
		information.setOnClickListener(this);
		brandView.setOnClickListener(this);
		direct_shot.setOnClickListener(this);
		
		runImage = (ImageView) findViewById(R.id.run_image);
		runAnimation();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**
		 * 因为两种方式都用到了startActivityForResult方法，
		 * 这个方法执行完后都会执行onActivityResult方法， 所以为了区别到底选择了那个方式获取图片要进行判断，
		 * 这里的requestCode跟startActivityForResult里面第二个参数对应
		 */
		switch(requestCode) {
		case CHOOSE_SMALL_PICTURE:
			ContentResolver resolver = getContentResolver();
	        //照片的原始资源地址
			Uri originalUri = data.getData(); 
         	Bitmap photo;
			try {
				photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
				if (photo != null) {
		    		//为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
		    	    myBitmap = scaleDownBitmap(photo,100,this);
		    		//释放原始图片占用的内存，防止out of memory异常发生
		            photo.recycle();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	Intent chooseIt = new Intent(HomeActivity.this, GetLocalActivity.class);
	    	chooseIt.putExtra("bitmap", CompressPhoto.compressImage(myBitmap));
	    	//if it's the localfile picture,transfer 0 to the next activity
	    	Bundle localBundle = new Bundle();
	    	localBundle.putInt("flag", CHOOSE_SMALL_PICTURE);
			chooseIt.putExtras(localBundle);
			startActivity(chooseIt);
			break;
		case TAKE_SMALL_PICTURE:
			Log.i(TAG, "TAKE_SMALL_PICTURE: data = " + data);
			//sent to crop
			cropImageUri(imageUri, 200, 300, CROP_SMALL_PICTURE);
			break;
		case CROP_SMALL_PICTURE://from crop_big_picture
			Log.d(TAG, "CROP_BIG_PICTURE: data = " + data);//it seems to be null
			if(imageUri != null){
				Bitmap bitmap = decodeUriAsBitmap(imageUri);
				Intent it = new Intent(HomeActivity.this, GetImageActivity.class);
				it.putExtra("bitmap", bitmap);
				//if it's the take photo,transfer 1 to the next activity
				Bundle bundle = new Bundle();
				bundle.putInt("flag", TAKE_SMALL_PICTURE);
				it.putExtras(bundle);
				startActivity(it);
			}else{
				Log.e(TAG, "CROP_SMALL_PICTURE: data = " + data);
			}
			break;
		default:
			break;
		}
	}
	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		if(v == dataUpdate){
			if (ResourceData.isNetworkConnected(getApplicationContext())){
				Intent itBrand = new Intent(HomeActivity.this,UpdateDataActivity.class);
				startActivity(itBrand);
			}else{
				MyToast.toast(getApplicationContext(), "网络未连接");
			}
			
		}else if(v == information){
			Intent info = new Intent(HomeActivity.this,InfoActivity.class);
			startActivity(info);
		}else if(v == brandView){
			Intent itBrand = new Intent(HomeActivity.this,CategoryActivity.class);
			startActivity(itBrand);
		}else if(v == direct_shot){
			final CharSequence[] items = {"从相册选择", "直接拍照" };
			AlertDialog dlg = new AlertDialog.Builder(HomeActivity.this).setTitle("选择图片").setItems(items,
					new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,
						int item) {
					// 这里item是根据选择的方式，
					// 在items数组里面定义了两种方式，拍照的下标为1所以就调用拍照方法
					if (item == 1) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
						intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						startActivityForResult(intent, TAKE_SMALL_PICTURE);
					} else {
						Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
				        openAlbumIntent.setType("image/*");
				        openAlbumIntent.putExtra("scale", true);
				        openAlbumIntent.putExtra("return-data", true);
				        openAlbumIntent.putExtra("noFaceDetection", true); // no face detection
				        startActivityForResult(openAlbumIntent, CHOOSE_SMALL_PICTURE);
					}
				}
	}).create();
	dlg.show();
		}
	}
	public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

		 final float densityMultiplier = context.getResources().getDisplayMetrics().density;        

		 int h= (int) (newHeight*densityMultiplier);
		 int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

		 photo=Bitmap.createScaledBitmap(photo, w, h, true);

		 return photo;
	}
	
	//Uri transfer to the Bitmap
	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	//get the camera to take photo
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 3);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}
	
	public void runAnimation() {
		down = AnimationUtils.loadAnimation(HomeActivity.this,
				R.anim.bg_del_down);
		up = AnimationUtils.loadAnimation(HomeActivity.this,
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
}
