package com.iproda.wallpapers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WallpaperService extends Service {

	private static final String TAG = "WallpaperService";
	public static final String ACTION_ALARM_WAKEUP = "com.iproda.wallpapers.ACTION_ALARM_WAKEUP";

	private int mPosition = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		int action = intent.getIntExtra("action", 0);
		if (action == 1) {
			setWallpaper();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void setWallpaper() {

		mPosition = WallpaperSettings.getWallpaperPosition(WallpaperService.this);
		WallpaperHelper helper = new WallpaperHelper(this);
		helper.selectWallpaper(mPosition);

		if (mPosition < helper.getImages().size() - 1) {
			mPosition++;

		} else {
			mPosition = 0;
		}
		WallpaperSettings.setWallpaperPosition(WallpaperService.this, mPosition);

	}

}
