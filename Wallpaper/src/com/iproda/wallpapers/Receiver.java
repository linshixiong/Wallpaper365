package com.iproda.wallpapers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(arg1.getAction())) {
			Intent service = new Intent(
					"com.iproda.wallpapers.WALLPAPER_SERVICE");
			arg0.startService(service);
		}

	}

}
