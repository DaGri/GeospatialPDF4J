package io.github.dagri.GeospatialPDF4J.exceptions;

import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;

/**
 * Exception to be thrown if a {@link MapLayer} could not be received.
 * 
 * @author DaGri
 * @since 18.01.2017
 *
 */
public class MapLayerNotReceivableException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -4089695064133535872L;

	/**
	 * Constructor for a {@link MapLayerNotReceivableException}.
	 */
	public MapLayerNotReceivableException() {
		super("Maplayer could not be received!");
	}
}
