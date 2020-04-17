package io.github.dagri.GeospatialPDF4J.draw.styles;

import java.awt.Color;

/**
 * Class to contain various informations on how to draw a point-style object in
 * a PDF document.
 * 
 * @author DaGri
 * @since 09.02.2017
 *
 */
public class PointStyle {

	// ATTRIBUTES

	/**
	 * The {@link Icon} to display instead of a drawn point indicator.
	 * 
	 * May be null. Standard value is null.
	 */
	public Icon pointIcon = null;

	/**
	 * {@link Boolean} indicating if the {@link Icon} shall be displayed
	 * centered or not.
	 * 
	 * Standard value is <code>false</code>.
	 */
	public boolean centered = false;

	/**
	 * The radius of the drawn point indicator or the {@link Icon} to display.
	 * In case of a non-circle-geometry this value will be used as edge-length.
	 * 
	 * Standard value is 1.0f;
	 */
	public float pointRadius = 1.0f;

	/**
	 * The width of the lines to draw a point indicator with.
	 * 
	 * Standard value is 1.0f;
	 */
	public float pointLineWidth = 1.0f;

	/**
	 * The stroke {@link Color} of the border or line to draw the point
	 * indicator with.
	 * 
	 * Standard value is BLACK.
	 */
	public Color pointColor = Color.BLACK;

	/**
	 * {@link Boolean} indicating if the drawn point indicator shall be filled
	 * (if possible).
	 * 
	 * Standard value is <code>true</code>.
	 */
	public boolean pointFilled = false;

	/**
	 * The filling {@link Color} of the drawn point indicator.
	 * 
	 * Standard value is BLACK.
	 */
	public Color pointFillColor = Color.BLACK;

	/**
	 * The point symbol contained in {@link EPointAppearance} enum.
	 * 
	 * Standard value is EPointAppearance.CIRCLE.
	 */
	public EPointAppearance pointSymbol = EPointAppearance.CIRCLE;

	// CONSTRUCTORS

	/**
	 * Empty constructor for a {@link PointStyle}. All values will be default
	 * values
	 */
	public PointStyle() {
		super();
	}

	/**
	 * Constructor for a {@link PointStyle} using various informations:
	 * 
	 * @param pointIcon
	 *            the {@link Icon} to display as point indicator (may be null!)
	 * @param centered
	 *            a {@link Boolean} indicating if the {@link Icon} or drawing
	 *            shall be centered above the position or if it shall be the
	 *            lower left corner
	 * @param pointRadius
	 *            the radius of the drawing or {@link Icon}
	 * @param pointLineWidth
	 *            the width of the stroke
	 * @param pointColor
	 *            the {@link Color} of the stroke
	 * @param pointFilled
	 *            a {@link Boolean} indicating if the drawing shall be filled or
	 *            not
	 * @param pointFillColor
	 *            the filling {@link Color} of the drawing
	 */
	public PointStyle(Icon pointIcon, boolean centered, float pointRadius, float pointLineWidth, Color pointColor,
			boolean pointFilled, Color pointFillColor) {
		super();
		// SET ALL PARAMETERS 
		this.pointIcon = pointIcon;
		this.centered = centered;
		this.pointRadius = pointRadius;
		this.pointLineWidth = pointLineWidth;
		this.pointColor = pointColor;
		this.pointFilled = pointFilled;
		this.pointFillColor = pointFillColor;
	}

	// METHODS

	// GETTERS AND SETTERS

	// OTHERS
}
