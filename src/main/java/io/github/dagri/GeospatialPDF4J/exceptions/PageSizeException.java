package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown if the page size is not valid.
 * 
 * @author DaGri
 * @since 14.01.2017
 *
 */
public class PageSizeException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -5434082387762469688L;

	/**
	 * Constructor for a {@link PageSizeException};
	 */
	public PageSizeException() {
		super("Error: Pagesize is not valid!");
	}

}
