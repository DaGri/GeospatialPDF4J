package io.github.dagri.GeospatialPDF4J.draw.geometries;

import org.geotools.geometry.DirectPosition2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.res.AdditionalInfo;

/**
 * Class to represent a draw-able {@link Point}.
 * 
 * This class extends the abstract class {@link DrawGeometry}.
 * 
 * @author DaGri
 * @since 04.02.2017
 */
public class DrawPoint extends DrawGeometry {

	// ATTRIBUTES

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link DrawPoint} using a {@link Coordinate2D}.
	 * 
	 * @param dp2D
	 *            the {@link Coordinate2D}
	 */
	public DrawPoint(DirectPosition2D dp2D) {
		GeometryFactory factory = new GeometryFactory();
		this.setJtsGeometry(factory.createPoint(new Coordinate(dp2D.getOrdinate(0), dp2D.getOrdinate(1))));
	}

	/**
	 * Constructor for a {@link DrawPoint} using a JTS {@link Point} and an
	 * {@link AdditionalInfo}.
	 * 
	 * @param p
	 *            the JTS {@link Point} to set
	 * @param info
	 *            the {@link AdditionalInfo} to set
	 */
	public DrawPoint(Point p, AdditionalInfo info) {
		this.setJtsGeometry(p);
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
		return "DRAWPOINT";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#reduce(
	 * double, double)
	 */
	@Override
	public void reduce(double xRed, double yRed) {
		this.getJtsGeometry().getCoordinate().x = this.getJtsGeometry().getCoordinate().x - xRed;
		this.getJtsGeometry().getCoordinate().y = this.getJtsGeometry().getCoordinate().y - yRed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#scale(
	 * double)
	 */
	@Override
	public void scale(double factor) {
		this.getJtsGeometry().getCoordinate().x = this.getJtsGeometry().getCoordinate().x * factor;
		this.getJtsGeometry().getCoordinate().y = this.getJtsGeometry().getCoordinate().y * factor;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * getJtsGeometry()
	 */
	@Override
	public Point getJtsGeometry() {
		return (Point) super.getJtsGeometry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * setJtsGeometry(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void setJtsGeometry(Geometry jtsGeometry) {
		if (jtsGeometry instanceof Point)
			super.setJtsGeometry(jtsGeometry);
	}
	
	// GETTERS AND SETTERS

	// OTHERS
}
