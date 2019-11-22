package edu.berea.walkerje.mswp.gfx;

import java.awt.Image;

import edu.berea.walkerje.mswp.IExtent;

public interface IImageProvider extends IExtent{
	/**
	 * @return the instance of the underlying image used for this object.
	 */
	public Image getImage();
}