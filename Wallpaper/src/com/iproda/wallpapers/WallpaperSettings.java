package com.iproda.wallpapers;

import android.content.Context;
import android.provider.Settings;

public class WallpaperSettings {

	public static int getWallpaperPosition(Context context) {
		return Settings.System.getInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_POSITION", 0);
	}

	public static void setWallpaperPosition(Context context, int position) {
		Settings.System.putInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_POSITION", position);

	}

	public static boolean isWallpaperChangerOn(Context context) {
		return Settings.System.getInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_CHANGER_ON",
				1) == 1;

	}

	public static void setWallpaperChangerOn(Context context, boolean on) {
		Settings.System.putInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_CHANGER_ON", on ? 1 : 0);

	}
}
