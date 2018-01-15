package io.github.dagri.GeospatialPDF4J.draw.geometries;

import com.vividsolutions.jts.geom.Geometry;

import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.pdf.PdfObject;
import io.github.dagri.GeospatialPDF4J.res.AdditionalInfo;

/**
 * Abstract class to be implemented by the geometries that can be displayed in a
 * PDF document.
 * 
 * This class extends the abstract {@link PdfObject} class so every child
 * contains an {@link AdditionalInfo}.
 * 
 * @author DaGri
 * @since 02.02.2017
 */
public abstract class DrawGeometry extends PdfObject {

	/**
	 * The JTS geometry of this {@link DrawGeometry}
	 */
	private Geometry jtsGeometry;

	/**
	 * Returns the name of the geometry as {@link String} in upper-case letters.
	 *
	 * @return the geometry name
	 */
	public abstract String getGeometryName();

	// TODO : KOENNTE NOCH METHODEN NUTZEN, DIE DIREKT EINE COORDINATE2D ODER
	// EINE BOUNDINGBOX ZUM REDUZIEREN NUTZT
	// TODO : EINTEILUNG IN X UND Y RICHTUNG IST IRREFUEHREND UND SOLLTE
	// ABGEAENDERT WERDEN

	/**
	 * Reduces the internal coordinates by the given values in X- and Y-
	 * direction.
	 *
	 * @param ord0
	 *            the reduction of the Ordinate 0
	 * @param ord1
	 *            the reduction of the Ordinate 1
	 */
	public abstract void reduce(double ord0, double ord1);

	/**
	 * Scales the internal coordinates by the given factor.
	 *
	 * @param the
	 *            factor to stale about
	 */
	public abstract void scale(double factor);

	/**
	 * Returns a {@link Boolean} that indicates if this {@link DrawGeometry}
	 * intersects the given {@link Geometry}.
	 *
	 * @param g
	 *            the {@link Geometry} to intersect
	 * @return <code>true</code> if it intersects the {@link Geometry};
	 *         <code>false</code> if not
	 */
	public abstract boolean intersectsGeometry(Geometry g);

	/**
	 * Returns a {@link Boolean} that indicates if this {@link DrawGeometry}
	 * intersects the given {@link BoundingBox}.
	 *
	 * @param bbox
	 * @return <code>true</code> if it intersects the {@link BoundingBox};
	 *         <code>false</code> if not
	 */
	public abstract boolean intersectsBBox(BoundingBox bbox);

	/**
	 * Returns the area of this {@link DrawGeometry} as {@link Double}.
	 *
	 * @return the area as double
	 */
	public abstract double getArea();

	/**
	 * Returns the length of this {@link DrawGeometry}. A point-style
	 * {@link Geometry} will return 0.0, a polygon-style {@link Geometry}
	 * returns the circumfence.
	 *
	 * @return the length as {@link Double}
	 */
	public abstract double getLength();

	/**
	 * Returns the JTS {@link Geometry} of this {@link DrawGeometry}.
	 *
	 * @return the jtsGeometry
	 */
	public Geometry getJtsGeometry() {
		return jtsGeometry;
	}

	/**
	 * Sets the {@link Geometry} of this {@link DrawGeometry}.
	 *
	 * @param jtsGeometry
	 *            the {@link Geometry} to set
	 */
	public void setJtsGeometry(Geometry jtsGeometry) {
		this.jtsGeometry = jtsGeometry;
	}

}
