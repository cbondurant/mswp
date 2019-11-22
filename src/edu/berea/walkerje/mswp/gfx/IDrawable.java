package edu.berea.walkerje.mswp.gfx;

import java.awt.Graphics2D;

public interface IDrawable {
	/**
	 * Draws whatever implements this interface
	 * @param gfx object to draw with.
	 */
	public void draw(Graphics2D gfx);
}