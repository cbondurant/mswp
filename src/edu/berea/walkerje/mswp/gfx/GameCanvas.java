package edu.berea.walkerje.mswp.gfx;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

public class GameCanvas extends Canvas{
	/** Randomly generated version ID.*/
	private static final long serialVersionUID = 4054812147904525803L;
	
	private Graphics2D graphics;
	private long frameStart = 0;
	
	private long frameAccum = 0;
	
	private int fpsCounter = 0;
	private int fps = 0;
	
	private Color backgroundColor = Color.BLACK;
	
	public GameCanvas(int width, int height) {
		super(getDefaultGraphicsConfig());
		createBufferStrategy(2);//Double-buffer this canvas by default.
		setSize(width, height);
	}
	
	/**
	 * @return Begins a frame cycle. Returns the graphics object.
	 */
	public Graphics2D beginFrame() {
		if (graphics == null) {
	        try {
	            graphics = (Graphics2D) getBufferStrategy().getDrawGraphics();
	            graphics.setColor(backgroundColor);
	            graphics.fill(graphics.getClipBounds());
	            
	            //Initialize FPS counter for the frame.
	            frameStart = System.nanoTime();
	        } catch (IllegalStateException e) {
	            return null;
	        }
		}else {
			throw new RuntimeException("Frame state has been flagged as active! Cannot start a new frame yet.");
		}
		return graphics;
	}
	
	/**
	 * @return the draw graphics object.
	 */
	public Graphics2D getDrawGraphics() {return graphics;}
	
	/**
	 * @return a boolean indicating if the canvas is currently working on a frame.
	 */
	public boolean inFrame() {return graphics != null;}
	
	/**
	 * @return ends this frame.
	 */
	public boolean endFrame() {
		if(graphics == null)
			throw new RuntimeException("Frame state has been flagged as inactive! Cannot end a non-existant frame.");
		
		graphics.dispose();
        graphics = null;
        try {
            getBufferStrategy().show();
            Toolkit.getDefaultToolkit().sync();//Sync with AWT thread. Might take this out, if it turns out to be a non-issue.
            final boolean success = (!getBufferStrategy().contentsLost());
            
            if(success) {
            	fpsCounter++;
            	frameAccum += (System.nanoTime() - frameStart);
            	
            	if(frameAccum >= 1000000000) {//1000000000 nanoseconds in a second.
            		fps = fpsCounter;
            		fpsCounter = 0;
            		frameAccum = 0;
            	}
            }
            
            return success;
        } catch (NullPointerException e) {
            return false;
        } catch (IllegalStateException e) {
            return false;
        }
	}
	
	/**
	 * @return the total frames, per second, between beginFrame and endFrame function calls.
	 */
	public int getFPS() {
		return fps;
	}
	
	/**
	 * @return the background color of this canvas. Defaults to black. (0,0,0)@RGB
	 */
	public Color getBackgroundColor() {return backgroundColor;}
	
	/**
	 * Sets the background color of this canvas to the specified color.
	 * @param c
	 */
	public void setBackgroundColor(Color c) {backgroundColor = c;}
	
	/**
	 * @return default graphics configuration
	 */
	public static GraphicsConfiguration getDefaultGraphicsConfig() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}
	
	public static BufferedImage bufferImage(VolatileImage i, boolean alpha) {
		BufferedImage cpy = createBufferedImage(i.getWidth(), i.getHeight(), alpha);
		Graphics2D g = cpy.createGraphics();
		g.drawImage(i, 0, 0, null);
		g.dispose();
		return cpy;
	}
	
	/**
	 * Creates a BufferedImage with the proper format for hardware acceleration.
	 * @param width
	 * @param height
	 * @param alpha
	 * @return
	 */
	public static BufferedImage createBufferedImage(int width, int height, boolean alpha) {
		return getDefaultGraphicsConfig().createCompatibleImage(width, height, alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
	}
	
	public static BufferedImage copyCompatImage(BufferedImage img) {
		BufferedImage out = getDefaultGraphicsConfig().createCompatibleImage(img.getWidth(), img.getHeight(), img.getTransparency());
		Graphics2D g = out.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return out;
	}
	
	/**
	 * Copies a BufferedImage with the proper format for hardware acceleration.
	 * @param img
	 * @return
	 */
	public static VolatileImage copyBufferedImage(BufferedImage img) {
		VolatileImage cpy = createVolatileImage(img.getWidth(), img.getHeight(), true);
		Graphics2D g = cpy.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return cpy;
	}
	
	/**
	 * Creates a VolatileImage with the proper format for hardware acceleration.
	 * When in doubt, stick to the BufferedImage variant of this function.
	 * VolatileImages are guaranteed to be placed in VRAM, while BufferedImage implementation is ambiguous on this. Depends on usage.
	 * @param width
	 * @param height
	 * @param alpha
	 * @return
	 */
	public static VolatileImage createVolatileImage(int width, int height, boolean alpha) {
		return getDefaultGraphicsConfig().createCompatibleVolatileImage(width, height, alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
	}
}