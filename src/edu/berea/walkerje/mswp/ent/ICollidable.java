package edu.berea.walkerje.mswp.ent;

import edu.berea.walkerje.mswp.IRegion;

public interface ICollidable extends IRegion{
	/**
	 * Try to collide with the specified collidable. On success, calls onCollide(...) member function.
	 * @param other collidable to test against
	 * @return a boolean indicating if a collision occured.
	 */
	public default boolean tryCollide(ICollidable other) {
		if(collides(other)) {
			onCollide(other);
			return true;
		}
		return false;
	}
	
	/**
	 * Called when this collidable has been "hit" by another.
	 * @param other collidable which has "hit" this collidable
	 */
	void onCollide(ICollidable other);
	
	/**
	 * @param other collidable to test against
	 * @return a boolean indicating if this collidable is inside the other
	 */
	public default boolean collides(ICollidable other) {
		if(Math.abs(getPositionX() - other.getPositionX()) < (getExtentWidth() + other.getExtentWidth()) / 2)
			if(Math.abs(getPositionY() - other.getPositionY()) < (getExtentHeight() + other.getExtentHeight()) / 2)
	            return true;
		return false;
	}
	
	/**
	 * @param other collidable to test against
	 * @return a boolean indicating if this collidable is inside the other
	 */
	public default boolean inside(ICollidable other) {
		if(Math.abs(getPositionX() - other.getPositionX()) < getExtentWidth() / 2)
			if(Math.abs(getPositionY() - other.getPositionY()) < getExtentHeight() / 2)
				return true;
		return false;
	}
}