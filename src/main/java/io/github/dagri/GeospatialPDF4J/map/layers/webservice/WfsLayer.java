package io.github.dagri.GeospatialPDF4J.map.layers.webservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotools.geometry.DirectPosition2D;
import org.jdom2.Element;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfLayer;

import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawLineString;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawPoint;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawPolygon;
import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.MapLayerNotReceivableException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.geo.GeometryContainer;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import io.github.dagri.GeospatialPDF4J.server.ServerTalker;
import lombok.extern.slf4j.Slf4j;

/**
 * Class process WFS-style informations and make them usable in a
 * GeospatialPDF-Document.
 * 
 * @author DaGri
 * @since 16.01.2017
 */
// TODO : Log through the complete class
@Slf4j
public class WfsLayer extends WebserviceLayer {

	// ATTRIBUTES

	/**
	 * The {@link GeometryContainer} used to store the {@link DrawGeometry}s.
	 */
	private GeometryContainer	geoContainer	= new GeometryContainer();

	/**
	 * The style used to display the contained {@link DrawGeometry}s.
	 */
	private WfsStyle			style			= new WfsStyle();

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link WfsLayer} using a {@link String}, containing the
	 * URL to the server to request; a {@link BoundingBox}; a {@link String}
	 * containing the version of the server to request and an {@link ArrayList}
	 * of layers that shall be requested.
	 * 
	 * @param url
	 *            the URL to the server as {@link String}
	 * @param layerBbox
	 *            the {@link BoundingBox} to define the area to request
	 * @param version
	 *            the version of the server to request as {@link String}
	 * @param layers
	 *            the layers to request contained in an {@link ArrayList}
	 * @param style
	 *            the {@link WfsStyle} to be used for this {@link WfsLayer}. May
	 *            be null: In this case a standard-{@link WfsStyle} will be
	 *            generated and used.
	 */
	public WfsLayer(String url, BoundingBox layerBbox, String version, ArrayList<String> layers, WfsStyle style) {
		super(url, layerBbox, version, layers);
		this.setStyles(style);
	}

	// METHODS
	
	/* (non-Javadoc)
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.IPdfAddable#receive()
	 */
	@Override
	public void receive() throws MapLayerNotReceivableException {
		for (int a = 0; a < this.getLayers().size(); a++) {

			// TODO : Check if the method is completed and works
			String request = this.getUrl() + "SERVICE=wfs&" + "REQUEST=GetFeature&" + "VERSION=" + this.getVersion() + "&" + "TYPENAMES=";

			request = request + this.getLayers().get(a) + "&BBOX=" + this.getLayerBBox().getCornersForRequestUTM() + "&SRSNAME=EPSG:25832&";

			log.info("WFS-REQUEST-STRING:" + request);

			// RECEIVE AN INSTANCE OF THE SERVERTALKER
			ServerTalker talker = ServerTalker.getInstance();

			log.debug("Creating the JDOM2-document by a wfs-server-request....");
			org.jdom2.Document doc = talker.wfsRequest(request, this.getLayers());
			log.debug("JDOM2-document created.");

			// CREATE THE DRAWPOINTS FROM THE RECEIVED DOCUMENT
			this.createDrawPoints(doc);
			// CREATE THE DRAWLINESTTRINGS FROM THE RECEIVED DOCUMENT
			this.createDrawLineStrings(doc);
			// CREATE THE DRAWPOLYGONS FROM THE RECEIVED DOCUMENT
			this.createDrawPolygons(doc);
		}

		// GIVE OUT SOME IMFORMATION TO THE CONSOLE
		log.info("NUMBER OF DRAWPOINTS RECEIVED: " + this.getGeoContainer().getDrawPoints().size());
		log.info("NUMBER OF DRAWLINESTRINGS RECEIVED: " + this.getGeoContainer().getDrawLineStrings().size());
		log.info("NUMBER OF DRAWPOLYGONS RECEIVED: " + this.getGeoContainer().getDrawPolygons().size());
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
		this.getGeoContainer().reduceContent(this.getLayerBBox());
		this.getGeoContainer().scaleContent(this.calcScalingFactor());
		this.setOffsets2Map(this.calcPixelOffsets(this.getMapBBox(), this.getLayerBBox()));
		this.getGeoContainer().addMarginOffset(doc.leftMargin(), doc.bottomMargin(), this.getxOffset2Map(), this.getyOffset2Map());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.dagri.GeospatialPDF4J.map.layers.IPdfAddable#addToPdf(com.
	 * lowagie.text.Document, com.lowagie.text.pdf.PdfWriter)
	 */
	@Override
	public void addToPdf(Document doc) throws MapLayerNotReceivableException {
		// this.getGeoContainer().intersect(this.getLayerBBox());

		PdfLayer overlayer = this.createParentalPdfLayer("Wfs-Layer", this.getWriter());
		WfsDrawer drawer = new WfsDrawer(this.getWriter(), this, this.createTopTreeElement("Wfs-Layer"), overlayer);
		drawer.drawAll();
	}

	/**
	 * Crates {@link DrawPolygon}s from the given {@link org.jdom2.Document} and
	 * adds them to the internal stored {@link GeometryContainer}.
	 *
	 * @param doc
	 *            the {@link org.jdom2.Document} to search in
	 * @param wfsStyle
	 *            the {@link WfsStyle} to use for this {@link DrawLineString}s
	 */
	private void createDrawPolygons(org.jdom2.Document doc) {
		// GET THE FIRST ELEMENT OF THE DOCUMENT
		Element elem = doc.getRootElement();

		ArrayList<Element> ergElem0 = findTag(elem, "LinearRing");

		// CONVERT THE VALUE OF THE POS TAGS (CHILDREN OF POINT)
		// INTO GEOPOINTS
		for (int c = 0; c < ergElem0.size(); c++) {

			ArrayList<Element> ergElem1 = findTag(ergElem0.get(c), "posList");

			for (int d = 0; d < ergElem1.size(); d++) {
				ArrayList<DirectPosition2D> dpList = new ArrayList<>();

				String elemText = ergElem1.get(d).getText();

				String[] parts = elemText.split(" ");

				for (int a = 0; a < parts.length - 1; a++) {

					int north = (int) (Double.parseDouble(parts[a + 1]));
					int east = (int) (Double.parseDouble(parts[a]));
					a++;

					try {
						dpList.add(new DirectPosition2D(CoordinateTransformer.getInstance().getUtmCrs(), east, north));
					} catch (CoordinateTransformException e) {
						e.printStackTrace();
					}
				}

				DrawPolygon dls = new DrawPolygon(dpList);
				this.getGeoContainer().addDrawGeometry(dls);
			}
		}
	}

	/**
	 * Crates {@link DrawLineString}s from the given {@link org.jdom2.Document}
	 * and adds them to the internal stored {@link GeometryContainer}.
	 *
	 * @param doc
	 *            the {@link org.jdom2.Document} to search in
	 * @param wfsStyle
	 *            the {@link WfsStyle} to use for this {@link DrawLineString}s
	 */
	private void createDrawLineStrings(org.jdom2.Document doc) {
		// GET THE FIRST ELEMENT OF THE DOCUMENT
		Element elem = doc.getRootElement();

		ArrayList<Element> lineStringElems = findTag(elem, "LineString");

		// CONVERT THE VALUE OF THE POS TAGS (CHILDREN OF POINT)
		// INTO GEOPOINTS
		for (int c = 0; c < lineStringElems.size(); c++) {

			ArrayList<Element> posListElems = findTag(lineStringElems.get(c), "posList");

			for (int d = 0; d < posListElems.size(); d++) {
				ArrayList<DirectPosition2D> dpList = new ArrayList<>();

				String elemText = posListElems.get(d).getText();

				String[] parts = elemText.split(" ");

				for (int a = 0; a < parts.length - 1; a++) {

					// TODO : WORKS ONLY FOR UTM-CRS. IN OTHER CRS THE
					// COORDIANTES MAY NOT BE ORDERED IN SUCH A WAY!
					int north = (int) (Double.parseDouble(parts[a + 1]));
					int east = (int) (Double.parseDouble(parts[a]));
					a++;

					try {
						dpList.add(new DirectPosition2D(CoordinateTransformer.getInstance().getUtmCrs(), east, north));
					} catch (CoordinateTransformException e) {
						e.printStackTrace();
					}
				}

				// CREATE THE NEW DRAWLINESTRING USING THE CREATED ARRAYLIST
				DrawLineString dls = new DrawLineString(dpList);

				// ADD THE CREATED DRAWLINESTRING TO THE GEOMETRYCONTAINER
				this.getGeoContainer().addDrawGeometry(dls);
			}
		}
	}

	/**
	 * TODO
	 *
	 * @param doc
	 * @param wfsStyle
	 */
	private void createDrawPoints(org.jdom2.Document doc) {
		// GET THE FIRST ELEMENT OF THE DOCUMENT
		Element elem = doc.getRootElement();

		ArrayList<Element> ergElem0 = findTag(elem, "Point");

		// CONVERT THE VALUE OF THE POS TAGS (CHILDREN OF POINT)
		// INTO GEOPOINTS
		for (int c = 0; c < ergElem0.size(); c++) {

			ArrayList<Element> ergElem1 = findTag(ergElem0.get(c), "pos");

			for (int d = 0; d < ergElem1.size(); d++) {
				String elemText = ergElem1.get(d).getText();

				String[] parts = elemText.split(" ");

				// TODO : WORKS ONLY FOR UTM CRS. IN OTHER CRS THIS ORDER MAY
				// NOT BE GIVEN
				int east = (int) (Double.parseDouble(parts[1]));
				int north = (int) (Double.parseDouble(parts[0]));

				DrawPoint g;
				try {
					g = new DrawPoint(new DirectPosition2D(CoordinateTransformer.getInstance().getUtmCrs(), east, north));
					this.getGeoContainer().addDrawGeometry(g);
				} catch (CoordinateTransformException e) {
					log.warn("COORDINATE TRANSFORM EXCEPTION ERROR: COULD NOT CREATE DRAWPOINT FROM XML!");
				}
			}
		}
	}

	/**
	 * Recursive method to find {@link Element}s beneath a given root
	 * {@link Element} containing a {@link String}-kind tag. Returns an
	 * {@link ArrayList} of all found {@link Element}s.
	 *
	 * @param root
	 *            the {@link Element} to search below
	 * @param tag
	 *            the {@link String} to search for
	 * @return an {@link ArrayList} of {@link Element}s
	 * @throws IllegalArgumentException
	 */
	public ArrayList<Element> findTag(Element root, String tag) throws IllegalArgumentException {
		// CREATE THE ARRAYLIST OF ELEMENTS TO BE RETURNED
		ArrayList<Element> erg = new ArrayList<>();

		// CHECK FOR EXISTING ROOT ELEMENT
		if (root == null)
			throw new IllegalArgumentException("Element is null.");

		// CREATE A LIST OF ELEMENTS THAT WILL BE FOUND DIRECTLY BELOW THE ROOT
		List<Element> enumChilds = root.getChildren();

		// CREATE A INTERATOR FOR THE CHILDREN
		Iterator<Element> iter = enumChilds.iterator();

		// RUN THROUGH THE ITERATOR
		while (iter.hasNext()) {
			// LOOK AT EVERY CHILD
			Element childElement = (Element) iter.next();
			// IF THE CHILD CONTAINS THE TAG TO SEARCH FOR
			if (childElement.getName().equals(tag)) {
				// ADD THE CHILD TO THE ARRAYLIST TO RETURN
				erg.add(childElement);
			}
			try {
				// LOOK AT EVERY FOUND ELEMENT THAT DOES NOT CONTAIN THE
				// SEARCHTAG DIRECTLY
				ArrayList<Element> foundElement = findTag(childElement, tag);
				// IF THE FOUND ELEMENT IS NOT NULL
				if (foundElement != null) {
					// RUN THROUGH THE FOUND ELEMENTS AND ADD THEM TO THE
					// ARRAYLIST TO RETURN
					for (int a = 0; a < foundElement.size(); a++) {
						erg.add(foundElement.get(a));
					}
				}
				// CATCH UPCOMING ERRORS
			} catch (IllegalArgumentException e) {
				log.error("ERROR: ROOT ELEMENT IS NULL.");
			}
		}
		// RETURN THE SOLUTION
		return erg;
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link GeometryContainer} of this {@link WfsLayer} as
	 * {@link GeometryContainer}.
	 *
	 * @return the {@link GeometryContainer} of this {@link WfsLayer}
	 */
	public GeometryContainer getGeoContainer() {
		return geoContainer;
	}

	/**
	 * Sets the {@link GeometryContainer} of this {@link WfsLayer}.
	 *
	 * @param geoContainer
	 *            the {@link GeometryContainer} to set
	 */
	public void setGeoContainer(GeometryContainer geoContainer) {
		this.geoContainer = geoContainer;
	}

	/**
	 * Returns the {@link WfsStyle} of this {@link WfsLayer} as {@link WfsStyle}
	 * .
	 *
	 * @return the {@link WfsStyle} of this {@link WfsLayer}
	 */
	public WfsStyle getStyles() {
		return style;
	}

	/**
	 * Sets the {@link WfsStyle} of this {@link WfsLayer}.
	 *
	 * @param style
	 *            the {@link WfsStyle} to set
	 */
	public void setStyles(WfsStyle styles) {
		this.style = styles;
	}

	// OTHERS
}
