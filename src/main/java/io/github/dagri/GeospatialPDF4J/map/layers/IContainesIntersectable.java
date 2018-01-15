package io.github.dagri.GeospatialPDF4J.map.layers;

import java.util.ArrayList;

import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry;

/**
 * Interface to be implemented by classes that contain children of
 * {@link DrawGeometry}.
 * 
 * @author DaGri
 * @since 15.08.2017
 */
public interface IContainesIntersectable {

	/**
	 * Returns an {@link ArrayList} of all {@link DrawGeometry}s that are
	 * contained in this class.
	 *
	 * @return an {@link ArrayList} of {@link DrawGeometry}s
	 */
	public ArrayList<DrawGeometry> getDrawGeometries();

}
