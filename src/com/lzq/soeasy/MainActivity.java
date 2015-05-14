package com.lzq.soeasy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class MainActivity extends RelativeLayout{
	private Context mContext;
	private Scroller mScroller;
	private int mScreenHeigh = 0;
	private int mLastDownY = 0;
	private int mCurryY;
	private int mDelY;
	private boolean mCloseFlag = false;
	private ImageView mImgView;

	public MainActivity(Context context) {
		super(context);
		mContext = context;
		setupView();
	}

	public MainActivity(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setupView();
	}

	@SuppressLint("NewApi")
	private void setupView() {

		// ���Interpolator��������ñ�� ������ѡ������е���Ч����Interpolator
		Interpolator polator = new BounceInterpolator();
		mScroller = new Scroller(mContext, polator);

		// ��ȡ��Ļ�ֱ���
		WindowManager wm = (WindowManager) (mContext
				.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenHeigh = dm.heightPixels;
		// ������һ��Ҫ���ó�͸������,��Ȼ��Ӱ���㿴���ײ㲼��
		this.setBackgroundColor(Color.argb(0, 0, 0, 0));
		mImgView = new ImageView(mContext);
		mImgView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		mImgView.setScaleType(ImageView.ScaleType.FIT_XY);// ���������Ļ
		mImgView.setImageResource(R.drawable.splash_bg); // Ĭ�ϱ���
		addView(mImgView);
	}

	// �����ƶ��ű���
	public void setBgImage(int id) {
		mImgView.setImageResource(id);
	}

	// �����ƶ��ű���
	public void setBgImage(Drawable drawable) {
		mImgView.setImageDrawable(drawable);
	}

	// �ƶ��ŵĶ���
	public void startBounceAnim(int startY, int dy, int duration) {
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastDownY = (int) event.getY();
			System.err.println("ACTION_DOWN=" + mLastDownY);
			return true;
		case MotionEvent.ACTION_MOVE:
			mCurryY = (int) event.getY();
			System.err.println("ACTION_MOVE=" + mCurryY);
			mDelY = mCurryY - mLastDownY;
			// ֻ׼�ϻ���Ч
			if (mDelY < 0) {
				scrollTo(0, -mDelY);
			}
			break;
		case MotionEvent.ACTION_UP:
			mCurryY = (int) event.getY();
			mDelY = mCurryY - mLastDownY;
			if (mDelY < 0) {
				if (Math.abs(mDelY) > mScreenHeigh / 5) {
					// ���ϻ�������1/5����Ļ�ߵ�ʱ�� ����������ʧ����
					startBounceAnim(this.getScrollY(), mScreenHeigh, 450);
					mCloseFlag = true;
				} else {
					// ���ϻ���δ����1/5����Ļ�ߵ�ʱ�� �������µ�������
					startBounceAnim(this.getScrollY(), -this.getScrollY(), 500);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {

		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			Log.i("scroller", "getCurrX()= " + mScroller.getCurrX()
					+ "     getCurrY()=" + mScroller.getCurrY()
					+ "  getFinalY() =  " + mScroller.getFinalY());
			// ��Ҫ���Ǹ��½���
			postInvalidate();
		} else {
			if (mCloseFlag) {
				this.setVisibility(View.GONE);
			}
		}
	}

}
