package edu.berea.walkerje.mswp.edit;

import java.io.File;
import java.math.BigDecimal;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import edu.berea.walkerje.mswp.EAssetType;
import edu.berea.walkerje.mswp.IAsset;
import edu.berea.walkerje.mswp.IExtent;
import edu.berea.walkerje.mswp.IRegion;
import edu.berea.walkerje.mswp.gfx.Tile;
import edu.berea.walkerje.mswp.gfx.TileMap;
import edu.berea.walkerje.mswp.gfx.Tilesheet;

public class TileStamp implements IExtent, IAsset{
	private int tilesX, tilesY;
	private Tile[][] tiles;
	private String name;
	
	private int tileDrawWidth, tileDrawHeight;
	
	/**
	 * Private empty constructor for reading stamps from a project.
	 */
	private TileStamp() {}
	
	/**
	 * Construct a new tile.
	 * @param name name of the tile
	 * @param tilesX tiles along the X axis
	 * @param tilesY tiles along the Y axis
	 * @param tileDrawWidth the drawn width of a tile
	 * @param tileDrawHeight the drawn height of a tile.
	 */
	public TileStamp(String name, int tilesX, int tilesY, int tileDrawWidth, int tileDrawHeight) {
		this.name = name;
		this.tilesX = tilesX;
		this.tilesY = tilesY;
		this.tiles = new Tile[tilesX][tilesY];
		this.tileDrawWidth = tileDrawWidth;
		this.tileDrawHeight = tileDrawHeight;
	}
	
	/**
	 * Returns the tile at the given coordinate.
	 * @param x component of the coordinate pair.
	 * @param y component of the coordinate pair.
	 * @return a tile
	 */
	public Tile get(int x, int y) {
		return tiles[x][y];
	}
	
	/**
	 * Sets the tile at the given coordinate.
	 * @param x component of the coordinate pair.
	 * @param y component of the coordinate pair.
	 * @param t the tile to set inside this stamp.
	 */
	public void set(int x, int y, Tile t) {
		tiles[x][y] = t;
	}
	
	/**
	 * Returns how many tiles there are on the X axis.
	 */
	@Override
	public int getExtentWidth() {
		return tilesX;
	}

	/**
	 * Returns how many tiles there are on the Y axis.
	 */
	@Override
	public int getExtentHeight() {
		return tilesY;
	}
	
	/**
	 * @return the drawn width of a single tile.
	 */
	public int getTileDrawWidth() {
		return tileDrawWidth;
	}
	
	/**
	 * Sets the drawn width of a single tile.
	 * @param tileDrawWidth
	 */
	public void setTileDrawWidth(int tileDrawWidth) {
		this.tileDrawWidth = tileDrawWidth;
	}
	
	/**
	 * @return the drawn height of a single tile.
	 */
	public int getTileDrawHeight() {
		return tileDrawHeight;
	}
	
	/**
	 * Sets the drawn height of a single tile.
	 * @param tileDrawHeight
	 */
	public void setTileDrawHeight(int tileDrawHeight) {
		this.tileDrawHeight = tileDrawHeight;
	}

	@Override
	public EAssetType getAssetType() {
		return EAssetType.TILE_STAMP;
	}

	/**
	 * Serializes this tile stamp given the specified object.
	 * @param projectDir project root directory
	 * @param obj JSON object to serialize to.
	 */
	@Override
	public void serializeProject(File projectDir, JsonObject obj) {
		obj.put("tilesX", tilesX);
		obj.put("tilesY", tilesY);
		obj.put("tileDrawWidth", tileDrawWidth);
		obj.put("tileDrawHeight", tileDrawHeight);
		
		JsonArray tileArray = new JsonArray();
		for(int y = 0; y < tilesY; y++) {
			for(int x = 0; x < tilesX; x++) {
				if(tiles[x][y] == null)
					tileArray.add(null);
				else{
					JsonObject tileObj = new JsonObject();
					Tile tile = tiles[x][y];
					
					tileObj.put("source", tile.getTilesheet().getAssetName());
					tileObj.put("id", tile.getSourceTileID());
					tileArray.add(tileObj);
				}
			}
		}
		obj.put("tiles", tileArray);
	}

	/**
	 * Deserializes this tile stamp from the given project information
	 * @param projectDir of the project. May be null.
	 * @param assetName of the stamp.
	 * @param obj 
	 */
	@Override
	public void deserializeProject(File projectDir, String assetName, JsonObject obj) {
		this.name = assetName;
		tilesX = ((BigDecimal)obj.get("tilesX")).intValue();
		tilesY = ((BigDecimal)obj.get("tilesY")).intValue();
		tileDrawWidth = ((BigDecimal)obj.get("tileDrawWidth")).intValue();
		tileDrawHeight = ((BigDecimal)obj.get("tileDrawHeight")).intValue();
		tiles = new Tile[tilesX][tilesY];
		
		JsonArray tileArray = (JsonArray)obj.get("tiles");
		for(int i = 0; i < tileArray.size(); i++) {
			final int destX = i % tilesX;//Convert from a tile ID to a tile coordinate.
			final int destY = i / tilesX;
			JsonObject tileObj = (JsonObject)tileArray.get(i);
			if(tileObj != null) {
				Tilesheet ts = (Tilesheet) Project.getCurrentProject().getAsset(EAssetType.TILESHEET, (String)tileObj.get("source"));
				tiles[destX][destY] = ts.get(((BigDecimal)tileObj.get("id")).intValue());
			}
		}
	}
	
	/**
	 * @return the name of this asset.
	 */
	@Override
	public String getAssetName() {
		return this.name;
	}
	
	/**
	 * Sets the name of this asset.
	 * @param val
	 */
	public void setAssetName(String val) {
		this.name = val;
	}
	
	/**
	 * Extracts a tile stamp from a given tile map.
	 * @param name of the stamp.
	 * @param map from which to derive the stamp's tiles.
	 * @param tileRegion a region, in tile space (e.g, tile coordinates).
	 * @return new stamp
	 */
	public static TileStamp extract(String name, TileMap map, IRegion tileRegion) {
		final IRegion drawSz = map.getTileDrawSize();
		TileStamp st = new TileStamp(name, tileRegion.getExtentWidth(), tileRegion.getExtentHeight(), drawSz.getExtentWidth(), drawSz.getExtentHeight());
		int destX = 0, destY = 0;
		for(int y = tileRegion.getPositionY(); y < tileRegion.getPositionY() + tileRegion.getExtentHeight(); y++) {
			destX = 0;
			for(int x = tileRegion.getPositionX(); x < tileRegion.getPositionX() + tileRegion.getExtentWidth(); x++) {
				st.tiles[destX][destY] = map.get(x, y);
				destX++;
			}
			destY++;
		}
		return st;
	}
	
	/**
	 * Reads this stamp from a given project.
	 * @param projectDir -- project root directory.
	 * @param assetName -- the name of the asset.
	 * @param obj -- the object to read from.
	 * @return the deserialized stamp.
	 */
	public static TileStamp projectRead(File projectDir, String assetName, JsonObject obj) {
		TileStamp ts = new TileStamp();
		ts.deserializeProject(projectDir, assetName, obj);
		return ts;
	}
}