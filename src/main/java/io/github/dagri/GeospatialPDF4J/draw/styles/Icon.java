package io.github.dagri.GeospatialPDF4J.draw.styles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;

/**
 * Class to store and manage an {@link BufferedImage} that can be used as
 * {@link Icon} for displaying point-type-information in a geospatial-PDF.
 * 
 * @author DaGri
 * @since 09.02.2017
 */
@Slf4j
public class Icon {

	// ATTRIBUTES

	/**
	 * The {@link BufferedImage} of this {@link Icon}.
	 */
	private BufferedImage image = null;
	
	/**
	 * The width of this {@link Icon} to display in the PDF in inches.
	 */
	private float width = Float.MIN_VALUE;

	// CONSTRUCTORS

	/**
	 * Empty constructor for an {@link Icon}.
	 */
	public Icon() {
		// NOTHING
	}

	/**
	 * Constructor for an {@link Icon} using a {@link String} that contains the
	 * path to the file to load.
	 * 
	 * @param path the path to the image-file
	 */
	public Icon(String path) {
		File f = new File(path);
		this.loadImage(f);
	}

	// METHODS

	/**
	 * Tries to load the image defined by the given path.
	 * 
	 * If it fails the standard image will be set.
	 *
	 * @param path
	 *            the {@link URL} to the file
	 */
	private void loadImage(File f) {
		try {
			// READ THE IMAGE FROM THE URL AND SET IT
			BufferedImage temp;
			if (f == null)
				throw new NullPointerException();
			temp = ImageIO.read(f);
			this.setImage(temp);
		} catch (IOException | NullPointerException e) {
			// ERROR ON READOING FROM URL
			try {
				// SET THE IMAGE TO THE STANDARD IMAGE
				this.setImage(ImageIO.read(new File("resources/Transparent_X.png")));
			} catch (IOException e1) {
				log.error("Could neighter set the desired image, nor the standard image!");
			}
		}
	}
	
	/**
	 * Sets the width in inches to fit the given value in millimeters.
	 *
	 * @param millimeters the width to set
	 */
	public void setWidthMM(float millimeters){
		this.setWidth(millimeters*0.03937f);
	}
	
	/**
	 * Sets the width in inches.
	 *
	 * @param inches
	 */
	public void setWidthInch(float inches){
		this.setWidth(inches);
	}
	
	/**
	 * Sets the width in inches to fit the given value in centimeters.
	 *
	 * @param centimeters
	 */
	public void setWidthCM(float centimeters){
		this.setWidth(centimeters*0.3937f);
	}
	
	
	// GETTERS AND SETTERS

	/**
	 * Returns the {@link BufferedImage} of this {@link Icon} as
	 * {@link BufferedImage}.
	 *
	 * @return the {@link BufferedImage} of this {@link Icon}
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Sets the {@link BufferedImage} of this {@link Icon}.
	 *
	 * @param image
	 *            the {@link BufferedImage} to set
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	/**
	 * Returns the width to display this {@link Icon} in inch as {@link Integer}.
	 *
	 * @return the width to display in {@link Integer}
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * Sets the width of this {@link Icon} to be displayed in inches.
	 *
	 * @param width the width to set
	 */
	private void setWidth(float width) {
		this.width = width;
	}

	// OTHERS
}
