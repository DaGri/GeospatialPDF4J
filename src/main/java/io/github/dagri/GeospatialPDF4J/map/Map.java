package io.github.dagri.GeospatialPDF4J.map;

import java.util.ArrayList;

import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.geotools.geometry.DirectPosition2D;

import io.github.dagri.GeospatialPDF4J.exceptions.BoundingboxNotCreatableException;
import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;
import io.github.dagri.GeospatialPDF4J.map.layers.ReferencedLayer;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to contain multiple {@link MapLayer}s and support actions on them.
 * 
 * @author DaGri
 * @since 10.01.2017
 */
@Slf4j
public class Map {

	// ATTRIBUTES

	/**
	 * An {@link Instance} of a {@link Map} according to the singleton-pattern.
	 */
	private static Map			instance;

	/**
	 * The {@link ArrayList} of {@link MapLayer}s.
	 */
	private ArrayList<MapLayer>	maplayers	= new ArrayList<>();

	/**
	 * The {@link BoundingBox} of this {@link Map}.
	 */
	private BoundingBox		mapBbox		= null;

	/**
	 * The width in inches of the {@link Map} on the PDF file.
	 */
	private double				inchWidth;

	/**
	 * The height in inches of the {@link Map} on the PDF file.
	 */
	private double				inchHeight;

	// CONSTRUCTORS

	/**
	 * Empty constructor for a {@link Map}.
	 */
	private Map() {
		log.info("Created a new Map.");
	}

	// METHODS

	/**
	 * Returns an instance of a {@link Map} according to the singleton pattern.
	 *
	 * @return an instance of a {@link Map}
	 */
	public static Map getInstance() {
		if (instance == null)
			instance = new Map();
		log.debug("Returning an instance of a Map.");
		return instance;
	}

	/**
	 * Calculates the {@link BoundingBox} of this {@link Map} by iterating
	 * over the internal {@link MapLayer}s.
	 * 
	 * @throws BoundingboxNotCreatableException
	 */
	private void calcMapBbox() throws BoundingboxNotCreatableException {
		// THE CONSTRUCTOR OF A MAPLAYER CONVERTS ALL GIVEN ORDINATES INTO
		// UTM-CRS (EPSG:25832) SO THE ORDAINTE 0 WILL CONTAIN THE EAST, AND THE
		// ORDNATE 1 WILL CONTAIN THE NORTH VALUES

		log.info("Calculating Map BoundingBox...");
		if (this.getMaplayers().size() >= 1) {

			log.debug("Using the values of the first contained Layer as reference values...");
			double minNorthing = this.getMaplayers().get(0).getLayerBBox().getLl().getOrdinate(1);
			double minEasting = this.getMaplayers().get(0).getLayerBBox().getLl().getOrdinate(0);
			double maxNorthing = this.getMaplayers().get(0).getLayerBBox().getUr().getOrdinate(1);
			double maxEasting = this.getMaplayers().get(0).getLayerBBox().getUr().getOrdinate(0);
			log.debug("Reference values set.");

			log.debug("Running through MapLayers and comparing them with the reference values...");
			for (int a = 0; a < this.getMaplayers().size(); a++) {
				// MIN NORTHING
				if (this.getMaplayers().get(a).getLayerBBox().getLl().getOrdinate(1) < minNorthing) {
					log.debug("Smaller northing found in MapLayer " + a);
					minNorthing = this.getMaplayers().get(a).getLayerBBox().getLl().getOrdinate(1);
				}

				// MIN EASTING
				if (this.getMaplayers().get(a).getLayerBBox().getLl().getOrdinate(0) < minEasting) {
					log.debug("Smaller easting found in MapLayer " + a);
					minEasting = this.getMaplayers().get(a).getLayerBBox().getLl().getOrdinate(0);
				}

				// MAX NORTHING
				if (this.getMaplayers().get(a).getLayerBBox().getUr().getOrdinate(1) > maxNorthing) {
					log.debug("Bigger northing found in MapLayer " + a);
					maxNorthing = this.getMaplayers().get(a).getLayerBBox().getUr().getOrdinate(1);
				}

				// MAX EASTING
				if (this.getMaplayers().get(a).getLayerBBox().getUr().getOrdinate(0) > maxEasting) {
					log.debug("Bigger easting found in MapLayer " + a);
					maxEasting = this.getMaplayers().get(a).getLayerBBox().getUr().getOrdinate(0);
				}
			}
			log.debug("Ran through MapLayers of the map: Minimal and maximal values found.");

			log.debug("Creating Map BoundingBox...");
			CoordinateTransformer t = CoordinateTransformer.getInstance();
			try {
				DirectPosition2D ll = new DirectPosition2D(t.getUtmCrs(), minEasting, minNorthing);
				DirectPosition2D ur = new DirectPosition2D(t.getUtmCrs(), maxEasting, maxNorthing);
				this.setMapBbox(new BoundingBox(ll, ur));
			} catch (CoordinateTransformException e) {
				log.error(e.getMessage());
				throw new BoundingboxNotCreatableException();
			}
			log.debug("Map BoundingBox created.");

		} else {
			log.error("The Map has no attached MapLayers!");
			throw new BoundingboxNotCreatableException();
		}
		log.info("Map BoundingBox calculated.");
	}

	/**
	 * Prepares the {@link Map} and its {@link MapLayer}s.
	 * 
	 * o Calculates the {@link BoundingBox} of the {@link Map}.
	 * 
	 * o Sets the {@link Map}s {@link BoundingBox} to all {@link MapLayer}s.
	 * 
	 * @throws BoundingboxNotCreatableException
	 *             if the {@link Map}s {@link BoundingBox} could not be
	 *             created
	 */
	public void prepare() throws BoundingboxNotCreatableException {
		log.info("Preparing the Map and its MapLayers...");
		if (this.getMapBbox() == null) {
			log.debug("MapBoundingbox is null. Calculating...");
			this.calcMapBbox();
		}

		log.info("Creating a ReferencedLayer for this Map...");
		this.createReferencedLayer();
		log.debug("ReferencedLayer added to the MapLayers-ArrayList at index 0.");

		// RUN THOUGHT LAYERS AND ADD THE MAP BOUNDINGBOX TO THEM
		log.info("Setting the Maps BoundingBox to all " + this.getMaplayers().size() + " MapLayers...");
		this.setMapBbox2Layers();
		log.info("Map BoundingBox set to all MapLayers.");
		// RUN THROUGHT THE LAYERS AND SET THE OFFSETS TO THEM
		log.info("Setting the offsets in X and Y to all " + this.getMaplayers().size() + " MapLayers...");
		this.setOffsets2Layer();
		log.info("Offsets set to all MapLayers.");
		log.info("Map and its MapLayers are prepared.");
	}

	/**
	 * Sets the {@link Map}s {@link BoundingBox} to all attached
	 * {@link MapLayer}s.
	 */
	private void setMapBbox2Layers() {
		log.debug("Setting the maps BoundingBox to all MapLayers...");
		for (int a = 0; a < this.getMaplayers().size(); a++) {
			log.debug("Setting to MapLayer:" + a);
			this.getMaplayers().get(a).setMapBBox(this.getMapBbox());
			log.debug("Set to MapLayer " + a);
		}
		log.debug("Setting the maps BoundingBox to all MapLayers done.");
	}

	/**
	 * Sets the offset of the {@link MapLayer}s to the {@link Map} to all
	 * contained {@link MapLayer}s.
	 */
	private void setOffsets2Layer() {
		log.debug("Setting offset to all contained Maplayers...");
		for (int a = 0; a < this.getMaplayers().size(); a++) {
			log.debug("Setting offset to MapLayer " + a);

			// CALCULATE THE DIFFERENCE IN METERS IN X
			double xDiffMeters = this.getMaplayers().get(a).getLayerBBox().getLl().getOrdinate(0) - this.getMapBbox().getLl().getOrdinate(0);
			// CALCUALTE THE DIFFERENCE IN METERS IN Y
			double yDiffMeters = this.getMaplayers().get(a).getLayerBBox().getLl().getOrdinate(1) - this.getMapBbox().getLl().getOrdinate(1);

			if (xDiffMeters != 0.0 && xDiffMeters != 0.0) {
				// SPITTED IN IF-ELSE BLOCK DUE TO A DIVISION TRHOUGH ZERO IF
				// THERE IS NO OFFSET TO ADD
				// CALCULATE THE RATIO BETWEEN THE MAPS BOUNDINGBOX AND THE
				// DIFFERENCE IN METERS IN X
				double xRatio = this.getMapBbox().getGeoWidth() / xDiffMeters;
				// CALCULATE THE RATIO BETWEEN THE MAPS BOUNDINGBOX AND THE
				// DIFFERENCE IN METERS IN Y
				double yRatio = this.getMapBbox().getGeoHeight() / yDiffMeters;

				// CALCUALTE THE OFFSET IN PIXELS
				double xOffset = xRatio * this.getInchWidth() * 72;
				double yOffset = yRatio * this.getInchHeight() * 72;

				// SET THE OFFSET TO THE MAPLAYER
				this.getMaplayers().get(a).setxOffset2Map(xOffset);
				this.getMaplayers().get(a).setyOffset2Map(yOffset);
			} else {
				// ELSE THERE IS NO OFFSET TO SET: SETTING OFFSET TO ZERO
				double xOffset = 0.0;
				double yOffset = 0.0;
				this.getMaplayers().get(a).setxOffset2Map(xOffset);
				this.getMaplayers().get(a).setyOffset2Map(yOffset);
			}
			log.debug("Offset set to MapLayer " + a);
		}
		log.debug("Offset set to all contained MapLayers.");
	}

	/**
	 * Creates a {@link ReferencedLayer} and adds it to the internal
	 * {@link ArrayList} of {@link MapLayer}s at the position 0.
	 */
	private void createReferencedLayer() {
		log.info("Creating a new ReferencedLayer with the Maps BoundingBox as BoundingBox...");
		ReferencedLayer temp = new ReferencedLayer(this.getMapBbox());
		log.debug("Adding the created ReferencedLayer to the MapLayers at position 0.");
		this.getMaplayers().add(0, temp);
		log.info("Created a new ReferencedLayer and added it to the ArrayList of Maplayers at position 0.");
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link ArrayList} of {@link MapLayer}s of this {@link Map} as
	 * {@link ArrayList}.
	 *
	 * @return the maplayers
	 */
	public ArrayList<MapLayer> getMaplayers() {
		return maplayers;
	}

	/**
	 * Returns the {@link BoundingBox} of this map as {@link BoundingBox}.
	 *
	 * @return the mapBbox
	 */
	public BoundingBox getMapBbox() {
		return mapBbox;
	}

	/**
	 * Sets the {@link BoundingBox} of this {@link Map}.
	 *
	 * @param mapBbox
	 *            the mapBbox to set
	 */
	private void setMapBbox(BoundingBox mapBbox) {
		this.mapBbox = mapBbox;
	}

	/**
	 * Returns the width in inches of this {@link Map} on the PDF page.
	 *
	 * @return the width of this {@link Map} in inches as {@link Double}
	 */
	public double getInchWidth() {
		return inchWidth;
	}

	/**
	 * Sets the width in inches of this {@link Map} on the PDF page.
	 *
	 * @param inchWidth
	 *            the inchWidth to set
	 */
	public void setInchWidth(double inchWidth) {
		this.inchWidth = inchWidth;
	}

	/**
	 * Returns the height in inches of this {@link Map} on the PDF page as
	 * {@link Double}.
	 *
	 * @return the height of this {@link Map} in inches as {@link Double}
	 */
	public double getInchHeight() {
		return inchHeight;
	}

	/**
	 * Sets the height in inches of this {@link Map} on the PDF page.
	 *
	 * @param inchHeight
	 *            the inchHeight to set
	 */
	public void setInchHeight(double inchHeight) {
		this.inchHeight = inchHeight;
	}

	// OTHERS
}
