package io.github.dagri.GeospatialPDF4J.draw.drawers;

import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfWriter;

import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawLineString;
import io.github.dagri.GeospatialPDF4J.map.layers.gps.GpsLayer;
import io.github.dagri.GeospatialPDF4J.map.layers.gps.GpsPoint;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to draw the content of a {@link GpsLayer} to a GeospatialPDF.
 * 
 * @author DaGri
 * @since 18.07.2017
 *
 */
@Slf4j
public class GpsDrawer extends PdfDrawer {

	// ATTRIBUTES

	/**
	 * The {@link GpsLayer} this {@link GpsDrawer} is drawing.
	 */
	private GpsLayer gpsLayer;

	// TODO ABSCHNEIDEN DER GEOMETRIEN DURCH DEN GEOMETRYCOTNAINER

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link GpsDrawer} that is able to draw the data
	 * included in a {@link GpsLayer} into a GeospatialPDF-document.
	 * 
	 * @param writer
	 *            the {@link PdfWriter} to be used to draw
	 * @param top
	 *            the {@link PdfStructureElement} to order the data below
	 * @param parentalLayer
	 *            the {@link PdfLayer} to order the data below
	 */
	public GpsDrawer(PdfWriter writer, GpsLayer gpsLayer, PdfStructureElement top, PdfLayer parentalLayer) {
		super(writer, top, parentalLayer);
		this.setGpsLayer(gpsLayer);
	}

	// INHERITED METHODS

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.pdf.PdfDrawer#drawAll()
	 */
	@Override
	public void drawAll() {
		log.info("Starting to draw all elements...");

		log.debug("Starting to draw the LineString...");
		// CREATE LAYER
		PdfLayer lineString = new PdfLayer("GPS-Track", this.getWriter());
		// BEGIN LAYER
		this.getContByte().beginLayer(lineString);
		// DRAW
		this.drawLineString();
		// END LAYER
		this.getContByte().endLayer();
		// ADD LAYER TO PARENT LAYER
		this.getParentalLayer().addChild(lineString);
		log.debug("Drawing of the LineString finished.");

		log.debug("Starting to draw the GpsPoints...");
		// CREATE LAYER
		PdfLayer gpsPoints = new PdfLayer("GPS-Points", this.getWriter());
		// BEGIN LAYER
		this.getContByte().beginLayer(gpsPoints);
		// DRAW
		this.drawPoints();
		// END LAYER
		this.getContByte().endLayer();
		// ADD LAYER TO PARENT LAYER
		this.getParentalLayer().addChild(gpsPoints);
		log.debug("Drawing of the GpsPoints finished.");

		log.debug("Starting to draw the colored GpsPoints...");
		// CREATE LAYER
		PdfLayer colorPoints = new PdfLayer("Colored GPS-Points", this.getWriter());
		// BEGIN LAYER
		this.getContByte().beginLayer(colorPoints);
		// DRAW
		this.drawColoredPoints();
		// END LAYER
		this.getContByte().endLayer();
		// ADD LAYER TO PARENTAL LAYER
		this.getParentalLayer().addChild(colorPoints);
		log.debug("Drawing of the colored GpsPoints finished.");
		log.info("All elements of the GPS-Layer drawn.");
	}

	// METHODS

	/**
	 * Draws the {@link GpsPoint}s of the {@link GpsLayer}.
	 */
	private void drawPoints() {
		log.debug("Drawing GpsPoints...");
		for (int a = 0; a < this.getGpsLayer().getGpsPoints().size(); a++) {
			log.debug("Drawing GpsPoint " + a + "...");
			GpsPoint temp = this.getGpsLayer().getGpsPoints().get(a);
			this.getContByte().beginMarkedContentSequence(this.createStructureElement(temp.getInfo(), "GPS-Point"));
			this.drawDrawPoint(temp.getDp(), this.getGpsLayer().getPointStyle());
			this.getContByte().endMarkedContentSequence();
			log.debug("Drawn GpsPoint " + a + ".");
		}
		log.debug("GpsPoints drawn.");
	}

	/**
	 * Draws the colored {@link GpsPoint}s of the {@link GpsLayer}.
	 */
	private void drawColoredPoints() {
		log.debug("Drawing colored GpsPoints...");
		for (int a = 0; a < this.getGpsLayer().getGpsColoredPoints().size(); a++) {
			log.debug("Drawing colored GpsPoint " + a + "...");
			GpsPoint temp = this.getGpsLayer().getGpsColoredPoints().get(a);
			this.getContByte().beginMarkedContentSequence(this.createStructureElement(temp.getInfo(), "Colored GPS-Point"));
			this.drawDrawPoint(temp.getDp(), temp.getStyle());
			this.getContByte().endMarkedContentSequence();
			log.debug("Drawn Colored GpsPoint " + a + ".");
		}
		log.debug("Colored GpsPoints drawn.");
	}

	/**
	 * Draws the {@link DrawLineString} of the {@link GpsLayer}.
	 */
	private void drawLineString() {
		log.debug("Drawing LineString...");
		DrawLineString temp = this.getGpsLayer().getDrawLine();
		this.getContByte().beginMarkedContentSequence(this.createStructureElement(temp.getInfo(), "GPS-Track"));
		this.drawLineString(temp, this.getGpsLayer().getLineStringStyle());
		this.getContByte().endMarkedContentSequence();
		log.debug("Linestring drawn.");
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link GpsLayer} of this {@link GpsDrawer}.
	 *
	 * @return the {@link GpsLayer} of this {@link GpsDrawer}
	 */
	public GpsLayer getGpsLayer() {
		return gpsLayer;
	}

	/**
	 * Sets the {@link GpsLayer} of this {@link GpsDrawer}.
	 *
	 * @param gpsLayer
	 *            the {@link GpsLayer} to set
	 */
	public void setGpsLayer(GpsLayer gpsLayer) {
		this.gpsLayer = gpsLayer;
	}

	// OTHERS
}
