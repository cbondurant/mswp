package edu.berea.walkerje.mswp.edit.gui;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import edu.berea.walkerje.mswp.MSWPApp;
import edu.berea.walkerje.mswp.edit.EToolType;
import edu.berea.walkerje.mswp.edit.TileStamp;
import edu.berea.walkerje.mswp.edit.ToolState;
import edu.berea.walkerje.mswp.gfx.GameCanvas;
import edu.berea.walkerje.mswp.gfx.Sprite;
import edu.berea.walkerje.mswp.gfx.Tile;
import edu.berea.walkerje.mswp.gfx.TileMap;
import edu.berea.walkerje.mswp.play.GameLevel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class GameLevelEditorPanel extends JPanel{
	private static final long serialVersionUID = -8926205393760399843L;
	
	private GameLevel level;
	private float scale = 3.0f;
	
	private BufferedImage tileBg;
	
	private Point hoveredTile = null;
	
	private ToolState toolState;
	
	private boolean showGrid = false;
	
	//Holds the rendered tilemaps, updated by the editor as necessary.
	private BufferedImage levelImageBuffer;
	
	/**
	 * Create the panel.
	 */
	public GameLevelEditorPanel(ToolState toolState, GameLevel level) {
		this.level = level;
		this.toolState = toolState;
		levelImageBuffer = GameCanvas.createBufferedImage(level.getTilesX() * level.getTilesDrawWidth(), level.getTilesY() * level.getTilesDrawHeight(), true);
		repaintLevel();
		try {
			tileBg = ImageIO.read(MSWPApp.readAsset("tilebg.png"));
		}catch(Exception e) {}
	}
	

	/**
	 * Returns the size of this editor panel, in pixels.
	 * Takes into account the scale.
	 */
	public Dimension getPreferredSize() {
		return getSize();
	}
	
	/**
	 * Returns the size of this editor panel, in pixels.
	 * Takes into account the scale.
	 */
	public Dimension getSize() {
		return new Dimension((int)(level.getTilesX() * level.getTilesDrawWidth() * scale), (int)(level.getTilesY() * level.getTilesDrawHeight() * scale));
	}

	/**
	 * @return the level being edited.
	 */
	public GameLevel getLevel() {
		return level;
	}

	/**
	 * @return the drawing scale of the level being edited.
	 */
	public float getScale() {return scale;}
	/**
	 * Sets the view scale of this level pane.
	 * @param sc
	 */
	public void setScale(float sc) {
		scale = sc;
	}
	
	/**
	 * Sets a boolean indicating if an overlaying tile grid should be drawn on top of the level.
	 * @param showGrid
	 */
	public void setShowGrid(boolean showGrid) {
		if(showGrid != this.showGrid) {
			this.showGrid = showGrid;
			repaint();
		}else this.showGrid = showGrid;
	}
	
	/**
	 * Returns a level-scope coordinate derived from a panel-scope coordinate.
	 * @param frameX component of the coordinate pair
	 * @param frameY component of the coordinate pair
	 * @return
	 */
	public Point getLevelCoord(int frameX, int frameY) {
		return new Point((int)(frameX / scale), (int)(frameY / scale));
	}
	
	/**
	 * Returns a tile coordinate from the specified panel-scope coordinate.
	 * @param frameX component of the coordinate pair
	 * @param frameY component of the coordinate pair
	 * @return
	 */
	public Point getTileCoord(int frameX, int frameY) {
		return new Point((int)((frameX / scale) / level.getTilesDrawWidth()), (int)((frameY / scale) / level.getTilesDrawHeight()));
	}
	
	/**
	 * Sets the currently hovered tile.
	 * @param p
	 */
	public void setHoveredTile(Point p) {
		this.hoveredTile = p;
	}
	
	/**
	 * Repaints the level's persistent bitmap, of all the tiles.
	 */
	public void repaintLevel() {
		Graphics2D g = levelImageBuffer.createGraphics();
		g.setBackground(new Color(255,255,255,0));
		g.clearRect(0, 0, levelImageBuffer.getWidth(), levelImageBuffer.getHeight());
		
		for(int i = 0; i < 3; i++) {
			TileMap layer = level.getTileLayer(i);
			layer.draw(g);
		}
		
		g.dispose();
	}
	
	/**
	 * Repaints a single tile in the level's persistent bitmap.
	 * Actually repaints every tile at that position in each layer, back-to-front.
	 * @param tileCoord coordinate of the requested tile.
	 */
	public void repaintLevelTile(Point tileCoord) {
		if(!level.isValidTile(tileCoord))
			return;
		
		Graphics2D g = (Graphics2D)levelImageBuffer.getGraphics();
		g.setBackground(new Color(255,255,255,0));
		//background = clear color
		final Rectangle tileRect = new Rectangle(
				tileCoord.x * level.getTilesDrawWidth(), tileCoord.y * level.getTilesDrawHeight(), level.getTilesDrawWidth(), level.getTilesDrawHeight());
		
		g.clearRect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
		
		//destination top left corner
		final int dx1 = tileRect.x;
		final int dy1 = tileRect.y;
		//destination bottom right corner
		final int dx2 = dx1 + tileRect.width;
		final int dy2 = dy1 + tileRect.height;
		
		for(int i = 0; i < 3; i++) {
			TileMap layer = level.getTileLayer(i);
			Tile tile = layer.get(tileCoord.x, tileCoord.y);
			if(tile == null)
				continue;
			
			Sprite cur = tile.getSpriteProvider().getNextSprite();
			
			//source top left corner
			final int sx1 = cur.getPositionX();
			final int sy1 = cur.getPositionY();
			//source bottom right corner.
			final int sx2 = sx1 + cur.getExtentWidth();
			final int sy2 = sy1 + cur.getExtentHeight();
			
			g.drawImage(cur.getImageProvider().getImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
		}
	}
	
	/**
	 * Paints this level editor panel.
	 */
	public void paint(Graphics g) {
		super.paintComponent(g);
		
		final int mapWidth = level.getTilesDrawWidth() * level.getTilesX();
		final int mapHeight = level.getTilesDrawHeight() * level.getTilesY();
		
		Graphics2D gt = (Graphics2D)g;
		gt.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		gt.scale(scale, scale);
		
		final int bgWidth = (int)(tileBg.getWidth());
		final int bgHeight = (int)(tileBg.getHeight());
		final int bgX = (int)(mapWidth / bgWidth);
		final int bgY = (int)(mapHeight / bgHeight);
		
		for(int y = 0; y < bgY; y++) {
			for(int x = 0; x < bgX; x++) {
				int xPos = bgWidth * x;
				int yPos = bgHeight * y;
				g.drawImage(tileBg, xPos, yPos, null);
			}
		}
		
		g.drawImage(levelImageBuffer, 0, 0, null);
		
		if(showGrid) {
			g.setColor(Color.RED);
			for(int y = 1; y < level.getTilesY(); y++) {
				final int yPos = y * level.getTilesDrawHeight();
				g.drawLine(0, yPos, mapWidth, yPos);
			}
			
			for(int x = 1; x < level.getTilesX(); x++) {
				final int xPos = x * level.getTilesDrawWidth();
				g.drawLine(xPos, 0, xPos, mapHeight);
			}
		}
		
		//We can cut the rendering short if this isn't the current level.
		//We compare pointers specifically for this.
		if(toolState.getCurrentLevel() != level)
			return;
		
		Rectangle selectRectangle = toolState.getSelectionBounds();
		if(selectRectangle != null) {
			gt.setColor(Color.MAGENTA);
			gt.drawRect(selectRectangle.x, selectRectangle.y, selectRectangle.width, selectRectangle.height);
		}
		
		if(hoveredTile != null) {
			if(toolState.getCurrentTile() != null && toolState.getTool() == EToolType.PENCIL) {
				Sprite cur = toolState.getCurrentTile().getSpriteProvider().getNextSprite();
				Graphics2D g2 = (Graphics2D)gt.create();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				
				//destination top left corner
				final int dx1 = hoveredTile.x * level.getTilesDrawWidth();
				final int dy1 = hoveredTile.y * level.getTilesDrawHeight();
				//destination bottom right corner
				final int dx2 = dx1 + level.getTilesDrawWidth();
				final int dy2 = dy1 + level.getTilesDrawHeight();
				
				//source top left corner
				final int sx1 = cur.getPositionX();
				final int sy1 = cur.getPositionY();
				//source bottom right corner.
				final int sx2 = sx1 + cur.getExtentWidth();
				final int sy2 = sy1 + cur.getExtentHeight();
				
				g2.drawImage(cur.getImageProvider().getImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
				g2.dispose();
			}else if(toolState.getCurrentStamp() != null && toolState.getTool() == EToolType.STAMP) {
				TileStamp cur = toolState.getCurrentStamp();
				Point hoverPoint = hoveredTile;
				
				Point stampTileOrigin = new Point();
				stampTileOrigin.x = hoverPoint.x - (int)(cur.getExtentWidth() / 2.0f);
				stampTileOrigin.y = hoverPoint.y - (int)(cur.getExtentHeight() / 2.0f);
				Point stampWorldOrigin = new Point(stampTileOrigin.x * level.getTilesDrawWidth(), stampTileOrigin.y * level.getTilesDrawHeight());
				
				Graphics2D g2 = (Graphics2D)gt.create();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				
				for(int tY = 0; tY < cur.getExtentHeight(); tY++) {
					for(int tX = 0; tX < cur.getExtentWidth(); tX++) {
						Tile curTile = cur.get(tX, tY);
						if(curTile == null)
							continue;
						
						Point tilePos = new Point(stampWorldOrigin.x + (tX * level.getTilesDrawWidth()), stampWorldOrigin.y + (tY * level.getTilesDrawHeight()));
						Sprite curSprite = curTile.getSpriteProvider().getNextSprite();
						
						final int dx1 = tilePos.x;
						final int dy1 = tilePos.y;
						//destination bottom right corner
						final int dx2 = dx1 + level.getTilesDrawWidth();
						final int dy2 = dy1 + level.getTilesDrawHeight();
						
						//source top left corner
						final int sx1 = curSprite.getPositionX();
						final int sy1 = curSprite.getPositionY();
						//source bottom right corner.
						final int sx2 = sx1 + curSprite.getExtentWidth();
						final int sy2 = sy1 + curSprite.getExtentHeight();
						
						g2.drawImage(curSprite.getImageProvider().getImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
					}
				}
			}
			
			if(toolState.getTool() != EToolType.RECT_SELECT) {
				g.setXORMode(Color.white);
				
				final int rX = hoveredTile.x * level.getTilesDrawWidth();
				final int rY = hoveredTile.y * level.getTilesDrawHeight();
				
				g.drawRect(rX, rY, level.getTilesDrawWidth(), level.getTilesDrawHeight());
			}
		}
	}
}
