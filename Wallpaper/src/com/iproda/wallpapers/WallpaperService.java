package com.iproda.wallpapers;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class WallpaperService extends Service {

	private static final String TAG = "WallpaperService";
	public static final String ACTION_ALARM_WAKEUP = "com.iproda.wallpapers.ACTION_ALARM_WAKEUP";
	private final IntentFilter filter = new IntentFilter();

	private WallpaperHelper helper;

	private int mPosition = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(ACTION_ALARM_WAKEUP);
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
		setAlarm();
		return super.onStartCommand(intent, flags, startId);
	}

	private void setAlarm() {
		Intent intent = new Intent(ACTION_ALARM_WAKEUP);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		manager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, sender);

	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (WallpaperService.ACTION_ALARM_WAKEUP.equals(intent.getAction())) {
				setWallpaper();
			}
		}

	};

	private void setWallpaper() {

		mPosition = WallpaperSettings
				.getWallpaperPosition(WallpaperService.this);
		helper.selectWallpaper(mPosition);

		if (mPosition < helper.getImages().size() - 1) {
			mPosition++;

		} else {
			mPosition = 0;
		}
		WallpaperSettings
				.setWallpaperPosition(WallpaperService.this, mPosition);
		WallpaperSettings.refreshLastWallpaperUpdateTime(WallpaperService.this);

	}

}
