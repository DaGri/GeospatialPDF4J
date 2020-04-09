package io.github.dagri.GeospatialPDF4J.res;

import java.util.HashMap;

/**
 * Class to save information consisting of a key and a value, both as Strings.
 * 
 * This class is not comparable to a {@link HashMap}, even if it seems so. The
 * key - value pairs are used to store information that will may be written into
 * a PDF-Document.
 * 
 * @author DaGri
 * @since 07.01.2017
 *
 */
public class InfoTupel {

	// ATTRIBUTES

	/**
	 * The key of this {@link InfoTupel} as {@link String}.
	 */
	private String key;

	/**
	 * The value of the {@link InfoTupel} as {@link String}.
	 */
	private String value;

	// CONSTRUCTORS

	/**
	 * Empty constructor for an {@link InfoTupel}.
	 */
	public InfoTupel() {
	}

	/**
	 * Constructor for an {@link InfoTupel} using a {@link String} input for the
	 * key and another one for the value.
	 * 
	 * @param key
	 *            the {@link String} as key
	 * @param value
	 *            the {@link String} as value
	 */
	public InfoTupel(String key, String value) {
		super();
		this.setKey(key);
		this.setValue(value);
	}

	// METHODS

	// GETTERS AND SETTERS

	/**
	 * Returns the key of this {@link InfoTupel} as {@link String}.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of this {@link InfoTupel}.
	 *
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		if(key == null)
			key = "noKey";
		this.key = key;
	}

	/**
	 * Returns the value of this {@link InfoTupel} as {@link String}.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of this {@link InfoTupel}.
	 *
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		if(value == null)
			value = "noValue";
		this.value = value;
	}

	// OTHERS

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InfoTupel [key=" + key + ", value=" + value + "]";
	};

}
