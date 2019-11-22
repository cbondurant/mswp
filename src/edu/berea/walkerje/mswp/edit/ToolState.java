package edu.berea.walkerje.mswp.edit;

import java.awt.Point;
import java.awt.Rectangle;

import edu.berea.walkerje.mswp.gfx.Tile;
import edu.berea.walkerje.mswp.gfx.Tilesheet;
import edu.berea.walkerje.mswp.play.GameLevel;

/**
 * The ToolState class holds all information about what the client is choosing to do
 * as far as tools go. It holds things like the current editing level,
 * current tile/spritesheet/stamp/layer, selection info, and options.
 */
public class ToolState {
	
	private EToolType current;
	private EToolType previous;
	
	private GameLevel editingLevel;
	
	private int currentLayer = GameLevel.LAYER_MIDGROUND;
	
	private Tilesheet currentTilesheet;
	private Tile currentTile;
	private TileStamp currentStamp;
	
	private Point currentTileCoord;
	
	private boolean snapSelectionToTiles = false;
	
//	private boolean selectSprites = false;
	
	private boolean selecting = false;
	private Point selectionBegin = null;
	private Point selectionEnd = null;
	
	/**
	 * Initializes a tool state, defaulting to the specified tool.
	 * @param t the default starting tool.
	 */
	public ToolState(EToolType t) {
		this.current = t;
		this.previous = t;
	}
	
	/**
	 * Sets the current level the tool state is working on.
	 * @param l
	 */
	public void setCurrentLevel(GameLevel l) {
		this.editingLevel = l;
	}
	
	/**
	 * Returns the current level the tool state is working on.
	 * @return
	 */
	public GameLevel getCurrentLevel() {
		return this.editingLevel;
	}
	
	/**
	 * Returns the current selection bounds, in pixels, and in frame space.
	 * Must be converted to either tile or level space at another point.
	 * @return
	 */
	public Rectangle getSelectionBounds() {
		if(selectionEnd == null || selectionBegin == null)
			return null;
		
		final int upperX = (selectionBegin.x > selectionEnd.x ? selectionBegin.x : selectionEnd.x);
		final int upperY = selectionBegin.y > selectionEnd.y ? selectionBegin.y : selectionEnd.y;
		final int lowerX = selectionBegin.x < selectionEnd.x ? selectionBegin.x : selectionEnd.x;
		final int lowerY = selectionBegin.y < selectionEnd.y ? selectionBegin.y : selectionEnd.y;
		return new Rectangle(lowerX, lowerY, upperX - lowerX, upperY - lowerY);
	}
	
	/**
	 * Returns the current stamp.
	 * @return
	 */
	public TileStamp getCurrentStamp() {
		return currentStamp;
	}
	
	/**
	 * Sets the current stamp.
	 * @param currentStamp
	 */
	public void setCurrentStamp(TileStamp currentStamp) {
		this.currentStamp = currentStamp;
	}
	
	/**
	 * Returns a boolean indicating if the specified tile point is within the current selection's bounds.
	 * @param p
	 * @return
	 */
	public boolean isPointInSelection(Point p) {
		final Rectangle r = getSelectionBounds();
		return r != null ? getSelectionBounds().contains(p) : true;
	}
	
	/**
	 * Sets a boolean indicating whether nor not newly created selections should snap to their nearest tiles.
	 * @param val
	 */
	public void setSelectionSnapToTile(boolean val) {
		this.snapSelectionToTiles = val;
	}
	
	/**
	 * @return a boolean indicating whether nor not newly created selections should snap to their nearest tiles.
	 */
	public boolean getSelectionSnapToTile() {
		return this.snapSelectionToTiles;
	}
	
	/**
	 * Sets a boolean indicating whether or not a selection is in the process of being created.
	 * @param val
	 */
	public void setSelectionState(boolean val) {
		this.selecting = val;
	}
	
	/**
	 * @return a boolean indicating whether or not a selection is in the process of being created.
	 */
	public boolean getSelectionState() {
		return this.selecting;
	}
	
	/**
	 * Sets the first point created during a selection.
	 * @param p
	 */
	public void setSelectionBegin(Point p) {
		this.selectionBegin = p;
	}
	
	/**
	 * @return the first point created during a selection.
	 */
	public Point getSelectionBegin() {
		return this.selectionBegin;
	}

	/**
	 * Sets the second point created during a selection.
	 * @param p
	 */
	public void setSelectionEnd(Point p) {
		this.selectionEnd = p;
	}
	
	/**
	 * @return the second point created during a selection.
	 */
	public Point getSelectionEnd() {
		return this.selectionEnd;
	}
	
	/**
	 * Returns the current working layer.
	 * @return
	 */
	public int getCurrentLayer() {
		return currentLayer;
	}
	
	/**
	 * Sets the current working layer.
	 * @param curLayer
	 */
	public void setCurrentLayer(int curLayer) {
		this.currentLayer = curLayer;
	}
	
	/**
	 * Sets the currently selected tilesheet.
	 * It is from this sheet that tiles are chosen.
	 * @param sht
	 */
	public void setCurrentTilesheet(Tilesheet sht) {
		currentTilesheet = sht;
	}
	
	/**
	 * @return the currently selected tilesheet.
	 */
	public Tilesheet getCurrentTilesheet() {
		return currentTilesheet;
	}
	
	/**
	 * Sets the currently hovered tile coordinate inside the current level.
	 */
	public void setCurrentTileCoord(Point p) {
		this.currentTileCoord = p;
	}
	
	/**
	 * @return the currently hovered tile coordinate inside the current level.
	 */
	public Point getCurrentTileCoord() {
		return currentTileCoord;
	}
	
	/**
	 * Sets the currently selected tile, used for pencil and fill operations.
	 * @param t
	 */
	public void setCurrentTile(Tile t) {
		this.currentTile = t;
	}
	
	/**
	 * @return the currently selected tile, used for pencil and fill operations.
	 */
	public Tile getCurrentTile() {
		return currentTile;
	}
	
	
	/**
	 * Sets the current tool.
	 * @param type
	 */
	public void setTool(EToolType type) {
		previous = current;
		current = type;
	}
	
	/**
	 * Returns the current tool.
	 * @return
	 */
	public EToolType getTool() {
		return current;
	}
	
	/**
	 * Returns the previous tool.
	 * @return
	 */
	public EToolType getPreviousTool() {
		return previous;
	}
}