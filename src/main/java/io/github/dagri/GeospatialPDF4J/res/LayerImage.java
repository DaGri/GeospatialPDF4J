package io.github.dagri.GeospatialPDF4J.res;

import com.lowagie.text.Image;

import io.github.dagri.GeospatialPDF4J.map.Map;

/**
 * Class to safe an iText {@link Image} and additional infos to it, like the
 * offset to the {@link Map}.
 * 
 * @author DaGri
 * @since 16.01.2017
 *
 */
public class LayerImage {

	// ATTRIBUTES

	/**
	 * The offset in X-direction from the lower left corner of the PDF file.
	 */
	private double xOffset;

	/**
	 * The offset in Y-direction from the lower left corner of the PDF file.
	 */
	private double yOffset;

	/**
	 * The image width.
	 */
	private int imgWidth;

	/**
	 * The image height.
	 */
	private int imgHeight;

	/**
	 * The image as iText {@link Image}.
	 */
	private Image image;

	// CONSTRUCTORS

	// METHODS

	// GETTERS AND SETTERS

	/**
	 * Returns the offset of this {@link LayerImage} to the lower left
	 * {@link Map} corner in X-direction as {@link Double} in pixels.
	 *
	 * @return the xOffset
	 */
	public double getxOffset() {
		return xOffset;
	}

	/**
	 * Sets the offset in X-direction.
	 *
	 * @param xOffset
	 *            the xOffset to set
	 */
	public void setxOffset(double xOffset) {
		this.xOffset = xOffset;
	}

	/**
	 * Returns the offset of this {@link LayerImage} to the lower left
	 * {@link Map} corner in Y-direction as {@link Double} in pixels.
	 *
	 * @return the yOffset
	 */
	public double getyOffset() {
		return yOffset;
	}

	/**
	 * Sets the offset in Y-direction.
	 *
	 * @param yOffset
	 *            the yOffset to set
	 */
	public void setyOffset(double yOffset) {
		this.yOffset = yOffset;
	}

	/**
	 * Returns the width of the {@link Image} as {@link Integer} in pixels.
	 *
	 * @return the imgWidth
	 */
	public int getImgWidth() {
		return imgWidth;
	}

	/**
	 * Sets the width of the {@link Image}.
	 *
	 * @param imgWidth
	 *            the imgWidth to set
	 */
	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}

	/**
	 * Returns the height of the image as {@link Integer} in pixels.
	 *
	 * @return the imgHeight
	 */
	public int getImgHeight() {
		return imgHeight;
	}

	/**
	 * Sets the height of the {@link Image}.
	 *
	 * @param imgHeight
	 *            the imgHeight to set
	 */
	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}

	/**
	 * Returns the {@link Image} of this {@link LayerImage}.
	 *
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Sets the {@link Image} of this {@link LayerImage} and sets the width and
	 * height attributes by reading out the image-data.
	 *
	 * @param image
	 *            the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
		this.setImgWidth((int) image.getScaledWidth());
		this.setImgHeight((int) image.getScaledHeight());
	}

	// OTHERS
}
