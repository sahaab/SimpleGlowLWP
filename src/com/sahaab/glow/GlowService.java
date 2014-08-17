package com.sahaab.glow;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GlowService extends WallpaperService {
	 
    @Override
    public Engine onCreateEngine() {
        return new GlowEngine();
    }
 
    private class GlowEngine extends Engine {
        private GlowDrawable mDrawable;
        private boolean mVisible = false;
        private boolean touchEnabled;
        SharedPreferences prefs;
        private final Handler mHandler = new Handler();
        private final Runnable mUpdateDisplay = new Runnable() {
            public void run() {
                draw();
            }
        };
 
        public GlowEngine() {
            super();
            prefs = PreferenceManager
                    .getDefaultSharedPreferences(GlowService.this);
            mDrawable = new GlowDrawable();

        	mDrawable.Changecolor(prefs.getInt("color1", 0), prefs.getInt("color2", 0));
            mDrawable.Changespeed(prefs.getString("speedx", "10.0"), prefs.getString("speedy", "20.0"));
            
        }
 
        private void draw() {
            
            SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();

                mDrawable.setBounds(c.getClipBounds());
                mDrawable.draw(c);
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }
            mHandler.removeCallbacks(mUpdateDisplay);
            if (mVisible) {
                mHandler.postDelayed(mUpdateDisplay, 100);
            }
            
        }
 
        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                draw();
            } else {
                mHandler.removeCallbacks(mUpdateDisplay);
            }
        }
 
        @Override
        public void onSurfaceChanged(SurfaceHolder holder,
                    int format, int width, int height) {
            draw();
        }
 
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }
 
        @Override
        public void onDestroy() {
            super.onDestroy();
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }
        
        @Override
        public void onTouchEvent(MotionEvent event) {
          touchEnabled = prefs.getBoolean("touch", false);
      	  mDrawable.Changecolor(prefs.getInt("color1", 0), prefs.getInt("color2", 0));
          mDrawable.Changespeed(prefs.getString("speedx", "10.0"), prefs.getString("speedy", "20.0"));
          if (touchEnabled == true) {

            float x = event.getX();
            float y = event.getY();
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
            	mDrawable.ChangeXY(x,y);

            } finally {
              if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
            }
            super.onTouchEvent(event);
          }
        }
    }
}
