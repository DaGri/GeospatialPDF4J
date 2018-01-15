package io.github.dagri.GeospatialPDF4J.draw.geometries;

import java.util.ArrayList;

import org.geotools.geometry.DirectPosition2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.MissingCrsException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.res.AdditionalInfo;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to represent a draw-able {@link Polygon}.
 * 
 * This class extends the abstract class {@link DrawGeometry}.
 * 
 * @author DaGri
 * @since 03.02.2017
 */
@Slf4j
public class DrawPolygon extends DrawGeometry {

	// ATTRIBUTES

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link DrawPolygon} using an {@link ArrayList} of
	 * {@link Coordinate2D}s.
	 * 
	 * @param coords
	 *            the {@link ArrayList} of {@link Coordinate2D}s
	 */
	public DrawPolygon(ArrayList<DirectPosition2D> coords) {
		log.info("Creating a new DrawPolygon...");
		log.debug("Creating a GeometryFactory...");
		GeometryFactory factory = new GeometryFactory();

		/**
		 * The next lines make sure that the first coordinate is equal to the
		 * last one. Per definition a polygon is defined by an equal start- and
		 * ending-position.
		 */
		if (
		// FIRST COMPARISON
		coords.get(0).getOrdinate(0) != coords.get(coords.size() - 1).getOrdinate(0)
				// OR
				||
				// SECOND COMPARISON
				coords.get(0).getOrdinate(1) != coords.get(coords.size() - 1).getOrdinate(1))
			coords.add(coords.get(0));

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

		log.debug("Creating a LinearRing...");
		LinearRing lr = factory.createLinearRing(c);

		log.debug("Setting the JTS-geometry...");
		this.setJtsGeometry(factory.createPolygon(lr, null));
		log.info("DrawPolygon created.");
	}

	/**
	 * Constructor for a {@link DrawPolygon} using a JTS {@link Polygon} and a
	 * {@link AdditionalInfo}.
	 * 
	 * @param p
	 *            the {@link Polygon} to set
	 * @param info
	 *            the {@link AdditionalInfo} to set
	 */
	public DrawPolygon(Polygon p, AdditionalInfo info) {
		log.info("Creating DrawPolygon...");
		log.debug("Setting JTS-geometry...");
		this.setJtsGeometry(p);
		log.debug("Setting AdditionalInfo...");
		this.setInfo(info);
		log.info("DrawPolygon created.");
	}

	// METHODS

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
	 * getGeometryName()
	 */
	@Override
	public String getGeometryName() {
		return "DRAWPOLYGON";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#reduce(
	 * double, double)
	 */
	@Override
	public void reduce(double xRed, double yRed) {
		log.debug("Reducing DrawPolygon...");
		for (int a = 0; a < this.getJtsGeometry().getCoordinates().length; a++) {
			this.getJtsGeometry().getCoordinates()[a].x = this.getJtsGeometry().getCoordinates()[a].x - xRed;
			this.getJtsGeometry().getCoordinates()[a].y = this.getJtsGeometry().getCoordinates()[a].y - yRed;
		}
		log.debug("DrawPolygon reduced.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#scale(
	 * double)
	 */
	@Override
	public void scale(double factor) {
		log.debug("Scaling DrawPolygon...");
		for (int a = 0; a < this.getJtsGeometry().getCoordinates().length; a++) {
			this.getJtsGeometry().getCoordinates()[a].x = this.getJtsGeometry().getCoordinates()[a].x * factor;
			this.getJtsGeometry().getCoordinates()[a].y = this.getJtsGeometry().getCoordinates()[a].y * factor;
		}
		log.debug("DrawPolygon scaled.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * getJtsGeometry()
	 */
	@Override
	public Polygon getJtsGeometry() {
		return (Polygon) super.getJtsGeometry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry#
	 * setJtsGeometry(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void setJtsGeometry(Geometry jtsGeometry) {
		if (jtsGeometry instanceof Polygon)
			super.setJtsGeometry(jtsGeometry);
	}

	// GETTERS AND SETTERS

	// OTHERS
}
