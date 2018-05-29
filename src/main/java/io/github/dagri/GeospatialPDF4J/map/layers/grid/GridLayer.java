package io.github.dagri.GeospatialPDF4J.map.layers.grid;

import java.awt.Color;
import java.util.ArrayList;

import org.geotools.geometry.DirectPosition2D;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfLayer;

import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawLineString;
import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.MapLayerNotReceivableException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.layers.DrawLayer;
import io.github.dagri.GeospatialPDF4J.map.layers.MapLayer;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import lombok.extern.slf4j.Slf4j;

/**
 * This children of the superclass {@link MapLayer} provides support for adding
 * a grid to the map displayed in the PDF-document. The grid can be adjusted in
 * the distance the lines shall be afield, the color of the lines and the
 * strength of the lines.
 * 
 * @author DaGri
 * @since 03.10.2017
 */
@Slf4j
public class GridLayer extends DrawLayer {

	// ATTRIBUTES

	/**
	 * The distance the lines of the grid shall be afield in meters. Standard
	 * value is 100 meters.
	 */
	private double						gridDistance	= 100.0;

	/*
	 * The color of the grid could be managed with a DrawLineStringStyle-object,
	 * too. But at this point it is desired that the user only has the
	 * opportunity to hand over a color and a strength.
	 */
	/**
	 * The {@link Color} the lines of the grid shall be displayed in. Standard
	 * value is Color.GRAY.
	 */
	private Color						gridColor		= Color.GRAY;

	/**
	 * The strength of the grid lines.
	 */
	private float						gridStrenght	= 0.1f;

	/**
	 * The {@link ArrayList} containing the {@link DrawLineString}s that need to
	 * be drawn.
	 */
	private ArrayList<DrawLineString>	lines			= new ArrayList<>();

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link GridLayer} using a
	 * <ul>
	 * <li>o {@link BoundingBox} to define the area to be covered by the
	 * {@link GridLayer}</li>
	 * <li>o a double to define the distance between the lines in meters</li>
	 * <li>o a {@link Color} to define the line color</li>
	 * <li>o a float to define the strength of the lines.</li>
	 * </ul>
	 * 
	 * The {@link Color} may be <code>null</code>: In this case the
	 * standard-value (GRAY) will be used.
	 * 
	 * @param layerBbox
	 *            the {@link BoundingBox} to define the layers area
	 * @param gridDistance
	 *            a {@link Double} to define the distance the lines shall be
	 *            afield
	 * @param gridColor
	 *            a {@link Color} defining the lines shall be colored with
	 * @param gridStrength
	 *            a {@link Float} defining the strength of the lines
	 */
	public GridLayer(BoundingBox layerBbox, double gridDistance, Color gridColor, float gridStrength) {
		super(layerBbox);
		this.setGridDistance(Math.abs(gridDistance));
		if (gridColor != null)
			this.setGridColor(gridColor);
		// ELSE THE STANDARD VALUE WILL BE USED
		this.setGridStrenght(gridStrength);
	}

	// METHODS FROM SUPERCLASS

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.dagri.GeospatialPDF4J.map.layers.DrawLayer#scaleContent(double)
	 */
	@Override
	protected void scaleContent(double factor) {
		log.debug("Scaling the GridLayers content...");
		for (int a = 0; a < this.getLines().size(); a++)
			this.getLines().get(a).scale(factor);
		log.debug("GridLayers content scaled.");
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
		log.debug("Reducing the GridLayers content...");
		for (int a = 0; a < this.getLines().size(); a++)
			this.getLines().get(a).reduce(bbox.getLl().getOrdinate(0), bbox.getLl().getOrdinate(1));
		log.debug("GridLayers content reduced.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.dagri.GeospatialPDF4J.map.layers.DrawLayer#addMarginOffset(
	 * double, double, double, double)
	 */
	@Override
	protected void addMarginOffset(double marginX, double marginY, double offsetX, double offsetY) {
		log.debug("Adding margin offset to the GridLayers content...");
		for (int a = 0; a < this.getLines().size(); a++)
			this.getLines().get(a).reduce(-offsetX - marginX, -offsetY - marginY);
		log.debug("Added margin offset to the GridLayers content.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.DrawLayer#receive()
	 */
	@Override
	public void receive() throws MapLayerNotReceivableException {
		/*
		 * Gaining the corner-coordinates from the GridLayers BoundingBox2D.
		 * These coordinates are used to calculate the start- and end-point of
		 * each line to be drawn.
		 */
		// ORDINATE 0 FROM LL (UTM --> EAST)
		double minE = this.getLayerBBox().getLl().getOrdinate(0);
		// ORDIANTE 1 FROM LL (UTM --> NORTH)
		double minN = this.getLayerBBox().getLl().getOrdinate(1);
		// ORDINATE 0 FROM UR (UTM --> EAST)
		double maxE = this.getLayerBBox().getUr().getOrdinate(0);
		// ORDIANTE 1 FROM UR (UTM --> NORTH)
		double maxN = this.getLayerBBox().getUr().getOrdinate(1);

		CoordinateTransformer t = CoordinateTransformer.getInstance();

		try {
			/*
			 * Running through the GridLayers BoundingBox2D area and calculating
			 * a vertical line that shifts to the right. It is starting at the
			 * left side of the BoundingBox plus the Grids distance.
			 */
			double act = minE + this.getGridDistance();

			while (act < maxE) {
				// CREATING START AND END DIRECTPOSITION2D OF THE DRAWLINE
				DirectPosition2D start = new DirectPosition2D(t.getUtmCrs(), act, minN);
				DirectPosition2D end = new DirectPosition2D(t.getUtmCrs(), act, maxN);
				// CREATE A NEW ARRAYLIST OF DIRECTPOSITION2DS
				ArrayList<DirectPosition2D> coords = new ArrayList<>();
				// ADDING THE DIRECTPOSITION2DS TO THE ARRAYLIST
				coords.add(start);
				coords.add(end);
				// ADDING NEW DRAWLINE TO THE ARRAYLIST OF DRAWLINES USING THE
				// ARRAYLIST OF DIRECTPOSITION2DS
				this.getLines().add(new DrawLineString(coords));

				// COUNTING ON THE ACTUAL POSITION
				act = act + this.getGridDistance();
			}

			/*
			 * Running through the GridLayers BoundingBox2D area and calculating
			 * a horizontal line that shifts to upward. It is starting at the
			 * bottom side of the BoundingBox plus the Grids distance.
			 */
			act = minN + this.getGridDistance();

			while (act < maxN) {
				// CREATING START AND END DIRECTPOSITION2D OF THE DRAWLINE
				DirectPosition2D start = new DirectPosition2D(t.getUtmCrs(), minE, act);
				DirectPosition2D end = new DirectPosition2D(t.getUtmCrs(), maxE, act);

				// CREATE A NEW ARRAYLIST OF DIRECTPOSITION2DS
				ArrayList<DirectPosition2D> coords = new ArrayList<>();
				// ADDING THE DIRECTPOSITION2DS TO THE ARRAYLIST
				coords.add(start);
				coords.add(end);

				// ADDING NEW DRAWLINE TO THE ARRAYLIST OF DRAWLINES USING THE
				// ARRAYLIST OF DIRECTPOSITION2DS
				this.getLines().add(new DrawLineString(coords));

				// COUNTING ON THE ACTUAL POSITION
				act = act + this.getGridDistance();
			}

		} catch (CoordinateTransformException e) {
			log.error("An error occured during the creation of the CRS!");
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
		/*
		 * Some of the following methods do - in fact - the same thing: Adding
		 * an offset to all of the internal content. They are using different
		 * input-types like a BoundingBox2D or pixel-offsets. They are split
		 * into different methods and must be filled in every new class to
		 * provide the working progress an all (and every) contained object.
		 */
		log.debug("Working on the GridLayers content...");
		log.debug("Reducing...");
		this.reduceContent(this.getLayerBBox());
		log.debug("Scaling...");
		this.scaleContent(this.calcScalingFactor());
		log.debug("Adding offset 2 the map...");
		this.setOffsets2Map(this.calcPixelOffsets(getMapBBox(), getLayerBBox()));
		log.debug("Adding offset to the margin...");
		this.addMarginOffset(doc.leftMargin(), doc.bottomMargin(), this.getxOffset2Map(), this.getyOffset2Map());
		log.debug("GridLayers content ready for adding.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.IPdfAddable#addToPdf(com.
	 * lowagie.text.Document, com.lowagie.text.pdf.PdfWriter)
	 */
	@Override
	public void addToPdf(Document doc) {
		log.debug("Creating a partental PDFLayer for the GridLayer; a GridDrawer and drawing the content...");
		PdfLayer overlayer = this.createParentalPdfLayer("Grid-Layer", this.getWriter());
		GridDrawer drawer = new GridDrawer(this.getWriter(), this, this.createTopTreeElement("Grid-Layer"), overlayer);
		drawer.drawAll();
		log.debug("Content drawn.");
	}

	// METHODS

	// GETTERS AND SETTERS

	/**
	 * Returns the distance the lines of the grid shall be afield as
	 * {@link Double} in meters.
	 *
	 * @return the gridDistance as {@link Double} in meters
	 */
	private double getGridDistance() {
		return gridDistance;
	}

	/**
	 * Sets the distance the lines of the grid shall be afield in meters.
	 *
	 * @param gridDistance
	 *            the {@link Double} to set
	 */
	private void setGridDistance(double gridDistance) {
		this.gridDistance = gridDistance;
	}

	/**
	 * Returns the color the lines of the grid shall be displayed as
	 * {@link Color}.
	 *
	 * @return the color of the lines as {@link Color}
	 */
	public Color getGridColor() {
		return gridColor;
	}

	/**
	 * Sets the color of the grid lines.
	 *
	 * @param gridColor
	 *            the {@link Color} to set
	 */
	private void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
	}

	/**
	 * Returns the strength of the grid lines as {@link Float}.
	 *
	 * @return the gridStrenght as {@link Float}
	 */
	public float getGridStrenght() {
		return gridStrenght;
	}

	/**
	 * Sets the strength of the grid lines.
	 *
	 * @param gridStrenght
	 *            the {@link Float} to set
	 */
	private void setGridStrenght(float gridStrenght) {
		this.gridStrenght = gridStrenght;
	}

	/**
	 * Returns the {@link ArrayList} of {@link DrawLineString}s of this
	 * {@link GridLayer}.
	 *
	 * @return the {@link ArrayList} of {@link DrawLineString}s
	 */
	public ArrayList<DrawLineString> getLines() {
		return lines;
	}

	// OTHERS
}
