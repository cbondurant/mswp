package edu.berea.walkerje.mswp.edit;

public enum EToolType {
	//In retrospect, image paths are invalid.
	//As of now, they are of no significance.
	PENCIL			("Pencil", "pencil.png"),
	ERASER			("Eraser", "eraser.png"),
	FILL			("Fill", "paintbucket.png"),
	RECT_SELECT		("Select", "rectselect.png"),
	STAMP			("Stamp", "stamp.png"),
	EYEDROP			("Eyedropper", "dropper.png"),
	PUT_SPRITE		("Put Sprite", "putsprite.png");
	
	/*Display Name String*/
	public final String displayName;
	public final String imagePath;
	
	EToolType(String displayName, String imagePath){
		this.displayName = displayName;
		this.imagePath = imagePath;
	}
}