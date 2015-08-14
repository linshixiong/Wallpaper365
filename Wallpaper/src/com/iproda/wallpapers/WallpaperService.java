package com.iproda.wallpapers;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class WallpaperService extends Service {

	private static final String TAG = "WallpaperService";
	public static final String ACTION_ALARM_WAKEUP = "com.iproda.wallpapers.ACTION_ALARM_WAKEUP";

	private int mPosition = 0;
	private IntentFilter filter;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_DATE_CHANGED);
		registerReceiver(receiver, filter);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		int action = intent.getIntExtra("action", 0);
		switch (action) {
		case 1:
			setWallpaper();
			break;
		case 2:
			setAlarm();
			break;
		}

		return super.onStartCommand(intent, Service.START_STICKY, startId);
	}

	public void setAlarm() {

		Intent intent = new Intent(WallpaperService.ACTION_ALARM_WAKEUP);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		AlarmManager manager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

	}

	private void setWallpaper() {

		if (WallpaperSettings.isWallpaperChangerOn(this)) {
			mPosition = WallpaperSettings.getWallpaperPosition(WallpaperService.this);
			WallpaperHelper helper = new WallpaperHelper(this);
			if (mPosition < helper.getImages().size() - 1) {
				mPosition++;

			} else {
				mPosition = 0;
			}

			helper.selectWallpaper(mPosition);

			WallpaperSettings.setWallpaperPosition(WallpaperService.this, mPosition);
			setAlarm();
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if (Intent.ACTION_DATE_CHANGED.equals(arg1.getAction())) {

			}
		}

	};
}
