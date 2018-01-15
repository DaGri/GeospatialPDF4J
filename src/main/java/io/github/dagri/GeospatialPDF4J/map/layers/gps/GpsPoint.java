package io.github.dagri.GeospatialPDF4J.map.layers.gps;

import org.geotools.geometry.DirectPosition2D;

import io.github.dagri.GeospatialPDF4J.draw.drawers.Intersectable;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawPoint;
import io.github.dagri.GeospatialPDF4J.draw.styles.PointStyle;
import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.MissingCrsException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.pdf.PdfObject;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to represent a position on the surface of the earth, measured by a
 * GPS-system.
 * 
 * @author DaGri
 * @since 18.07.2017
 */
@Slf4j
public class GpsPoint extends PdfObject implements Intersectable {

	// ATTRIBUTES

	/**
	 * The {@link DrawPoint} of this {@link GpsPoint}.
	 */
	private DrawPoint	dp;

	/**
	 * The {@link PointStyle} of this {@link GpsPoint}.
	 */
	private PointStyle	style;

	/**
	 * The height measured in the GPS-file.
	 */
	private double		height;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link GpsPoint} using a {@link Coordinate2D} to define
	 * the position on the surface of the earth and a {@link PointStyle} to give
	 * the possibility of an individual point-design for every {@link GpsPoint}.
	 * 
	 * @param dp2D
	 *            the {@link Coordinate2D}
	 * @param pointStyle
	 *            the {@link PointStyle}
	 * @throws MissingCrsException
	 */
	public GpsPoint(DirectPosition2D dp2D, double height, PointStyle pointStyle) throws MissingCrsException {

		if (dp2D.getCoordinateReferenceSystem() == null)
			throw new MissingCrsException();

		CoordinateTransformer t = CoordinateTransformer.getInstance();

		try {
			if (dp2D.getCoordinateReferenceSystem() != t.getUtmCrs())
				dp2D = t.transform(dp2D, t.getUtmCrs());
		} catch (CoordinateTransformException e) {
			log.error(e.getMessage());
			throw new MissingCrsException();
		}

		this.setHeight(height);
		this.setDp(new DrawPoint(dp2D));
		this.setStyle(pointStyle);
	}

	// INTERFACE METHODS

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.drawers.Intersectable#
	 * getDrawGeometry()
	 */
	@Override
	public DrawGeometry getDrawGeometry() {
		return this.getDp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.drawers.Intersectable#
	 * setDrawGeometry(io.github.dagri.GeospatialPDF4J.draw.geometries.
	 * DrawGeometry)
	 */
	@Override
	public void setDrawGeometry(DrawGeometry g) {
		if (g instanceof DrawPoint)
			this.setDp((DrawPoint) g);
		else {
			log.error("Given DrawGeometry was not a DrawPoint!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.dagri.GeospatialPDF4J.draw.drawers.Intersectable#intersects(io.
	 * github.dagri.GeospatialPDF4J.geo.BoundingBox)
	 */
	@Override
	public boolean intersects(BoundingBox bbox) {
		return this.getDp().intersectsBBox(bbox);
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link DrawPoint} of this {@link GpsPoint} as
	 * {@link DrawPoint}.
	 *
	 * @return the {@link DrawPoint} of this {@link GpsPoint} as
	 *         {@link DrawPoint}
	 */
	public DrawPoint getDp() {
		return dp;
	}

	/**
	 * Sets the {@link DrawPoint} of this {@link GpsPoint}
	 *
	 * @param dp
	 *            the {@link DrawPoint} to set
	 */
	public void setDp(DrawPoint dp) {
		this.dp = dp;
	}

	/**
	 * Returns the {@link PointStyle} of this {@link GpsPoint} as
	 * {@link PointStyle}.
	 *
	 * @return the {@link PointStyle} of this {@link GpsPoint} as
	 *         {@link PointStyle}
	 */
	public PointStyle getStyle() {
		return style;
	}

	/**
	 * Sets the {@link PointStyle} of this {@link GpsPoint}.
	 *
	 * @param style
	 *            the {@link PointStyle} to set
	 */
	public void setStyle(PointStyle style) {
		this.style = style;
	}

	/**
	 * Returns the height of this {@link GpsPoint} as {@link Double}.
	 *
	 * @return the height as {@link Double}
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the height of this {@link GpsPoint}.
	 *
	 * @param height
	 *            the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	// OTHERS
}
