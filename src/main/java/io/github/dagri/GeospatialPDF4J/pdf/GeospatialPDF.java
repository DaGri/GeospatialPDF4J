package io.github.dagri.GeospatialPDF4J.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;

import io.github.dagri.GeospatialPDF4J.exceptions.PageSizeException;
import io.github.dagri.GeospatialPDF4J.exceptions.PdfCreationException;
import io.github.dagri.GeospatialPDF4J.exceptions.PdfHandlerException;
import io.github.dagri.GeospatialPDF4J.map.Map;
import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to provide support for creating a {@link GeospatialPDF}.
 * 
 * @author DaGri
 * @since 12.01.2017
 */
@Slf4j
public class GeospatialPDF {

	// ATTRIBUTES

	/**
	 * The Map of this {@link GeospatialPDF}.
	 */
	private Map		map					= Map.getInstance();

	/**
	 * The name of the PDF-file in the file system.
	 */
	private String	pdfName;

	/**
	 * The width of the PDF page in pixels.
	 */
	private int		pageWidthPixel;

	/**
	 * The height of the PDF page in pixels.
	 */
	private int		pageHeightPixel;

	/**
	 * The width of the PDF page in inches.
	 */
	private float	pageWidthInches		= Float.NEGATIVE_INFINITY;;

	/**
	 * The height of the PDF page in inches.
	 */
	private float	pageHeightInches	= Float.NEGATIVE_INFINITY;;

	/**
	 * {@link Boolean} to indicate if the margins of the document shall be
	 * printed (no map on them).
	 */
	private boolean	printMargins		= true;

	/**
	 * The margins of the PDF as {@link Integer} in pixels.
	 */
	private int		margins				= 50;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link GeospatialPDF} using a float value to define the
	 * width in inches and one for the height in inches.
	 * 
	 * @param inchX
	 *            the width of the page to create in inches
	 * @param inchY
	 *            the height of the page to create in inches
	 */
	public GeospatialPDF(float inchX, float inchY) throws PdfCreationException {
		log.info("Creating a GeospatialPDF with a standard name and the given size in inches...");
		this.setName(null);
		try {
			this.setPageSizeInches(inchX, inchY);
		} catch (PageSizeException e) {
			log.error("Given page size is eqzal to zero in width and / or height!");
			throw new PdfCreationException();
		}
	}

	/**
	 * Constructor for a {@link GeospatialPDF} using a {@link String} to define
	 * the name of the file to create inside the file system, a float value to
	 * define the width in inches and one for the height in inches.
	 * 
	 * @param pdfName
	 *            the name to set
	 * @param inchWidth
	 *            the width of the page to create in inches
	 * @param inchHeight
	 *            the height of the page to create in inches
	 */
	public GeospatialPDF(String pdfName, float inchWidth, float inchHeight) throws PdfCreationException {
		log.info("Creating a GeospatialPDF with a given name and size in inches...");
		this.setPdfName(pdfName);
		try {
			this.setPageSizeInches(inchWidth, inchHeight);
		} catch (PageSizeException e) {
			log.error("Given page size is eqzal to zero in width and / or height!");
			throw new PdfCreationException();
		}
	}

	/**
	 * Constructor for a {@link GeospatialPDF} using a {@link String} to define
	 * the name of the file to create inside the file system, a float value to
	 * define the width in inches and one for the height in inches.
	 * 
	 * @param pdfName
	 *            the name to set
	 * @param inchWidth
	 *            the width of the page to create
	 * @param inchHeight
	 *            the height of the page to create
	 * @param margins
	 *            the margins to set
	 * @throws PdfCreationException
	 */
	public GeospatialPDF(String pdfName, float inchWidth, float inchHeight, int margins) throws PdfCreationException {
		log.info("Creating a GeospatialPDF with a given name, size in inches and margins...");
		this.setName(pdfName);
		try {
			this.setPageSizeInches(inchWidth, inchHeight);
		} catch (PageSizeException e) {
			log.error("Given page size is eqzal to zero in width and / or height!");
			throw new PdfCreationException();
		}
		this.setPrintMargins(true);
		this.setMargins(margins);
	}

	// METHODS

	/**
	 * Sets the size of the page of the GeospatialPDF to the given values in
	 * millimeters.
	 *
	 * @param mmW
	 *            the width of the page in millimeters
	 * @param mmH
	 *            the height of the page in millimeter
	 * @throws PageSizeException
	 */
	public void setPageSizeMM(float mmW, float mmH) throws PageSizeException {
		log.debug("Setting page size...");
		log.debug("Converting possible negative pagesize values...");
		mmW = Math.abs(mmW);
		mmH = Math.abs(mmH);

		log.debug("Checking for pagesize values equal to zero...");
		if (mmW == 0.0f || mmH == 0.0f) {
			log.error("Page size is equal to zero in width and / or height!");
			throw new PageSizeException();
		} else {
			log.debug("Page size is okay.");
			log.debug("Calculating width and height of the page in inches...");
			// FACTOR MM -> INCH = 0.03937f
			this.setPageWidthInches(mmW * 0.03937f);
			this.setPageHeightInches(mmH * 0.03937f);

			log.debug("Calculating width and height of the page in pixel...");
			this.setPageSizePixel();
		}
		log.debug("Page size set.");
	}

	/**
	 * Calculates and sets the pages width and height in pixel by multiplying
	 * the pages width and height in inches with the default DPI of a PDF-file
	 * (72 Pixels per Inch).
	 */
	private void setPageSizePixel() {
		this.setPageWidthPixel(Math.round(this.getPageWidthInches() * 72));
		this.setPageHeightPixel(Math.round(this.getPageHeightInches() * 72));
	}

	/**
	 * Sets the size of the page of the GeospatialPDF to the given values in
	 * centimeters.
	 * 
	 * @param cmW
	 *            the width of the page in centimeters
	 * @param cmH
	 *            the height of the page in centimeters
	 * @throws PageSizeException
	 */
	public void setPageSizeCM(float cmW, float cmH) throws PageSizeException {
		log.debug("Setting page size...");
		log.debug("Converting possible negative pagesize values...");
		cmW = Math.abs(cmW);
		cmH = Math.abs(cmH);

		log.debug("Checking for pagesize values equal to zero...");
		if (cmW == 0.0f || cmH == 0.0f) {
			log.error("Page size is equal to zero in width and / or height!");
			throw new PageSizeException();
		} else {
			log.debug("Page size is okay.");
			log.debug("Calculating width and height of the page in inches...");
			// FACTOR CM -> INCH = 0.3937f
			this.setPageWidthInches(cmH * 0.3937f);
			this.setPageHeightInches(cmW * 0.3937f);

			log.debug("Calculating width and height of the page in pixel...");
			// STANDARD 72 DPI
			this.setPageWidthPixel(Math.round(this.getPageWidthInches() * 72));
			this.setPageHeightPixel(Math.round(this.getPageHeightInches() * 72));
		}
		log.debug("Page size set.");
	}

	/**
	 * Sets the size of the page of the GeospatialPDF to the given values in
	 * inches.
	 *
	 * @param inchesW
	 *            the width of the page in inches
	 * @param inchesH
	 *            the height of the page in inches
	 * @throws PageSizeException
	 */
	public void setPageSizeInches(float inchesW, float inchesH) throws PageSizeException {
		log.debug("Setting page size...");
		log.debug("Converting possible negative pagesize values...");
		inchesW = Math.abs(inchesW);
		inchesH = Math.abs(inchesH);

		log.debug("Checking for pagesize values equal to zero...");
		if (inchesW == 0.0f || inchesH == 0.0f) {
			log.error("Page size is equal to zero in width and / or height!");
			throw new PageSizeException();
		} else {

			log.debug("Page size is okay.");
			log.debug("Calculating width and height of the page in inches...");
			this.setPageWidthInches(inchesW);
			this.setPageHeightInches(inchesH);

			log.debug("Calculating width and height of the page in pixel...");
			// STANDARD 72 DPI
			this.setPageWidthPixel(Math.round(this.getPageWidthInches() * 72));
			this.setPageHeightPixel(Math.round(this.getPageHeightInches() * 72));
		}
		log.debug("Page size set.");
	}

	/**
	 * Creates the GeospatialPDF-document in the file system, gains the data and
	 * adds it to the document.
	 *
	 * @throws PdfCreationException
	 */
	public void create() throws PdfCreationException {
		// TODO : IF THERE IS NO PAGE SIZE GIVEN HERE COULD BE SOME MAGIC TO
		// GAIN A POSSIBLE SIZE OUT OF THE BOUNDINGBOX OF THE MAP (AT THIS POINT
		// THE MAP HAS NO CALCULATED MAP-BOUNDINGBOX)
		log.info("Creating the Geospatial-PDF...");
		log.info("Preparing the document...");

		log.debug("Cretaing Rectangle (iText) for the pagesize...");
		Rectangle rec = new Rectangle(this.getPageWidthPixel(), this.getPageHeightPixel());
		log.debug("Rectangle created: width = " + rec.getWidth() + ", height = " + rec.getHeight() + " (pixel).");
		log.debug("Creating document (iText) with rectangle...");

		log.info("Creating the document...");
		Document doc;
		if (!this.isPrintMargins()) {
			log.info("Margins are disabled. So they will be set to zero.");
			doc = new Document(rec, 0, 0, 0, 0);
		} else {
			log.info("Margins are enabled. Setting all margins to " + this.getMargins());
			doc = new Document(rec, this.getMargins(), this.getMargins(), this.getMargins(), this.getMargins());
		}

		log.info("Document created.");

		log.debug("Setting author and meta informations...");
		doc.addAuthor("GeospatialPDF4J");
		doc.addSubject("This is a geospatial-PDF created by GeospatialPDF4J.");
		log.debug("Author and meta informations set.");
		log.info("Document prepared.");

		log.info("Writing Data to the document...");
		PdfHandler handler;
		try {
			log.debug("Creating PdfHandler.");
			handler = new PdfHandler(doc, this.getMap(), this.getPdfName());
			log.info("Filling the PDF...");
			handler.fillPDF();
			log.info("PDF filled!");
		} catch (PdfHandlerException e) {
			log.error(e.getMessage().toString());
			throw new PdfCreationException();
		}
		log.debug("Makeing sure the document is closed...");
		if (doc.isOpen())
			doc.close();
		log.debug("Document is closed.");
		log.info("Data written.");
		log.info("Geospatial-PDF created and filled. Have fun with it!");
	}

	/**
	 * Sets the name of the {@link GeospatialPDF}.
	 * 
	 * If the given {@link String} is null a name will be generated from the
	 * current system-time in milliseconds.
	 *
	 * @param pdfName
	 *            the name to set; may be null
	 */
	private void setName(String pdfName) {
		if (pdfName == null) {
			log.debug("Pdf name is null. Creating new Pdf-name.");
			pdfName = "GeospatialPDF4J_" + System.currentTimeMillis();
		}
		this.setPdfName(pdfName);
		log.info("Pdf name was null. Set to: \"" + pdfName + "\".");
	}

	/**
	 * Adds a {@link MapLayer} to the {@link Map} of this {@link GeospatialPDF}.
	 *
	 * @param layer
	 *            the {@link MapLayer} to add
	 */
	public void addMapLayer(MapLayer layer) {
		if (layer != null) {
			log.debug("Adding MapLayer...");
			this.getMap().getMaplayers().add(layer);
			log.debug("MapLayer added.");
		} else
			log.warn("MapLayer was null and could not be added!");
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link Map} of this {@link GeospatialPDF} as {@link Map}.
	 *
	 * @return the {@link Map}
	 */
	private Map getMap() {
		return map;
	}

	/**
	 * Returns the name of the {@link GeospatialPDF} as {@link String}.
	 *
	 * @return the pdfName as {@link String}
	 */
	public String getPdfName() {
		return pdfName;
	}

	/**
	 * Sets the name of the PDF.
	 *
	 * @param pdfName
	 *            the pdfName to set
	 */
	private void setPdfName(String pdfName) {
		this.pdfName = pdfName;
	}

	/**
	 * Returns the width of the page as {@link Integer} in pixel.
	 *
	 * @return the pageWidthPixels as {@link Integer} in pixel.
	 */
	public int getPageWidthPixel() {
		return pageWidthPixel;
	}

	/**
	 * Sets the width of the page in pixel.
	 *
	 * @param pageWidthPixels
	 *            the value to set
	 */
	public void setPageWidthPixel(int pageWidthPixel) {
		this.pageWidthPixel = pageWidthPixel;
	}

	/**
	 * Returns the height of the page in pixel.
	 *
	 * @return the pageHeightPixel
	 */
	public int getPageHeightPixel() {
		return pageHeightPixel;
	}

	/**
	 * Sets the height of the page in pixel.
	 *
	 * @param pageHeightPixel
	 *            the value to set
	 */
	public void setPageHeightPixel(int pageHeightPixel) {
		this.pageHeightPixel = pageHeightPixel;
	}

	/**
	 * Returns the width of the page as {@link Float} in inches.
	 *
	 * @return the pageWidthInches
	 */
	public float getPageWidthInches() {
		return pageWidthInches;
	}

	/**
	 * Sets the width of the page in inches.
	 *
	 * @param pageWidthInches
	 *            the value to set
	 */
	public void setPageWidthInches(float pageWidthInches) {
		this.pageWidthInches = pageWidthInches;
	}

	/**
	 * Returns the height of the page as {@link Float} in inches.
	 *
	 * @return the pageHeightInches
	 */
	public float getPageHeightInches() {
		return pageHeightInches;
	}

	/**
	 * Sets the height of the page in inches.
	 *
	 * @param pageHeightInches
	 *            the value to set
	 */
	public void setPageHeightInches(float pageHeightInches) {
		this.pageHeightInches = pageHeightInches;
	}

	/**
	 * Returns the {@link Boolean} if the margins shall be plotted in the PDF
	 * document.
	 *
	 * @return the printMargins
	 */
	public boolean isPrintMargins() {
		return printMargins;
	}

	/**
	 * Sets the {@link Boolean} that indicates if the margins shall be plotted
	 * in the PDF file.
	 *
	 * @param printMargins
	 *            the printMargins to set
	 */
	public void setPrintMargins(boolean printMargins) {
		this.printMargins = printMargins;
	}

	/**
	 * Returns the {@link Integer} values of the margins to use in Pixels.
	 *
	 * @return the margins in pixels as {@link Integer}
	 */
	public int getMargins() {
		return margins;
	}

	/**
	 * Sets the margins in Pixels.
	 *
	 * @param margins
	 *            the margins to set
	 */
	// NEEDS TO BE PUBLIC
	public void setMargins(int margins) {
		this.setPrintMargins(true);
		this.margins = Math.abs(margins);
	}

	// OTHERS
}
