package io.github.dagri.GeospatialPDF4J.exceptions;

import io.github.dagri.GeospatialPDF4J.res.TileArray;

/**
 * Exception to be thrown if the number of columns or rows given to a
 * {@link TileArray} is <= zero.
 * 
 * @author DaGri
 * @since 11.01.2017
 */
public class TileArrayDimensionException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 6310639326275298753L;

	/**
	 * Constructor for a {@link TileArrayDimensionException}.
	 */
	public TileArrayDimensionException() {
		super("Error: TileArray could not be created. The dimension of rows or columns is <= zero!");
	}

}
