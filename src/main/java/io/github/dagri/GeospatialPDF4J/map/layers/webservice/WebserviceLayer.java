package io.github.dagri.GeospatialPDF4J.map.layers.webservice;

import java.util.ArrayList;

import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;

/**
 * Class to be used as parental class for all kinds of {@link MapLayer} that
 * interact with a web service like a WMS or a WFS.
 * 
 * @author DaGri
 * @since 14.01.2017
 *
 */
public abstract class WebserviceLayer extends MapLayer {

	// ATTRIBUTES

	/**
	 * The {@link ArrayList} of {@link String}s containing the layers to be
	 * requested from the server.
	 */
	private ArrayList<String>	layers;

	/**
	 * The URL to the server as {@link String}.
	 */
	private String				url;

	/**
	 * The version of the server to be requested as {@link String} (like
	 * "1.3.0");
	 */
	private String				version;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link WebserviceLayer} using a {@link String} that
	 * contains the URL to the server, a {@link BoundingBox} to be used for
	 * the {@link WmsLayer}, a {@link String} to define the server-version to
	 * request and an {@link ArrayList} of {@link String}s to contain the layers
	 * to request from the server.
	 * 
	 * @param url
	 *            the URL to the server
	 * @param layerBbox
	 *            the {@link BoundingBox} to use
	 * @param version
	 *            the server version to request
	 * @param layers
	 *            the {@link ArrayList} of layers to request
	 */
	public WebserviceLayer(String url, BoundingBox layerBbox, String version, ArrayList<String> layers) {
		super(layerBbox);
		this.setUrl(url);
		this.setLayers(layers);
		this.setVersion(version);
	}

	// METHODS

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link ArrayList} of {@link String}s containing the layers to
	 * request.
	 *
	 * @return the {@link ArrayList} of {@link String}s containing the layers
	 */
	public ArrayList<String> getLayers() {
		return layers;
	}

	/**
	 * Sets the {@link ArrayList} of {@link String}s containing the layers.
	 *
	 * @param layers
	 *            the layers to set
	 */
	private void setLayers(ArrayList<String> layers) {
		this.layers = layers;
	}

	/**
	 * Returns the {@link String} containing the url to the server.
	 *
	 * @return the url as {@link String}
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL to the server.
	 *
	 * @param url
	 *            the {@link String} to set
	 */
	private void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Returns the {@link String} containing the server version to request.
	 *
	 * @return the version contained in a {@link String}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the {@link String} containing the server version.
	 *
	 * @param version
	 *            the version to set
	 */
	private void setVersion(String version) {
		this.version = version;
	}

	// OTHERS
}
