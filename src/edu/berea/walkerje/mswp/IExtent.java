package edu.berea.walkerje.mswp;

import java.awt.Rectangle;

public interface IExtent {
	
	/**
	 * @return the width of this extent.
	 */
	public int getExtentWidth();
	
	/**
	 * @return the height of this extent.
	 */
	public int getExtentHeight();
	
	/**
	 * @return rectangle covering width and height of this extent.
	 */
	public default Rectangle getRegionBounds() {
		return new Rectangle(getExtentWidth(), getExtentHeight());
	}
}