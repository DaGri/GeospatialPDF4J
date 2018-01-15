package io.github.dagri.GeospatialPDF4J.map.layers.webservice;

import java.util.ArrayList;

import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;
import io.github.dagri.GeospatialPDF4J.res.LayerImage;

/**
 * Abstract class to be used as parental class for all {@link MapLayer}s that
 * include an image.
 * 
 * @author DaGri
 * @since 14.01.2017
 */
public abstract class ImageLayer extends WebserviceLayer {

	// ATTRIBUTES

	/**
	 * The {@link ArrayList} of {@link Integer}s containing the DPI-values for
	 * the layers.
	 */
	private ArrayList<Integer> dpis = new ArrayList<>();

	/**
	 * The {@link LayerImage} containing the image of this {@link ImageLayer}.
	 */
	private ArrayList<LayerImage> layerImages = new ArrayList<>();

	// CONSTRUCTORS

	/**
	 * Constructor for an {@link ImageLayer}.
	 * 
	 * @param url
	 *            the link to the server as {@link String}
	 * @param layerBbox
	 *            the {@link BoundingBox} of this layer
	 * @param version
	 *            the version to request as {@link String}
	 * @param layers
	 *            the layers to request as {@link ArrayList} of {@link String}s
	 */
	public ImageLayer(String url, BoundingBox layerBbox, String version, ArrayList<String> layers) {
		super(url, layerBbox, version, layers);
	}

	// METHODS

	/**
	 * Adds a {@link LayerImage} to the internal {@link ArrayList} of
	 * {@link LayerImage}s.
	 *
	 * @param image
	 *            the {@link LayerImage} to add
	 */
	public void addLayerImage(LayerImage image) {
		if (image != null)
			this.getLayerImages().add(image);
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link ArrayList} of DPI values.
	 *
	 * @return the {@link ArrayList} of DPI values
	 */
	public ArrayList<Integer> getDpis() {
		return dpis;
	}

	/**
	 * Sets the {@link ArrayList} of DPI values.
	 *
	 * @param dpis
	 *            the {@link ArrayList} to set
	 */
	public void setDpis(ArrayList<Integer> dpis) {
		if (dpis != null)
			this.dpis = dpis;
	}

	/**
	 * Returns the {@link LayerImage}s of this {@link ImageLayer} as
	 * {@link ArrayList}.
	 *
	 * @return the {@link ArrayList} of {@link LayerImage}s.
	 */
	public ArrayList<LayerImage> getLayerImages() {
		return layerImages;
	}

	// OTHERS
}
