package com.iproda.wallpapers;

import android.content.Context;
import android.provider.Settings;

public class WallpaperSettings {

	public static int getWallpaperPosition(Context context) {
		return Settings.System.getInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_POSITION", 0);
	}

	public static void setWallpaperPosition(Context context, int position) {
		Settings.System.putInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_POSITION", position);

		int collectionCount = getWallpaperCollectionCount(context);
		if (collectionCount < context.getResources().getStringArray(R.array.wallpapers).length) {
			setWallpaperCollectionCount(context, position + 1);
		}
	}

	public static boolean isWallpaperChangerOn(Context context) {
		return Settings.System.getInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_CHANGER_ON",
				1) == 1;

	}

	public static void setWallpaperChangerOn(Context context, boolean on) {
		Settings.System.putInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_CHANGER_ON", on ? 1 : 0);

	}

	public static int getWallpaperCollectionCount(Context context) {
		return Settings.System.getInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_COL_COUNT", 0);
	}

	public static void setWallpaperCollectionCount(Context context, int count) {
		Settings.System.putInt(context.getContentResolver(), "com.iproda.wallpapers.WALLPAPER_COL_COUNT", count);

	}

}
