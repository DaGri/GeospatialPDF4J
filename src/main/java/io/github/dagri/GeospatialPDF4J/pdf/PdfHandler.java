package io.github.dagri.GeospatialPDF4J.pdf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfDeveloperExtension;
import com.lowagie.text.pdf.PdfWriter;

import io.github.dagri.GeospatialPDF4J.exceptions.BoundingboxNotCreatableException;
import io.github.dagri.GeospatialPDF4J.exceptions.CalcualteLayerInchesException;
import io.github.dagri.GeospatialPDF4J.exceptions.MapLayerNotReceivableException;
import io.github.dagri.GeospatialPDF4J.exceptions.PdfCreationException;
import io.github.dagri.GeospatialPDF4J.exceptions.PdfHandlerException;
import io.github.dagri.GeospatialPDF4J.map.Map;
import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;
import io.github.dagri.GeospatialPDF4J.map.layers.ReferencedLayer;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to handle various operations on a PDF document.
 * 
 * @author DaGri
 * @since 13.01.2017
 */
@Slf4j
public class PdfHandler {

	// ATTRIBUTES

	/**
	 * The {@link Map} to work with.
	 */
	private Map			map;

	/**
	 * The {@link Document} to insert the data.
	 */
	private Document	doc;

	/**
	 * The writer of the {@link Document}.
	 */
	private PdfWriter	writer;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link PdfHandler} using a {@link Document}, a
	 * {@link Map} and a name, for the document in the file-system, as
	 * {@link String}.
	 * 
	 * Throws a {@link PdfHandlerException} if an error occurs.
	 * 
	 * @param doc
	 *            the {@link Document}
	 * @param map
	 *            the {@link Map}
	 * @param pdfName
	 *            the name of the PDF as {@link String}
	 * @throws PdfHandlerException
	 */
	public PdfHandler(Document doc, Map map, String pdfName) throws PdfHandlerException {
		log.info("Creating new PdfHandler...");

		// CHECKING THE DOCUMENT
		if (doc != null) {
			log.debug("Setting document...");
			this.setDoc(doc);
		} else {
			log.error("Document is null.");
			throw new PdfHandlerException();
		}

		// CHECKING THE MAP
		if (map != null) {
			log.debug("Setting map...");
			this.setMap(map);
		} else {
			log.error("Map is null.");
			throw new PdfHandlerException();
		}

		try {
			log.debug("Setting PDFWriter...");
			this.setWriter(PdfWriter.getInstance(doc, new FileOutputStream("output/" + pdfName + ".pdf")));
			this.getWriter().setTagged();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage().toString());
			throw new PdfHandlerException();
		} catch (DocumentException e) {
			log.error(e.getMessage().toString());
			throw new PdfHandlerException();
		}

		log.debug("Setting PDFWriter options...");
		// SET PDF VERSION TO 1.7
		writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
		// SET THE EXTENSION LEVEL
		writer.addDeveloperExtension(PdfDeveloperExtension.ADOBE_1_7_EXTENSIONLEVEL3);
		// SET USERPROPERTIS TO TRUE
		writer.setUserProperties(true);

		log.info("New PDF handler created successfully.");
	}

	// METHODS

	/**
	 * Fills the PDF document with the content.
	 * 
	 * Throws a {@link PdfCreationException} if an error occurs during the the
	 * preparation of the {@link Map} or the receiving of the
	 * {@link ReferencedLayer}.
	 * 
	 * @throws PdfCreationException
	 *             if an error occurs during the preparation of the {@link Map}
	 *             or the receiving of the {@link ReferencedLayer}
	 */
	public void fillPDF() throws PdfCreationException {
		// FILL THE PDF DOCUMENT
		try {
			log.info("Preparing the Map...");
			this.getMap().prepare();
			log.info("Map is prepared!");
		} catch (BoundingboxNotCreatableException e1) {
			log.error(e1.getMessage().toString());
			throw new PdfCreationException();
		}

		log.info("Opening the document...");
		this.getDoc().open();
		log.info("Document opened.");

		log.debug("Calculating map inches...");
		this.calcMapInch();

		log.info("Running throug the MapLayers: Receiving and adding the content to the Map...");
		int a = 0;
		while (this.getMap().getMaplayers().size() > 0) {
			log.debug("Looking at MapLayer No." + a + "...");
			log.debug("Removing MapLayer at position 0...");
			MapLayer actLayer = this.getMap().getMaplayers().remove(0);
			log.debug("Setting MapLayer informations...");
			actLayer.setMapInchesWidth(this.getMap().getInchWidth());
			actLayer.setMapInchesHeight(this.getMap().getInchHeight());
			actLayer.setWriter(this.getWriter());
			actLayer.setOffsets2Map(actLayer.calcPixelOffsets(actLayer.getMapBBox(), actLayer.getLayerBBox()));
			log.debug("MapLayers informations set.");

			try {
				log.debug("Adding MapLayer " + a + " to the PDF...");
				try {
					log.debug("Calculating the inches the MapLayer will be covering...");
					actLayer.calcLayerInches();
				} catch (CalcualteLayerInchesException e) {
					throw new MapLayerNotReceivableException();
				}
				log.debug("Receiving the MapLayers content...");
				actLayer.receive();
				log.debug("Processing the MapLayers content...");
				actLayer.prepareForAdding(this.getDoc());
				log.debug("Adding the MapLayers content to the PDF...");
				actLayer.addToPdf(this.getDoc());
				log.debug("MapLayer " + a + " was added to the PDF.");

			} catch (MapLayerNotReceivableException e) {
				if (actLayer instanceof ReferencedLayer) {
					log.error("ReferencedLayer could not be added to the Pdf document!");
					log.error(e.getMessage().toString());
					throw new PdfCreationException();
				} else {
					log.error(e.getMessage().toString());
					log.error("MapLayer " + a + " could not be received!");
				}
			}
			// COUNTING UP A
			a++;
		}
		log.info("Running throug the MapLayers finished.");
	}

	/**
	 * Calculates the inch the {@link Map} (the whole map, not only a MapLayer)
	 * will occupy in the PDF-file.
	 * 
	 * To do so, the method runs through every {@link MapLayer} and calculates
	 * the inches of the layer. The size of the largest {@link MapLayer} will be
	 * saved.
	 */
	private void calcMapInch() {
		log.debug("Calculating the width of the map in pixels...");
		float maxWidthMapPixels = (this.getDoc().getPageSize().getWidth() - this.getDoc().leftMargin() - this.getDoc().rightMargin());

		log.debug("Calculating the height of the map in pixels...");
		float maxHeightMapPixels = (this.getDoc().getPageSize().getHeight() - this.getDoc().topMargin() - this.getDoc().bottomMargin());

		log.debug("Calculating the widht and height of the map in inches...");
		double maxWidthMapInches = maxWidthMapPixels / 72;
		double maxHeightMapInches = maxHeightMapPixels / 72;

		log.debug("Calculating the meters per inch in width...");
		double meterPerInchWidth = this.getMap().getMapBbox().getGeoWidth() / maxWidthMapInches;

		log.debug("Looking at the maps layout (portrait / landscape)...");
		if (meterPerInchWidth * maxHeightMapInches >= this.getMap().getMapBbox().getGeoHeight()) {
			log.debug("Map will fit on the page in widht.");
			log.debug("Setting the map-width in inches...");
			this.getMap().setInchWidth(maxWidthMapInches);

			log.debug("Calculating and setting the map-height in inches...");
			this.getMap().setInchHeight(this.getMap().getMapBbox().getGeoHeight() / meterPerInchWidth);
		} else {
			log.debug("Map will not fit on the page.");
			log.debug("Calculating the meters per inch in height...");
			double meterPerInchHeight = this.getMap().getMapBbox().getGeoHeight() / maxHeightMapInches;
			log.debug("Setting the map-height in inches...");
			this.getMap().setInchHeight(maxHeightMapInches);
			log.debug("Calculating and setting the map-width in inches...");
			this.getMap().setInchWidth(this.getMap().getMapBbox().getGeoWidth() / meterPerInchHeight);
		}
		log.debug("Map size calculated and set.");
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link Map} of this {@link PdfHandler}.
	 *
	 * @return the {@link Map} of this {@link PdfHandler}
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * Sets the {@link Map} of this {@link PdfHandler}.
	 *
	 * @param map
	 *            the map to set
	 */
	private void setMap(Map map) {
		this.map = map;
	}

	/**
	 * Returns the {@link Document} of this {@link PdfHandler} as
	 * {@link Document}.
	 *
	 * @return the {@link Document} of this {@link PdfHandler}
	 */
	public Document getDoc() {
		return doc;
	}

	/**
	 * Sets the {@link Document} of this {@link PdfHandler}.
	 *
	 * @param doc
	 *            the doc to set
	 */
	private void setDoc(Document doc) {
		this.doc = doc;
	}

	/**
	 * Returns the {@link PdfWriter} of this {@link PdfHandler} as
	 * {@link PdfWriter}.
	 *
	 * @return the {@link PdfWriter} of this {@link PdfHandler}
	 */
	public PdfWriter getWriter() {
		return writer;
	}

	/**
	 * Sets the {@link PdfWriter} of this {@link PdfHandler}.
	 *
	 * @param writer
	 *            the writer to set
	 */
	private void setWriter(PdfWriter writer) {
		this.writer = writer;
	}

	// OTHERS
}
