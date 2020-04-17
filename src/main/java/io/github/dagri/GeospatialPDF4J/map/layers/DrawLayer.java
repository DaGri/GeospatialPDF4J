package io.github.dagri.GeospatialPDF4J.map.layers;

import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.Map;

/**
 * Class to extend the {@link MapLayer} class and define methods for all fully
 * implemented children of this class that contain content to be drawn (e.g.
 * DrawPoints, DrawLineStrings, DrawPolygons...).
 * 
 * @author DaGri
 * @since 23.07.2017
 */
public abstract class DrawLayer extends MapLayer {

	// ATTRIBUTES

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link DrawLayer} using a {@link BoundingBox} to
	 * define the {@link DrawLayer}s coverage area.
	 * 
	 * @param layerBbox
	 *            the {@link BoundingBox} to set
	 */
	public DrawLayer(BoundingBox layerBbox) {
		super(layerBbox);
	}

	// INHERTITED METHOD

	/*
	 * All methods from the Interface 'IPdfAddable'.
	 */
	// TODO : KOENNTE DIE KLASSE NICHT AUCH DURCH EIN INTERFACE ERSETZT WERDEN?

	// METHODS

	/**
	 * <ul>
	 * <li>Reduces ALL drawable content in northing and easting about the lower
	 * left corner's northing and easting of the given {@link BoundingBox}.
	 * </li>
	 * <li>You will have to fill and call this method to bring the content of
	 * different {@link MapLayer}s above another and in the correct distance to
	 * the origin of the lower left corner of the PDF-page.</li>
	 * <li>Explanation: You cut of the offset of the drawable content to the
	 * UTM-CRS (like moving it there) as a step to convert the coordinates into
	 * the page-coordinate-system of the PDF.</li>
	 * </ul>
	 *
	 * @param bbox
	 *            the {@link BoundingBox} to reduce with
	 */
	protected abstract void reduceContent(BoundingBox bbox);

	/**
	 * <ul>
	 * <li>Scales ALL drawable content in northing and easting by the given
	 * factor.</li>
	 * <li>You will have to fill and call this method to bring the content of
	 * different {@link MapLayer}s in the correct relation to each other.</li>
	 * <li>Explanation: You scale the internal coordinates of all drawable
	 * content to an expansion that fits the PDF-pages size as a step to convert
	 * the coordinates into the page-coordinate-system of the PDF.</li>
	 * </ul>
	 * 
	 * @param factor
	 *            the value to scale with
	 */
	protected abstract void scaleContent(double factor);

	/**
	 * <ul>
	 * <li>Adds an offset to all drawable content, in northing and easting,
	 * about the given values to respect the margins of the document (marginX &
	 * marginY) and the offset of the {@link MapLayer} to the {@link Map}
	 * (offsetX, offsetY).</li>
	 * <li>You will have to fill and call this method to move the drawable
	 * elements from the lower left corner of the PDF-page to the start of the
	 * map-image (over the margin of the document) and from this point to the
	 * offset of the MapLayer to the maps origin</li>
	 * <li>Explanation: the reduced and scaled drawable contents are allready
	 * converted to the PDF-internal coordiante-system, but still need to be
	 * moved to their correct position inside of this local CRS.</li>
	 * </ul>
	 * 
	 * Positive values will be subtracted, negative will be added.
	 *
	 * @param marginX
	 *            the width of the margin offset in x-direction
	 * @param marginY
	 *            the width of th margin offset in y-direction
	 * @param offsetX
	 *            the offset to the map in x-direction
	 * @param offsetY
	 *            the offset to the map in y-direction
	 */
	protected abstract void addMarginOffset(double marginX, double marginY, double offsetX, double offsetY);

	// GETTERS AND SETTERS

	// OTHERS
}
