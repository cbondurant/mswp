package edu.berea.walkerje.mswp.gfx;

import java.util.ArrayList;

public class SpriteAnimation implements ISpriteProvider{
	//Frame bean class for individual animation frames.
	public static class Frame{
		public ISpriteProvider sprite = null;
		public long duration = 0;
	}
	
	//random access isn't needed, just a sequence.
	private ArrayList<Frame> frames = new ArrayList<Frame>();
	private int curFrame = 0;
	private long lastQuery = System.currentTimeMillis();
	
	public SpriteAnimation() {}
	
	public void addFrame(Frame frame) {
		frames.add(frame);
	}
	
	/**
	 * Resets the current animation frame (sets an internal index to 0)
	 */
	public void reset() {
		curFrame = 0;
		lastQuery = System.currentTimeMillis();
		//lastQuery would be mentioned in the comment, if it was a public member.
	}
	
	/**
	 * @return the ordinal index of the current frame.
	 */
	public int getCurrentFrameIndex() {
		return curFrame;
	}
	
	/**
	 * Sets the current frame index.
	 * @param fr
	 */
	void setCurrentFrameIndex(int fr) {
		curFrame = fr;
	}
	
	/**
	 * Returns the current sprite in this animation.
	 * Does nothing in regards to the progress of the animation itself.
	 */
	@Override
	public Sprite getCurrentSprite() {
		return frames.get(curFrame).sprite.getCurrentSprite();
	}
	
	/**
	 * @return the length, in milliseconds, of this animation.
	 */
	public long getAnimationLength() {
		long len = 0;
		for(Frame fr : frames)
			len += fr.duration;
		return len;
	}

	/**
	 * @return the next sprite in this animation, depending on its duration.
	 */
	@Override
	public Sprite getNextSprite() {
		if(System.currentTimeMillis() >= frames.get(curFrame).duration + lastQuery) {
			//If enough time has passed, increment the frame counter.
			//This includes a check to make sure we don't get an invalid
			//index while we do so.
			if(curFrame + 1 >= frames.size()) curFrame = 0;
			else curFrame++;
			lastQuery = System.currentTimeMillis();
		}
		
		return frames.get(curFrame).sprite.getCurrentSprite();
	}

	/**
	 * @return the total number of frames in this sprite animation.
	 */
	@Override
	public int getTotalSprites() {
		return frames.size();
	}

	/**
	 * @return the internal arraylist used to hold the individual frames.
	 */
	public ArrayList<Frame> getFrames(){
		return frames;
	}
	
	public static Frame createFrame(ISpriteProvider prov, long duration) {
		Frame f = new Frame();
		f.sprite = prov;
		f.duration = duration;
		return f;
	}
}