package io.github.dagri.GeospatialPDF4J.exceptions;

import io.github.dagri.GeospatialPDF4J.map.layers.ReferencedLayer;

/**
 * Exception to be thrown on errors according to a {@link ReferencedLayer}.
 * 
 * @author DaGri
 * @since 17.01.2017
 *
 */
public class ReferencedLayerException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 3133507385140267849L;

	public ReferencedLayerException(){
		super("Error on creating / handling the ReferencedLayer.");
	}
	
}
