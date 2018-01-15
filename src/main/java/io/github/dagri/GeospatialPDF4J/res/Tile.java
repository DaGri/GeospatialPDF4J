package io.github.dagri.GeospatialPDF4J.res;

import java.awt.image.BufferedImage;

import io.github.dagri.GeospatialPDF4J.exceptions.TileException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;

/**
 * Class to represent a {@link Tile}, that can be used to receive a map image
 * from a WMS server.
 * 
 * @author DaGri
 * @since 11.01.2017
 *
 */
public class Tile {

	// ATTRIBUTES

	/**
	 * The {@link BufferedImage} of this {@link Tile}.
	 */
	private BufferedImage	tileImage;

	/**
	 * The {@link BoundingBox} of this {@link Tile}.
	 */
	private BoundingBox	tileBBox;

	/**
	 * The width of the image to be requested as {@link Integer}.
	 */
	private int				imageWidth;

	/**
	 * The height of the image to be requested as {@link Integer}.
	 */
	private int				imageHeight;

	/**
	 * The number of requests this {@link Tile} went through.
	 */
	private int				requestTry	= 0;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link Tile} using a {@link BoundingBox} to be set as
	 * the {@link Tile}s {@link BoundingBox} and two {@link Integer}s to
	 * define the {@link Tile}s image width and height.
	 * 
	 * Throws a {@link TileException} if the {@link BoundingBox} is null or if
	 * the image width or height are equal or smaller to zero.
	 * 
	 * @param tileBBox
	 *            the {@link BoundingBox} to set
	 * @param imageWidth
	 *            the width of the image to set
	 * @param imageHeight
	 *            the height of the image to set
	 * @throws TileException
	 */
	public Tile(BoundingBox tileBBox, int imageWidth, int imageHeight) throws TileException {
		if (tileBBox == null || imageWidth <= 0 || imageHeight <= 0) {
			throw new TileException();
		} else {
			this.setTileBBox(tileBBox);
			this.setImageWidth(imageWidth);
			this.setImageHeight(imageHeight);
		}
	}

	// METHODS

	/**
	 * Counts up the number of requests this {@link Tile} went through.
	 */
	public void countTriesUp() {
		this.requestTry++;
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the tile image of this {@link Tile} as {@link BufferedImage}.
	 *
	 * @return the tileImage as {@link BufferedImage}
	 */
	public BufferedImage getTileImage() {
		return tileImage;
	}

	/**
	 * Sets the tile image of this {@link Tile}.
	 *
	 * @param tileImage
	 *            the {@link BufferedImage} to set
	 */
	public void setTileImage(BufferedImage tileImage) {
		this.tileImage = tileImage;
	}

	/**
	 * Returns the {@link BoundingBox} of this {@link Tile} as
	 * {@link BoundingBox}.
	 *
	 * @return the tileBBox as {@link BoundingBox}
	 */
	public BoundingBox getTileBBox() {
		return tileBBox;
	}

	/**
	 * Sets the {@link BoundingBox} of this {@link Tile}
	 *
	 * @param tileBBox
	 *            the {@link BoundingBox} to set
	 */
	private void setTileBBox(BoundingBox tileBBox) {
		this.tileBBox = tileBBox;
	}

	/**
	 * Returns the width of the image of this {@link Tile} as {@link Integer}.
	 *
	 * @return the width of the {@link Tile}s image as {@link Integer}
	 */
	public int getImageWidth() {
		return imageWidth;
	}

	/**
	 * Sets the the width of the {@link Tile}s image.
	 *
	 * @param imageWidth
	 *            the imageWidth to set
	 */
	private void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	/**
	 * Returns the height of the image of this {@link Tile} as {@link Integer}.
	 *
	 * @return the height of the {@link Tile}s image as {@link Integer}
	 */
	public int getImageHeight() {
		return imageHeight;
	}

	/**
	 * Sets the height of the image of this {@link Tile}s image.
	 *
	 * @param imageHeight
	 *            the imageHeight to set
	 */
	private void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	/**
	 * Returns the number of requests this {@link Tile} went through.
	 *
	 * @return the number of tries as {@link Integer}
	 */
	public int getRequestTry() {
		return requestTry;
	}

	// OTHERS
}
