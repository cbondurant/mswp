package edu.berea.walkerje.mswp.gfx;

import java.awt.Point;

import edu.berea.walkerje.mswp.IExtent;

public class Tile implements IExtent{	
	private ISpriteProvider spriteProvider;//generic interface allows for animations! how neat is that?

	private Tilesheet parent;
	
	public Tile(ISpriteProvider sprite, Tilesheet sht) {
		spriteProvider = sprite;
		parent = sht;
	}
	
	/**
	 * Sets the sprite provider of this tile.
	 * @param p 
	 */
	public void setSpriteProvider(ISpriteProvider p) {spriteProvider = p;}
	
	/**
	 * @return the sprite provider of this tile.
	 */
	public ISpriteProvider getSpriteProvider() {return spriteProvider;}

	/**
	 * @return the tilesheet to which this tile belongs.
	 */
	public Tilesheet getTilesheet() {
		return parent;
	}
	
	public final Point getSourceTileCoord() {
		return new Point(spriteProvider.getCurrentSprite().getPositionX() / spriteProvider.getCurrentSprite().getExtentWidth(),
						 spriteProvider.getCurrentSprite().getPositionY() / spriteProvider.getCurrentSprite().getExtentHeight());
	}
	
	public final int getSourceTileID() {
		final Point sourceCoord = getSourceTileCoord();
		return sourceCoord.y * parent.getTilesX() + sourceCoord.x;
	}
	
	@Override
	public int getExtentWidth() {
		return parent.getTileExtentWidth();
	}

	@Override
	public int getExtentHeight() {
		return parent.getTileExtentHeight();
	}
}