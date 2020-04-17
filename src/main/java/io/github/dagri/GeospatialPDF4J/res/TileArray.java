package io.github.dagri.GeospatialPDF4J.res;

import org.geotools.geometry.DirectPosition2D;

import io.github.dagri.GeospatialPDF4J.exceptions.BoundingboxNotCreatableException;
import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.TileArrayDimensionException;
import io.github.dagri.GeospatialPDF4J.exceptions.TileException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to store and manage an array of {@link Tile}s for tiled receiving of
 * image-data.
 * 
 * @author DaGri
 * @since 11.01.2017
 */
@Slf4j
public class TileArray {
	// ATTRIBUTES

	/**
	 * The count of columns.
	 */
	private int				columns	= 0;

	/**
	 * The count of rows.
	 */
	private int				rows	= 0;

	/**
	 * The 2D - array of {@link Tile}s; first rows then columns.
	 */
	private Tile[][]		tiles;

	/**
	 * The {@link BoundingBox} of this {@link TileArray}.
	 */
	private BoundingBox	arrayBbox;

	/**
	 * The maximum size (in x- or y- direction) of pixels to request.
	 */
	private int				maxPixels;

	/**
	 * The width of the image to request in total in pixels.
	 */
	private int				imgWidth;

	/**
	 * The height of the image to request in total in pixels.
	 */
	private int				imgHeight;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link TileArray} using various input informations.
	 * 
	 * @param maxPixels
	 *            the maximum count of pixels (in X- and Y- direction) that can
	 *            be received from the server in a single tile as
	 *            {@link Integer}
	 * @param layerBBox
	 *            the {@link BoundingBox} of the {@link MapLayer} to request
	 * @param imgWidth
	 *            the width the computed image shall be in pixels as
	 *            {@link Integer}
	 * @param imgHeight
	 *            the height the computed image shall be in pixels as
	 *            {@link Integer}
	 * @param columns
	 *            the number of columns as {@link Integer}
	 * @param rows
	 *            the number of rows as {@link Integer}
	 */
	public TileArray(int maxPixels, BoundingBox layerBBox, int imgWidth, int imgHeight, int columns, int rows) {
		log.debug("Setting maximum pixel count to " + maxPixels + "...");
		this.setMaxPixels(maxPixels);

		log.debug("Setting the layers BoundingBox to the TileArray...");
		this.setArrayBbox(layerBBox);

		log.debug("Setting the number of columns to " + columns + "...");
		this.setColumns(columns);

		log.debug("Setting the number of columns to " + columns + "...");
		this.setRows(rows);

		log.debug("Setting the image width to " + imgWidth + "...");
		this.setImgWidth(imgWidth);

		log.debug("Setting the image height to " + imgHeight + "...");
		this.setImgHeight(imgHeight);
	}

	// METHODS

	/**
	 * Creates the array of {@link Tile}s with the given number of columns and
	 * rows.
	 *
	 * @param columns
	 *            the number of columns as {@link Integer}
	 * @param rows
	 *            the number of rows as {@link Integer}
	 * @throws TileArrayDimensionException
	 *             if the number of columns or rows is equal or below 0
	 */
	private void createArray(int columns, int rows) throws TileArrayDimensionException {
		log.debug("Creating the array...");
		if (columns <= 0 || rows <= 0) {
			log.error("Number of rows or columns is <= 0.");
			throw new TileArrayDimensionException();
		}
		log.debug("Setting columns to " + columns + "...");
		this.setColumns(columns);
		log.debug("Setting rows to " + rows + "...");
		this.setRows(rows);
		log.debug("Setting new TileArray...");
		this.setTiles(new Tile[this.getColumns()][this.getRows()]);
		log.debug("TileArray set.");
	}

	/**
	 * Prepares the array for filling.
	 * 
	 * Calculates the size of the {@link Tile}s {@link BoundingBox}s and sets
	 * them to the {@link Tile}s.
	 *
	 * @throws TileArrayDimensionException
	 *             if the given count of rows or columns is not acceptable
	 * @throws BoundingboxNotCreatableException
	 *             if a {@link BoundingBox} could not be created
	 * @throws TileException
	 *             if an error occurred during the creation of a {@link Tile}
	 */
	public void prepareArray() throws TileArrayDimensionException, BoundingboxNotCreatableException, TileException {
		log.info("Preparing the TileArray...");
		log.debug("Creating the array...");
		this.createArray(this.getColumns(), this.getRows());

		log.debug("Saving the image width and height into variables...");
		int heightLeft = this.getImgHeight();
		int widthLeft = this.getImgWidth();

		// DUE TO THE INTERNAL STRUCTURE OF THE BBOX ITS CRS IS UTM ORDINATE 0
		// --> EASTING
		log.debug("Setting act easting to the UL coordinate of the BBOX...");
		double actEasting = this.getArrayBbox().getUl().getOrdinate(0);

		log.debug("Creating new variables for the actual pixel width and height...");
		int actWidthPixels = 0;
		int actHeightPixels = 0;

		log.debug("Running through the columns...");
		for (int cols = 0; cols < this.getColumns(); cols++) {
			log.debug("Actual column: " + cols);

			// DUE TO THE INTERNAL STRUCTURE OF THE BBOX ITS CRS IS UTM ORDINATE
			// 1 --> NORTHING
			log.debug("Setting act northing to the upper left corner of the TileArray BBOX...");
			double actNorthing = this.getArrayBbox().getUl().getOrdinate(1);

			log.debug("Calculating the actual width request pixel size...");
			if (widthLeft > this.getMaxPixels()) {
				log.debug("The rest of the image width can not be requested in one request.");
				log.debug("Setting the actual width to request to the maximum request size...");
				actWidthPixels = this.getMaxPixels();
				log.debug("Reducing the width left with the actual width...");
				widthLeft = widthLeft - actWidthPixels;
				log.debug("Width left: " + widthLeft + ".");
			} else {
				log.debug("The rest of the image width can be requested in one request.");
				log.debug("Setting the actual width to the width left...");
				actWidthPixels = widthLeft;
				log.debug("Actual width: " + actWidthPixels);
				log.debug("Setting the width left to '-100'...");
				widthLeft = -100;
			}

			log.debug("Calculating the percentage in width covered by the actual width...");
			double ratioWidth = actWidthPixels / (double) this.getImgWidth();
			log.debug("Percentage covered: " + ratioWidth + ".");
			log.debug("Calculating the width covered in meters...");
			double geoWidthStep = this.getArrayBbox().getGeoWidth() * ratioWidth;
			log.debug("Meters covered: " + geoWidthStep + ".");

			log.debug("Running through the rows...");
			for (int rows = 0; rows < this.getRows(); rows++) {
				log.debug("Actual row: " + rows + ".");

				log.debug("Calculating the actual height request pixel size...");
				if (heightLeft > this.getMaxPixels()) {
					log.debug("The rest of the image width can not be requested in one request.");
					log.debug("Setting the actual width to request to the maximum request size...");
					actHeightPixels = this.getMaxPixels();
					log.debug("Reducing the width left with the actual width...");
					heightLeft = heightLeft - actHeightPixels;
					log.debug("Height left: " + heightLeft + ".");
				} else {
					log.debug("The rest of the image height can be requested in one request.");
					log.debug("Setting the actual height to the width left...");
					actHeightPixels = heightLeft;
					log.debug("Actual height: " + actHeightPixels);
					log.debug("Setting the width left to '-100'...");
					heightLeft = -100;
				}

				log.debug("Calculating the percentage in width covered by the actual height...");
				double ratioHeight = actHeightPixels / (double) this.getImgHeight();
				log.debug("Percentage covered: " + ratioHeight + ".");
				log.debug("Calculating the height covered in meters...");
				double geoHeightStep = this.getArrayBbox().getGeoHeight() * ratioHeight;
				log.debug("Meters covered: " + geoWidthStep + ".");

				log.debug("Calculating and saving the corner-coordinates for the actual request Tile...");
				double lln = actNorthing - geoHeightStep;
				double lle = actEasting;
				double urn = actNorthing;
				double ure = actEasting + geoWidthStep;

				log.debug("Gaining instance of a CoordinateTransformator...");
				CoordinateTransformer trans = CoordinateTransformer.getInstance();

				try {
					// IN THE FOLLOWING LINES THE USED CRS IS UTM, SO ORDINATE 0
					// IS EASTING; ORDINATE 1 IS NORTHING
					log.debug("Creating the lower left DirectPosition2D...");
					DirectPosition2D dp2DLl = new DirectPosition2D(trans.getUtmCrs(), lle, lln);
					log.debug("Creating the upper right DirectPosition2D...");
					DirectPosition2D dp2DUr = new DirectPosition2D(trans.getUtmCrs(), ure, urn);
					log.debug("Creating the BoundingBox for the Tile...");
					BoundingBox tileBBox = new BoundingBox(dp2DLl, dp2DUr);

					log.debug("Creating the Tile...");
					Tile t = new Tile(tileBBox, actWidthPixels, actHeightPixels);

					log.debug("Setting the Tile to the TileArray...");
					this.getTiles()[cols][rows] = t;

				} catch (CoordinateTransformException e) {
					log.error(e.getMessage());
					throw new BoundingboxNotCreatableException();
				}

				log.debug("Substracting the actual northing with the geoheight step...");
				actNorthing = actNorthing - geoHeightStep;
			}
			log.debug("Resetting the height-left-variable...");
			heightLeft = this.getImgHeight();
			log.debug("Resetting the actual-easting-variable...");
			actEasting = actEasting + geoWidthStep;
		}
		log.info("TileArray prepared.");
	};

	// GETTERS AND SETTERS

	/**
	 * Returns the the count of columns of the array of {@link Tile}s as
	 * {@link Integer}.
	 *
	 * @return the columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * Sets the count of columns of the array of {@link Tile}s.
	 *
	 * @param columns
	 *            the columns to set
	 */
	private void setColumns(int columns) {
		this.columns = columns;
	}

	/**
	 * Returns the count of rows of the array of {@link Tile}s as
	 * {@link Integer}.
	 *
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Sets the count of rows of the array of {@link Tile}s.
	 *
	 * @param rows
	 *            the rows to set
	 */
	private void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * Returns the 2D array of {@link Tile}s.
	 *
	 * @return the tiles
	 */
	public Tile[][] getTiles() {
		return tiles;
	}

	/**
	 * Sets the 2D array of {@link Tile}s.
	 *
	 * @param tiles
	 *            the tiles to set
	 */
	private void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}

	/**
	 * Returns the arrays {@link BoundingBox}.
	 *
	 * @return the arrayBbox
	 */
	public BoundingBox getArrayBbox() {
		return arrayBbox;
	}

	/**
	 * Sets the arrays {@link BoundingBox}.
	 *
	 * @param arrayBbox
	 *            the arrayBbox to set
	 */
	private void setArrayBbox(BoundingBox arrayBbox) {
		this.arrayBbox = arrayBbox;
	}

	/**
	 * Returns the maximum width / height of pixels that can be received by once
	 * from the server as {@link Integer}.
	 *
	 * @return the maxPixels as {@link Integer}
	 */
	public int getMaxPixels() {
		return maxPixels;
	}

	/**
	 * Sets the maximum width / height of pixels that can be received by once
	 * from the server.
	 *
	 * @param maxPixels
	 *            the maxPixels to set
	 */
	public void setMaxPixels(int maxPixels) {
		this.maxPixels = maxPixels;
	}

	/**
	 * Returns the width of the image to gain as {@link Integer}.
	 *
	 * @return the imgWidth as {@link Integer}
	 */
	public int getImgWidth() {
		return imgWidth;
	}

	/**
	 * Sets the width of the image to gain.
	 *
	 * @param imgWidth
	 *            the imgWidth to set
	 */
	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}

	/**
	 * Returns the height of the image to gain as {@link Integer}.
	 *
	 * @return the imgHeight as {@link Integer}
	 */
	public int getImgHeight() {
		return imgHeight;
	}

	/**
	 * Sets the height of the image to gain.
	 *
	 * @param imgHeight
	 *            the imgHeight to set
	 */
	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}

	// OTHERS
}
