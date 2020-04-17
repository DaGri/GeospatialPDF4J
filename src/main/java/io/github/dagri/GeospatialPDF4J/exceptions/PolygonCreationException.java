package io.github.dagri.GeospatialPDF4J.exceptions;

import com.vividsolutions.jts.geom.Polygon;

/**
 * Exception to be thrown if an error occurs while creating a {@link Polygon}.
 * 
 * @author DaGri
 * @since 08.10.2017
 */
public class PolygonCreationException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -4115516155893037232L;

	/**
	 * Constructor for a {@link PolygonCreationException}.
	 */
	public PolygonCreationException() {
		super("Error on creating a Polygon!");
	}

}
