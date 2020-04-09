package io.github.dagri.GeospatialPDF4J.pdf;

import io.github.dagri.GeospatialPDF4J.res.AdditionalInfo;

/**
 * Class to be used as parental class for all classes that can be displayed in a
 * PDF-file.
 * 
 * @author DaGri
 * @since 07.01.2017
 *
 */
public abstract class PdfObject {

	// ATTRIBUTES

	/**
	 * The {@link AdditionalInfo} of this {@link PdfObject}.
	 */
	private AdditionalInfo info = new AdditionalInfo();

	// CONSTRUCTORS

	// METHODS

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link AdditionalInfo} object of this {@link PdfObject}.
	 *
	 * @return the info as {@link AdditionalInfo}
	 */
	public AdditionalInfo getInfo() {
		return info;
	}

	/**
	 * Sets the {@link AdditionalInfo} of this {@link PdfObject}.
	 *
	 * @param info the {@link AdditionalInfo} to set
	 */
	public void setInfo(AdditionalInfo info) {
		this.info = info;
	}

	// OTHERS
}
