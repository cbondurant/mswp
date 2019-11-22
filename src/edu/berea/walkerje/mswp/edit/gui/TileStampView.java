package edu.berea.walkerje.mswp.edit.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import edu.berea.walkerje.mswp.edit.TileStamp;
import edu.berea.walkerje.mswp.gfx.Sprite;
import edu.berea.walkerje.mswp.gfx.Tile;

@SuppressWarnings("serial")
public class TileStampView extends JPanel {
	
	private TileStamp stamp;
	private float scale;
	
	public TileStampView(TileStamp stamp, float scale) {
		this.stamp = stamp;
		this.scale = scale;
	}
	
	public TileStampView(TileStamp stamp) {
		this.stamp = stamp;
		this.scale = 1.0f;
	}
	
	public void setStamp(TileStamp st) {
		this.stamp = st;
	}
	
	public TileStamp getStamp() {
		return stamp;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public float getScale() {
		return scale;
	}

	@Override
	public Dimension getPreferredSize() {
		return getSize();
	}
	
	@Override
	public Dimension getSize() {
		return new Dimension((int)(stamp.getExtentWidth() * stamp.getTileDrawWidth() * scale), (int)(stamp.getExtentHeight() * stamp.getTileDrawHeight() * scale));
	}
	
	public void paint(Graphics g) {
		for(int y = 0; y < stamp.getExtentHeight(); y++) {
			for(int x = 0; x < stamp.getExtentWidth(); x++) {
				Tile t = stamp.get(x, y);
				if(t == null)
					continue;
				
				Sprite spr = t.getSpriteProvider().getNextSprite();
				Image image = spr.getImageProvider().getImage();
				
				final int sx1 = spr.getPositionX();
				final int sy1 = spr.getPositionY();
				final int sx2 = sx1 + spr.getExtentWidth();
				final int sy2 = sy1 + spr.getExtentHeight();
				
				final int dx1 = (int)(x*stamp.getTileDrawWidth()*scale);
				final int dy1 = (int)(y*stamp.getTileDrawHeight()*scale);
				final int dx2 = dx1 + (int)((stamp.getTileDrawWidth() * scale));
				final int dy2 = dy1 + (int)((stamp.getTileDrawHeight() * scale));
				
				g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
			}
		}
	}
}
