package io.github.dagri.GeospatialPDF4J.map.layers.grid;

import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfWriter;

import io.github.dagri.GeospatialPDF4J.draw.drawers.PdfDrawer;
import io.github.dagri.GeospatialPDF4J.draw.styles.LineStringStyle;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to extend the abstract parental class {@link PdfDrawer} and draw
 * grid-styled lines.
 * 
 * @author DaGri
 * @since 05.10.2017
 */
@Slf4j
public class GridDrawer extends PdfDrawer {

	// ATTRIBUTES

	/**
	 * The {@link GridLayer} to gain the information from.
	 */
	private GridLayer gridLayer;

	// CONSTRUCTORS FROM SUPERCLASS

	/**
	 * Constructor for a {@link GridDrawer} using
	 * <ul>
	 * <li>a {@link PdfWriter} to draw the content</li>
	 * <li>a {@link GridLayer} to gain the information from</li>
	 * <li>a {@link PdfStructureElement} to add the information below</li>
	 * <li>and a {@link PdfLayer} to draw inside.</li>
	 * </ul>
	 * 
	 * @param writer
	 *            the {@link PdfWriter} used to draw
	 * @param gridLayer
	 *            the {@link GridLayer} to gain the information from
	 * @param top
	 *            the {@link PdfStructureElement} to add the information below
	 * @param parentalLayer
	 *            the {@link PdfLayer} to draw inside
	 */
	public GridDrawer(PdfWriter writer, GridLayer gridLayer, PdfStructureElement top, PdfLayer parentalLayer) {
		super(writer, top, parentalLayer);
		/*
		 * If the given GridLayer is null the drawAll-Method will catch this.
		 */
		this.setGridLayer(gridLayer);
	}

	// INHERITED METHODS

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.drawers.PdfDrawer#drawAll()
	 */
	@Override
	public void drawAll() {
		// IF THE GRID LAYER IS NOT NULL
		if (this.getGridLayer() != null) {
			this.getContByte().beginLayer(this.getParentalLayer());

			// CREATE A NEW LINESTRINGSTYLE AND SET THE ATTRIBUTE VALUES TO THE
			// GRIDLAYER-ONES
			LineStringStyle gridStyle = new LineStringStyle();
			gridStyle.lineStringColor = this.getGridLayer().getGridColor();
			gridStyle.lineStringStrength = this.getGridLayer().getGridStrenght();

			log.debug("Drawing the Grid-lines...");
			for (int a = 0; a < this.getGridLayer().getLines().size(); a++) {
				this.drawLineString(this.getGridLayer().getLines().get(a), gridStyle);
			}
			log.debug("Grid-lines drawn.");

			this.getContByte().endLayer();
		} else
			log.warn("GRIDLAYER IS NULL! NO GRID TO BE DRAWN!");
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link GridLayer} of this {@link GridDrawer} as
	 * {@link GridLayer}.
	 *
	 * @return the {@link GridLayer}
	 */
	private GridLayer getGridLayer() {
		return gridLayer;
	}

	/**
	 * Sets the {@link GridLayer} of this {@link GridDrawer}.
	 *
	 * @param gridLayer
	 *            the {@link GridLayer} to set
	 */
	private void setGridLayer(GridLayer gridLayer) {
		this.gridLayer = gridLayer;
	}

	// OTHERS
}
