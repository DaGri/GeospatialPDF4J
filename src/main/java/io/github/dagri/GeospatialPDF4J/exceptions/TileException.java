package io.github.dagri.GeospatialPDF4J.exceptions;

import io.github.dagri.GeospatialPDF4J.res.Tile;

/**
 * Exception to be thrown if the an error occurs during the creation of a
 * {@link Tile}.
 * 
 * @author DaGri
 * @since 02.07.2017
 */
public class TileException extends Exception {

	// ATTRIBUTES

	/**
	 * 
	 */
	private static final long serialVersionUID = -7349363548709786957L;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link TileArrayDimensionException}.
	 */
	public TileException() {
		super("Error: Tile could not be created. BoundingBox is null || Width or height of the image are < 0!");
	}

	// METHODS

	// GETTERS AND SETTERS

	// OTHERS
}
