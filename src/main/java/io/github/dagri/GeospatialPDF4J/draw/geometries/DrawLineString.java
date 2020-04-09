package io.github.dagri.GeospatialPDF4J.draw.geometries;

import java.util.ArrayList;

import org.geotools.geometry.DirectPosition2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.MissingCrsException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.res.AdditionalInfo;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to represent a draw-able {@link LineString}.
 * 
 * This class extends the abstract class {@link DrawGeometry}.
 * 
 * @author DaGri
 * @since 04.02.2017
 */
@Slf4j
public class DrawLineString extends DrawGeometry {

	// ATTRIBUTES

	// CONSTRUCTORS

	/**
	 * Constructor for {@link DrawLineString} using an {@link ArrayList} of
	 * {@link Coordinate2D}s.
	 * 
	 * @param coords
	 *            the {@link ArrayList} of {@link Coordinate2D}s
	 */
	public DrawLineString(ArrayList<DirectPosition2D> coords) {
		log.info("Creating a new DrawLineString...");
		log.debug("Creating a GeometryFactory...");
		GeometryFactory factory = new GeometryFactory();

		log.debug("Creating an array of JTS-coordinates...");
		Coordinate[] c = new Coordinate[coords.size()];

		log.debug("Instanciating a CoordianteTransformer...");
		CoordinateTransformer t = CoordinateTransformer.getInstance();

		log.debug("Converting DirectPosition2Ds into JTS-coordaintes...");
		for (int a = 0; a < coords.size(); a++) {
			log.debug("Transforming DirectPosition " + a + "...");
			try {
				if (coords.get(a).getCoordinateReferenceSystem() != t.getUtmCrs()) {
					log.debug("Transforming to internal used UTM-CRS (EPSG:25832)...");
					coords.set(a, t.transform(coords.get(a), t.getUtmCrs()));
					log.debug("Transformation succesfull...");
				}
				log.debug("Adding DirectPosition2Ds data to the JTS-coordiante array...");
				c[a] = new Coordinate(coords.get(a).getOrdinate(0), coords.get(a).getOrdinate(1));
			} catch (CoordinateTransformException | MissingCrsException e) {
				log.warn("Could not convert DirectPosition to a JTS-coordinate!");
			}
		}
		log.debug("DirectPosition2Ds transformed.");

		log.debug("Creating the LineString...");
		LineString ls = factory.createLineString(c);

		log.debug("Setting the JTS-geometry...");
		this.setJtsGeometry(ls);
		log.info("DrawPolygon created.");
	}

	/**
	 * Constructor for a {@link DrawLineString} using a JTS {@link LineString}
	 * and an {@link AdditionalInfo}.
	 * 
	 * @param ls
	 *            the JTS {@link LineString} to set
	 * @param info
	 *            the {@link AdditionalInfo} to set
	 */
	public DrawLineString(LineString ls, AdditionalInfo info) {
		log.info("Creating DrawLineString...");
		log.debug("Setting JTS-geometry...");
		this.setJtsGeometry(ls);
		log.debug("Setting AdditionalInfo...");
		this.setInfo(info);
		log.info("DrawPolygon created.");
	}

	// INHERITED METHODS

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * getGeometryName()
	 */
	@Override
	public String getGeometryName() {
		return "DRAWLINESTRING";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#reduce(
	 * double, double)
	 */
	@Override
	public void reduce(double xRed, double yRed) {
		for (int a = 0; a < this.getJtsGeometry().getCoordinates().length; a++) {
			this.getJtsGeometry().getCoordinateN(a).x = this.getJtsGeometry().getCoordinateN(a).x - xRed;
			this.getJtsGeometry().getCoordinateN(a).y = this.getJtsGeometry().getCoordinateN(a).y - yRed;
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
		for (int a = 0; a < this.getJtsGeometry().getCoordinates().length; a++) {
			this.getJtsGeometry().getCoordinateN(a).x = this.getJtsGeometry().getCoordinateN(a).x * factor;
			this.getJtsGeometry().getCoordinateN(a).y = this.getJtsGeometry().getCoordinateN(a).y * factor;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * getJtsGeometry()
	 */
	@Override
	public LineString getJtsGeometry() {
		return (LineString) super.getJtsGeometry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * setJtsGeometry(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void setJtsGeometry(Geometry jtsGeometry) {
		if (jtsGeometry instanceof LineString)
			super.setJtsGeometry(jtsGeometry);
	}

	// METHODS

	/**
	 * Returns the number of points this {@link DrawLineString} containts as
	 * {@link Integer}.
	 *
	 * @return the number of contained points as {@link Integer}
	 */
	public int getJtsPointNumber() {
		return this.getJtsGeometry().getNumPoints();
	}

	// GETTERS AND SETTERS

	// OTHERS
}
