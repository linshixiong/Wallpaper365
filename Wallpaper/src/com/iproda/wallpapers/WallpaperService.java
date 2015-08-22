package com.iproda.wallpapers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import android.widget.Toast;

public class WallpaperService extends Service {

	private static final String TAG = "WallpaperService";
	public static final String ACTION_ALARM_WAKEUP = "com.iproda.wallpapers.ACTION_ALARM_WAKEUP";

	private int mPosition = 0;
	private PendingIntent sender;
	private AlarmManager manager;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Intent intent = new Intent(WallpaperService.ACTION_ALARM_WAKEUP);
		sender = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		manager = (AlarmManager) this.getSystemService(ALARM_SERVICE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		this.registerReceiver(receiver, filter);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String action = arg1.getAction();

			if (Intent.ACTION_TIME_CHANGED.equals(action)
					|| Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {

				setAlarm();
			}

		}

	};

	@Override
	public void onDestroy() {

		super.onDestroy();
		this.unregisterReceiver(receiver);
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
			if (needUpdateWallpaper()) {
				setWallpaper();
			}
		case 3:
			calcelAlarm();
			break;
		case 4:
			setAlarm();
			break;
		}

		return super.onStartCommand(intent, Service.START_STICKY, startId);
	}

	public void setAlarm() {

		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

	}

	public void calcelAlarm() {
		manager.cancel(sender);
	}

	private void setWallpaper() {

		if (WallpaperSettings.isWallpaperChangerOn(this)) {
			mPosition = WallpaperSettings
					.getWallpaperPosition(WallpaperService.this);
			WallpaperHelper helper = new WallpaperHelper(this);
			if (mPosition < helper.getImages().size() - 1) {
				mPosition++;

			} else {
				mPosition = 0;
			}

			helper.selectWallpaper(mPosition);

			WallpaperSettings.setWallpaperPosition(WallpaperService.this,
					mPosition);
			WallpaperSettings
					.refreshLastWallpaperUpdateTime(WallpaperService.this);

			setAlarm();
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

			if (nowDate.getYear() > lastDate.getYear()
					|| nowDate.getMonth() > lastDate.getMonth()
					|| nowDate.getDate() > lastDate.getDate()) {
				return true;
			}
			return false;

		} else {
			WallpaperSettings
					.refreshLastWallpaperUpdateTime(WallpaperService.this);
			return false;
		}
	}

}
