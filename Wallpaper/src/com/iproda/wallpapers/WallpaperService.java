package com.iproda.wallpapers;

import java.util.Calendar;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Time;
import android.widget.Toast;

public class WallpaperService extends Service {

	private final IntentFilter filter = new IntentFilter();

	private WallpaperHelper helper;

	private int mPosition = 0;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		filter.addAction(Intent.ACTION_SCREEN_ON);
		// filter.addAction(Intent.ACTION_DATE_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(receiver, filter);
		helper = new WallpaperHelper(this);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	private void updateTime() {
		Time t = new Time();
		t.setToNow(); // 取得系统时间。
		year = t.year;
		month = t.month;
		day = t.monthDay;
		hour = t.hour; // 0-23
		minute = t.minute;
		second = t.second;

	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
				updateTime();
				//if (hour == 0 && minute == 0) {

					mPosition=WallpaperSettings.getWallpaperPosition(WallpaperService.this);
					helper.selectWallpaper(mPosition);

					if (mPosition < helper.getImages().size() - 1) {
						mPosition++;

					} else {
						mPosition = 0;
					}
					WallpaperSettings.setWallpaperPosition(WallpaperService.this, mPosition);
				//}

			}
		}

	};

}
