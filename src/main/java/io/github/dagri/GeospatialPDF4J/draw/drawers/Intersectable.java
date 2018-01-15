package io.github.dagri.GeospatialPDF4J.draw.drawers;

import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;

/**
 * Interface to be implemented by all classes that contain drawable geometries
 * that may be intersected by the maps bounds.
 * 
 * @author DaGri
 * @since 15.08.2017
 */
public interface Intersectable {

	/**
	 * Returns the {@link DrawGeometry} contained in the class that implements
	 * this interface, no matter what kind it is.
	 *
	 * @return a {@link DrawGeometry}
	 */
	public DrawGeometry getDrawGeometry();

	/**
	 * Sets the {@link DrawGeometry} of this {@link Intersectable}.
	 *
	 * @param g
	 *            the {@link DrawGeometry} to set
	 */
	public void setDrawGeometry(DrawGeometry g);

	/**
	 * Returns a {@link Boolean} that indicates if the {@link DrawGeometry} of
	 * this object intersetcs the given {@link BoundingBox}.
	 * 
	 * This method may return the return the {@link Boolean} returned by an
	 * internal {@link DrawGeometry}.intersectBbox(...)-call.
	 *
	 * @param bbox
	 *            the {@link BoundingBox} to intersect
	 * @return <code>true</code> if it intersects the {@link BoundingBox},
	 *         <code>false</code> if not
	 */
	public boolean intersects(BoundingBox bbox);

}
