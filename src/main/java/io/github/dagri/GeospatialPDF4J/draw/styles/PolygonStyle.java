package io.github.dagri.GeospatialPDF4J.draw.styles;

import java.awt.Color;

/**
 * Class to contain various informations on how to draw a polygon-style object
 * in a PDF document.
 * 
 * @author DaGri
 * @since 09.02.2017
 */
public class PolygonStyle {

	// ATTRIBUTES

	/**
	 * The width of the stroke line of the polygon.
	 * 
	 * Standard value is 1.0f.
	 */
	public float polygonStrokeWidth = 1.0f;

	/**
	 * The polygon stroke {@link Color}.
	 * 
	 * Standard value is BLACK.
	 */
	public Color polygonColor = Color.BLACK;

	/**
	 * {@link Boolean} indicating if the drawn point indicator shall be filled
	 * (if possible).
	 * 
	 * Standard value is <code>true</code>.
	 */
	public boolean polygonFilled = true;

	/**
	 * The filling {@link Color} of the polygon.
	 * 
	 * Standard value is BLACK.
	 */
	public Color polygonFillColor = Color.BLACK;

	/**
	 * {@link Boolean} indicating if the suspension points of the polygon shall
	 * be drawn thicker.
	 * 
	 * Standard value is <code>false</code>.
	 */
	public boolean polygonPoints = false;

	/**
	 * The radius of the suspension points of the exterior / interior rings.
	 *
	 * Standard value is 1.0f.
	 */
	public float polygonPointRadius = 1.0f;

	// CONSTRUCTORS

	/**
	 * Empty constructor for a {@link PolygonStyle}. All values will be default
	 * values.
	 */
	public PolygonStyle() {
		// NOTHING
	}

	/**
	 * Constructor for a {@link PolygonStyle} using various input values:
	 * 
	 * @param polygonStrokeWidth
	 *            the width of the stroke to draw
	 * @param polygonColor
	 *            the stroke {@link Color} of the polygon
	 * @param polygonFilled
	 *            a {@link Boolean} indicating if the polygon shall be filled
	 * @param polygonFillColor
	 *            the filling color of the polygon
	 * @param polygonPoints
	 *            a {@link Boolean} indicating if the suspension points shall be
	 *            plotted
	 * @param polygonPointRadius
	 *            the radius of the suspension points
	 */
	public PolygonStyle(float polygonStrokeWidth, Color polygonColor, boolean polygonFilled, Color polygonFillColor,
			boolean polygonPoints, float polygonPointRadius) {
		super();
		this.polygonStrokeWidth = polygonStrokeWidth;
		this.polygonColor = polygonColor;
		this.polygonFilled = polygonFilled;
		this.polygonFillColor = polygonFillColor;
		this.polygonPoints = polygonPoints;
		this.polygonPointRadius = polygonPointRadius;
	}

	// METHODS

	// GETTERS AND SETTERS

	// OTHERS
}
