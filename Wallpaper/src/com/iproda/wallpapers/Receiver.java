package com.iproda.wallpapers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (WallpaperService.ACTION_ALARM_WAKEUP.equals(arg1.getAction())) {
			Intent service = new Intent("com.iproda.wallpapers.WALLPAPER_SERVICE");
			service.putExtra("action", 1);
			arg0.startService(service);
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(arg1.getAction())) {
			//Toast.makeText(arg0, "ACTION_BOOT_COMPLETED", Toast.LENGTH_LONG).show();
			Intent service = new Intent("com.iproda.wallpapers.WALLPAPER_SERVICE");
			service.putExtra("action", 2);
			arg0.startService(service);
		}
	}

}
