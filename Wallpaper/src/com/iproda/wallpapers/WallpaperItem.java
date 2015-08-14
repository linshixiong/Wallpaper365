package com.iproda.wallpapers;


import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class WallpaperItem extends Activity implements ViewSwitcher.ViewFactory {
	private ImageSwitcher mSwitcher;

	private int mPosition = 0;
	private GestureDetector mGestureDetector;

	private Handler _handle;
	private Runnable _runable;

	private static final float HORIZONTAL_SCROLL_DISTANCE = 10f;

	private List<Integer> mImageIds = null;

	private View mDecorView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wallpaper_item_view);
		mDecorView = getWindow().getDecorView();

		hideSystemUI();
		mSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitch);
		mSwitcher.setFactory(this);
		WallpaperHelper helper = new WallpaperHelper(this);

		mImageIds = helper.getImages().subList(0, WallpaperSettings.getWallpaperCollectionCount(this));

		setupOnTouchListeners(findViewById(R.id.rootview));
		mPosition = getIntent().getIntExtra("position", 0);
		mSwitcher.setImageResource(mImageIds.get(mPosition));
		_handle = new Handler();
		_runable = new Runnable() {
			@Override
			public void run() {
				if (mPosition == (mImageIds.size() - 1)) {
					// Toast.makeText(WallpaperItem.this, "最后一张", 0).show();
				} else {
					mSwitcher.setInAnimation(AnimationUtils.loadAnimation(WallpaperItem.this, R.anim.slide_in_right));
					mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(WallpaperItem.this, R.anim.slide_out_left));
					mSwitcher.setImageResource(mImageIds.get(++mPosition));

					_handle.postDelayed(_runable, 3000);
				}
			}
		};
	}

	private void setupOnTouchListeners(View rootView) {
		mGestureDetector = new GestureDetector(this, new MyGestureListener());

		OnTouchListener rootListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mGestureDetector.onTouchEvent(event);
				return true;
			}
		};

		rootView.setOnTouchListener(rootListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		_handle.removeCallbacks(_runable);
	}

	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (Math.abs(velocityY) <= Math.abs(velocityX) && Math.abs(velocityX) > HORIZONTAL_SCROLL_DISTANCE) {

				hideSystemUI();

				if (velocityX > 0) {
					if (mPosition > 0) {
						_handle.removeCallbacks(_runable);

						mSwitcher
								.setInAnimation(AnimationUtils.loadAnimation(WallpaperItem.this, R.anim.slide_in_left));
						mSwitcher.setOutAnimation(
								AnimationUtils.loadAnimation(WallpaperItem.this, R.anim.slide_out_right));
						mSwitcher.setImageResource(mImageIds.get(--mPosition));

					}
				} else {
					if (mPosition < (mImageIds.size() - 1)) {
						_handle.removeCallbacks(_runable);

						mSwitcher.setInAnimation(
								AnimationUtils.loadAnimation(WallpaperItem.this, R.anim.slide_in_right));
						mSwitcher.setOutAnimation(
								AnimationUtils.loadAnimation(WallpaperItem.this, R.anim.slide_out_left));
						mSwitcher.setImageResource(mImageIds.get(++mPosition));

					} else if (mPosition == (mImageIds.size() - 1)) {
						_handle.removeCallbacks(_runable);

						return true;
					}
				}
			}

			return true;
		}

	}

	@Override
	public View makeView() {
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_XY);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		return i;
	}

	private void hideSystemUI() {

		mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);

	}

	private void showSystemUI() {
		mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

	}
}