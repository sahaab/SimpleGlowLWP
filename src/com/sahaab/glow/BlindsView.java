/**
 Copyright (c) 2013, Sony Mobile Communications AB
 All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of Sony Mobile nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 @author Johan Henricson
 */

package com.sahaab.glow;

import java.util.ArrayList;
import com.sahaab.glow.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class BlindsView extends LinearLayout {

	// Config
	private static final boolean LOG_ON = true;
	private static String LOG_TAG; // assigned in init
	private static final float CONFIG_MAX_ROTATIONX = 45f;
	private static final float CONFIG_MAX_ROTATIONY = 15f;
	private static final float CONFIG_CAMERA_DISTANCE_Z = -35;
	private static float mMaxAffectRadius;
	private static final float CONFIG_MIN_SCALING = 0.97f;
	private static final float CONFIG_MAX_YOFFSET = 16;
	private static final int CONFIG_BLINDSTROKE_BASECOLOR = Color.DKGRAY;
	private static final int CONFIG_BLINDSTROKE_ALPHA = 175;
	private static final int CONFIG_BLINDSTROKE_BEVEL_ANGLE = 45;
	private static float mConfigStrokeWidth;

	// Drawing tools
	private Bitmap mUndistortedBitmap;
	private Canvas mUndistortedCanvas;
	private BitmapDrawable mBgDrawable;
	private Paint mBlindPaint, mBlindStrokePaint;
	private final Camera mCamera = new Camera();

	// State
	private ArrayList<BlindInfo> mBlindSet = null;
	private boolean mIsInBlindMode = false;

	public BlindsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();

	}

	public BlindsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BlindsView(Context context) {
		super(context);
		init();
	}

	//
	// View and ViewGroup duties and overrides:
	/**
	 * Called by draw to draw the child views. This may be overridden by derived
	 * classes to gain control just before its children are drawn (but after its
	 * own view has been drawn).
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (LOG_ON) {
			Log.d(LOG_TAG,
					"dispatchDraw     (dispatching draw calls to all children)");
		}
		drawCustomStuff(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setupBlinds((int) getResources().getDimension(R.dimen.blindHeight));
		if (LOG_ON) {
			Log.d(LOG_TAG,
					"onLayout. Layout properties changed - blinds set rebuilt. New set contains "
							+ mBlindSet.size() + " blinds");
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			mIsInBlindMode = true;
			calculateBlindRotations(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mIsInBlindMode = false;
			break;
		default:
			// no change
			break;

		}
		invalidate();
		return true;
	}

	//
	// Internal convenience methods:

	/**
	 * Constructor addition
	 */
	private void init() {
		LOG_TAG = this.getClass().getSimpleName();

		mBlindPaint = new Paint();
		mBlindPaint.setStyle(Paint.Style.FILL);
		mBlindPaint.setAntiAlias(true);
		mBlindPaint.setFilterBitmap(true);

		mConfigStrokeWidth = getResources().getDimension(
				R.dimen.blindStrokeWidth);
		mBlindStrokePaint = new Paint();
		mBlindStrokePaint.setColor(CONFIG_BLINDSTROKE_BASECOLOR);
		mBlindStrokePaint.setAlpha(CONFIG_BLINDSTROKE_ALPHA);
		mBlindStrokePaint.setStrokeWidth(mConfigStrokeWidth);
		mBlindStrokePaint.setAntiAlias(true);
		mBlindStrokePaint.setFilterBitmap(true);

		mMaxAffectRadius = getResources().getDimension(
				R.dimen.touchEffectRadius);
	}

	private void drawCustomStuff(Canvas screenCanvas) {
		if (LOG_ON) {
			Log.d(LOG_TAG,
					"drawCustomStuff  (doing the custom drawing of this ViewGroup)");
		}

		final boolean initBmpAndCanvas = (mIsInBlindMode && (!(mUndistortedBitmap != null && !mUndistortedBitmap
				.isRecycled())));

		if (!mIsInBlindMode || (mIsInBlindMode && initBmpAndCanvas)) {
			// Draw normally
			if (mIsInBlindMode && initBmpAndCanvas) {
				mUndistortedBitmap = Bitmap.createBitmap(getWidth(),
						getHeight(), Bitmap.Config.ARGB_8888);
				mUndistortedCanvas = new Canvas(mUndistortedBitmap);
			}

			Canvas canvasToDrawTo = mIsInBlindMode ? mUndistortedCanvas
					: screenCanvas;

			drawUndistorted(canvasToDrawTo);
		}
		if (mIsInBlindMode) {
			// Draw blinds version
			drawBlinds(screenCanvas);
		}
	}

	private void drawUndistorted(Canvas canvas) {
		if (mBgDrawable != null) {
			mBgDrawable.draw(canvas);
		}
		super.dispatchDraw(canvas);
	}

	private void drawBlinds(Canvas canvas) {
		// Draw each blind in order, starting from the top one.
		for (BlindInfo blind : mBlindSet) {
			drawBlind(blind, canvas);
		}
	}

	private void drawBlind(BlindInfo info, Canvas canvas) {
		// Read params
		final int width = info.getWidth();
		final int height = info.getHeight();
		final int coordX = info.getLeft();
		final int coordY = info.getTop();
		final float xRotation = info.getRotationX();
		final float yRotation = info.getRotationY();
		final float zRotation = info.getRotationZ();
		final float scale = info.getScale();
		final float yOffset = info.getYoffset();
		final boolean drawBottomStroke = info.getDrawStroke();

		// Prepare Canvas and Camera
		canvas.save();
		mCamera.save();
		mCamera.setLocation(0, 0, CONFIG_CAMERA_DISTANCE_Z);
		canvas.translate((coordX + (width / 2f)), (coordY + (height / 2f)));

		// Apply transformations
		mCamera.rotateY(yRotation);
		mCamera.rotateX(xRotation);
		canvas.scale(scale, scale, 0f, 0f);
		canvas.translate(0f, yOffset);

		Matrix cameraMatrix = new Matrix();
		mCamera.getMatrix(cameraMatrix);
		canvas.concat(cameraMatrix);

		mBlindPaint.setColorFilter(calculateLight(xRotation));

		// Draw
		final Rect src = new Rect(coordX, coordY, (coordX + width),
				(coordY + height));
		final RectF dst = new RectF(-(width / 2f), -(height / 2f), width / 2f,
				height / 2f);
		canvas.drawBitmap(mUndistortedBitmap, src, dst, mBlindPaint);
		if (drawBottomStroke) {
			mBlindStrokePaint.setColorFilter(calculateLight(xRotation
					+ CONFIG_BLINDSTROKE_BEVEL_ANGLE));
			canvas.drawLine(dst.left, (dst.bottom - mConfigStrokeWidth / 2f),
					dst.right, (dst.bottom - mConfigStrokeWidth / 2f),
					mBlindStrokePaint);
		}

		// Restore Canvas and Camera
		mCamera.restore();
		canvas.restore();

		if (LOG_ON) {
			Log.d(LOG_TAG, "Drew blind with size " + width + " by " + height
					+ " px with rotation (" + xRotation + ", " + yRotation
					+ ", " + zRotation + ") (x,y,z) at coordinates " + coordX
					+ ", " + coordY);
		}
	}

	/**
	 * Creates a new Blinds set, given a blind height. Requires the BlindsView
	 * to be measure and have an assigned width and height.
	 * 
	 * @param blindHeight
	 *            in dip
	 * @param bvHeight
	 *            in dip
	 * @param bvWidth
	 *            in dip
	 */
	private void setupBlinds(int blindHeight) {

		if (blindHeight == 0) {
			throw new IllegalArgumentException("baseHeight must be >0");
		}

		ArrayList<BlindInfo> bi = new ArrayList<BlindInfo>();
		int accumulatedHeight = 0;
		do {
			bi.add(new BlindInfo(0, accumulatedHeight, getWidth(),
					accumulatedHeight + blindHeight));
			accumulatedHeight += blindHeight;
		} while (accumulatedHeight < getHeight());

		mBlindSet = bi;
	}

	private synchronized void calculateBlindRotations(float xPos, float yPos) {

		float currentBlindPivotY;
		float normalizedVerticalDistanceFromTouch;

		for (BlindInfo currentBlind : mBlindSet) {
			currentBlindPivotY = currentBlind.getTop()
					+ (float) currentBlind.getHeight() / 2f;

			normalizedVerticalDistanceFromTouch = Math
					.abs((yPos - currentBlindPivotY) / mMaxAffectRadius);

			float xRotation = 0;
			float yRotation = 0;
			float scaling = 1f;
			float yOffset = 0f;
			boolean drawStroke = false;
			// Only rotate if within valid range
			if (normalizedVerticalDistanceFromTouch <= 1f) {

				// X AXIS ROTATION:
				// rot(d) = -((d-0.55)*2)^2+1 where 0<=d
				final double normalizedRotationX = Math
						.max(0d,
								(-Math.pow(
										((normalizedVerticalDistanceFromTouch - 0.55f) * 2f),
										2) + 1));

				// Blind above touch means negative angle
				if ((currentBlindPivotY < yPos)) {
					xRotation = (float) -(CONFIG_MAX_ROTATIONX * normalizedRotationX);
				} else {
					xRotation = (float) (CONFIG_MAX_ROTATIONX * normalizedRotationX);
				}

				// Y AXIS ROTATION:
				// -1 <= normalizedHorizontalDistanceFromPivot <= 1
				final float normalizedHorizontalDistanceFromPivot = ((xPos / getWidth()) - 0.5f) / 0.5f;
				// 0 <= linearDeclineFactor <= 1
				final float linearDeclineFactor = 1 - normalizedVerticalDistanceFromTouch;
				yRotation = CONFIG_MAX_ROTATIONY
						* normalizedHorizontalDistanceFromPivot
						* linearDeclineFactor;

				// SCALING:
				// 1 at both end points, CONFIG_MIN_SCALING at center and
				// declining with the squared distance in between.
				scaling = 1f
						- (1f - normalizedVerticalDistanceFromTouch
								* normalizedVerticalDistanceFromTouch)
						* (1f - CONFIG_MIN_SCALING);

				// Y OFFSET:
				yOffset = ((1f - normalizedVerticalDistanceFromTouch
						* normalizedVerticalDistanceFromTouch))
						* CONFIG_MAX_YOFFSET;

				// SET STROKE DRAWING
				drawStroke = true;

			}
			currentBlind.setRotations(xRotation, yRotation, 0f);
			currentBlind.setScale(scaling);
			currentBlind.setYoffset(yOffset);
			currentBlind.setDrawStroke(drawStroke);
		}

	}

	// Lighting effect shamelessly nicked from Anders:
	// http://developer.sonymobile.com/wp/2010/05/31/android-tutorial-making-your-own-3d-list-part-2/
	// "Let there be light"
	/** Ambient light intensity */
	private static final int AMBIENT_LIGHT = 55;

	/** Diffuse light intensity */
	private static final int DIFFUSE_LIGHT = 255;

	/** Specular light intensity */
	private static final float SPECULAR_LIGHT = 70;

	/** Shininess constant */
	private static final float SHININESS = 255;

	/** The max intensity of the light */
	private static final int MAX_INTENSITY = 0xFF;

	/** Light source angular offset */
	private static final float LIGHT_SOURCE_ANGLE = 38f;

	private LightingColorFilter calculateLight(float rotation) {
		rotation -= LIGHT_SOURCE_ANGLE;
		final double cosRotation = Math.cos(Math.PI * rotation / 180);
		int intensity = AMBIENT_LIGHT + (int) (DIFFUSE_LIGHT * cosRotation);
		int highlightIntensity = (int) (SPECULAR_LIGHT * Math.pow(cosRotation,
				SHININESS));

		if (intensity > MAX_INTENSITY) {
			intensity = MAX_INTENSITY;
		}
		if (highlightIntensity > MAX_INTENSITY) {
			highlightIntensity = MAX_INTENSITY;
		}

		final int light = Color.rgb(intensity, intensity, intensity);
		final int highlight = Color.rgb(highlightIntensity, highlightIntensity,
				highlightIntensity);

		return new LightingColorFilter(light, highlight);
	}

	//
	// Public interface
	public void setBackground(int id) {
		mBgDrawable = (BitmapDrawable) getResources().getDrawable(id);
		centerBgDrawable();
	}

	@Override
	public void setBackground(Drawable background) {
		mBgDrawable = (BitmapDrawable) background;
		centerBgDrawable();
	}

	private void centerBgDrawable() {
		if (mBgDrawable != null) {
			final DisplayMetrics dm = getResources().getDisplayMetrics();
			mBgDrawable.setTargetDensity(dm);
			mBgDrawable.setGravity(android.view.Gravity.CENTER);
			mBgDrawable.setBounds(0, 0, dm.widthPixels, dm.heightPixels);
		}
		postInvalidate();
	}

}
