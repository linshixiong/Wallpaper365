package com.iproda.wallpapers;

import java.io.IOException;
import java.util.ArrayList;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;

public class WallpaperHelper {

	private Context mContext;

	private ArrayList<Integer> mThumbs;
	private ArrayList<Integer> mImages;

	private WallpaperManager wallpaperManager;

	public WallpaperHelper(Context context) {
		this.mContext = context;
		wallpaperManager = WallpaperManager.getInstance(mContext);
		findWallpapers();
	}

	public ArrayList<Integer> getThumbs() {

		return mThumbs;
	}

	public ArrayList<Integer> getImages() {

		return mImages;
	}

	private void findWallpapers() {

		final Resources resources = mContext.getResources();
		final String packageName = mContext.getApplicationContext().getPackageName();
		final int arrayLength = resources.getStringArray(R.array.wallpapers).length;
		mThumbs = new ArrayList<Integer>(arrayLength);
		mImages = new ArrayList<Integer>(arrayLength);

		addWallpapers(resources, packageName, R.array.wallpapers);

	}

	private void addWallpapers(Resources resources, String packageName, int list) {
		final String[] extras = resources.getStringArray(list);
		for (String extra : extras) {
			int res = resources.getIdentifier(extra, "drawable", packageName);
			if (res != 0) {
				final int thumbRes = resources.getIdentifier(extra + "_small", "drawable", packageName);

				if (thumbRes != 0) {
					mThumbs.add(thumbRes);
					mImages.add(res);
				}
			}
		}
	}
	public void selectWallpaperResource(int resource) {
		wallpaperManager.suggestDesiredDimensions(1024, 600);

		try {
			wallpaperManager.setResource(resource);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	public void selectWallpaper(int position) {
		selectWallpaperResource(mImages.get(position));
		/*
		wallpaperManager.suggestDesiredDimensions(1024, 600);

		try {
			wallpaperManager.setResource(mImages.get(position));
		} catch (IOException e) {

			e.printStackTrace();
		}*/
	}

}
