package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown if an error occurs while converting an BufferedImage
 * to an iText-Image.
 * 
 * @author DaGri
 * @since 17.01.2017
 *
 */
public class ImageCovertingException extends Exception {

	/**
	 * The servial verion UID.
	 */
	private static final long serialVersionUID = 7404224462632875453L;

	public ImageCovertingException() {
		super("Error while converting an BufferedImage to an iText-Image.");
	}

}
