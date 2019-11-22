package edu.berea.walkerje.mswp.ent;

import java.awt.Graphics2D;

import edu.berea.walkerje.mswp.IRegion;
import edu.berea.walkerje.mswp.gfx.IDrawable;

public class EntityBase implements IRegion, IDrawable{
	protected int posX, posY, width, height;
	
	private long lastTickTime = System.currentTimeMillis();
	
	public EntityBase(){}
	
	@Override
	public void draw(Graphics2D gfx) {
		//By default, translate the graphics object to the upper-left origin of the bounds of this entity.
		gfx.translate(posX - (width / 2), posY - (height / 2));
	}
	
	/**Updates this entity.
	 * @param dt delta time between frames.*/
	public void update(float dt) {
		if(lastTickTime + getTickPeriod() >= System.currentTimeMillis()) {
			onTick();
			lastTickTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * @return the tick period (in milliseconds) of this entity. controls how often "onTick" is called.
	 */
	public long getTickPeriod() {
		return 1000;//Tick once every second by default.
	}
	
	/**
	 * Called on tick. Rate depends on what is returned as the tick period of the entity.
	 * @param progress percentage of the way through any given whole second (0...1)
	 */
	public void onTick() {}

	@Override
	public int getPositionX() {
		return posX;
	}

	@Override
	public int getPositionY() {
		return posY;
	}

	@Override
	public int getExtentWidth() {
		return width;
	}

	@Override
	public int getExtentHeight() {
		return height;
	}
}
