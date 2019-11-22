package edu.berea.walkerje.mswp.gfx;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import edu.berea.walkerje.mswp.IExtent;
import edu.berea.walkerje.mswp.IRegion;

public class TileMap implements IDrawable, IExtent{
	private Tile[][] tiles;//2D array of tiles. Self explanatory.
	private IRegion size;//more or less the size of the 2D tiles array
	private IRegion tileDrawSize;//the size, in pixels, of each individual tile to be drawn.
		
	/**
	 * Instantiates a new TileMap, as a constructor.
	 * @param width of this TileMap, in tiles
	 * @param height of this TileMap, in tiles
	 * @param tileDrawWidth size of the width, in pixels, used to draw this TileMap's tiles.
	 * @param tileDrawHeight size of the height, in pixels, used to draw this TileMap's tiles.
	 */
	public TileMap(int width, int height, int tileDrawWidth, int tileDrawHeight) {
		this(IRegion.wrap(width, height), IRegion.wrap(tileDrawWidth, tileDrawHeight));
	}
	
	/**
	 * Instantiates a new TileMap, as a constructor.
	 * @param size of this TileMap, in tiles.
	 * @param tileDrawSize to draw the tiles in this TileMap, in pixels.
	 */
	public TileMap(IExtent size, IExtent tileDrawSize) {
		tiles = new Tile[size.getExtentWidth()][size.getExtentHeight()];
		this.size = IRegion.wrap(size);
		this.tileDrawSize = IRegion.wrap(tileDrawSize);
	}
	
	/**
	 * Sets the tile at the specified tile coordinate.
	 * @param x component of the coordinate pair
	 * @param y component of the coordinate pair
	 * @param t tile to assign inside this TileMap.
	 */
	public void set(int x, int y, Tile t) {
		tiles[x][y] = t;
	}
	
	/**
	 * Returns a tile at the given tile coordinate.
	 * @param x component of the coordinate pair
	 * @param y component of the coordinate pair.
	 * @return the tile reference for the given location.
	 */
	public Tile get(int x, int y) {
		return tiles[x][y];
	}
	
	/**
	 * Draws this tilemap using the specified graphics object.
	 * @param gfx the graphics object with which to draw.
	 */
	@Override
	public void draw(Graphics2D gfx) {
		//TODO: Check for performance of this.
		for(int y = 0; y < size.getExtentHeight(); y++) {
			for(int x = 0; x < size.getExtentWidth(); x++) {
				if(tiles[x][y] == null)
					continue;
				
				Sprite spr = tiles[x][y].getSpriteProvider().getNextSprite();
				
				//destination top left corner
				final int dx1 = x * tileDrawSize.getExtentWidth();
				final int dy1 = y * tileDrawSize.getExtentHeight();
				//destination bottom right corner
				final int dx2 = dx1 + tileDrawSize.getExtentWidth();
				final int dy2 = dy1 + tileDrawSize.getExtentHeight();
				
				//source top left corner
				final int sx1 = spr.getPositionX();
				final int sy1 = spr.getPositionY();
				//source bottom right corner.
				final int sx2 = sx1 + spr.getExtentWidth();
				final int sy2 = sy1 + spr.getExtentHeight();
				
				gfx.drawImage(spr.getImageProvider().getImage(),
						dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
			}
		}
	}

	/**
	 * @return the width of this TileMap, in tiles.
	 */
	@Override
	public int getExtentWidth() {
		return size.getExtentWidth();
	}

	/**
	 * @return the height of this TileMap, in tiles.
	 */
	@Override
	public int getExtentHeight() {
		return size.getExtentHeight();
	}
	
	/**
	 * @return the drawn width of this TileMap, in pixels.
	 */
	public int getDrawExtentWidth() {
		return size.getExtentWidth() * tileDrawSize.getExtentWidth();
	}
	
	/**
	 * @return the drawn height of this TileMap, in pixels.
	 */
	public int getDrawExtentHeight() {
		return size.getExtentHeight() * tileDrawSize.getExtentHeight();
	}
	
	/**
	 * @return a rectangle of the drawable bounds of this tilemap.
	 */
	public Rectangle getDrawBounds() {
		return new Rectangle(getDrawExtentWidth(), getDrawExtentHeight());
	}
	
	/**
	 * @return the region of the tile draw size, in pixels.
	 */
	public IRegion getTileDrawSize() {
		return tileDrawSize;
	}
}