package edu.berea.walkerje.mswp.gfx;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import edu.berea.walkerje.mswp.EAssetType;
import edu.berea.walkerje.mswp.IAsset;
import edu.berea.walkerje.mswp.IExtent;
import edu.berea.walkerje.mswp.IRegion;

public class Spritesheet implements IImageProvider, IAsset, IExtent{
	private BufferedImage image;
	
	private String assetName = "NOT NAMED";
	private Map<String, ISpriteProvider> sprites;
	
	private Spritesheet() {
		sprites = new HashMap<String, ISpriteProvider>();
	}
	
	public Spritesheet(String assetName, BufferedImage image) {
		this.assetName = assetName;
		this.image = image;
		
		sprites = new HashMap<String, ISpriteProvider>();
	}
	
	public Sprite getLooseSprite(IRegion region) {
		return new Sprite(region, this);
	}
	
	public ISpriteProvider getSprite(String name) {
		return sprites.get(name);
	}
	
	public void addSprite(String name, ISpriteProvider provider) {
		sprites.put(name, provider);
	}
	
	/**
	 * Returns the instance of the sprite registered with the specified name.
	 * @param name
	 * @return
	 */
	public ISpriteProvider get(String name) {
		return sprites.get(name);
	}
	
	public final EAssetType getAssetType() {
		return EAssetType.SPRITESHEET;
	}
	
	/**
	 * @return the name of this spritesheet asset.
	 */
	public String getAssetName() {
		return assetName;
	}
	
	/**
	 * @return the width, in pixels, of this spritesheet.
	 */
	public int getExtentWidth() {
		return image.getWidth();
	}
	
	/**
	 * @return the height, in pixels, of this spritesheet.
	 */
	public int getExtentHeight() {
		return image.getHeight();
	}
	
	public Map<String, ISpriteProvider> getSprites(){
		return sprites;
	}

	@Override
	public Image getImage() {
		return image;
	}

	@Override
	public void serializeProject(File projectDir, JsonObject obj) {
		obj.put("imagePath", String.format("%s.png", assetName));
		JsonObject spriteListObj = new JsonObject();
		
		sprites.forEach((k, v)->{
			if(v instanceof SpriteAnimation) {
				JsonArray spriteArray = new JsonArray();
				SpriteAnimation spriteAnim = (SpriteAnimation)v;
				
				spriteAnim.getFrames().forEach((f)->{
					Sprite sprite = f.sprite.getCurrentSprite();
					final long duration = f.duration;
					
					JsonObject spriteObj = new JsonObject();
					JsonObject boundsObj = new JsonObject();
					boundsObj.put("originX", sprite.getPositionX());
					boundsObj.put("originY", sprite.getPositionY());
					boundsObj.put("widthPx", sprite.getExtentWidth());
					boundsObj.put("heightPx", sprite.getExtentHeight());
					spriteObj.put("bounds", boundsObj);
					spriteObj.put("duration", duration);
					
					spriteArray.add(spriteObj);
				});
				spriteListObj.put(k, spriteArray);
			}else if(v instanceof Sprite) {
				JsonObject spriteObj = new JsonObject();
				Sprite sprite = (Sprite)v;
				spriteObj.put("originX", sprite.getPositionX());
				spriteObj.put("originY", sprite.getPositionY());
				spriteObj.put("widthPx", sprite.getExtentWidth());
				spriteObj.put("heightPx", sprite.getExtentHeight());
				spriteListObj.put(k, spriteObj);
			}
		});
		
		obj.put("sprites", spriteListObj);
		try {
			ImageIO.write(image, "png", new File(projectDir, String.format("/assets/spritesheets/%s.png", assetName)));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deserializeProject(File projectDir, String assetName, JsonObject obj) {
		this.assetName = assetName;
		try {
			image = GameCanvas.copyCompatImage(ImageIO.read(new File(projectDir, String.format("/assets/spritesheets/%s", (String)obj.get("imagePath")))));
			obj.remove("imagePath");
			JsonObject spriteCollection = (JsonObject)obj.get("sprites");
			spriteCollection.forEach((k,v)->{
				if(!(v instanceof JsonArray)) {
					JsonObject spriteRoot = (JsonObject)v;
					int spriteWidth = ((BigDecimal)(spriteRoot.get("widthPx"))).intValue();
					int spriteHeight = ((BigDecimal)(spriteRoot.get("heightPx"))).intValue();
					int spriteX = ((BigDecimal)(spriteRoot.get("originX"))).intValue();
					int spriteY = ((BigDecimal)(spriteRoot.get("originY"))).intValue();
					addSprite(k, new Sprite(spriteX, spriteY, spriteWidth, spriteHeight, this));
				}else {
					JsonArray spriteArray = (JsonArray)v;
					SpriteAnimation animation = new SpriteAnimation();
					
					spriteArray.forEach((entry)->{
						JsonObject spriteEntry = (JsonObject)entry;
						JsonObject boundsEntry = (JsonObject)spriteEntry.get("bounds");
						int spriteWidth = ((BigDecimal)(boundsEntry.get("widthPx"))).intValue();
						int spriteHeight = ((BigDecimal)(boundsEntry.get("heightPx"))).intValue();
						int spriteX = ((BigDecimal)(boundsEntry.get("originX"))).intValue();
						int spriteY = ((BigDecimal)(boundsEntry.get("originY"))).intValue();
						long duration = ((BigDecimal)(spriteEntry.get("duration"))).longValue();
						animation.addFrame(SpriteAnimation.createFrame(new Sprite(spriteX, spriteY, spriteWidth, spriteHeight, this), duration));
					});
					
					addSprite(k, animation);
				}
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Spritesheet projectRead(File projectDir, String assetName, JsonObject obj) {
		Spritesheet sheet = new Spritesheet();
		sheet.deserializeProject(projectDir, assetName, obj);
		return sheet;
	}
}