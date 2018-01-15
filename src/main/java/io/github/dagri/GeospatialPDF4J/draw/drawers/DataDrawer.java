package io.github.dagri.GeospatialPDF4J.draw.drawers;

import java.util.ArrayList;

import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfWriter;

import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry;
import io.github.dagri.GeospatialPDF4J.geo.GeometryContainer;

/**
 * Abstract class that extends the abstract parental class {@link PdfDrawer}
 * with an {@link GeometryContainer} that shall be used to store
 * {@link DrawGeometry}s inside to provide intersections and other features.
 * 
 * @author DaGri
 * @since 22.07.2017
 */
public abstract class DataDrawer extends PdfDrawer {

	// ATTRIBUTES

	/**
	 * The {@link GeometryContainer} used by this {@link DataDrawer}.
	 */
	private GeometryContainer container = new GeometryContainer();

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link DataDrawer} using a {@link PdfWriter}, a
	 * {@link PdfStructureElement} and an parental {@link PdfLayer} to draw
	 * below.
	 * 
	 * @param writer the {@link PdfWriter} to use
	 * @param top the top element to structure below
	 * @param parentalLayer the {@link PdfLayer} to draw below
	 */
	public DataDrawer(PdfWriter writer, PdfStructureElement top, PdfLayer parentalLayer) {
		super(writer, top, parentalLayer);
	}

	// INHERITED METHODS

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.draw.drawers.PdfDrawer#drawAll()
	 */
	@Override
	public abstract void drawAll();

	// METHODS

	/**
	 * Adds a {@link DrawGeometry} to the internal {@link GeometryContainer}.
	 *
	 * @param g the {@link DrawGeometry} to add
	 */
	public void addToContainer(DrawGeometry g) {
		this.getContainer().addDrawGeometry(g);
	}

	/**
	 * Adds an {@link ArrayList} of {@link DrawGeometry}s to the internal {@link GeometryContainer}.
	 *
	 * @param g the {@link ArrayList} of {@link DrawGeometry}s to add
	 */
	public void addToContainer(ArrayList<DrawGeometry> g) {
		for (int a = 0; a < g.size(); a++)
			this.getContainer().addDrawGeometry(g.get(a));
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link GeometryContainer} of this {@link DataDrawer}.
	 *
	 * @return the {@link GeometryContainer} of this {@link DataDrawer}
	 */
	public GeometryContainer getContainer() {
		return container;
	}

	/**
	 * Sets the {@link GeometryContainer} of this {@link DataDrawer}.
	 *
	 * @param container
	 *            the {@link GeometryContainer} to set
	 */
	protected void setContainer(GeometryContainer container) {
		this.container = container;
	}

	// OTHERS
}
