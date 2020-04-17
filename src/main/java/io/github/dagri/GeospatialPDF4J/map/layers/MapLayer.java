package io.github.dagri.GeospatialPDF4J.map.layers;

import java.util.ArrayList;

import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfStructureTreeRoot;
import com.lowagie.text.pdf.PdfWriter;

import io.github.dagri.GeospatialPDF4J.exceptions.CalcualteLayerInchesException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class to be used as parent of all {@link MapLayer}s.
 * 
 * @author DaGri
 * @since 10.01.2017
 */
@Slf4j
public abstract class MapLayer implements IPdfAddable {

	// ATTRIBUTES

	/**
	 * The {@link BoundingBox} of this {@link MapLayer}.
	 */
	private BoundingBox	layerBBox;

	/**
	 * The width of the map this {@link MapLayer} is contained in, as
	 * {@link Double} in inches.
	 * 
	 * The standard value is Double.NEGATIVE_INFINITY.
	 */
	private double			mapInchesWidth		= Double.NEGATIVE_INFINITY;

	/**
	 * The height of the map this {@link MapLayer} is contained in, as
	 * {@link Double} in inches.
	 * 
	 * The standard value is Double.NEGATIVE_INFINITY.
	 */
	private double			mapInchesHeight		= Double.NEGATIVE_INFINITY;

	/**
	 * The offset in X-direction to the lower left corner of the map as
	 * {@link Double} in pixels.
	 * 
	 * The standard value is Double.NEGATIVE_INFINITY.
	 */
	private double			xOffset2Map			= Double.NEGATIVE_INFINITY;

	/**
	 * The offset in Y-direction to the lower left corner of the map as
	 * {@link Double} in pixels.
	 * 
	 * The standard value is Double.NEGATIVE_INFINITY.
	 */
	private double			yOffset2Map			= Double.NEGATIVE_INFINITY;

	/**
	 * The {@link BoundingBox} of the {@link Map} this {@link MapLayer} is
	 * attached to.
	 */
	private BoundingBox	mapBBox;

	/**
	 * The width of this {@link MapLayer} as {@link Double} in inches.
	 * 
	 * The standard value is Double.NEGATIVE_INFINITY.
	 */
	private double			layerInchesWidth	= Double.NEGATIVE_INFINITY;

	/**
	 * The height of this {@link MapLayer} as {@link Double} in inches.
	 * 
	 * The standard value is Double.NEGATIVE_INFINITY.
	 */
	private double			layerInchesHeight	= Double.NEGATIVE_INFINITY;

	/**
	 * The {@link PdfWriter} that may be used to add content to the PDF-file.
	 */
	private PdfWriter		writer;

	// CONSTRUCTORS

	/**
	 * Constructor for all children of the abstract class {@link MapLayer}
	 * taking a {@link BoundingBox} to define the covered area of this
	 * {@link MapLayer}.
	 * 
	 * @param layerBbox
	 *            the {@link BoundingBox} to set
	 */
	public MapLayer(BoundingBox layerBbox) {
		this.setLayerBBox(layerBbox);
	}

	// METHODS

//	/**
//	 * Abstract method that needs to be implemented by every children of this
//	 * {@link MapLayer}.
//	 * 
//	 * In this method all sub-methods and actions shall be performed, that are
//	 * necessary to display this {@link MapLayer}s information.
//	 */
//	protected abstract void receive() throws MapLayerNotReceivableException;

	/**
	 * Calculates the width and height of this {@link MapLayer} in the PDF in
	 * inches.
	 * 
	 * @throws CalcualteLayerInchesException
	 *             if one of the needed values are <code>null</code> or
	 *             incorrect.
	 */
	public void calcLayerInches() throws CalcualteLayerInchesException {
		// MAKE SURE NOTHING NEEDED IS NULL OR INVALID VALUE
		if (this.getMapBBox() != null && this.getLayerBBox() != null && this.getMapInchesWidth() != Double.NEGATIVE_INFINITY && this.getMapInchesHeight() != Double.NEGATIVE_INFINITY) {

			this.setLayerInchesWidth((this.getLayerBBox().getGeoWidth() / this.getMapBBox().getGeoWidth()) * this.getMapInchesWidth());
			this.setLayerInchesHeight((this.getLayerBBox().getGeoHeight() / this.getMapBBox().getGeoHeight()) * this.getMapInchesHeight());
		} else
			throw new CalcualteLayerInchesException();
	}

	// TODO : GGF IN KLASSE MAP AUSLAGERN, DIE BEIM EMPFANGEN DANN DAS OFFSET IN
	// DIE MAPLAYER SETZT?

	/**
	 * Calculates the offset between two {@link BoundingBox}s in X- and
	 * Y-direction in pixels and returns them in a double array with the size of
	 * two: The first index contains the X-offset, the second the Y-offset.
	 *
	 * @param large
	 *            the larger {@link BoundingBox}
	 * @param small
	 *            the {@link BoundingBox} inside the large
	 *            {@link BoundingBox}
	 * @return {@link Double}[2]
	 */
	public double[] calcPixelOffsets(BoundingBox large, BoundingBox small) {
		double[] erg = new double[2];
		// THE MAPS LOWER LEFT CORNER IS AT THIS POINT ALWAYS <= THE LAYERS
		// LOWER
		// LEFT CORNER IN NORTHING AND EASTING BECAUSE THE MAP ALREADY LOOKED AT
		// THE MAPLAYERS AND CALCULATED ITS BOUNDINGBOX FROM THEIR BBOXES

		// THE BOUNDINGBOX INTERNAL COORDAINTE SYSTEM WILL BE UTM AT THIS POINT,
		// SO THE ORDINATE ORDER IS: ORDIANTE 0 --> EAST; ORDINATE 1 --> NORTH
		double geoWdiff = Math.abs(large.getLl().getOrdinate(0) - small.getLl().getOrdinate(0));
		double geoHdiff = Math.abs(large.getLl().getOrdinate(1) - small.getLl().getOrdinate(1));

		double ratioW = geoWdiff / large.getGeoWidth();
		double ratioH = geoHdiff / large.getGeoHeight();

		erg[0] = ratioW * this.getMapInchesWidth() * 72;
		erg[1] = ratioH * this.getMapInchesHeight() * 72;
		return erg;
	}

	/**
	 * Sets the offset of this {@link MapLayer} to the {@link Map} in pixels,
	 * received from the given {@link Double} array.
	 * 
	 * The first index must contain the X-value, the second one the Y-value.
	 *
	 * @param calcPixelOffsets
	 *            the {@link ArrayList} of {@link Double}s (size = 2)
	 */
	public void setOffsets2Map(double[] calcPixelOffsets) {
		this.setxOffset2Map(calcPixelOffsets[0]);
		this.setyOffset2Map(calcPixelOffsets[1]);
	}

	/**
	 * Creates a {@link PdfStructureElement} with the given name right below the
	 * tree root-element and returns it.
	 * 
	 * May return <code>null</code> if an error occurs.
	 *
	 * @param name
	 *            the name of the {@link PdfStructureElement} to create
	 * @return the {@link PdfStructureElement}
	 */
	protected PdfStructureElement createTopTreeElement(String name) {
		// MAKE SURE THE WRITER IS NOT NULL (NOT SET YET)
		if (this.getWriter() != null) {
			PdfStructureTreeRoot treeRoot = this.getWriter().getStructureTreeRoot();
			PdfStructureElement top = new PdfStructureElement(treeRoot, new PdfName(name));
			return top;
		} else {
			log.error("MapLayer has no PdfWriter attached! Returning null.");
			return null;
		}
	}

	/**
	 * Creates a {@link PdfLayer} to be used as parental layer for other
	 * (children-) layers with the given text, using the given {@link PdfWriter}
	 * . The child-layers will be ordered in a dropdown-kind below the parental
	 * layer.
	 *
	 * @param text
	 *            the text of the parental layer to create as {@link StrictMath}
	 *            .
	 * @param writer
	 *            the {@link PdfWriter} to use to create the {@link PdfLayer}
	 * @return a {@link PdfLayer} instance
	 */
	protected PdfLayer createParentalPdfLayer(String text, PdfWriter writer) {
		PdfLayer parentalLayer = new PdfLayer(text, writer);
		parentalLayer.setOn(true);
		parentalLayer.setView(true);
		parentalLayer.setOnPanel(true);
		return parentalLayer;
	}

	/**
	 * Returns the width of this {@link MapLayer} in pixels, calculated from the
	 * layers width in inches multiplied by the standard PDF-DPI-value of 72.
	 *
	 * @return the width in pixels as {@link Integer}
	 */
	public int getLayerPixelWidth() {
		return (int) (this.getLayerInchesWidth() * 72);
	}

	/**
	 * Returns the height of this {@link MapLayer} in pixels, calculated from
	 * the layers height in inches multiplied by the standard PDF-DPI-value of
	 * 72.
	 *
	 * @return the height in pixels as {@link Integer}
	 */
	public int getLayerPixelHeight() {
		return (int) (this.getLayerInchesHeight() * 72);
	}

	/**
	 * Calculates the scaling factor of the {@link MapLayer}s content (e.g. for
	 * WFS-data) by calculating the factor in width and height and returning the
	 * median of the two values.
	 *
	 * @return the factor to scale with as {@link Double}
	 */
	public double calcScalingFactor() {
		return ((double) this.getLayerPixelWidth() / this.getLayerBBox().getGeoWidth() + (double) this.getLayerPixelHeight() / this.getLayerBBox().getGeoHeight()) / 2;
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link BoundingBox} of this {@link MapLayer} as
	 * {@link BoundingBox}.
	 *
	 * @return the layer- {@link BoundingBox}
	 */
	public BoundingBox getLayerBBox() {
		return layerBBox;
	}

	/**
	 * Sets the {@link BoundingBox} of this {@link MapLayer}.
	 *
	 * @param layerBBox
	 *            the layerBBox to set
	 */
	protected void setLayerBBox(BoundingBox layerBBox) {
		this.layerBBox = layerBBox;
	}

	/**
	 * Returns the {@link BoundingBox} of the {@link Map} this
	 * {@link MapLayer} is attached to as {@link BoundingBox}.
	 *
	 * @return the maps {@link BoundingBox}
	 */
	public BoundingBox getMapBBox() {
		return mapBBox;
	}

	/**
	 * Sets the {@link BoundingBox} of the {@link Map} this {@link MapLayer}
	 * is attached to.
	 *
	 * @param mapBBox
	 *            the maps {@link BoundingBox} to set
	 */
	public void setMapBBox(BoundingBox mapBBox) {
		this.mapBBox = mapBBox;
	}

	/**
	 * Returns the inches to be displayed in width by this {@link MapLayer} as
	 * {@link Double} in inches.
	 *
	 * @return the inches to be displayed in width as {@link Double}
	 */
	public double getMapInchesWidth() {
		return mapInchesWidth;
	}

	/**
	 * Sets the inches to be displayed in width by this {@link MapLayer}.
	 *
	 * @param inchesToDisplayWidth
	 *            the inches to set
	 */
	public void setMapInchesWidth(double inchesToDisplayWidth) {
		this.mapInchesWidth = inchesToDisplayWidth;
	}

	/**
	 * Returns the inches to be displayed in height by this {@link MapLayer} as
	 * {@link Double} in inches.
	 *
	 * @return the inches to be displayed in height as {@link Double}
	 */
	public double getMapInchesHeight() {
		return mapInchesHeight;
	}

	/**
	 * Sets the inches to be displayed in height by this {@link MapLayer}.
	 *
	 * @param inchesToDisplayHeight
	 *            the inches to set
	 */
	public void setMapInchesHeight(double inchesToDisplayHeight) {
		this.mapInchesHeight = inchesToDisplayHeight;
	}

	/**
	 * Returns the offset to the lower left {@link Map}-corner in X-direction as
	 * {@link Double} in pixels.
	 *
	 * @return the x offset to the {@link Map} as {@link Double}
	 */
	public double getxOffset2Map() {
		return xOffset2Map;
	}

	/**
	 * Sets the offset to the lower left {@link Map}-corner in X-direction.
	 *
	 * @param xOffset2Map
	 *            the x offset to set
	 */
	public void setxOffset2Map(double xOffset2Map) {
		this.xOffset2Map = xOffset2Map;
	}

	/**
	 * Returns the offset to the lower left {@link Map}-corner in Y-direction as
	 * {@link Double} in pixels.
	 *
	 * @return the y offset to the {@link Map} as {@link Double}
	 */
	public double getyOffset2Map() {
		return yOffset2Map;
	}

	/**
	 * Sets the offset to the lower left {@link Map}-corner in Y-direction.
	 *
	 * @param yOffset2Map
	 *            the y offset to set
	 */
	public void setyOffset2Map(double yOffset2Map) {
		this.yOffset2Map = yOffset2Map;
	}

	/**
	 * Returns the width of this {@link MapLayer} as {@link Double} in inches.
	 *
	 * @return the layerInchesWidth
	 */
	public double getLayerInchesWidth() {
		return layerInchesWidth;
	}

	/**
	 * Sets the width of this {@link MapLayer} in inches.
	 *
	 * @param layerInchesWidth
	 *            the layerInchesWidth to set
	 */
	public void setLayerInchesWidth(double layerInchesWidth) {
		this.layerInchesWidth = layerInchesWidth;
	}

	/**
	 * Returns the height of this {@link MapLayer} as {@link Double} in inches.
	 *
	 * @return the layerInchesHeight
	 */
	public double getLayerInchesHeight() {
		return layerInchesHeight;
	}

	/**
	 * Sets the height of this {@link MapLayer} as {@link Double} in inches.
	 *
	 * @param layerInchesHight
	 *            the layerInchesHeight to set
	 */
	public void setLayerInchesHeight(double layerInchesHight) {
		this.layerInchesHeight = layerInchesHight;
	}

	/**
	 * Returns the {@link PdfWriter} of this {@link MapLayer}.
	 *
	 * @return the {@link PdfWriter} of this {@link MapLayer}
	 */
	public PdfWriter getWriter() {
		return writer;
	}

	/**
	 * Sets the {@link PdfWriter} of this {@link MapLayer}.
	 *
	 * @param writer
	 *            the {@link PdfWriter} to set
	 */
	public void setWriter(PdfWriter writer) {
		this.writer = writer;
	}

	// OTHERS
}
