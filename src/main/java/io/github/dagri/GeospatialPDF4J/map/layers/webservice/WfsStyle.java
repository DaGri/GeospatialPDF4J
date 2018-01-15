package io.github.dagri.GeospatialPDF4J.map.layers.webservice;

import io.github.dagri.GeospatialPDF4J.draw.styles.LineStringStyle;
import io.github.dagri.GeospatialPDF4J.draw.styles.PointStyle;
import io.github.dagri.GeospatialPDF4J.draw.styles.PolygonStyle;

/**
 * Class to contain and combine a {@link PointStyle}, {@link LineStringStyle}
 * and a {@link PolygonStyle} so it can be used for displaying WFS data.
 * 
 * @author DaGri
 * @since 06.02.2017
 *
 */
public class WfsStyle {

	// ATTRIBUTES

	/**
	 * The {@link PointStyle} of this {@link WfsStyle}.
	 */
	public PointStyle		pointStyle		= new PointStyle();

	/**
	 * The {@link LineStringStyle} of this {@link WfsStyle}.
	 */
	public LineStringStyle	lineStringStyle	= new LineStringStyle();

	/**
	 * The {@link PolygonStyle} of this {@link WfsStyle}.
	 */
	public PolygonStyle		polygonStyle	= new PolygonStyle();

	// CONSTRUCTORS

	/**
	 * Empty constructor for a {@link WfsStyle}.
	 */
	public WfsStyle() {
		// NOTHING
	}

	// METHODS

	// GETTERS AND SETTERS

	// OTHERS
}
