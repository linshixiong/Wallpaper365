/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iproda.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.WallpaperManager;

public class Home extends Activity implements OnClickListener {
	/**
	 * Tag used for logging errors.
	 */
	private static final String LOG_TAG = "Home";

	private static boolean mWallpaperChecked;
	private final BroadcastReceiver mWallpaperReceiver = new WallpaperIntentReceiver();
	private boolean mHomeDown;
	private boolean mBackDown;

	private ImageButton buttonApp;
	private ImageButton buttonCol;
	private ImageButton buttonNavi;
	private ImageButton buttonSettings;

	private WallpaperManager wallpaperManager;
	
	
	private LinearLayout layoutMain;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		setContentView(R.layout.home);

		registerIntentReceivers();

		buttonApp = (ImageButton) findViewById(R.id.imageButtonApp);
		buttonApp.setOnClickListener(this);
		buttonCol = (ImageButton) findViewById(R.id.imageButtonCol);
		buttonCol.setOnClickListener(this);
		buttonNavi = (ImageButton) findViewById(R.id.imageButtonNavi);
		buttonNavi.setOnClickListener(this);
		buttonSettings = (ImageButton) findViewById(R.id.imageButtonSettings);
		buttonSettings.setOnClickListener(this);

		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		wallpaperManager = WallpaperManager.getInstance(this);
		wallpaperManager.suggestDesiredDimensions(1024, 1024);
		setDefaultWallpaper();
		
		layoutMain=(LinearLayout)findViewById(R.id.layout_main);
		layoutMain.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
		        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);

		        pickWallpaper.setComponent(new ComponentName(getPackageName(), WallpaperPickerActivity.class.getName()));
		        startActivityForResult(pickWallpaper, 10);
				return false;
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.imageButtonApp:
			// startActivity(new Intent(this, Launcher.class));
			break;
		case R.id.imageButtonCol:
			startWallpaperColActivity();
			break;
		case R.id.imageButtonNavi:
			startNaviActivity();
			break;
		case R.id.imageButtonSettings:
			startSettingsActivity();
			break;
		}

	}

	private void startSettingsActivity() {
		Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			this.startActivity(intent);
		} catch (Exception ex) {

		}

	}

	private void startWallpaperColActivity() {
		Intent intent = new Intent("com.iproda.wallpapers.WALLPAPER_COL");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			this.startActivity(intent);
		} catch (Exception ex) {

		}

	}

	private void startNaviActivity() {
		Intent intent = new Intent("");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			this.startActivity(intent);
		} catch (Exception ex) {

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// Close the menu
		if (Intent.ACTION_MAIN.equals(intent.getAction())) {
			getWindow().closeAllPanels();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mWallpaperReceiver);
	}

	/**
	 * Registers various intent receivers. The current implementation registers
	 * only a wallpaper intent receiver to let other applications change the
	 * wallpaper.
	 */
	private void registerIntentReceivers() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		registerReceiver(mWallpaperReceiver, filter);
	}

	/**
	 * When no wallpaper was manually set, a default wallpaper is used instead.
	 */
	private void setDefaultWallpaper() {
		final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

		getWindow().setBackgroundDrawable(wallpaperDrawable);
	}

	/**
	 * Receives intents from other applications to change the wallpaper.
	 */
	private class WallpaperIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (Intent.ACTION_WALLPAPER_CHANGED.equals(intent.getAction())) {
				setDefaultWallpaper();
			} else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
				getWindow().setBackgroundDrawableResource(
						R.drawable.ic_launcher_home);

			} else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
				Handler handler = new Handler();

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						setDefaultWallpaper();
					}
				}, 2000);
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!hasFocus) {
			mBackDown = mHomeDown = false;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				mBackDown = true;

				return true;
			case KeyEvent.KEYCODE_HOME:

				mHomeDown = true;
				return true;
			}
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				if (!event.isCanceled()) {
					// Do BACK behavior.
				}
				mBackDown = true;
				return true;
			case KeyEvent.KEYCODE_HOME:
				if (!event.isCanceled()) {
					// Do HOME behavior.
				}
				mHomeDown = true;
				// hideApplications();
				return true;
			}
		}

		return super.dispatchKeyEvent(event);
	}

}
