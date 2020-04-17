package io.github.dagri.GeospatialPDF4J.map.layers;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;

import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.ImageCovertingException;
import io.github.dagri.GeospatialPDF4J.exceptions.MapLayerNotReceivableException;
import io.github.dagri.GeospatialPDF4J.exceptions.MissingCrsException;
import io.github.dagri.GeospatialPDF4J.exceptions.PdfCreationException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import io.github.dagri.GeospatialPDF4J.res.ImageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to be used as {@link MapLayer} containing the informations for the
 * georeferencing.
 * 
 * @author DaGri
 * @since 12.01.2017
 */
@Slf4j
public class ReferencedLayer extends MapLayer {

	// ATTRIBUTES

	/**
	 * The (iText) {@link Image} of this {@link ReferencedLayer}.
	 */
	private Image img;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link ReferencedLayer} using a {@link BoundingBox}
	 * as the layers {@link BoundingBox}.
	 * 
	 * By the fact that this is a {@link ReferencedLayer} this should be the
	 * Maps {@link BoundingBox}.
	 * 
	 * @param layerBbox
	 *            the {@link BoundingBox} to set
	 */
	public ReferencedLayer(BoundingBox layerBbox) {
		super(layerBbox);
	}

	// METHODS

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.MapLayer#receive()
	 */
	@Override
	public void receive() throws MapLayerNotReceivableException {
		log.info("Receiving data for a ReferencedLayer...");
		ImageHandler handler = ImageHandler.getInstance();
		try {
			log.debug("Creating BufferedImage by using the width and height of the Map in inches...");
			BufferedImage tempBuff = handler.createStandardDPIImage(this.getMapInchesWidth(), this.getMapInchesHeight());
			log.debug("Converting the BufferedImage to an iText Image...");
			Image temp = handler.convertToImage(tempBuff, 0);
			log.debug("Scaling the iText-Image to fit the width and height of the Map in inches...");
			temp = handler.scaleToFitInches(temp, this.getMapInchesWidth(), this.getMapInchesHeight());
			log.debug("Setting image...");
			this.setImg(temp);
			log.info("Data for the ReferencedLayer completed.");

		} catch (ImageCovertingException e) {
			log.error(e.getMessage().toString());
			throw new MapLayerNotReceivableException();
		}
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
		log.debug("Gaining the Image of the Referenced Layer.");
		Image img = this.getImg();
		log.debug("Setting the absolute position of the Image.");
		img.setAbsolutePosition(doc.topMargin(), doc.topMargin());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.IPdfAddable#addToPdf(com.
	 * lowagie.text.pdf.PdfWriter)
	 */
	@Override
	public void addToPdf(Document doc) throws MapLayerNotReceivableException {
		log.info("Adding geospatial data to the Image and the Image to the PDF...");
		this.addReferencedImage(doc, this.getWriter());
		log.info("ReferencedLayer added.");
	}

	/**
	 * Adds the geospatial information to the {@link Image} and places it inside
	 * the {@link Document}.
	 *
	 * @param img
	 *            the image to add
	 * @throws PdfCreationException
	 */
	private void addReferencedImage(Document doc, PdfWriter writer) throws MapLayerNotReceivableException {
		try {
			log.info("Adding referenced image to the PDF-document...");
			log.info("For further informations on georeferencing look in ISO-32000-1 from Adobe Systems.");

			// BOOLEAN TO INDICATE IF ANOTHER CRS THAN UTM (EPSG:25832) SHALL BE
			// USED
			boolean otherCRS = false;
			if (otherCRS)
				log.info("Adding informations in UTM CRS to the document.");
			else
				log.info("Adding informations in different CRS.");

			log.debug("Creating PdfArray: GPTS (Corner Coordinates).");
			PdfArray gpts = new PdfArray();

			log.debug("Create PdfDictionary 'wktDic'.");
			PdfDictionary wktDic = new PdfDictionary();

			// CREATE WKT STRING
			String wkt;

			if (otherCRS == true) {
				log.debug("Other CRS selected.");

				log.debug("Gaining other CRS-WKT information...");
				wkt = CRS.decode("EPSG:4326").toWKT();

				try {
					log.debug("Gaining LL DirectPosition2D...");
					DirectPosition2D ll = CoordinateTransformer.getInstance().transform(this.getMapBBox().getLl(), CRS.decode("EPSG:4326"));

					log.debug("Gaining UL DirectPosition2D...");
					DirectPosition2D ul = CoordinateTransformer.getInstance().transform(this.getMapBBox().getUl(), CRS.decode("EPSG:4326"));

					log.debug("Gaining UR DirectPosition2D...");
					DirectPosition2D ur = CoordinateTransformer.getInstance().transform(this.getMapBBox().getUr(), CRS.decode("EPSG:4326"));

					log.debug("Gaining LR DirectPosition2D...");
					DirectPosition2D lr = CoordinateTransformer.getInstance().transform(this.getMapBBox().getLr(), CRS.decode("EPSG:4326"));

					log.debug("Adding corner Coordinates to the GPTS PdfArray...");
					gpts.add(
							// CLOCKWISE
							new float[] {
									// LL ORDINATE 0
									(float) ll.getOrdinate(0),
									// LL ORDINATE 1
									(float) ll.getOrdinate(1),
									// UL ORDAINTE 0
									(float) ul.getOrdinate(0),
									// UL ORDINATE 1
									(float) ul.getOrdinate(1),
									// UR ORDINATE 0
									(float) ur.getOrdinate(0),
									// UR ORDINATE 1
									(float) ur.getOrdinate(1),
									// LR ORDAINTE 0
									(float) lr.getOrdinate(0),
									// LR ORDAINTE 1
									(float) lr.getOrdinate(1)
							// GPTS END
							});

					log.debug("Filling wkt-Dictionary...");
					wktDic.put(PdfName.TYPE, new PdfName("GEOGCS"));
					wktDic.put(new PdfName("EPSG"), new PdfNumber("4326"));

				} catch (CoordinateTransformException e) {
					log.error("Error during conversion to WGS84 CRS (EPSG:4326).");
					log.error(e.getMessage());
					throw new MapLayerNotReceivableException();
				} catch (MissingCrsException e) {
					log.error("Error during conversion to WGS84 CRS (EPSG:4326).");
					log.error(e.getMessage());
					throw new MapLayerNotReceivableException();
				}
			} else {
				log.debug("UTM CRS selected.");

				log.debug("Gaining other CRS-WKT information...");
				wkt = CRS.decode("EPSG:25832").toWKT();

				log.debug("Adding corner Coordinates to the GPTS PdfArray...");
				gpts.add(new float[] {
						// CLOCKWISE
						// LOWER LEFT NORTHING (UTM --> ORDIANTE 1)
						(float) this.getMapBBox().getLl().getOrdinate(1),
						// LOWER LEFT EASTING (UTM --> ORDIANTE 0)
						(float) this.getMapBBox().getLl().getOrdinate(0),
						// UPPER LEFT NORTHING (UTM --> ORDIANTE 1)
						(float) this.getMapBBox().getUl().getOrdinate(1),
						// UPPER LEFT EASTING (UTM --> ORDIANTE 0)
						(float) this.getMapBBox().getUl().getOrdinate(0),
						// UPPER RIGHT NORTHING (UTM --> ORDIANTE 1)
						(float) this.getMapBBox().getUr().getOrdinate(1),
						// UPPER RIGHT EASTING (UTM --> ORDIANTE 0)
						(float) this.getMapBBox().getUr().getOrdinate(0),
						// LOWER RIGHT NORTHING (UTM --> ORDIANTE 1)
						(float) this.getMapBBox().getLr().getOrdinate(1),
						// LOWER RIGHT EASTING (UTM --> ORDIANTE 0)
						(float) this.getMapBBox().getLr().getOrdinate(0)
						// GPTS END
				});
				log.debug("GPTS-array filled.");

				log.debug("Filling wkt-Dictionary...");
				wktDic.put(PdfName.TYPE, new PdfName("PROJCS"));
				wktDic.put(new PdfName("EPSG"), new PdfNumber("25832"));
			}

			PdfArray bounds = new PdfArray(new float[] {
					// COCKWISE
					0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f });

			log.debug("Creating and filling PdfArray: PDU (Measurement Units)...");
			PdfArray pdu = new PdfArray();
			pdu.add(new PdfName("KM"));
			pdu.add(new PdfName("SQKM"));
			pdu.add(new PdfName("DEG"));

			log.debug("Creating and filling PdfArray: LPTS...");
			PdfArray lpts = new PdfArray();
			lpts.add(new float[] {
					// CLOCKWISE
					0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f });

			log.debug("Creating PdfDictionary 'measure' and set the TYPE and the SUBTYPE.");
			PdfDictionary measure = new PdfDictionary();
			measure.put(PdfName.TYPE, new PdfName("Measure"));
			measure.put(PdfName.SUBTYPE, new PdfName("GEO"));

			log.debug("Putting WKT to the WKT-dictionary...");
			wktDic.put(new PdfName("WKT"), new PdfString(wkt));

			log.debug("Create an PdfIndirectObject by adding the wkt-Dictionary to the PDF-body...");
			PdfIndirectObject indObj = writer.addToBody(wktDic);

			log.debug("Adding created PdfArrays to 'measure'-dictionary...");
			measure.put(new PdfName("Bounds"), bounds);
			// USE THE INDIRECT REFERENCE FOR "GCS" AND "DCS"
			measure.put(new PdfName("GCS"), indObj.getIndirectReference());
			measure.put(new PdfName("DCS"), indObj.getIndirectReference());
			measure.put(new PdfName("GPTS"), gpts);
			measure.put(new PdfName("LPTS"), lpts);
			measure.put(new PdfName("PDU"), pdu);

			// CREATE SECOND INDIRECT OBJECT REFERENCING TO MEASURE
			// USE THIS INDIRECT REFERENCE FOR THE GEOREFERENCING OF THE MAP
			log.debug("Creating PdfIndirectObject by adding the 'measure'-dictionary to the PDF-body...");
			PdfIndirectObject indObj2 = writer.addToBody(measure);

			// CREATE ITEXT IMAGE FROM THE MAP IMAGE
			// THE ITEXT IMAGE IS A STREAM-TYPE OBJECT
			// MEASURE-DICTIONARY ADDED VIA INDIRECT OBJECT
			log.debug("Creating a PdfImage as stream of the ReferencedLayer-image.");
			PdfImage stream = new PdfImage(img, "", null);

			log.debug("Putting the IndirectObjectReference to the stream...");
			stream.put(new PdfName("Measure"), indObj2.getIndirectReference());

			// CREATE INDIRECT OBJECT REFERENCING TO THE STREAM
			log.debug("Creating a PdfIndirectObject by adding the stream to the Pdf-body.");
			PdfIndirectObject ref = writer.addToBody(stream);

			// ADD THE REFERENCE TO THE IMAGE
			log.debug("Setting the direct reference of the image...");
			img.setDirectReference(ref.getIndirectReference());

			// ADD IMAGE TO THE DOCUMENT
			log.debug("Adding the image to the document...");
			doc.add(img);

			// EMPTY WRITER AND CLOSE THE DOCUMENT
			log.debug("Flushing the writer...");
			writer.flush();
			log.debug("Writer flushed.");
			log.info("ReferencedLayer image added successfully to the PDF-document.");
		} catch (UnsupportedOperationException | FactoryException | IOException | DocumentException e) {
			log.error("Error adding the Geo-Reference to the Image!");
			throw new MapLayerNotReceivableException();
		}
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link Image} of this {@link ReferencedLayer} as iText
	 * {@link Image}.
	 *
	 * @return the img as itext {@link Image}
	 */
	public Image getImg() {
		return img;
	}

	/**
	 * Sets the {@link Image} of this {@link ReferencedLayer}.
	 *
	 * @param img
	 *            the {@link Image} to set
	 */
	public void setImg(Image img) {
		this.img = img;
	}

	// OTHERS
}