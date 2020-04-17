package io.github.dagri.GeospatialPDF4J.map.layers;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

import io.github.dagri.GeospatialPDF4J.exceptions.MapLayerNotReceivableException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.map.Map;

/**
 * Interface to be used by the abstract parental map layer classes to force some
 * methods.
 * 
 * Every method defined by this interface may throw an
 * {@link MapLayerNotReceivableException}, because depending on the layer a
 * fatal error can occur in each method that can prevent the {@link MapLayer}
 * from being completely addable.
 * 
 * @author DaGri
 * @since 05.02.2017
 */
public interface IPdfAddable {

	/**
	 * In this method all content of a {@link MapLayer} shall be received in its
	 * original CRS. In the following method-call 'prepare for adding' the
	 * received objects shall be transformed into the local system of the
	 * PDF-page.
	 * 
	 * If you use this interface for a {@link MapLayer} typically the
	 * {@link BoundingBox} of the {@link Map} is present in the {@link MapLayer}
	 * , as well as the inches the {@link MapLayer} covers.
	 */
	public abstract void receive() throws MapLayerNotReceivableException;

	/**
	 * This method will be called after the 'receive' method and before the
	 * 'addToPdf(...)'-method. Use this method to transform the objects you
	 * received from their origin-CRS to the PDF-page internal
	 * coordinate-system.
	 * 
	 * If you use this interface for a {@link MapLayer} typically the
	 * {@link BoundingBox} of the {@link Map} is present in the {@link MapLayer}
	 * , as well as the inches the {@link MapLayer} covers.
	 * 
	 * @throws MapLayerNotReceivableException
	 */
	public abstract void prepareForAdding(Document doc) throws MapLayerNotReceivableException;

	/**
	 * Adds the {@link MapLayer} to the PDF-document using the internal stored
	 * informations and the {@link PdfWriter} of the document.
	 * 
	 * If you use this interface for a {@link MapLayer} typically the
	 * {@link BoundingBox} of the {@link Map} is present in the {@link MapLayer}
	 * , as well as the inches the {@link MapLayer} covers.
	 *
	 * @throws MapLayerNotReceivableException
	 */
	public abstract void addToPdf(Document doc) throws MapLayerNotReceivableException;

}
