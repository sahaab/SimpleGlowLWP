package com.sahaab.glow;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.preference.PreferenceManager;

public class GlowDrawable extends ShapeDrawable {
	 
    private Rect mBounds;
    private float mCenterX = 0.0f;
    private float mCenterY = 0.0f;
    private float mOffsetX = 40.0f;
    private float mOffsetY = 80.0f;
    private float mRadius = 0.0f;
    private float mSpeedX = 10.0f;
    private float mSpeedY = 20.0f;
 
    private int mColorFG = Color.rgb(0x42, 0xec, 0xff);
    private int mColorBG = Color.rgb(0x00, 0x97, 0xa7); 
 
    public GlowDrawable() {
        this(new RectShape());
    }
 
    public GlowDrawable(Shape s) {
        super(s);
    }
 
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        mBounds = bounds;
        if (mRadius == 0.0f) {
            mCenterX = (mBounds.right - mBounds.left)/2.0f;
            mCenterY = (mBounds.bottom - mBounds.top)/2.0f;
            mRadius = mCenterX + mCenterY;
        }
    }
 
    public void animate() {      
        mCenterX += mSpeedX;
        mCenterY += mSpeedY;
 
        if (mCenterX < mBounds.left + mOffsetX ||
            mCenterX > mBounds.right - mOffsetX) {
            mSpeedX *= -1.0f;
        }
 
        if (mCenterY < mBounds.top + mOffsetY || 
            mCenterY > mBounds.bottom - mOffsetY) {
            mSpeedY *= -1.0f;
        }
    }
 
    public Paint getPaint(float width, float height) {
        animate();
 
        RadialGradient shader = new RadialGradient(
            mCenterX, mCenterY, mRadius, 
            mColorFG, mColorBG, 
            Shader.TileMode.CLAMP);
 
        Paint paint = new Paint();
        paint.setShader(shader);
        return paint;
    }
 
    public void ChangeXY(float x, float y) {
    	mCenterX = x;
    	mCenterY = y;
    }
    
    public void Changecolor(int bg, int fg) {
        mColorBG = bg;
        mColorFG = fg; 
    }    
   
    public void Changespeed(String x, String y) {
    	if(x != null && !x.isEmpty()) {
    	    mSpeedX = Float.parseFloat(x);
		
    	}
    	if(y != null && !y.isEmpty()) {
    	    mSpeedY = Float.parseFloat(y);       		
    	}    	
    }       
    
    public void draw(Canvas c) {
        float width = c.getWidth();
        float height = c.getHeight();
        c.drawRect(0, 0, width, height, getPaint(width, height));
    }
}
