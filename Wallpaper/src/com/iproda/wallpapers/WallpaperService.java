package com.iproda.wallpapers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class WallpaperService extends Service {

	private final IntentFilter filter = new IntentFilter();

	private WallpaperHelper helper;
	
	private int mPosition=0;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		filter.addAction(Intent.ACTION_SCREEN_ON);
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

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			
			helper.selectWallpaper(mPosition);
			
			if(mPosition<helper.getImages().size()-1){
				mPosition++;
				
			}else{
				mPosition=0;
			}
			}
		
	};

}
