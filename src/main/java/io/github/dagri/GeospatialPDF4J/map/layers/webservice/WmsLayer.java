package io.github.dagri.GeospatialPDF4J.map.layers.webservice;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfWriter;

import io.github.dagri.GeospatialPDF4J.exceptions.BoundingboxNotCreatableException;
import io.github.dagri.GeospatialPDF4J.exceptions.CapabilitiesRequestException;
import io.github.dagri.GeospatialPDF4J.exceptions.ImageCovertingException;
import io.github.dagri.GeospatialPDF4J.exceptions.MapLayerNotReceivableException;
import io.github.dagri.GeospatialPDF4J.exceptions.TileArrayDimensionException;
import io.github.dagri.GeospatialPDF4J.exceptions.TileException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;
import io.github.dagri.GeospatialPDF4J.res.ImageHandler;
import io.github.dagri.GeospatialPDF4J.res.LayerImage;
import io.github.dagri.GeospatialPDF4J.res.Tile;
import io.github.dagri.GeospatialPDF4J.res.TileArray;
import io.github.dagri.GeospatialPDF4J.server.ServerTalker;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link MapLayer} to represent a WMS-Layer and providing support to receive
 * data from such a server and adding it to the GeospatoalPDF-document.
 * 
 * @author DaGri
 * @since 16.01.2017
 */
@Slf4j
public class WmsLayer extends ImageLayer {

	// ATTRIBUTES

	/**
	 * The {@link ArrayList} of styles to request for the layers.
	 */
	private ArrayList<String>	styles			= new ArrayList<>();

	/**
	 * The {@link ArrayList} of opacities to request for the layers.
	 */
	private ArrayList<Integer>	opacities		= new ArrayList<>();

	/**
	 * The {@link ArrayList} of {@link Boolean}s indicating if the RAM-mode is
	 * active for the specific layers.
	 */
	private ArrayList<Boolean>	ramModes		= new ArrayList<>();

	/**
	 * The maximum amount of pixels in width and/or height that can be received
	 * in a getMap request. The default value (1000) is a placeholder: The
	 * resolution may be set to a accepted value by the server.
	 */
	private int					maxRequestPixel	= 1000;

	/**
	 * The maximum MB RAM a requested is allowed to use. If the requested image
	 * needs more space the RAM-mode will be activated and the image will be
	 * received in tiles.
	 */
	private int					maxImageMB		= 200;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link WmsLayer}.
	 * 
	 * @param url
	 *            the link to the server as {@link String}
	 * @param layerBbox
	 *            the {@link BoundingBox} to request
	 * @param version
	 *            the version of the WMS to access
	 * @param layers
	 *            the {@link ArrayList} of layers to request
	 * @param dpis
	 *            the {@link ArrayList} of DPIs as {@link Integer}
	 * @param styles
	 *            the {@link ArrayList} of styles (as {@link String})
	 * @param opacities
	 *            the {@link ArrayList} of opacities as {@link Integer}
	 */
	public WmsLayer(String url, BoundingBox layerBbox, String version, ArrayList<String> layers, ArrayList<Integer> dpis, ArrayList<String> styles, ArrayList<Integer> opacities) {
		super(url, layerBbox, version, layers);
		log.info("Creating a new WMS-MapLayer...");

		log.debug("Looking at the DPI-array...");
		if (dpis.size() != layers.size()) {
			log.warn("More wms-layers given than DPIs are given. Adjusting DPIs size...");
			log.info("Extending the ArrayList of DPIs with an entry of '10'...");
			while (dpis.size() != layers.size())
				dpis.add(10);
			log.info("ArrayList of DPIs succesfully extended.");
		}

		log.debug("Looking at the STYLES-array...");
		if (styles == null)
			styles = new ArrayList<>();
		if (styles.size() != layers.size()) {
			log.warn("More wms-layers given than styles are given. Adjusting styles size...");
			log.info("Extending the ArrayList of styles with entry of 'default'...");
			while (styles.size() != layers.size())
				styles.add("default");
			log.info("ArrayList of styles succesfully extended.");
		}

		log.debug("Looking at the OPACITIES-array...");
		if (opacities == null)
			opacities = new ArrayList<>();
		if (opacities.size() != layers.size()) {
			log.warn("More wms-layers given than opacities are given. Adjusting opacities size...");
			log.info("Extending the ArrayList of opacities with entry of '255'...");
			while (opacities.size() != layers.size())
				opacities.add(255);
			log.info("ArrayList of DPIs succesfully extended.");
		}

		log.debug("Settig DPI-array...");
		this.setDpis(dpis);
		log.debug("Setting STYLES-array...");
		this.setStyles(styles);
		log.debug("Setting the OPACITIES-array...");
		this.setOpacities(opacities);

		log.info("WMS-MapLayer created.");
	}

	// METHODS

	/**
	 * This method shall be called first in the receive()-method and gain
	 * informations from the server by receiving its capabilities.
	 * 
	 * TODO : EXPAND FILL AND TEST METHOD AND CALLED METHODS
	 */
	private void capabilitiesPrepare() {
		ServerTalker st = ServerTalker.getInstance();
		try {
			st.receiveCapabilities(this.getUrl());
		} catch (CapabilitiesRequestException e) {
			// TODO : EXCEPTION HANDLING
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.IPdfAddable#receive()
	 */
	@Override
	public void receive() throws MapLayerNotReceivableException {
		this.capabilitiesPrepare();

		log.debug("Calculating the ratio between the layer and the map BBOX in widht...");
		double layerRatioWidth = this.getLayerBBox().getGeoWidth() / this.getMapBBox().getGeoWidth();

		log.debug("Calculating the ratio between the layer and the map BBOX in height...");
		double layerRatioHeight = this.getLayerBBox().getGeoHeight() / this.getMapBBox().getGeoHeight();

		log.debug("Calculating the inches to cover in widht...");
		double inchesToCoverWidth = this.getMapInchesWidth() * layerRatioWidth;

		log.debug("Calculating the inches to cover in height...");
		double inchesToCoverHeight = this.getMapInchesHeight() * layerRatioHeight;
		log.debug("Inches to cover: width= " + inchesToCoverWidth + ", height= " + inchesToCoverHeight);

		log.debug("Running thought the layers of this WmsLayer an receiving the data...");
		for (int a = 0; a < this.getLayers().size(); a++) {
			log.info("Looking at layer " + a + " of this WMSLayer...");

			log.debug("Calculating the width and height of the complete map-image to request...");
			int imgWidth = (int) (inchesToCoverWidth * this.getDpis().get(a));
			int imgHeight = (int) (inchesToCoverHeight * this.getDpis().get(a));

			log.debug("Calculating the Array Size...");
			// MULTIPLE CASTS ARE NECCESSARRY BECAUSE AN DOUBLE DEVIDED BY AND
			// INT WILL RETURN AN INT AND NOT A DOUBLE VALUE :-(
			int partedWidth = (int) Math.ceil(((double) imgWidth / (double) this.getMaxRequestPixel()));
			int partedHeight = (int) Math.ceil(((double) imgHeight / (double) this.getMaxRequestPixel()));
			log.debug("Array must be " + partedWidth + " in width and " + partedHeight + " in height.");

			// CALCULATE THE RAM NEEDS FOR THIS MAPLAYER
			log.warn("Image to request: Width= " + imgWidth + " Pixels, height = " + imgHeight + " Pixels.");
			log.debug("Calculating the MB the image will need in RAM...");
			double imgMB = ((imgWidth * imgHeight * 16) / 1048576);
			log.warn("Image to receive is " + imgMB + " MB large!");

			/*
			 * ATTENTION: IT IS SAVING THE RAM-MODE IN AN ARRAYLIST OF BOOLEANS
			 * FOR THIS MAPLAYER, BECAUSE THIS INFORMATION IS NEEDED IN OTHER
			 * METHODS TOO, BUT THEY ARE NOT CALLED YET. IN THE FOLLOWING OF
			 * !THIS! METHOD ONLY LAYERS OF THIS WMSLAYER WILL BE RECEIVED THAT
			 * DO !NOT! NEED RAM-MODE-ADDING. OTHERS WILL BE RECEIVED LATER ON.
			 */
			if (imgMB > this.getMaxImageMB()) {
				log.warn("Image to receive for this WMSLayers layer is larger than " + this.getMaxImageMB() + " MB. Activating RAM-mode for this MapLayer...");
				this.getRamModes().add(true);
				log.warn("RAM-mode is activated for this WMSLayers layer!");
			} else {
				log.debug("RAM-mode is not neccessary for this WMSLayers layer.");
				this.getRamModes().add(false);
			}

			log.debug("Looking at the RAM-mode indicator for this WMSLayers layer " + a + "...");
			if (!this.getRamModes().get(this.getRamModes().size() - 1)) {
				log.debug("RAM-mode-adding is not neccessary for this WMSLayers layer.");

				try {
					log.debug("Creating a new TileArray...");
					TileArray t = new TileArray(this.getMaxRequestPixel(), this.getLayerBBox(), imgWidth, imgHeight, partedWidth, partedHeight);

					log.debug("Creating a new LayerImage...");
					LayerImage tempImg;

					log.debug("Preparing the TileArray...");
					t.prepareArray();

					log.debug("Running through the TileArray...");
					for (int rows = 0; rows < t.getRows(); rows++) {
						for (int cols = 0; cols < t.getColumns(); cols++) {
							Tile actTile = t.getTiles()[cols][rows];
							log.debug("Setting the TileImage for the Tile " + cols + ", " + rows + "...");
							actTile.setTileImage(ServerTalker.getInstance().tileImageRequest(
									// BY THE TILE
									actTile,
									// THE URL OF THIS WMSLAYER
									this.getUrl(),
									// THE VERSION TO REQUEST
									this.getVersion(),
									// THE LAYER TO REQUEST IN THE
									// MOMENT
									this.getLayers().get(a),
									// THE STYLE TO REQUEST IN THE
									// MOMENT
									this.getStyles().get(a),
									// THE OPACITY TO REQUEST IN THE
									// MOMENT
									this.getOpacities().get(a)));
						}
					}

					log.debug("Creating a BufferedImage from the TileArrays images...");
					BufferedImage temp = ImageHandler.getInstance().computeImage(t);

					log.debug("Converting the BufferedImage to a LayerImage...");
					tempImg = ImageHandler.getInstance().convertToLayerImage(temp, this.getxOffset2Map(), this.getyOffset2Map(), inchesToCoverWidth, inchesToCoverHeight, this.getOpacities().get(a));

					log.debug("Adding the LayerImage to the ArrayList of LayerImages...");
					this.getLayerImages().add(tempImg);
				} catch (TileArrayDimensionException | BoundingboxNotCreatableException | ImageCovertingException | TileException e) {
					log.error("Layer " + a + " of the WmsLayer could not be received and added!");
				}
				// END OF RAM-MODE-IF
			} else
				log.warn("RAM-mode-adding is neccessary for this WMSLayers layer " + a + ". Receiving the image later on!");
			log.info("Layer " + a + " of this WMSLayer finished.");
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
		// NOTHING TO DO HERE AT THIS POINT, BECAUSE THE IMAGES ARE ALREADY DONE
		// IN THEIR METHODS
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.IPdfAddable#addToPdf(com.
	 * lowagie.text.Document, com.lowagie.text.pdf.PdfWriter)
	 */
	@Override
	public void addToPdf(Document doc) throws MapLayerNotReceivableException {
		log.debug("Creating PdfLayer layer to be used as parental Layer...");
		PdfLayer overlayer = this.createParentalPdfLayer("WMS-Layer: " + this.getUrl(), this.getWriter());

		log.debug("Gaining the PdfContentByte...");
		PdfContentByte contByte = this.getWriter().getDirectContent();

		log.debug("Running through the WMSLayers layer...");
		for (int a = 0; a < this.getLayers().size(); a++) {
			log.debug("Creating a PdfLayer as child-layer for the parental PdfLayer...");
			PdfLayer sublayer = this.createChildLayer("" + this.getLayers().get(a), this.getWriter());

			log.debug("Beginning the child PdfLayer...");
			contByte.beginLayer(sublayer);

			/*
			 * IF THE RAM MODE FOR THE MAPSLAYERS LAYER IS NOT RAM MODE THE
			 * LAYER ALREADY CONTAINS THE IMAGE(S) TO ADD. IF THE RAM MODE IS
			 * ACTIVE FOR THIS MAPLAYERS LAYER THE ADDING HAS TO BE EXECUTED VIA
			 * RAMMODE ADDING METHOD
			 */
			if (this.getRamModes().get(a)) {
				log.debug("RAM-mode is active for this layer. Calling RAM-mode adding...");
				this.ramModeAdding(a, doc, contByte);
				log.debug("Adding completed.");
			} else {
				log.debug("RAM-mode is not active for this layer. Calling normal-mode adding...");
				this.normalAdding(this.getLayerImages().get(a), doc, contByte);
				log.debug("Adding completed.");
			}

			log.debug("Ending the child PdfLayer...");
			contByte.endLayer();

			log.debug("Adding child PdfLayer to the parental PdfLayer...");
			overlayer.addChild(sublayer);
		}
		log.info("WmsLayer added.");
	}

	/**
	 * Adds the image optimized for smaller RAMs.
	 * 
	 * Receives and adds every single tile directly to the PDF document, without
	 * putting it together in a larger image.
	 *
	 * @param a
	 *            the index of the layer to add in RAM-mode.
	 * @param doc
	 *            the {@link Document} to add the image to
	 * @param writer
	 *            the writer to use to add the image
	 */
	private void ramModeAdding(int a, Document doc, PdfContentByte contByte) {
		log.info("Beginning RAM-mode adding...");
		int maxPixels = this.getMaxRequestPixel();
		log.info("Maximum pixle size for a WMS request is set to 1000.");

		log.debug("Calculating the inches to cover by this WmsLayer...");
		double mapRatioWidth = this.getLayerBBox().getGeoWidth() / this.getMapBBox().getGeoWidth();
		double mapRatioHeight = this.getLayerBBox().getGeoHeight() / this.getMapBBox().getGeoHeight();
		double inchesToCoverWidth = this.getMapInchesWidth() * mapRatioWidth;
		double inchesToCoverHeight = this.getMapInchesHeight() * mapRatioHeight;
		log.debug("Inches to cover: width= " + inchesToCoverWidth + ", height= " + inchesToCoverHeight);

		log.debug("Calculating the width and height of the image to request...");
		int imgWidth = (int) (inchesToCoverWidth * this.getDpis().get(a));
		int imgHeight = (int) (inchesToCoverHeight * this.getDpis().get(a));
		log.debug("Width to request: " + imgWidth + ", height to request: " + imgHeight + ".");

		log.debug("Calculating the Array Size...");
		// MULTIPLE CASTS ARE NECCESSARRY BECAUSE AN DOUBLE DEVIDED BY AND
		// INT WILL RETURN AN INT AND NOT A DOUBLE VALUE :-(
		int partedWidth = (int) Math.ceil(((double) imgWidth / (double) maxPixels));
		int partedHeight = (int) Math.ceil(((double) imgHeight / (double) maxPixels));
		log.debug("Array must be " + partedWidth + " in width and " + partedHeight + " in height.");

		log.debug("Creating TileArray...");
		TileArray t = new TileArray(maxPixels, this.getLayerBBox(), imgWidth, imgHeight, partedWidth, partedHeight);

		try {
			log.debug("Preparing TileArray...");
			t.prepareArray();

			for (int rows = 0; rows < t.getRows(); rows++) {
				log.debug("Actual row: " + rows + ".");
				for (int cols = 0; cols < t.getColumns(); cols++) {
					log.debug("Actual column: " + cols + ".");

					log.debug("Gaining actual tile from the TileArray...");
					Tile actTile = t.getTiles()[cols][rows];

					log.debug("Setting TileImage for the actual Tile...");
					actTile.setTileImage(
							// REQUEST THE IMAGE
							ServerTalker.getInstance().tileImageRequest(
									// BY THE TILE
									actTile,
									// THE URL OF THIS WMSLAYER
									this.getUrl(),
									// THE VERSION TO REQUEST
									this.getVersion(),
									// THE LAYER TO REQUEST IN THE
									// MOMENT
									this.getLayers().get(a),
									// THE STYLE TO REQUEST IN THE
									// MOMENT
									this.getStyles().get(a),
									// THE OPACITY TO REQUEST IN THE
									// MOMENT
									this.getOpacities().get(a)));
					// THE TILE ACTTILE CONTAINS THE IMAGE AT THIS MOMENT, NOW
					// IT NEEDS TO BE DIRECTLY INTEGRATED TO THE PDF DOCUMENT
					log.debug("Tile contains the TileImage now. Due to RAM-mode it will be added directly...");

					log.debug("Calculating the inces to be covered by the TileImage...");
					double tileInchesToCoverWidth = inchesToCoverWidth * (actTile.getImageWidth() / (double) imgWidth);
					double tileInchesToCoverHeight = inchesToCoverHeight * (actTile.getImageHeight() / (double) imgHeight);

					// ADD THE OFFSET IN PIXELS TO THE LOWER LEFT CORNER OF THE
					// MAPLAYER IMAGE
					log.debug("Calculating the offset of the TileImage to the lower left map corner...");
					double[] tileOffset = this.calcPixelOffsets(this.getLayerBBox(), actTile.getTileBBox());

					log.debug("Extracting offset from the double-array...");
					double tileOffsetX = this.getxOffset2Map() + tileOffset[0];
					double tileOffsetY = this.getyOffset2Map() + tileOffset[1];

					log.debug("Convert TileImage to LayerImage...");
					LayerImage tempImg = ImageHandler.getInstance().convertToLayerImage(
							//
							actTile.getTileImage(),
							//
							tileOffsetX, tileOffsetY,
							//
							tileInchesToCoverWidth,
							//
							tileInchesToCoverHeight,
							//
							this.getOpacities().get(a));

					log.debug("Adding converted LayerImage to the Document...");
					/*
					 * Adds an Image to the page. The positioning of the Image
					 * is done with the transformation matrix. To position an
					 * image at (x,y) use addImage(image, image_width, 0, 0,
					 * image_height, x, y).
					 */
					contByte.addImage(tempImg.getImage(),
							// THE SCALED WIDTH
							tempImg.getImage().getScaledWidth(),
							// ZREO
							0f,
							// ZERO
							0f,
							// THE SCALED HEIGHT
							tempImg.getImage().getScaledHeight(),
							// THE X POSITON: MAP MARGIN AND OFFSET
							(float) (doc.topMargin() + tempImg.getxOffset()),
							// THE Y POSITON: MAP MARGIN AND OFFSET
							(float) (doc.topMargin() + tempImg.getyOffset())
					// DONE
					);
					log.debug("LayerImage added...");

					// TRY TO SAVE SOME MORE RAM
					actTile = null;
					tempImg = null;
				}
			}

		} catch (DocumentException | ImageCovertingException | BoundingboxNotCreatableException | TileArrayDimensionException | TileException e) {
			log.error("Image could not be added to the document!");
		}
		log.info("RAM-mode adding completed.");
	}

	/**
	 * The normal (non-RAM-mode) way to add a {@link LayerImage} to the
	 * document.
	 *
	 * @param layerImage
	 *            the {@link LayerImage} to add
	 * @param doc
	 *            the {@link Document} to add the image to
	 * @param contByte
	 *            the {@link PdfContentByte} to use to add the image
	 */
	private void normalAdding(LayerImage layerImage, Document doc, PdfContentByte contByte) {
		try {
			log.debug("Normal adding a LayerImage to the document...");
			/*
			 * Adds an Image to the page. The positioning of the Image is done
			 * with the transformation matrix. To position an image at (x,y) use
			 * addImage(image, image_width, 0, 0, image_height, x, y).
			 */
			contByte.addImage(layerImage.getImage(),
					// THE SCALED WIDTH
					layerImage.getImage().getScaledWidth(),
					// ZREO
					0f,
					// ZERO
					0f,
					// THE SCALED HEIGHT
					layerImage.getImage().getScaledHeight(),
					// THE X POSITON: MAP MARGIN AND OFFSET
					(float) (doc.topMargin() + layerImage.getxOffset()),
					// THE Y POSITON: MAP MARGIN AND OFFSET
					(float) (doc.topMargin() + layerImage.getyOffset())
			// DONE
			);
			log.debug("LayerImage added.");
		} catch (DocumentException e) {
			log.error("Image could not be added to the document!");
		}
	}

	/**
	 * Creates a child {@link PdfLayer} with the given label by the given writer
	 * and returns it.
	 * 
	 * Sets the {@link PdfLayer} to "on" and "on panel". Do not forget to end
	 * the given {@link PdfLayer} after opening it.
	 *
	 * @param label
	 *            the lable to show as {@link String}
	 * @param writer
	 *            the {@link PdfWriter} to create the {@link PdfLayer}
	 * @return a {@link PdfLayer}
	 */
	private PdfLayer createChildLayer(String label, PdfWriter writer) {
		log.debug("Creating child PdfLayer...");
		PdfLayer sublayer = new PdfLayer(label, writer);
		log.debug("Setting the attributes of the child PdfLayer...");
		sublayer.setOn(true);
		sublayer.setOnPanel(true);
		log.debug("Child PdfLayer created.");
		return sublayer;
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link ArrayList} of styles as {@link String}s.
	 *
	 * @return the {@link ArrayList} of styles as {@link String}s
	 */
	public ArrayList<String> getStyles() {
		return styles;
	}

	/**
	 * Sets the {@link ArrayList} of styles.
	 *
	 * @param styles
	 *            the styles to set
	 */
	private void setStyles(ArrayList<String> styles) {
		if (styles != null)
			this.styles = styles;
	}

	/**
	 * Returns the {@link ArrayList} of opacities as {@link Integer}s.
	 *
	 * @return the {@link ArrayList} of opacities as {@link Integer}s
	 */
	public ArrayList<Integer> getOpacities() {
		return opacities;
	}

	/**
	 * Sets the {@link ArrayList} of opacities.
	 *
	 * @param opacities
	 *            the {@link ArrayList} to set
	 */
	private void setOpacities(ArrayList<Integer> opacities) {
		if (opacities != null)
			this.opacities = opacities;
	}

	/**
	 * Returns the {@link ArrayList} of {@link Boolean}s indicating if the layer
	 * to request at this index shall be requested via RamMode or not.
	 *
	 * @return the ramModes as {@link ArrayList}
	 */
	private ArrayList<Boolean> getRamModes() {
		return ramModes;
	}

	/**
	 * Returns the maximum amount of pixels in X and/or Y direction to be
	 * requested in a GetMapRequest as {@link Integer}.
	 *
	 * @return the maxRequestPixel as {@link Integer}
	 */
	public int getMaxRequestPixel() {
		return maxRequestPixel;
	}

	/**
	 * Sets the maximum amount of pixels in X and/or Y direction to be requested
	 * in a GetMapRequest.
	 *
	 * @param maxRequestPixel
	 *            the {@link Integer} to set
	 */
	public void setMaxRequestPixel(int maxRequestPixel) {
		this.maxRequestPixel = maxRequestPixel;
	}

	/**
	 * Returns the maximum size in MB an single image (received by an
	 * GetMapRequest) can be without activating the RAM-Mode as {@link Integer}.
	 *
	 * @return the maxImageMB in MB as {@link Integer}
	 */
	public int getMaxImageMB() {
		return maxImageMB;
	}

	/**
	 * Sets the maximum size in MB an single image (received by an
	 * GetMapRequest) can be without activating the RAM-Mode.
	 *
	 * @param maxImageMB
	 *            the maxImageMB to set as {@link Integer}
	 */
	public void setMaxImageMB(int maxImageMB) {
		this.maxImageMB = maxImageMB;
	}

	// OTHERS
}
