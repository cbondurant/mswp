package edu.berea.walkerje.mswp;

import java.awt.Rectangle;

public interface IRegion extends IPosition, IExtent{
	class ImmutableRectangleRegion implements IRegion{
		public final Rectangle rect;

		ImmutableRectangleRegion(Rectangle r){
			rect = r;
		}
		
		@Override
		public int getPositionX() {
			return rect.x;
		}

		@Override
		public int getPositionY() {
			return rect.y;
		}

		@Override
		public int getExtentWidth() {
			return rect.width;
		}

		@Override
		public int getExtentHeight() {
			return rect.height;
		}
	}
	
	/**
	 * Return the bounding rectangle of this region.
	 */
	public default Rectangle getRegionBounds() {
		return new Rectangle(getPositionX(), getPositionY(), getExtentWidth(), getExtentHeight());
	}
	
	/**
	 * @param r the rectangle instance to wrap inside an IRegion instance.
	 * @return an instance of an IRegion.
	 */
	public static IRegion wrap(Rectangle r) {
		return new ImmutableRectangleRegion(r);
	}
	
	public static IRegion wrap(IExtent ext) {
		return new ImmutableRectangleRegion(ext.getRegionBounds());
	}
	
	/**
	 * Wraps the given extent inside an IRegion instance.
	 * @param width
	 * @param height
	 * @return
	 */
	public static IRegion wrap(int width, int height) {
		return new ImmutableRectangleRegion(new Rectangle(width, height));
	}
	
	/**
	 * Wraps the given parameters in an IRegion instance.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static IRegion wrap(int x, int y, int width, int height) {
		return new ImmutableRectangleRegion(new Rectangle(x, y, width, height));
	}
}