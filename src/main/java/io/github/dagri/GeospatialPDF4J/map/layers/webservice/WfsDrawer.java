package io.github.dagri.GeospatialPDF4J.map.layers.webservice;

import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfWriter;

import io.github.dagri.GeospatialPDF4J.draw.drawers.DataDrawer;

/**
 * Class that extend the abstract class {@link DataDrawer} with the intend to be
 * used to draw various types of WFS-styled objects that are contained in a
 * {@link WfsLayer}.
 * 
 * @author DaGri
 * @since 08.10.2017
 */
public class WfsDrawer extends DataDrawer {

	// ATTRIBUTES

	/**
	 * The {@link WfsLayer} of this {@link WfsDrawer} used to gain the objects
	 * to be drawn from.
	 */
	private WfsLayer wfsLayer;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link WfsDrawer} using
	 * <ul>
	 * <li>a {@link PdfWriter} to draw</li>
	 * <li>a {@link WfsLayer} to gain the information from</li>
	 * <li>a {@link PdfStructureElement} to add the information below</li>
	 * <li>a {@link PdfLayer} to draw inside</li>
	 * </ul>
	 * 
	 * @param writer
	 *            the {@link PdfWriter}
	 * @param layer
	 *            the {@link WfsLayer}
	 * @param top
	 *            the {@link PdfStructureElement}
	 * @param parentalLayer
	 *            the {@link PdfLayer}
	 */
	public WfsDrawer(PdfWriter writer, WfsLayer layer, PdfStructureElement top, PdfLayer parentalLayer) {
		super(writer, top, parentalLayer);
		this.setWfsLayer(layer);
	}

	// INHERITED METHODS

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.drawers.DataDrawer#drawAll()
	 */
	@Override
	public void drawAll() {
		PdfLayer polygons = new PdfLayer("Polygons", this.getWriter());
		// BEGIN LAYER
		this.getContByte().beginLayer(polygons);
		for (int c = 0; c < this.getWfsLayer().getGeoContainer().getDrawPolygons().size(); c++)
			this.drawPolygon(this.getWfsLayer().getGeoContainer().getDrawPolygons().get(c), this.getWfsLayer().getStyles().polygonStyle);
		this.getContByte().endLayer();

		PdfLayer lineStrings = new PdfLayer("LineStrings", this.getWriter());
		// BEGIN LAYER
		this.getContByte().beginLayer(lineStrings);
		for (int b = 0; b < this.getWfsLayer().getGeoContainer().getDrawLineStrings().size(); b++)
			this.drawLineString(this.getWfsLayer().getGeoContainer().getDrawLineStrings().get(b), this.getWfsLayer().getStyles().lineStringStyle);
		this.getContByte().endLayer();

		PdfLayer points = new PdfLayer("Points", this.getWriter());
		// BEGIN LAYER
		this.getContByte().beginLayer(points);

		for (int a = 0; a < this.getWfsLayer().getGeoContainer().getDrawPoints().size(); a++)
			this.drawDrawPoint(this.getWfsLayer().getGeoContainer().getDrawPoints().get(a), this.getWfsLayer().getStyles().pointStyle);
		this.getContByte().endLayer();

		this.getParentalLayer().addChild(polygons);
		this.getParentalLayer().addChild(lineStrings);
		this.getParentalLayer().addChild(points);

	}

	// METHODS

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link WfsLayer} of this {@link WfsDrawer} as
	 * {@link WfsLayer}.
	 *
	 * @return the {@link WfsLayer} of this {@link WfsDrawer}
	 */
	public WfsLayer getWfsLayer() {
		return wfsLayer;
	}

	/**
	 * Sets the {@link WfsLayer} of this {@link WfsDrawer}.
	 *
	 * @param wfsLayer
	 *            the {@link WfsLayer} to set
	 */
	public void setWfsLayer(WfsLayer wfsLayer) {
		this.wfsLayer = wfsLayer;
	}

	// OTHERS
}
