package edu.berea.walkerje.mswp.gfx;

import java.awt.Image;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.imageio.ImageIO;

import com.github.cliftonlabs.json_simple.JsonObject;

import edu.berea.walkerje.mswp.EAssetType;
import edu.berea.walkerje.mswp.IAsset;
import edu.berea.walkerje.mswp.IExtent;
import edu.berea.walkerje.mswp.IRegion;

public class Tilesheet implements IImageProvider, IAsset{
	private VolatileImage image;
	private String assetName = "UNNAMED";
	private IRegion tileSz;
	private int tilesX, tilesY;
	
	private Tile[][] tiles;
	
	private Tilesheet() {}
	
	public Tilesheet(String assetName, VolatileImage image, IExtent tileSz) {
		this.tileSz = IRegion.wrap(tileSz);
		this.image = image;
		this.assetName = assetName;
		tilesX = image.getWidth() / tileSz.getExtentWidth();
		tilesY = image.getHeight() / tileSz.getExtentHeight();
		this.tiles = new Tile[tilesX][tilesY];
		
		for(int y = 0; y < tilesY; y++) {
			for(int x = 0; x < tilesX; x++) {
				tiles[x][y] = new Tile(spriteFor(x,y), this);
			}
		}
	}

	@Override
	public void serializeProject(File projectDir, JsonObject obj) {
		obj.put("tileWidthPx", tileSz.getExtentWidth());
		obj.put("tileHeightPx", tileSz.getExtentHeight());
		obj.put("imagePath", String.format("%s.png", assetName));
		try {
			final File outputFile = new File(projectDir, String.format("/assets/tilesheets/%s.png", assetName));
			outputFile.getParentFile().mkdirs();
			ImageIO.write(image.getSnapshot(), "png", outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deserializeProject(File projectDir, String assetName, JsonObject obj) {
		this.assetName = assetName;
		tileSz = IRegion.wrap(((BigDecimal)(obj.get("tileWidthPx"))).intValue(), ((BigDecimal)(obj.get("tileHeightPx"))).intValue());
		try {
			image = GameCanvas.copyBufferedImage(ImageIO.read(new File(projectDir, String.format("/assets/tilesheets/%s", (String)obj.get("imagePath")))));
			tilesX = image.getWidth() / tileSz.getExtentWidth();
			tilesY = image.getHeight() / tileSz.getExtentHeight();
			
			this.tiles = new Tile[tilesX][tilesY];
			
			for(int y = 0; y < tilesY; y++) {
				for(int x = 0; x < tilesX; x++) {
					tiles[x][y] = new Tile(spriteFor(x,y), this);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param x coordinate of the tile
	 * @param y coordinate of the tile
	 * @return the tile at the specified coordinate pair.
	 */
	public Tile get(int x, int y) {
		return tiles[x][y];
	}
	
	/**
	 * @param id of the tile
	 * @return the tile associated with the specified ID.
	 */
	public Tile get(int id) {
		return get(id % tilesX, id / tilesX);
	}
	
	/**
	 * Returns the sprite appropriate for the tile at the given tilesheet coordinate.
	 * @param x
	 * @param y
	 * @return
	 */
	public Sprite spriteFor(int x, int y) {
		return new Sprite(x*tileSz.getExtentWidth(), y*tileSz.getExtentHeight(), tileSz.getExtentWidth(), tileSz.getExtentHeight(), this);
	}
	
	@Override
	public int getExtentWidth() {
		return image.getWidth();
	}

	@Override
	public int getExtentHeight() {
		return image.getHeight();
	}
	
	/**
	 * @return how many tiles there are along the X axis
	 */
	public int getTilesX() {
		return tilesX;
	}
	
	/**
	 * @return how many tiles there are along the Y axis
	 */
	public int getTilesY() {
		return tilesY;
	}
	
	public int getTileExtentWidth() {
		return tileSz.getExtentWidth();
	}
	
	public int getTileExtentHeight() {
		return tileSz.getExtentHeight();
	}
	
	/**
	 * @return the extent of any given tile that resides in this tilesheet.
	 */
	public IExtent getTileExtent() {
		return tileSz;
	}

	@Override
	public Image getImage() {
		return image;
	}

	@Override
	public EAssetType getAssetType() {
		return EAssetType.TILESHEET;
	}

	@Override
	public String getAssetName() {
		return assetName;
	}
	
	public static Tilesheet projectRead(File projectDir, String assetName, JsonObject obj) {
		Tilesheet sheet = new Tilesheet();
		sheet.deserializeProject(projectDir, assetName, obj);
		return sheet;
	}
}