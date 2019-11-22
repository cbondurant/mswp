package edu.berea.walkerje.mswp.gfx;

public interface ISpriteProvider {
	
	/**
	 * @return the current sprite offered by this provider.
	 */
	public Sprite getCurrentSprite();
	
	/**
	 * @return the next sprite in the sequence.
	 */
	public Sprite getNextSprite();
	
	/**
	 * @return the total number of sprites that reside in this provider.
	 */
	public int getTotalSprites();
}