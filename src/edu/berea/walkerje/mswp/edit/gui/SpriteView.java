package edu.berea.walkerje.mswp.edit.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JComponent;

import edu.berea.walkerje.mswp.gfx.ISpriteProvider;
import edu.berea.walkerje.mswp.gfx.Sprite;

public class SpriteView extends JComponent{
	private static final long serialVersionUID = 5385890751770481364L;
	
	private ISpriteProvider provider = null;
	private float scale = 1.0f;
	
	public SpriteView(ISpriteProvider provider, float scale) {
		this.provider = provider;
		this.scale = scale;
		this.setOpaque(false);
		this.setForeground(new Color(0,0,0,0));
	}
	
	public SpriteView(ISpriteProvider provider) {
		this.provider = provider;
		setForeground(new Color(0,0,0,0));
	}
	
	public void setProvider(ISpriteProvider provider) {
		this.provider = provider;
	}
	
	public ISpriteProvider getProvider() {
		return provider;
	}
	
	public void setScale(float sc) {
		scale = sc;
	}
	
	public float getScale() {
		return scale;
	}
	
	public Dimension getPreferredSize() {
		return getSize();
	}
	
	@Override
	public Dimension getSize() {
		if(provider == null)
			return new Dimension((int)(32*scale),(int)(32*scale));//Default to 32x32 by scale size.
		Sprite spr = provider.getCurrentSprite();
		return new Dimension((int)(spr.getExtentWidth() * scale), (int)(spr.getExtentHeight() * scale));
	}
	
	@Override
	public void paint(Graphics g) {
		if(provider == null)
			return;
		
		Sprite spr = provider.getNextSprite();
		Image image = spr.getImageProvider().getImage();
		
		//destination top left corner
		final int dx1 = spr.getPositionX();
		final int dy1 = spr.getPositionY();
		//destination bottom right corner
		final int dx2 = dx1 + spr.getExtentWidth();
		final int dy2 = dy1 + spr.getExtentHeight();
		
		g.drawImage(image, 0, 0, (int)(spr.getExtentWidth() * scale), (int)(spr.getExtentHeight() * scale), dx1, dy1, dx2, dy2, null);
		
		((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, getForeground().getAlpha() / 255.0f));
		g.setColor(getForeground());
		g.fillRect(0, 0, spr.getExtentWidth(), spr.getExtentHeight());
	}
}