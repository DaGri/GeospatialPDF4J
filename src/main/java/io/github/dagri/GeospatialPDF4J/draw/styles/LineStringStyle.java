package io.github.dagri.GeospatialPDF4J.draw.styles;

import java.awt.Color;

/**
 * Class to contain various informations on how to draw a line-string-style
 * object in a PDF document.
 * 
 * @author DaGri
 * @since 09.02.2017
 */
public class LineStringStyle {

	// ATTRIBUTES

	/**
	 * The strength of the drawn line.
	 * 
	 * Standard value is 1.0f;
	 */
	public float	lineStringStrength		= 1.0f;

	/**
	 * The stroke {@link Color} of the drawn line.
	 * 
	 * Default value is BLACK.
	 */
	public Color	lineStringColor			= Color.BLACK;

	/**
	 * {@link Boolean} indicating if the suspension points of the line shall be
	 * drawn thicker.
	 * 
	 * Standard value is <code>false</code>.
	 */
	public boolean	lineStringPoints		= false;

	/**
	 * The radius of the suspension points of the line.
	 *
	 * Standard value is 1.0f.
	 */
	public float	lineStringPointRadius	= 1.0f;

	// CONSTRUCTORS

	/**
	 * Empty constructor for a {@link LineStringStyle}. All values will be
	 * default values
	 */
	public LineStringStyle() {
		super();
	}

	/**
	 * Constructor for a {@link LineStringStyle} using various informations:
	 * 
	 * @param lineStringWidth
	 *            the width of the line to draw
	 * @param lineStringColor
	 *            the {@link Color} of the line to draw
	 * @param lineStringPoints
	 *            a {@link Boolean} indicating if the suspension points shall be
	 *            drawn or not
	 * @param lineStringPointRadius
	 *            the radius of the suspension points
	 */
	public LineStringStyle(float lineStringWidth, Color lineStringColor, boolean lineStringPoints, float lineStringPointRadius) {
		super();
		this.lineStringStrength = lineStringWidth;
		this.lineStringColor = lineStringColor;
		this.lineStringPoints = lineStringPoints;
		this.lineStringPointRadius = lineStringPointRadius;
	}

	// METHODS

	// GETTERS AND SETTERS

	/*
	 * Getters and Setters are not planned at this point. All attributes are
	 * public.
	 */

	// OTHERS
}
