package edu.berea.walkerje.mswp;

import java.awt.Point;

public interface IPosition {
	/**
	 * @return the X coordinate of this position.
	 */
	public int getPositionX();
	
	/**
	 * @return the Y coordinate of this position.
	 */
	public int getPositionY();
	
	/**
	 * @return the position of this object, as a point.
	 */
	public default Point getPosition() {
		return new Point(getPositionX(), getPositionY());
	}
}