package io.github.dagri.GeospatialPDF4J.exceptions;

import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;

/**
 * Exception to be thrown if the {@link MapLayer}s inches in the document could
 * not be calculated.
 * 
 * @author DaGri
 * @since 14.08.2017
 */
public class CalcualteLayerInchesException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -3762955693527743599L;

	/**
	 * Constructor for a {@link ImpossibleCoordinateOrderException}.
	 */
	public CalcualteLayerInchesException() {
		super("Error while calculating the MapLayers inches in widht and height because of missing data!");
	}

}
