/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iproda.wallpapers;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.iproda.wallpapers.R;

public class WallpaperList extends Activity {

	private GridView mGallery;

	private WallpaperHelper helper;
	private boolean isWallpaperChangerOn;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		helper = new WallpaperHelper(this);

		setContentView(R.layout.wallpaper);
		isWallpaperChangerOn = WallpaperSettings.isWallpaperChangerOn(this);
		mGallery = (GridView) findViewById(R.id.gallery);
		Drawable drawable = getResources().getDrawable(R.drawable.grid_selector_background);
		mGallery.setSelector(drawable);
		mGallery.setAdapter(new ImageAdapter(this));

		mGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent("com.iproda.wallpapers.WALLPAPER_VIEW");
				intent.putExtra("position", position);
				startActivity(intent);
			}
		});

		/*
		 * Intent service = new
		 * Intent("com.iproda.wallpapers.WALLPAPER_SERVICE");
		 * service.putExtra("action", 1); startService(service);
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		MenuItem item = menu.getItem(0);
		item.setChecked(isWallpaperChangerOn);
		item.setTitle(isWallpaperChangerOn ? R.string.swicther_on : R.string.swicther_off);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.switcher) {
			item.setChecked(!item.isChecked());

			isWallpaperChangerOn = item.isChecked();
			WallpaperSettings.setWallpaperChangerOn(this, isWallpaperChangerOn);
			item.setTitle(isWallpaperChangerOn ? R.string.swicther_on : R.string.swicther_off);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		ImageAdapter(WallpaperList context) {
			mLayoutInflater = context.getLayoutInflater();
		}

		@Override
		public int getCount() {
			return WallpaperSettings.getWallpaperCollectionCount(WallpaperList.this);
			// return 365;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout layout;

			if (convertView == null) {
				layout = (RelativeLayout) mLayoutInflater.inflate(R.layout.wallpaper_item, parent, false);
			} else {
				layout = (RelativeLayout) convertView;
			}

			int thumbRes = helper.getThumbs().get(position);
			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(thumbRes);
			return layout;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

}
