package com.iproda.wallpapers;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (WallpaperService.ACTION_ALARM_WAKEUP.equals(arg1.getAction())) {
			Intent service = new Intent("com.iproda.wallpapers.WALLPAPER_SERVICE");
			service.putExtra("action", 1);
			arg0.startService(service);
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(arg1.getAction())) {
			setAlarm(arg0);
		}
	}

	public static void setAlarm(Context context) {
		Intent intent = new Intent(WallpaperService.ACTION_ALARM_WAKEUP);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, sender);

	}

}
