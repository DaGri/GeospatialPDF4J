package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown at CSV reading errors.
 * 
 * @author DaGri
 * @since 18.07.2017
 *
 */
public class CsvReadingException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -3181217063626268304L;
	
	/**
	 * Constructor for a {@link CsvReadingException}.
	 */
	public CsvReadingException() {
		super("Error on transforming a Coordinate to another CRS: The transformation could not be done!");
	}

	}
