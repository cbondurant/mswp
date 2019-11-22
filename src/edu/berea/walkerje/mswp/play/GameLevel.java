package edu.berea.walkerje.mswp.play;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.math.BigDecimal;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import edu.berea.walkerje.mswp.EAssetType;
import edu.berea.walkerje.mswp.IAsset;
import edu.berea.walkerje.mswp.edit.Project;
import edu.berea.walkerje.mswp.gfx.Sprite;
import edu.berea.walkerje.mswp.gfx.Tile;
import edu.berea.walkerje.mswp.gfx.TileMap;
import edu.berea.walkerje.mswp.gfx.Tilesheet;

public class GameLevel implements IAsset{
	public static final int LAYER_FOREGROUND = 2;
	public static final int LAYER_MIDGROUND = 1;
	public static final int LAYER_BACKGROUND = 0;
	
	private String name = "Unnamed";
	
	private int tilesX;
	private int tilesY;
	private int tilesDrawWidth;
	private int tilesDrawHeight;
	
	private TileMap[] tilemaps = new TileMap[3];
	
	private GameLevel(String assetName) {
		this.name = assetName;
	}
	
	public GameLevel(String name, int tilesX, int tilesY, int tilesDrawWidth, int tilesDrawHeight) {
		this.name = name;
		this.tilesX = tilesX;
		this.tilesY = tilesY;
		this.tilesDrawWidth = tilesDrawWidth;
		this.tilesDrawHeight = tilesDrawHeight;
		for(int i = 0; i < 3; i++)
			tilemaps[i] = new TileMap(tilesX, tilesY, tilesDrawWidth, tilesDrawHeight);	
	}
	
	public int getTilesX() {
		return tilesX;
	}
	
	public int getTilesY() {
		return tilesY;
	}
	
	public int getTilesDrawWidth() {
		return tilesDrawWidth;
	}
	
	public int getTilesDrawHeight() {
		return tilesDrawHeight;
	}
	
	public TileMap getTileLayer(int layerNum) {
		return tilemaps[layerNum];
	}
	
	public TileMap getForeground() {
		return tilemaps[LAYER_FOREGROUND];
	}
	
	public TileMap getBackground() {
		return tilemaps[LAYER_BACKGROUND];
	}
	
	public TileMap getMidground() {
		return tilemaps[LAYER_MIDGROUND];
	}
	
	public boolean isValidTile(Point tilePoint) {
		return (tilePoint.x > -1 && tilePoint.x < tilesX) && (tilePoint.y > -1 && tilePoint.y < tilesY);
	}
	
	public void paint(Graphics g) {
		Graphics2D gfx = (Graphics2D)g;

		for(int l = 0; l < 3; l++) {
			TileMap tiles = tilemaps[l];
			
			for(int y = 0; y < tilesY; y++) {
				for(int x = 0; x < tilesX; x++) {
					if(tiles.get(x, y) == null)
						continue;
					Sprite spr = tiles.get(x, y).getSpriteProvider().getNextSprite();

					//destination top left corner
					final int dx1 = x * tilesDrawWidth;
					final int dy1 = y * tilesDrawHeight;
					//destination bottom right corner
					final int dx2 = dx1 + tilesDrawWidth;
					final int dy2 = dy1 + tilesDrawHeight;
					
					//source top left corner
					final int sx1 = spr.getPositionX();
					final int sy1 = spr.getPositionY();
					//source bottom right corner.
					final int sx2 = sx1 + spr.getExtentWidth();
					final int sy2 = sy1 + spr.getExtentHeight();
					
					gfx.drawImage(spr.getImageProvider().getImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
				}
			}
		}
	}
	
	public String getAssetName() {
		return name;
	}
	
	public EAssetType getAssetType() {
		return EAssetType.LEVEL;
	}

	@Override
	public void serializeProject(File projectDir, JsonObject obj) {
		obj.put("tilesX", tilesX);
		obj.put("tilesY", tilesX);
		obj.put("tilesDrawWidth", tilesDrawWidth);
		obj.put("tilesDrawHeight", tilesDrawHeight);
		
		JsonArray layerArray = new JsonArray();
		
		for(int i = 0; i < 3; i++) {
			TileMap layer = tilemaps[i];
			JsonArray destArray = new JsonArray();
			
			for(int y = 0; y < tilesY; y++) {
				for(int x = 0; x < tilesX; x++) {
					if(layer.get(x, y) != null) {
						Tile t = layer.get(x, y);
						JsonObject tileObj = new JsonObject();
						final int id = t.getSourceTileID();
						tileObj.put("id", id);
						tileObj.put("source", t.getTilesheet().getAssetName());
						destArray.add(tileObj);
					}else destArray.add(null);//fill in a null object
				}
			}
			layerArray.add(destArray);
		}
		
		obj.put("tiles", layerArray);
	}

	@Override
	public void deserializeProject(File projectDir, String assetName, JsonObject obj) {
		tilesX = ((BigDecimal)(obj.get("tilesX"))).intValue();
		tilesY = ((BigDecimal)(obj.get("tilesY"))).intValue();
		tilesDrawWidth = ((BigDecimal)(obj.get("tilesDrawWidth"))).intValue();
		tilesDrawHeight = ((BigDecimal)(obj.get("tilesDrawHeight"))).intValue();
		
		JsonArray layerArray = (JsonArray)obj.get("tiles");

		for(int i = 0; i < 3; i++) {
			TileMap layer = new TileMap(tilesX, tilesY, tilesDrawWidth, tilesDrawHeight);
			JsonArray tileArray = (JsonArray)layerArray.get(i);

			for(int j = 0; j < tileArray.size(); j++) {
				JsonObject tileObj = (JsonObject) tileArray.get(j);
				if(tileObj == null)
					continue;
				final int tileID = ((BigDecimal)(tileObj.get("id"))).intValue();
				final String tsSourceName = (String)tileObj.get("source");
				
				Tilesheet ts = (Tilesheet)Project.getCurrentProject().getAsset(EAssetType.TILESHEET, tsSourceName);
				final int x = j % tilesX;
				final int y = j / tilesX;
				layer.set(x, y, ts.get(tileID));
			}
			
			tilemaps[i] = layer;
		}
	}
	
	public static GameLevel projectRead(File projectDir, String assetName, JsonObject obj) {
		GameLevel level = new GameLevel(assetName);
		level.deserializeProject(projectDir, assetName, obj);
		return level;
	}
}