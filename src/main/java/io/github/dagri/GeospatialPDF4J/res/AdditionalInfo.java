package io.github.dagri.GeospatialPDF4J.res;

import java.util.ArrayList;

/**
 * Class to manage {@link InfoTupel}s.
 * 
 * This class stores {@link InfoTupel} in an {@link ArrayList} can be used as an
 * attachment to various data objects.
 * 
 * @author DaGri
 * @since 07.01.2017
 */
public class AdditionalInfo {

	// ATTRIBUTES

	/**
	 * The {@link ArrayList} of {@link InfoTupel}s.
	 */
	private ArrayList<InfoTupel> infos = new ArrayList<>();

	// CONSTRUCTORS

	/**
	 * Empty constructor for an {@link AdditionalInfo}.
	 *
	 */
	public AdditionalInfo() {
	}

	// METHODS

	/**
	 * Adds the {@link InfoTupel} to the internal {@link ArrayList}.
	 *
	 * @param tupel
	 */
	public void addInfo(InfoTupel tupel) {
		if (tupel != null)
			this.getInfos().add(tupel);
	}

	/**
	 * Adds a new {@link InfoTupel} to the internal {@link ArrayList} using the
	 * given values.
	 *
	 * @param key
	 *            the key to use
	 * @param value
	 *            the value to use
	 */
	public void addInfo(String key, String value) {
		this.getInfos().add(new InfoTupel(key, value));
	}

	/**
	 * Returns the count of stored {@link InfoTupel}s as {@link Integer}.
	 *
	 * @return the count of {@link InfoTupel} as {@link Integer}
	 */
	public int infoCount() {
		return this.getInfos().size();
	}

	/**
	 * Returns the {@link InfoTupel} at the specified index (starting with
	 * zero!!!).
	 *
	 * @param index
	 *            the index of the {@link InfoTupel} to get
	 * @return a {@link InfoTupel} object
	 */
	public InfoTupel getInfo(int index) throws IndexOutOfBoundsException {
		if (this.getInfos().size() < index)
			return this.getInfos().get(this.getInfos().size() - 1);
		else
			return this.getInfos().get(index);
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link ArrayList} of {@link InfoTupel}s.
	 *
	 * @return the {@link ArrayList} of {@link InfoTupel}s
	 */
	public ArrayList<InfoTupel> getInfos() {
		return infos;
	}

	// OTHERS

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AdditionalInfo [infos=" + infos + "]";
	}

}
