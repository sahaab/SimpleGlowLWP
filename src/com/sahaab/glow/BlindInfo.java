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

import android.graphics.Rect;

/**
 * Dumb, stateless data holder describing a Blind, as used by BlindsView.
 */
public class BlindInfo {
	private final Rect mBounds;
	private float mRotationX, mRotationY, mRotationZ;
	private float mScale = 1f;
	private float mYoffset = 0f;
	private boolean mDrawStroke = false;

	public BlindInfo(int l, int t, int r, int b) {
		mBounds = new Rect(l, t, r, b);
	}

	public int getHeight() {
		return mBounds.height();
	}

	public int getWidth() {
		return mBounds.width();
	}

	public int getLeft() {
		return mBounds.left;
	}

	public int getRight() {
		return mBounds.right;
	}

	public int getTop() {
		return mBounds.top;
	}

	public int getBottom() {
		return mBounds.bottom;
	}

	public void setRotations(float xRotation, float yRotation, float zRotation) {
		mRotationX = xRotation;
		mRotationY = yRotation;
		mRotationZ = zRotation;
	}

	public float getRotationX() {
		return mRotationX;
	}

	public float getRotationY() {
		return mRotationY;
	}

	public float getRotationZ() {
		return mRotationZ;
	}

	public void setScale(float s) {
		mScale = s;
	}

	public float getScale() {
		return mScale;
	}

	public void setYoffset(float offset) {
		mYoffset = offset;
	}

	public float getYoffset() {
		return mYoffset;
	}

	public void setDrawStroke(boolean d) {
		mDrawStroke = d;
	}

	public boolean getDrawStroke() {
		return mDrawStroke;
	}
}
