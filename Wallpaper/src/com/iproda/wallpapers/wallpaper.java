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
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.iproda.wallpapers.R;

public class wallpaper extends Activity implements AdapterView.OnItemSelectedListener,
        OnClickListener {



    private GridView mGallery;
    //private ImageView mImageView;
    //private TextView mInfoView;
   // private boolean mIsWallpaperSet;
    private  WallpaperHelper helper;
    private Bitmap mBitmap;
	private int mGalleryItemBackground;
    private WallpaperLoader mLoader;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //findWallpapers();
        helper=new WallpaperHelper(this);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.wallpaper_chooser);

        mGallery = (GridView) findViewById(R.id.gallery);
        mGallery.setAdapter(new ImageAdapter(this));
        mGallery.setOnItemSelectedListener(this);
        //mGallery.setCallbackDuringFling(false);
        //mGallery.setUnselectedAlpha(0.3f);
        //mGallery.setSpacing(getResources().getDimensionPixelSize(R.dimen.gallery_spacing));
        TypedArray a = this 
                .obtainStyledAttributes(R.styleable.Gallery);
		mGalleryItemBackground = a.getResourceId(
		        R.styleable.Gallery_android_galleryItemBackground, 1);
      
        Intent service =new Intent();
        service.setClass(this, WallpaperService.class);
        this.startService(service);

    }


    @Override
    protected void onResume() {
        super.onResume();
       // mIsWallpaperSet = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
            mLoader.cancel(true);
            mLoader = null;
        }
    }

    public void onItemSelected(AdapterView parent, View v, int position, long id) {
        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
            mLoader.cancel();
        }
        mLoader = (WallpaperLoader) new WallpaperLoader().execute(position);
    }





    public void onNothingSelected(AdapterView parent) {
    }

    private class ImageAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        ImageAdapter(wallpaper context) {
            mLayoutInflater = context.getLayoutInflater();
        }

        public int getCount() {
            return helper.getThumbs().size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image;

            if (convertView == null) {
                image = (ImageView) mLayoutInflater.inflate(R.layout.wallpaper_item, parent, false);
            } else {
                image = (ImageView) convertView;
            }
            
            int thumbRes = helper.getThumbs().get(position);
            image.setImageResource(thumbRes);

            //image.setAdjustViewBounds(false);
	
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

    public void onClick(View v) {
        
        

        helper.selectWallpaper(mGallery.getSelectedItemPosition());
        
        
        setResult(Activity.RESULT_OK);
        finish();
    }

    class WallpaperLoader extends AsyncTask<Integer, Void, Bitmap> {
        BitmapFactory.Options mOptions;

        WallpaperLoader() {
            mOptions = new BitmapFactory.Options();
            mOptions.inDither = false;
            mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;            
        }
        
        protected Bitmap doInBackground(Integer... params) {
            if (isCancelled()) return null;
            try {
                return BitmapFactory.decodeResource(getResources(),
                        helper.getImages().get(params[0]), mOptions);
            } catch (OutOfMemoryError e) {
                return null;
            }            
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            if (b == null) return;

            if (!isCancelled() && !mOptions.mCancel) {
                // Help the GC
                if (mBitmap != null) {
                    mBitmap.recycle();
                }

                //mInfoView.setText(getResources().getStringArray(R.array.info)[mGallery.getSelectedItemPosition()]);

                //final ImageView view = mImageView;
                //view.setImageBitmap(b);

                mBitmap = b;

                //final Drawable drawable = view.getDrawable();
               // drawable.setFilterBitmap(true);
                //drawable.setDither(true);

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
