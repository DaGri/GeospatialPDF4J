package io.github.dagri.GeospatialPDF4J.draw.geometries;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.res.AdditionalInfo;

/**
 * Class to represent a draw-able {@link MultiPolygon}.
 * 
 * This class extends the abstract class {@link DrawGeometry}.
 * 
 * @author DaGri
 * @since 03.02.2017
 *
 */
public class DrawMultiPolygon extends DrawGeometry {

	// ATTRIBUTES

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link DrawMultiPolygon} using a JTS
	 * {@link MultiPolygon} and an {@link AdditionalInfo}.
	 * 
	 * @param mp
	 *            the JTS {@link MultiPolygon} to set
	 * @param info
	 *            the {@link AdditionalInfo} to set
	 */
	public DrawMultiPolygon(MultiPolygon mp, AdditionalInfo info) {
		this.setJtsGeometry(mp);
		this.setInfo(info);
	}

	// METHODS

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * getGeometryName()
	 */
	@Override
	public String getGeometryName() {
		return "DRAWMULTIPOLYGON";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#reduce(
	 * double, double)
	 */
	@Override
	public void reduce(double xRed, double yRed) {
		// RUN THROUGH CONTAINED GEOMETRIES
		for (int a = 0; a < this.getJtsGeometry().getNumGeometries(); a++) {
			// RUN THROUGH THE COORDINATES OF THE NTH GEOMETY
			for (int b = 0; b < this.getJtsGeometry().getGeometryN(a).getCoordinates().length; b++) {
				this.getJtsGeometry().getGeometryN(a).getCoordinates()[b].x = this.getJtsGeometry().getGeometryN(a).getCoordinates()[b].x + xRed;
				this.getJtsGeometry().getGeometryN(a).getCoordinates()[b].y = this.getJtsGeometry().getGeometryN(a).getCoordinates()[b].y + yRed;

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#scale(
	 * double)
	 */
	@Override
	public void scale(double factor) {
		// RUN THROUGH CONTAINED GEOMETRIES
		for (int a = 0; a < this.getJtsGeometry().getNumGeometries(); a++) {
			// RUN THROUGH THE COORDINATES OF THE NTH GEOMETY
			for (int b = 0; b < this.getJtsGeometry().getGeometryN(a).getCoordinates().length; b++) {
				this.getJtsGeometry().getGeometryN(a).getCoordinates()[b].x = this.getJtsGeometry().getGeometryN(a).getCoordinates()[b].x * factor;
				this.getJtsGeometry().getGeometryN(a).getCoordinates()[b].y = this.getJtsGeometry().getGeometryN(a).getCoordinates()[b].y * factor;

			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * intersectsBBox(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public boolean intersectsGeometry(Geometry g) {
		return this.getJtsGeometry().intersects(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * intersectsBBox(io.github.dagri.GeospatialPDF4J.geo.BoundingBox)
	 */
	@Override
	public boolean intersectsBBox(BoundingBox bbox) {
		DrawPolygon intersect = new DrawPolygon(bbox.getCoordsAsArrayList());
		return this.intersectsGeometry(intersect.getJtsGeometry());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#getArea()
	 */
	@Override
	public double getArea() {
		return this.getJtsGeometry().getArea();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#getLength()
	 */
	@Override
	public double getLength() {
		return this.getJtsGeometry().getLength();
	}

	// GETTERS AND SETTERS

	// OTHERS
}
