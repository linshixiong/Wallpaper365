package com.iproda.wallpapers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

public class WallpaperService extends Service {

	private static final String TAG = "WallpaperService";

	private final IntentFilter filter = new IntentFilter();

	private WallpaperHelper helper;

	private int mPosition = 0;
	private int hour;
	private int minute;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(receiver, filter);
		helper = new WallpaperHelper(this);

		Log.d(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {

		Intent service = new Intent("com.iproda.wallpapers.WALLPAPER_SERVICE");
		startService(service);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		updateTime();
		setWallpaper();
		return super.onStartCommand(intent, flags, startId);
	}

	private void updateTime() {
		Time t = new Time();
		t.setToNow();
		hour = t.hour;
		minute = t.minute;
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
				updateTime();
				if (hour == 0 && minute == 0) {
					setWallpaper();
				}

			}
		}

	};

	private void setWallpaper() {

		if (needUpdateWallpaper()) {

			mPosition = WallpaperSettings
					.getWallpaperPosition(WallpaperService.this);
			helper.selectWallpaper(mPosition);

			if (mPosition < helper.getImages().size() - 1) {
				mPosition++;

			} else {
				mPosition = 0;
			}
			WallpaperSettings.setWallpaperPosition(WallpaperService.this,
					mPosition);
			WallpaperSettings
					.refreshLastWallpaperUpdateTime(WallpaperService.this);
		}

	}

	public boolean needUpdateWallpaper() {
		String lastUpdateTime = WallpaperSettings
				.getLastWallpaperUpdateTime(WallpaperService.this);
		if (lastUpdateTime != null && !"".equals(lastUpdateTime)) {

			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date lastDate = null;
			try {
				lastDate = formatter.parse(lastUpdateTime);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
			Date nowDate = new Date(System.currentTimeMillis());
			//long diff = nowDate.getTime() - lastDate.getTime();
			//long days = diff / (1000 * 60 * 60 * 24);

			if (nowDate.getYear() > lastDate.getYear()
					|| nowDate.getMonth() > lastDate.getMonth()
					|| nowDate.getDate() > lastDate.getDate()) {
				return true;
			}
			return false;

		} else {
			return true;
		}
	}

}
