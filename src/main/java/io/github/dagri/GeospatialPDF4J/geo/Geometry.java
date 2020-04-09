package io.github.dagri.GeospatialPDF4J.geo;

import io.github.dagri.GeospatialPDF4J.pdf.PdfObject;

/**
 * Class to be used as parental class for all classes of geometry-kind.
 * 
 * The class saves the geometry-type as {@link String}. This geometry-type has
 * to be adapted by all children of this class to their specific value.
 * 
 * @author DaGri
 * @since 07.01.2017
 *
 */
public abstract class Geometry extends PdfObject {

	// ATTRIBUTES

	/**
	 * The geometry-Type of this {@link Geometry} as {@link String}.
	 */
	private String geometryType;

	// CONSTRUCTORS

	/**
	 * Constructor of the abstract parental class Geometry using a
	 * {@link String} to set the geometry-type.
	 * 
	 * @param geomType
	 *            the geometry-type to set
	 */
	public Geometry() {
	}

	// METHODS

	// GETTERS AND SETTERS

	/**
	 * Returns the geometry-type of this {@link Geometry} as {@link String}.
	 *
	 * @return the geometry-type of this {@link Geometry} as {@link String}
	 */
	public String getGeometryType() {
		return geometryType;
	}

	/**
	 * Sets the geometry-type of this {@link Geometry}.
	 *
	 * @param geometryType
	 *            the type to set
	 */
	public void setGeometryType(String geometryType) {
		this.geometryType = geometryType;
	}

	// OTHERS
}
