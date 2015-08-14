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

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
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
import android.widget.Toast;

import com.iproda.wallpapers.R;

public class WallpaperList extends Activity implements
		GridView.OnItemClickListener {

	private GridView mGallery;

	private WallpaperHelper helper;
	private Bitmap mBitmap;
	private int mGalleryItemBackground;
	private WallpaperLoader mLoader;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		helper = new WallpaperHelper(this);

		setContentView(R.layout.wallpaper);

		mGallery = (GridView) findViewById(R.id.gallery);
		mGallery.setAdapter(new ImageAdapter(this));

		mGallery.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(WallpaperList.this, "onItemClick:+" + arg2,
						Toast.LENGTH_SHORT).show();
			}
		});	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mLoader != null
				&& mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
			mLoader.cancel(true);
			mLoader = null;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onItemClick:+" + arg2, Toast.LENGTH_SHORT).show();
	}

	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		ImageAdapter(WallpaperList context) {
			mLayoutInflater = context.getLayoutInflater();
		}

		@Override
		public int getCount() {
			return WallpaperSettings
					.getWallpaperCollectionCount(WallpaperList.this);
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;

			if (convertView == null) {
				image = (ImageView) mLayoutInflater.inflate(
						R.layout.wallpaper_item, parent, false);
			} else {
				image = (ImageView) convertView;
			}

			int thumbRes = helper.getThumbs().get(position);
			image.setImageResource(thumbRes);

			// image.setAdjustViewBounds(false);

			// image.setScaleType(ImageView.ScaleType.FIT_XY);

			image.setBackgroundResource(mGalleryItemBackground);

			Drawable thumbDrawable = image.getDrawable();
			if (thumbDrawable != null) {
				thumbDrawable.setDither(true);
			} else {
				Log.e("Paperless System", String.format(
						"Error decoding thumbnail resId=%d for wallpaper #%d",
						thumbRes, position));
			}
			return image;
		}
	}

	class WallpaperLoader extends AsyncTask<Integer, Void, Bitmap> {
		BitmapFactory.Options mOptions;

		WallpaperLoader() {
			mOptions = new BitmapFactory.Options();
			mOptions.inDither = false;
			mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			if (isCancelled())
				return null;
			try {
				return BitmapFactory.decodeResource(getResources(), helper
						.getImages().get(params[0]), mOptions);
			} catch (OutOfMemoryError e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap b) {
			if (b == null)
				return;

			if (!isCancelled() && !mOptions.mCancel) {
				// Help the GC
				if (mBitmap != null) {
					mBitmap.recycle();
				}

				// mInfoView.setText(getResources().getStringArray(R.array.info)[mGallery.getSelectedItemPosition()]);

				// final ImageView view = mImageView;
				// view.setImageBitmap(b);

				mBitmap = b;

				// final Drawable drawable = view.getDrawable();
				// drawable.setFilterBitmap(true);
				// drawable.setDither(true);

				// view.postInvalidate();

				mLoader = null;
			} else {
				b.recycle();
			}
		}

		void cancel() {
			mOptions.requestCancelDecode();
			super.cancel(true);
		}
	}

}
