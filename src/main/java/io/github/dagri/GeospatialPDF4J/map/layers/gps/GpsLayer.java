package io.github.dagri.GeospatialPDF4J.map.layers.gps;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.geotools.geometry.DirectPosition2D;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfLayer;

import io.github.dagri.GeospatialPDF4J.draw.drawers.GpsDrawer;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawLineString;
import io.github.dagri.GeospatialPDF4J.draw.styles.LineStringStyle;
import io.github.dagri.GeospatialPDF4J.draw.styles.PointStyle;
import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.CsvReadingException;
import io.github.dagri.GeospatialPDF4J.exceptions.MapLayerNotReceivableException;
import io.github.dagri.GeospatialPDF4J.exceptions.MissingCrsException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.layers.DrawLayer;
import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;
import io.github.dagri.GeospatialPDF4J.res.ColorAssistant;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to manage GPS-data on a {@link MapLayer}-level.
 * 
 * @author DaGri
 * @since 18.07.2017
 */
@Slf4j
public class GpsLayer extends DrawLayer {

	// ATTRIBUTES

	/**
	 * The path to the CSV-GPS-file in the filesystem.
	 */
	private String				path				= "";

	/**
	 * The EPSG-code to identify the CRS (CoordinateReferenceSystem) of the
	 * data.
	 */
	private int					epsgCode;

	/**
	 * {@link ArrayList} to store the 3-dimensional coordinates, read from the
	 * file, in {@link Coordinate3D}s.
	 */
	// private ArrayList<DirectPosition2D> coords = new ArrayList<>();

	/**
	 * The {@link ArrayList} of {@link GpsPoint}s.
	 */
	private ArrayList<GpsPoint>	gpsPoints			= new ArrayList<>();

	/**
	 * The {@link ArrayList} of colored {@link GpsPoint}s.
	 */
	private ArrayList<GpsPoint>	gpsColoredPoints	= new ArrayList<>();

	/**
	 * The {@link DrawLineString} of this {@link GpsLayer}.
	 */
	private DrawLineString		drawLine			= null;

	/**
	 * The {@link PointStyle} to display the uncolored {@link GpsPoint}s with.
	 */
	private PointStyle			pointStyle			= new PointStyle();

	/**
	 * The {@link LineStringStyle} to display the {@link DrawLineString} with.
	 */
	private LineStringStyle		lineStringStyle		= new LineStringStyle();

	/**
	 * Constructor for a {@link GpsLayer} using a {@link String} to define the
	 * path to the CSV-GPS-file in the file system and an {@link Integer} to
	 * define the CRS of the Data.
	 */
	public GpsLayer(BoundingBox layerBbox, String path, int epsgCode) {
		super(layerBbox);

		log.debug("Setting the path to the CSV-GPS-file...");
		this.setPath(path);
		log.debug("Path set.");
		log.debug("Setting the EPSG-code...");
		this.setEpsgCode(epsgCode);
		log.debug("EPSG-Code set.");
	}

	// METHODS FROM SUPERCLASS

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.IPdfAddable#receive()
	 */
	@Override
	public void receive() throws MapLayerNotReceivableException {

		// READING THE DATA
		try {
			log.info("Reading the CSV-GPS-file...");
			this.readCSVData();
			log.info("CSV-GPS-file read successfully.");

		} catch (CsvReadingException e) {
			log.error("Could not read data!");
		}

		// AT THIS POINT THE CSV FILE HAS BEEN CONVERTED INTO COORDINATE3DS
		// INSIDE THE ARRAYLIST OF COORDIANTE3DS.

		log.debug("Converting Coordiante3Ds to draw objects...");
		this.convertToDrawObjects();
		log.debug("Converting done.");

		// AT THIS POINT THE COORDIANTES WERE CONVERTED INTO TWO TYPES:
		// DRAWPOINTS AND A LINESTRING, BUT THEY ARE NOT YET REDUCED. THIS WILL
		// HAPPEN ON THE ADDTOPDF METHOD.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.dagri.GeospatialPDF4J.map.layers.IPdfAddable#prepareForAdding(
	 * com.lowagie.text.Document)
	 */
	@Override
	public void prepareForAdding(Document doc) throws MapLayerNotReceivableException {
		log.debug("Reducing the content by the lower-left corner of the GPS-Layers BoundingBox...");
		this.reduceContent(this.getLayerBBox());
		log.debug("Content reduced.");

		log.debug("Scaling the content...");

		this.scaleContent(this.calcScalingFactor());
		log.debug("Content scaled.");

		// OFFSET AUF DIE MAP BEACHTEN
		log.debug("Adding document margins and layer offset...");
		this.addMarginOffset(doc.leftMargin(), doc.bottomMargin(), this.getxOffset2Map(), this.getyOffset2Map());
		log.debug("Offset added.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.IPdfAddable#addToPdf(com.
	 * lowagie.text.Document, com.lowagie.text.pdf.PdfWriter)
	 */
	@Override
	public void addToPdf(Document doc) throws MapLayerNotReceivableException {
		log.debug("Creating parental PDF-Layer and drawing the content...");
		PdfLayer overlayer = this.createParentalPdfLayer("GPS-Layer", this.getWriter());
		GpsDrawer drawer = new GpsDrawer(this.getWriter(), this, this.createTopTreeElement("GPS-Layer"), overlayer);
		drawer.drawAll();
		log.debug("Content drawn.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.dagri.GeospatialPDF4J.map.layers.DrawLayer#reduceContent(io.
	 * github.dagri.GeospatialPDF4J.geo.BoundingBox)
	 */
	@Override
	protected void reduceContent(BoundingBox bbox) {
		// DUE TO THE INTERNAL USED BBOX SYSTEM THE PRESENT CRS IS UTM
		// ORDINATE 0 --> EASTING
		// ORDIANTE 1 --> NORTHING
		for (int a = 0; a < this.getGpsPoints().size(); a++)
			this.getGpsPoints().get(a).getDp().reduce(bbox.getLl().getOrdinate(0), bbox.getLl().getOrdinate(1));

		for (int b = 0; b < this.getGpsColoredPoints().size(); b++)
			this.getGpsColoredPoints().get(b).getDp().reduce(bbox.getLl().getOrdinate(0), bbox.getLl().getOrdinate(1));

		if (this.getDrawLine() != null)
			this.getDrawLine().reduce(bbox.getLl().getOrdinate(0), bbox.getLl().getOrdinate(1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.dagri.GeospatialPDF4J.map.layers.DrawLayer#scaleContent(double)
	 */
	@Override
	protected void scaleContent(double factor) {
		// SCALE THE GPS-POINTS
		for (int a = 0; a < this.getGpsPoints().size(); a++)
			this.getGpsPoints().get(a).getDp().scale(factor);
		// SCALE THE GPS-COLORED-POINTS
		for (int b = 0; b < this.getGpsColoredPoints().size(); b++)
			this.getGpsColoredPoints().get(b).getDp().scale(factor);
		// SCALE THE DRAWLINE
		if (this.getDrawLine() != null)
			this.getDrawLine().scale(factor);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.dagri.GeospatialPDF4J.map.layers.DrawLayer#addOffsetToContent(
	 * double, double)
	 */
	@Override
	protected void addMarginOffset(double marginX, double marginY, double offsetX, double offsetY) {
		// ADD MARGIN TO THE GPS-POINTS
		for (int a = 0; a < this.getGpsPoints().size(); a++)
			this.getGpsPoints().get(a).getDp().reduce(-offsetX - marginX, -offsetY - marginY);
		// ADD MARGIN TO THE GPS-COLORED-POINTS
		for (int b = 0; b < this.getGpsColoredPoints().size(); b++)
			this.getGpsColoredPoints().get(b).getDp().reduce(-offsetX - marginX, -offsetY - marginY);
		// ADDING MARGIN TO THE DRAWLINE
		if (this.getDrawLine() != null)
			this.getDrawLine().reduce(-offsetX - marginX, -offsetY - marginY);
	}

	// METHODS

	/**
	 * Method to read the data contained in the CSV-GPS-file. Throws a
	 * {@link CsvReadingException} if an error occurs.
	 *
	 * @throws CsvReadingException
	 *             if an error occurs
	 */
	private void readCSVData() throws CsvReadingException {

		ArrayList<DirectPosition2D> lsPoints = new ArrayList<>();

		log.debug("Create a BufferedReader and needed Strings...");
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		log.debug("Created a BufferedReader and needed Strings.");

		log.debug("Creating an instance of a CooordinateTransformer...");
		CoordinateTransformer transformer = CoordinateTransformer.getInstance();
		log.debug("Instance created.");

		try {
			log.debug("Set BufferedReader to the file...");
			br = new BufferedReader(new FileReader(this.getPath()));
			log.debug("BufferedReader set to file.");

			log.info("Start reading CSV file...");
			while ((line = br.readLine()) != null) {

				log.debug("Splitting CSV-line...");
				String[] splitted = line.split(splitBy);
				log.debug("Line splitted.");

				try {
					if (splitted.length >= 3) {
						log.debug("Start parsing Strings into double-values...");
						log.debug("Paring northing from String...");
						Double northing = Double.parseDouble(splitted[0]);
						log.debug("Parsing easting from String...");
						Double easting = Double.parseDouble(splitted[1]);
						log.debug("Parsing height from Sting...");
						Double height = Double.parseDouble(splitted[2]);
						log.debug("Strings parsed.");

						log.debug("Transforming from CRS=EPSG:" + this.getEpsgCode() + " to CRS=EPSG:25832...");
						// THIS WILL ONLY WORK FOR CRS WITH NORTHING - EASTING
						// ORDIANTES LIKE WGS84
						DirectPosition2D dPos = transformer.transformUTM(this.getEpsgCode(), northing, easting);
						log.debug("Coordinate transformed.");

						log.debug("Adding new Coordinate3D to the Arraylist...");

						GpsPoint temp = new GpsPoint(dPos, height, this.getPointStyle());
						temp.getInfo().addInfo("Northing", "" + dPos.getOrdinate(1));
						temp.getInfo().addInfo("Easting", "" + dPos.getOrdinate(0));
						temp.getInfo().addInfo("Height", "" + height);
						this.getGpsPoints().add(temp);

						GpsPoint tempCol = new GpsPoint(dPos, height, new PointStyle());
						tempCol.getInfo().addInfo("Northing", "" + dPos.getOrdinate(1));
						tempCol.getInfo().addInfo("Easting", "" + dPos.getOrdinate(0));
						tempCol.getInfo().addInfo("Height", "" + height);
						this.getGpsColoredPoints().add(tempCol);

						lsPoints.add(dPos);

						log.debug("Coordinate3D added.");
					}

				} catch (CoordinateTransformException e) {
					log.warn("Could not parse into EPSG:25832 (UTM!");
				} catch (NumberFormatException e) {
					log.warn("Could not convert String to double in this line!");
				} catch (MissingCrsException e) {
					log.warn("Could not create GpsPoint due to missing CRS!");
				}

			}
			// INFORM THE USER THAT THE FILE WAS READ
			log.info("CSV file read.");

			DrawLineString ls = new DrawLineString(lsPoints);
			// END OF WORKAROUND

			ls.getInfo().addInfo("Point Number", "" + lsPoints.size());
			ls.getInfo().addInfo("Length", "" + ls.getLength() + "m");

			// SET DRAWLINESTING
			this.setDrawLine(ls);
			log.debug("DrawLineString created.");

		} catch (FileNotFoundException e) {
			log.error("CSV-File could not be found!");
			throw new CsvReadingException();
		} catch (IOException e) {
			log.error("Could not read CSV line!");
		}
	}

	/**
	 * Method to convert the internal stored {@link Coordinate3D}s into
	 * DrawObjects.
	 */
	private void convertToDrawObjects() {

		// CREATE THE VALUES TO SAVE THE MIN AND MAX VALUE IN HEIGHT
		log.info("Converting read coordinates to drawable objects...");

		log.debug("Calculating minmal and maximal height values...");
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int a = 0; a < this.getGpsPoints().size(); a++) {
			// if (this.getCoords().get(a).getDimension() >= 3) {
			if (this.getGpsPoints().get(a).getHeight() < min)
				min = this.getGpsPoints().get(a).getHeight();

			if (this.getGpsPoints().get(a).getHeight() > max)
				max = this.getGpsPoints().get(a).getHeight();
			// }
		}
		log.debug("Minmal (" + min + "m) and maximum (" + max + "m) values calculated.");

		// ADD NEW INFORMATION TO THE DRAWLINESTING
		this.getDrawLine().getInfo().addInfo("Max height", "" + max + "(m)");
		this.getDrawLine().getInfo().addInfo("Min height", "" + min + "(m)");

		ColorAssistant cAss = ColorAssistant.getInstance();

		// RUN THROUGH THE ARRAYLIST AND CREATE A DRAWPOINT IN EACH ARRAYLIST
		// FOR EVERY COORINDATE2D
		log.debug("Looking at every coordinate and creating GpsPoints and colored GpsPoints...");
		for (int a = 0; a < this.getGpsColoredPoints().size(); a++) {

			// CREATE POINTSTYLE FOR THE GPSPOINTS
			PointStyle actStyle = new PointStyle();
			actStyle.pointRadius = this.getPointStyle().pointRadius;

			// COLOR CORRECTIONS
			Color actCol = cAss.getGradientWhite2Channel(min, max, this.getGpsPoints().get(a).getHeight(), ColorAssistant.channel.RED);

			actStyle.pointColor = actCol;
			actStyle.pointFillColor = actCol;
			actStyle.pointFilled = true;
			this.getGpsColoredPoints().get(a).setStyle(actStyle);
		}

		log.debug("GpsPoints and colored GpsPoints created.");

		log.info("Converted read coordiantes to drawable objects.");
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the path to the CSV-GPS-file of this {@link GpsLayer} in the file
	 * system as {@link String}.
	 *
	 * @return the path as {@link String}
	 */
	private String getPath() {
		return path;
	}

	/**
	 * Sets the path to the CSV-GPS-file of this {@link GpsLayer}.
	 *
	 * @param path
	 *            the path to set
	 */
	private void setPath(String path) {
		this.path = path;
	}

	/**
	 * Returns the EPSG-code the data is contained in as {@link Integer}.
	 *
	 * @return the epsgCode
	 */
	protected int getEpsgCode() {
		return epsgCode;
	}

	/**
	 * Sets the EPSG-code the data is contained in.
	 *
	 * @param epsgCode
	 *            the epsgCode to set
	 */
	protected void setEpsgCode(int epsgCode) {
		this.epsgCode = epsgCode;
	}

	/**
	 * Returns the {@link ArrayList} of uncolored {@link GpsPoint}s of this
	 * {@link GpsLayer}.
	 *
	 * @return the {@link ArrayList} of {@link GpsPoint}s
	 */
	public ArrayList<GpsPoint> getGpsPoints() {
		return gpsPoints;
	}

	/**
	 * Sets the {@link ArrayList} of colored {@link GpsPoint}s.
	 *
	 * @param gpsColoredPoints
	 *            the {@link ArrayList} of {@link GpsPoint}s to set
	 */
	public void setGpsPoints(ArrayList<GpsPoint> gpsPoints) {
		this.gpsPoints = gpsPoints;
	}

	/**
	 * Returns the {@link ArrayList} of colored {@link GpsPoint}s of this
	 * {@link GpsLayer}.
	 *
	 * @return the {@link ArrayList} of {@link GpsPoint}s
	 */
	public ArrayList<GpsPoint> getGpsColoredPoints() {
		return gpsColoredPoints;
	}

	/**
	 * Sets the {@link ArrayList} of colored {@link GpsPoint}s.
	 *
	 * @param gpsColoredPoints
	 *            the {@link ArrayList} of {@link GpsPoint}s to set
	 */
	public void setGpsColoredPoints(ArrayList<GpsPoint> gpsColoredPoints) {
		this.gpsColoredPoints = gpsColoredPoints;
	}

	/**
	 * Returns the {@link DrawLineString} of this {@link GpsLayer} as
	 * {@link DrawLineString}.
	 *
	 * @return the {@link DrawLineString} of this {@link GpsLayer} as
	 *         {@link DrawLineString}
	 */
	public DrawLineString getDrawLine() {
		return drawLine;
	}

	/**
	 * Sets the {@link DrawLineString} of this {@link GpsLayer}.
	 *
	 * @param drawLine
	 *            the {@link DrawLineString} to set
	 */
	public void setDrawLine(DrawLineString drawLine) {
		this.drawLine = drawLine;
	}

	/**
	 * Returns the {@link PointStyle} of this {@link GpsLayer} as
	 * {@link PointStyle}.
	 *
	 * @return the {@link PointStyle} of this {@link GpsLayer} as
	 *         {@link PointStyle}
	 */
	public PointStyle getPointStyle() {
		return pointStyle;
	}

	/**
	 * Sets the {@link PointStyle} of this {@link GpsLayer}.
	 *
	 * @param pointStyle
	 *            the {@link PointStyle} to set
	 */
	public void setPointStyle(PointStyle pointStyle) {
		this.pointStyle = pointStyle;
	}

	/**
	 * Returns the {@link LineStringStyle} of this {@link GpsLayer} as
	 * {@link LineStringStyle}.
	 *
	 * @return the {@link LineStringStyle} of this {@link GpsLayer} as
	 *         {@link LineStringStyle}
	 */
	public LineStringStyle getLineStringStyle() {
		return lineStringStyle;
	}

	/**
	 * Sets the {@link LineStringStyle} of this {@link GpsLayer}.
	 *
	 * @param lineStringStyle
	 *            the {@link LineStringStyle} to set
	 */
	public void setLineStringStyle(LineStringStyle lineStringStyle) {
		this.lineStringStyle = lineStringStyle;
	}

	// OTHERS
}
