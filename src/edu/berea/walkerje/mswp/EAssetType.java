package edu.berea.walkerje.mswp;

public enum EAssetType {
	SPRITESHEET("Spritesheets"),
	TILESHEET("Tilesheets"),
	LEVEL("Levels"),
	TILE_STAMP("Stamps");
	
	public final String displayName;
	
	private EAssetType(String displayName) {
		this.displayName = displayName;
	}
	
	public String toString() {
		return displayName;
	}
}