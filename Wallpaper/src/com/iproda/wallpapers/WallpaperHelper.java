package com.iproda.wallpapers;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.WindowManager;

public class WallpaperHelper {

	private Context mContext;
	
    protected static final float WALLPAPER_SCREENS_SPAN = 2f;

    private static final String PREF_KEY = "wallpaper_prefs";
    protected static final String WALLPAPER_WIDTH_KEY = "wallpaper.width";
    protected static final String WALLPAPER_HEIGHT_KEY = "wallpaper.height";
    
    private static SharedPreferences mPrefs;
    

    private ArrayList<Integer> mThumbs;
    private ArrayList<Integer> mImages;
    
	public WallpaperHelper(Context context)
	{
		this.mContext=context;
        mPrefs =mContext.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        
        findWallpapers();
	}
	
	
	public ArrayList<Integer> getThumbs(){
		
		return mThumbs;
	}
	
	
	public ArrayList<Integer> getImages(){
		
		return mImages;
	}
	
    private void findWallpapers() {
        mThumbs = new ArrayList<Integer>(24);
        mImages = new ArrayList<Integer>(24);

        final Resources resources = mContext.getResources();
        final String packageName = mContext.getApplicationContext().getPackageName();

        addWallpapers(resources, packageName, R.array.wallpapers);
       // addWallpapers(resources, packageName, R.array.extra_wallpapers);
    }

    private void addWallpapers(Resources resources, String packageName, int list) {
        final String[] extras = resources.getStringArray(list);
        for (String extra : extras) {
            int res = resources.getIdentifier(extra, "drawable", packageName);
            if (res != 0) {
                final int thumbRes = resources.getIdentifier(extra + "_small",
                        "drawable", packageName);

                if (thumbRes != 0) {
                    mThumbs.add(thumbRes);
                    mImages.add(res);
                }
            }
        }
    }

	
    @SuppressLint("NewApi")
	protected Point getDefaultWallpaperSize(Resources res, WindowManager windowManager) {
        // Uses suggested size if available
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
        int suggestedWidth = wallpaperManager.getDesiredMinimumWidth();
        int suggestedHeight = wallpaperManager.getDesiredMinimumHeight();
        if (suggestedWidth != 0 && suggestedHeight != 0) {
            return new Point(suggestedWidth, suggestedHeight);
        }

        // Else, calculate desired size from screen size
        Point minDims = new Point();
        Point maxDims = new Point();
        windowManager.getDefaultDisplay().getCurrentSizeRange(minDims, maxDims);

        int maxDim = Math.max(maxDims.x, maxDims.y);
        int minDim = Math.max(minDims.x, minDims.y);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Point realSize = new Point();
            windowManager.getDefaultDisplay().getRealSize(realSize);
            maxDim = Math.max(realSize.x, realSize.y);
            minDim = Math.min(realSize.x, realSize.y);
        }

        // We need to ensure that there is enough extra space in the wallpaper
        // for the intended
        // parallax effects
        final int defaultWidth, defaultHeight;
        if (false) {
            defaultWidth = (int) (maxDim * wallpaperTravelToScreenWidthRatio(maxDim, minDim));
            defaultHeight = maxDim;
        } else {
            defaultWidth = Math.max((int) (minDim * WALLPAPER_SCREENS_SPAN), maxDim);
            defaultHeight = maxDim;
        }
        return new Point(defaultWidth, defaultHeight);
    }

    // As a ratio of screen height, the total distance we want the parallax effect to span
    // horizontally
    protected float wallpaperTravelToScreenWidthRatio(int width, int height) {
        float aspectRatio = width / (float) height;

        // At an aspect ratio of 16/10, the wallpaper parallax effect should span 1.5 * screen width
        // At an aspect ratio of 10/16, the wallpaper parallax effect should span 1.2 * screen width
        // We will use these two data points to extrapolate how much the wallpaper parallax effect
        // to span (ie travel) at any aspect ratio:

        final float ASPECT_RATIO_LANDSCAPE = 16/10f;
        final float ASPECT_RATIO_PORTRAIT = 10/16f;
        final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE = 1.5f;
        final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT = 1.2f;

        // To find out the desired width at different aspect ratios, we use the following two
        // formulas, where the coefficient on x is the aspect ratio (width/height):
        //   (16/10)x + y = 1.5
        //   (10/16)x + y = 1.2
        // We solve for x and y and end up with a final formula:
        final float x =
            (WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE - WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT) /
            (ASPECT_RATIO_LANDSCAPE - ASPECT_RATIO_PORTRAIT);
        final float y = WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT - x * ASPECT_RATIO_PORTRAIT;
        return x * aspectRatio + y;
    }

    protected RectF getMaxCropRect(
            int inWidth, int inHeight, int outWidth, int outHeight, boolean leftAligned) {
        RectF cropRect = new RectF();
        // Get a crop rect that will fit this
        if (inWidth / (float) inHeight > outWidth / (float) outHeight) {
             cropRect.top = 0;
             cropRect.bottom = inHeight;
             cropRect.left = (inWidth - (outWidth / (float) outHeight) * inHeight) / 2;
             cropRect.right = inWidth - cropRect.left;
             if (leftAligned) {
                 cropRect.right -= cropRect.left;
                 cropRect.left = 0;
             }
        } else {
            cropRect.left = 0;
            cropRect.right = inWidth;
            cropRect.top = (inHeight - (outHeight / (float) outWidth) * inWidth) / 2;
            cropRect.bottom = inHeight - cropRect.top;
        }
        return cropRect;
    }

    protected void cropImageAndSetWallpaper(int resId) {
        Point outSize =new Point(1024,600);
        final BitmapCropTask cropTask = new BitmapCropTask(mContext, mContext.getResources(), resId,
                null, 0, outSize.x, outSize.y, true, false, null);
        Point inSize = cropTask.getImageBounds();
        final RectF crop = getMaxCropRect(inSize.x, inSize.y, outSize.x, outSize.y, false);
        cropTask.setCropBounds(crop);
        Runnable onEndCrop = new Runnable() {
            public void run() {
                Point point = cropTask.getImageBounds();
                updateWallpaperDimensions(point.x, point.y);

            }
        };
        cropTask.setOnEndRunnable(onEndCrop);
        cropTask.execute();
    }

    protected void updateWallpaperDimensions(int width, int height) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (width != 0 && height != 0) {
            editor.putInt(WALLPAPER_WIDTH_KEY, width);
            editor.putInt(WALLPAPER_HEIGHT_KEY, height);
        } else {
            editor.remove(WALLPAPER_WIDTH_KEY);
            editor.remove(WALLPAPER_HEIGHT_KEY);
        }
        editor.commit();

        suggestWallpaperDimension(mContext.getResources(),WallpaperManager.getInstance(mContext));
    }

    public void suggestWallpaperDimension(Resources res,
            final WallpaperManager wallpaperManager) {
        final Point defaultWallpaperSize =new Point(1024,600);

        new Thread("suggestWallpaperDimension") {
            public void run() {
                // If we have saved a wallpaper width/height, use that instead
                int savedWidth = mPrefs.getInt(WALLPAPER_WIDTH_KEY, defaultWallpaperSize.x);
                int savedHeight = mPrefs.getInt(WALLPAPER_HEIGHT_KEY, defaultWallpaperSize.y);
                wallpaperManager.suggestDesiredDimensions(savedWidth, savedHeight);
            }
        }.start();
    }
    
    /*
     * When using touch if you tap an image it triggers both the onItemClick and
     * the onTouchEvent causing the wallpaper to be set twice. Ensure we only
     * set the wallpaper once.
     */
    public void selectWallpaper(int position) {

    	/*
        if (mIsWallpaperSet) {
        	           return;
               }
        mIsWallpaperSet = true;*/
       cropImageAndSetWallpaper(mImages.get(position));
      

    }


}
