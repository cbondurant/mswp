package edu.berea.walkerje.mswp.gfx;

import java.awt.Rectangle;

import edu.berea.walkerje.mswp.IRegion;

public class Sprite implements IRegion, ISpriteProvider{
	private int srcX, srcY;
	private int width, height;
	private IImageProvider srcImg;
	
	/**
	 * Sprite constructor. Specifies source domain and owning spritesheet.
	 * @param srcX
	 * @param srcY
	 * @param width
	 * @param height
	 * @param srcImg
	 */
	public Sprite(int srcX, int srcY, int width, int height, IImageProvider parent) {
		this.srcX = srcX;
		this.srcY = srcY;
		this.width = width;
		this.height = height;
		
		this.srcImg = parent;
	}
	
	public Sprite(IRegion r, IImageProvider parent) {
		this.srcX = r.getPositionX();
		this.srcY = r.getPositionY();
		this.width = r.getExtentWidth();
		this.height = r.getExtentHeight();
		
		this.srcImg = parent;
	}
	
	public Sprite(Rectangle r, IImageProvider parent) {
		this(IRegion.wrap(r), parent);
	}
	
	/**
	 * @return the image provider to which this sprite belongs.
	 */
	public IImageProvider getImageProvider() {
		return srcImg;
	}
	
	/**
	 * Sets the width, in pixels, of this sprite.
	 * @param w
	 */
	public void setExtentWidth(int w) {
		width = w;
	}
	
	/**
	 * @return the width, of this sprite, in pixels
	 */
	@Override
	public int getExtentWidth() {
		return width;
	}

	/**
	 * Sets the height, in pixels, of this sprite.
	 * @param h
	 */
	public void setExtentHeight(int h) {
		height = h;
	}
	
	/**
	 * @return the height, of this sprite, in pixels
	 */
	@Override
	public int getExtentHeight() {
		return height;
	}
	
	/**
	 * @param x
	 */
	public void setPositionX(int x) {
		srcX = x;
	}
	
	/**
	 * @return the source X coordinate from the spritesheet.
	 */
	public int getPositionX() {
		return srcX;
	}
	
	/**
	 * Sets the Y coordinate from the spritesheet.
	 * @param y
	 */
	public void setPositionY(int y) {
		srcY = y;
	}
	
	/**
	 * @return the source Y coordinate from the spritesheet.
	 */
	public int getPositionY() {
		return srcY;
	}

	//satisfy spriteprovider obligations
	
	/**
	 * Returns the current sprite.
	 * Always returns this.
	 */
	@Override
	public Sprite getCurrentSprite() {
		return this;
	}

	/**
	 * Returns the next sprite.
	 * Always returns this.
	 */
	@Override
	public Sprite getNextSprite() {
		return this;
	}

	/**
	 * Returns the total number of sprites.
	 * Of course, this always returns 1.
	 */
	@Override
	public int getTotalSprites() {
		return 1;
	}
}