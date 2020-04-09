package io.github.dagri.GeospatialPDF4J.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import io.github.dagri.GeospatialPDF4J.exceptions.CapabilitiesRequestException;
import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.res.ImageHandler;
import io.github.dagri.GeospatialPDF4J.res.Tile;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to provide various methods to interact with a server.
 * 
 * @author DaGri
 * @since 18.01.2017
 */
@Slf4j
public class ServerTalker {

	// ATTRIBUTES

	/**
	 * An instance of a {@link ServerTalker} according to the singleton pattern.
	 */
	private static ServerTalker	instance;

	/**
	 * The capabilities, received from a server and put into a JDOM2-
	 * {@link Document}.
	 */
	private Document			capabilities;

	/**
	 * The JDOM2 root-{@link Element} of the capabilities document.
	 */
	private Element				capabilitiesRootElement;

	/**
	 * An {@link ArrayList} of {@link String}s containing the existing layers on
	 * a server.
	 */
	private ArrayList<String>	existingLayers				= new ArrayList<>();

	/**
	 * An {@link ArrayList} of {@link String}s containing the existing versions
	 * on a server.
	 */
	private ArrayList<String>	existingVersions			= new ArrayList<>();

	/**
	 * An {@link ArrayList} of {@link String}s containing the existing
	 * EPSG-codes supported by the server.
	 */
	private ArrayList<String>	existingEpsgCodes			= new ArrayList<>();

	/**
	 * The maximum resolution to be requested in a single tile (in X- and/or Y-
	 * direction).
	 */
	private int					maxResolution				= 1000;

	/**
	 * Boolean that indicates if any received images shall be written to the
	 * file system (output folder).
	 */
	private boolean				writeImagesToFileSystem		= false;

	/**
	 * Boolean that indicates if any created request string shall be written out
	 * in the console.
	 */
	private boolean				writeRequestStringToConsole	= true;

	// CONSTRUCTORS

	/**
	 * Private empty constructor, according to the singleton pattern.
	 */
	private ServerTalker() {
		// NOTHING
	}

	// METHODS

	/**
	 * Returns an instance of a {@link ServerTalker} according to the singleton
	 * pattern.
	 *
	 * @return an instance of a {@link ServerTalker}
	 */
	public static ServerTalker getInstance() {
		if (instance == null)
			instance = new ServerTalker();
		return instance;
	}

	/**
	 * Method to convert a {@link String} to a {@link URL}.
	 * 
	 * @param s
	 *            the {@link String} to convert
	 * @return the coverted {@link URL}
	 */
	public URL toURL(String s) {
		URL erg;
		try {
			erg = new URL(s);
			return erg;
		} catch (MalformedURLException e) {
			log.error("Could not create URL!");
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * LAYER AUS DER XML STRUKTUR ZIEHEN UNTERSTUETZTE EPSGS AUS DER STRUKTUR
	 * ZIEHEN MAXIMALE AUFLOESUNG AUS DER XML ZIEHEN VERSIONEN RAUS ZIEHEN
	 */

	/**
	 * Extracts the possible receivable layer names and adds them to the
	 * internal {@link ArrayList} 'existingLayers' of this {@link ServerTalker}.
	 */
	private void extractLayers() {
		// EXTRACT THE LAYERS BELOW THE ROOT ELEMENT
		ArrayList<Element> layers = this.findTag(this.getCapabilitiesRootElement(), "Layer");
		// RUN THROUGHT THE EXTRACTED LAYERS ARRAYLIST
		for (int a = 0; a < layers.size(); a++) {
			// EXTRACT THE TAGS CONTAINING 'NAME'
			ArrayList<Element> names = this.findTag(layers.get(a), "Name");
			// RUN THROUGHT THE EXTRACTED NAME TAGS
			for (int b = 0; b < names.size(); b++) {
				// IF THE TEXT OF THE TAG IS NOT 'DEFAULT'
				if (!names.get(b).getText().equalsIgnoreCase("default")) {
					// CHECK IF THE EXTRACTED LAYER IS ALREADY IN THE EXISTING
					// LAYERS ARRAYLIST
					boolean included = false;
					for (int c = 0; c < this.getExistingLayers().size(); c++) {
						if (this.getExistingLayers().get(c).equalsIgnoreCase(names.get(b).getText()))
							included = true;
					}
					// IF IT IS NOT INCLUDED ADD IT TO THE ARRAYLIST
					if (!included)
						this.getExistingLayers().add(names.get(b).getText());
				}
			}
		}
	}

	/**
	 * Extracts the possible versions and adds them to the internal
	 * {@link ArrayList} 'existingVersions' of this {@link ServerTalker}.
	 */
	private void extractVersions() {
		// System.out.println("DEBUG BREAKPOINT");
		// TODO : SEEMS TO BE IMPOSSIBLE FROM A GETCAPABILITIES REQUEST
	}

	/**
	 * Extracts the possible EPSG codes and adds them to the internal
	 * {@link ArrayList} 'existingEpsgVersions' of this {@link ServerTalker}.
	 */
	private void extractEpsgs() {
		// System.out.println("DEBUG BREAKPOINT");
		// TODO : SEEMS TO BE IMPOSSIBLE FROM A GETCAPABILITIES REQUEST
	}

	/**
	 * Tries to extract the maximal allowed resolution from the requested server
	 * to store it in the 'maxResolution' of this class.
	 */
	private void extractMaxResolution() {
		ArrayList<Element> maxWidth = this.findTag(this.getCapabilitiesRootElement(), "maxwidth");

		// TRY TO RECEIVE THE WIDTH
		int maxW = this.getMaxResolution();
		if (maxWidth.size() > 0)
			try {
				maxW = Integer.parseInt(maxWidth.get(0).getText());
			} catch (NumberFormatException e) {
				// CATCHED BY NOT SETTING THE VALUE
			}
		ArrayList<Element> maxHeight = this.findTag(this.getCapabilitiesRootElement(), "maxheight");

		// TRY TO RECEIVE THE HEIGHT
		int maxH = this.getMaxResolution();
		if (maxHeight.size() > 0) {
			try {
				maxH = Integer.parseInt(maxHeight.get(0).getText());
			} catch (NumberFormatException e) {
				// CATCHED BY NOT SETTING THE VALUE
			}
		}

		// CHECK IF ONE OF THE VALUES DIFFERS FROM THE PRESET MAX RESOLUTION: IF
		// IT IS SO, SET THE MAXIMUM RESOLUTION TO THE NEW VALUE
		if (maxW != this.getMaxResolution()) {
			this.setMaxResolution(maxW);
		} else if (maxH != this.getMaxResolution()) {
			this.setMaxResolution(maxH);
		}
	}

	/**
	 * Method to receive the capabilities of the server.
	 *
	 * @param link
	 * @throws CapabilitiesRequestException
	 *             if an error occurs
	 */
	// TODO : WENN SERVER NICHT ERREICHBAR, DANN GGF. ANDERE FEHLERMELDUNG
	// WERFEN? SIEHE HIER METHODE serverReachable : boolean
	public void receiveCapabilities(String link) throws CapabilitiesRequestException {
		try {
			link = link + "REQUEST=GETCAPABILITIES";
			URL url = this.toURL(link);
			// this.toTextFile(url);
			this.setCapabilities(new SAXBuilder().build(url));
			this.setCapabilitiesRootElement(this.getCapabilities().getRootElement());
			// FILL INTERNAL STRUCTURES
			this.extractLayers();
			this.extractVersions();
			this.extractEpsgs();
			this.extractMaxResolution();
		} catch (JDOMException | IOException e) {
			log.error(e.getMessage().toString());
			log.error("Could not receive the capabilities from the given server!");
			throw new CapabilitiesRequestException();
		}
	}

	/**
	 * TODO : BESCHREIBUNG FEHLT
	 *
	 * @param link
	 * @return
	 */
	public boolean serverReachable(String link) {
		// TODO : FILL WITH CONTENT
		// TODO : TEST THE METHOD
		return false;
	}

	/**
	 * Empties all internal structures to save heap space.
	 */
	public void flush() {
		// TODO : ALLE INTERNEN STRUKTUREN LEEREN
	}

	/**
	 * Receives the response from a server by an given {@link URL} and writes it
	 * down as .txt in the output folder.
	 *
	 * @param link
	 *            the {@link URL} to receive from
	 */
	protected void toTextFile(URL link) {
		// CREATE A TXT TO WRITE THE DOWNLOADED XML TO
		File f = new File("output/" + System.currentTimeMillis() + ".txt");

		// CREATE A BYTE CHANNEL TO READ THE STUFF
		ReadableByteChannel rbc;
		try {
			// OPEN THE STREAM OF THE LINK
			rbc = Channels.newChannel(link.openStream());

			// CREATE A FILEOUTPUT STREAM TO WRITE TO THE TXT
			FileOutputStream fos = new FileOutputStream(f);

			//
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

			//
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to find a tag (as {@link String}) in a XML document, under the
	 * root {@link Element} "root".
	 * 
	 * https://www.die-informatiker.net/topic/Fachsimpelei/
	 * JDOM_Element_durchsuchen/6522
	 * 
	 * @author H009334
	 * @since 30.10.2015
	 * @version 0.3
	 * @param root
	 *            : the root {@link Element} of the XML file
	 * @param tag
	 *            : the tag to look for
	 * @return erg : {@link ArrayList} of {@link Element}s with the desired tag
	 * @throws IllegalArgumentException
	 */
	public ArrayList<Element> findTag(Element root, String tag) throws IllegalArgumentException {
		ArrayList<Element> erg = new ArrayList<>();

		if (root == null)
			throw new IllegalArgumentException("Element is null.");
		List<Element> enumChilds = root.getChildren();
		Iterator<Element> iter = enumChilds.iterator();
		while (iter.hasNext()) {
			Element childElement = (Element) iter.next();
			if (childElement.getName().equals(tag)) {
				erg.add(childElement);
			}
			try {
				ArrayList<Element> foundElement = findTag(childElement, tag);
				if (foundElement != null) {
					for (int a = 0; a < foundElement.size(); a++) {
						erg.add(foundElement.get(a));
					}
				}
			} catch (IllegalArgumentException e) {
			}
		}
		return erg;
	}

	/**
	 * Checks if the {@link ArrayList} of supported layer-names contains the
	 * given layer-name.
	 *
	 * @param layerName
	 *            the name to check
	 * @return <code>true</code> if it is contained, <code>false</code> if not
	 */
	public boolean layerExists(String layerName) {
		for (int a = 0; a < this.getExistingLayers().size(); a++) {
			if (this.getExistingLayers().get(a).equalsIgnoreCase(layerName))
				return true;
		}
		return false;
	}

	/**
	 * Checks if the {@link ArrayList} of supported EPSG-codes contains the
	 * given EPSG-code.
	 *
	 * @param epsgCode
	 *            the EPSG code to check
	 * @return <code>true</code> if it is contained, <code>false</code> if not
	 */
	public boolean epsgSupport(String epsgCode) {
		for (int a = 0; a < this.getExistingLayers().size(); a++) {
			if (this.getExistingEpsgCodes().get(a).equalsIgnoreCase(epsgCode))
				return true;
		}
		return false;
	}

	// TODO : ABGLEICH
	// TODO : KOMMENTIEREN
	/**
	 * Starts a 'getMap' request to a WMS-server.
	 * 
	 * The {@link BoundingBox} used to request the image and the width and the
	 * height are gained from the given {@link Tile}.
	 * 
	 * The other needed values are the link to the server, the version to
	 * request, the layer to request, the style of the layer to request and the
	 * opacity.
	 *
	 * @param t
	 *            the {@link Tile}
	 * @param url
	 *            the URL as {@link String}
	 * @param version
	 *            the server-version to request as {@link String}
	 * @param layer
	 *            the layer to request as {@link String}
	 * @param style
	 *            the style to request as {@link String}
	 * @param opacities
	 *            the opacity to request as {@link Integer}
	 * @return the received {@link BufferedImage}
	 */
	public BufferedImage tileImageRequest(Tile t, String url, String version, String layer, String style, int opacities) {

		// THE RESULT BUFFERED IMAGE
		BufferedImage erg = null;

		// THE MAXIMUM AMOUNT OF RETRIES
		int maxRequestTries = 2;

		// STRINGS TO BE USED TO CREATE THE REQUEST-STRING
		String and = "&";
		String request = "REQUEST=GETMAP";
		String versionPart = "VERSION=";
		String width = "WIDTH=";
		String height = "HEIGHT=";
		String format = "FORMAT=image/png";
		String bbox = "BBOX=";
		String crs = "CRS=";
		String layers = "LAYERS=";
		String transparent = "TRANSPARENT=TRUE";
		String styles = "styles=";

		// CREATE THE REQUEST-STRING
		String requestString = "" + url + request + and + versionPart + version + and + width + t.getImageWidth() + and + height + t.getImageHeight() + and + format + and + styles + style + and
				+ transparent
				// TODO : GEHT NUR FUER UTM
				// TODO : OPACITIES RICHTIG UEBERGEBEN?
				+ and + bbox + t.getTileBBox().getCornersForRequestUTM() + and + layers + layer + and + crs + "EPSG:25832";

		// MAYBE PRINT THE REQUEST STRING FOR DEBUGGING REASONS
		if (this.isWriteRequestStringToConsole())
			System.out.println("REQUEST STRING = " + requestString);

		while (t.getRequestTry() <= maxRequestTries && erg == null) {
			t.countTriesUp();
			try {
				erg = ImageIO.read(new URL(requestString));
			} catch (IOException e) {
				log.error("Requesting try " + t.getRequestTry() + " failed.");
				e.printStackTrace();
			}
		}

		// ABFRAGE GESCHEITERT : LEERES BILD ERSTELLEN UND ZURUCKGEBEN
		if (erg == null && t!=null&&t.getImageWidth()!=0&&t.getImageHeight()!=0) {
			log.error("Could not receive Tile image from the server!");
			log.debug("Creating an empty image to add the received image to...");
//			log.info(""+t.getImageWidth()+", "+t.getImageHeight());
//			ImageHandler imgH = ImageHandler.getInstance();
//			erg = imgH.createEmptyImage(t.getImageWidth(), t.getImageHeight());
			erg = new BufferedImage(t.getImageWidth(), t.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
			log.debug("Created empty image.");
		} 

		// MAY WRITE THE IMAGES TO THE FILE SYSTEM, IF DESIRED
		if (this.isWriteImagesToFileSystem()) {
			log.info("Writing image to the file system...");
			ImageHandler.getInstance().writeToFileSystem(erg);
			log.info("Image written into output folder.");
		}

		// float transF = (float)opacities/255.0f;
		// System.out.println("OPACITIE GIVEN: " + opacities);
		// System.out.println("TRANSPARENCY:" + transF);
		//
		// try {
		// erg=Thumbnails.of(erg).size(erg.getWidth(),
		// erg.getHeight()).watermark(Positions.BOTTOM_LEFT, erg,
		// transF).asBufferedImage();
		// } catch (IOException e) {
		// log.warn("Could not add transparency to the image!");
		// }

		return erg;
	}

	/**
	 * Starts a WFS-request to a server adressed by a link and tries to download the given layers from it. 
	 *
	 * @param link
	 * @param wfsLayers
	 * @return
	 */
	public Document wfsRequest(String link, ArrayList<String> wfsLayers) {
		System.out.println("### WFS REQUEST STARTING ###");
		this.downloadXmlStructureToFile(link, "output/tempXML.xml");
		System.out.println("### FILE WRITTEN ###");
		System.out.println("### WFS REQUEST ENDED ###");

		Document doc = new Document();

		try {

			// LINK THE DOCUMENT ELEMENT TO THE FILE PATH GIVEN
			return doc = new SAXBuilder().build("output/tempXML.xml");

		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void downloadXmlStructureToFile(String link, String path) {
		// CREATE A TXT TO WRITE THE DOWNLOADED XML TO
		File f = new File(path);

		// CREATE A BYTE CHANNEL TO READ THE STUFF
		ReadableByteChannel rbc;
		try {
			// OPEN THE STREAM OF THE LINK
			URL url = new URL(link);
			rbc = Channels.newChannel(url.openStream());

			// CREATE A FILEOUTPUT STREAM TO WRITE TO THE TXT
			FileOutputStream fos = new FileOutputStream(f);

			//
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

			//
			fos.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deleteFile(String path) {
		File f = new File(path);
		boolean deleted = f.delete();
		if (deleted)
			log.info("File deleted successfully!");
		else
			log.error("File to delete could not be found!");
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the capabilities received from a server by this
	 * {@link ServerTalker} as {@link Document}.
	 *
	 * @return the capabilities as {@link Document}
	 */
	public Document getCapabilities() {
		return capabilities;
	}

	/**
	 * Sets the capabilities received from a server by this {@link ServerTalker}
	 * .
	 *
	 * @param capabilities
	 *            the {@link Document} to set
	 */
	private void setCapabilities(Document capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * Returns the root element of the capabilities XML as {@link Element}.
	 *
	 * @return the capabilitiesRootElement
	 */
	private Element getCapabilitiesRootElement() {
		return capabilitiesRootElement;
	}

	/**
	 * Sets the root element of the capabilities XML.
	 *
	 * @param capabilitiesRootElement
	 *            the {@link Element} to set
	 */
	private void setCapabilitiesRootElement(Element capabilitiesRootElement) {
		this.capabilitiesRootElement = capabilitiesRootElement;
	}

	/**
	 * Returns the existing layers on the server as {@link ArrayList} of
	 * {@link String}s.
	 *
	 * @return the existingLayers
	 */
	public ArrayList<String> getExistingLayers() {
		return existingLayers;
	}

	// /**
	// * Sets the existing layers that can be requested.
	// *
	// * @param existingLayers
	// * the existingLayers to set
	// */
	// private void setExistingLayers(ArrayList<String> existingLayers) {
	// this.existingLayers = existingLayers;
	// }

	/**
	 * Returns the possible server versions to request as {@link ArrayList} of
	 * {@link String}s.
	 * 
	 * @return the possible server versions as {@link ArrayList} of
	 *         {@link String}s
	 */
	public ArrayList<String> getExistingVersions() {
		return existingVersions;
	}

	// /**
	// * Sets the possible server versions to request.
	// *
	// * @param existingVersions
	// * the {@link ArrayList} of {@link Strings}s to set
	// */
	// private void setExistingVersions(ArrayList<String> existingVersions) {
	// this.existingVersions = existingVersions;
	// }

	/**
	 * Returns the possible EPSG codes, delivered by the server, as
	 * {@link ArrayList} of strings.
	 *
	 * @return the existingEpsgCodes as {@link ArrayList} of {@link String}s
	 */
	public ArrayList<String> getExistingEpsgCodes() {
		return existingEpsgCodes;
	}

	/**
	 * Returns the maximal possible resolution to request from a WMS-server as
	 * {@link Integer}
	 *
	 * @return the maximum request resolution as {@link Integer}
	 */
	public int getMaxResolution() {
		return maxResolution;
	}

	/**
	 * Sets the maximal possible resolution to request from a WMS-sever.
	 *
	 * @param maxResolution
	 *            the maximum resolution to set
	 */
	public void setMaxResolution(int maxResolution) {
		this.maxResolution = maxResolution;
	}

	/**
	 * Returns the {@link Boolean} that indicates if the requested images shall
	 * be written into the output folder.
	 *
	 * @return the writeImagesToFileSystem as {@link Boolean}
	 */
	public boolean isWriteImagesToFileSystem() {
		return writeImagesToFileSystem;
	}

	/**
	 * Sets the {@link Boolean} that indicates if the requested images shall be
	 * written into the output folder.
	 *
	 * @param writeImagesToFileSystem
	 *            the {@link Boolean} to set
	 */
	public void setWriteImagesToFileSystem(boolean writeImagesToFileSystem) {
		this.writeImagesToFileSystem = writeImagesToFileSystem;
	}

	/**
	 * Returns the {@link Boolean} that indicates if the request strings shall
	 * be written into the console.
	 *
	 * @return the writeRequestStringToConsole as {@link Boolean}
	 */
	public boolean isWriteRequestStringToConsole() {
		return writeRequestStringToConsole;
	}

	/**
	 * Sets the {@link Boolean} that indicates if the request strings shall be
	 * written into the console.
	 *
	 * @param writeRequestStringToConsole
	 *            the {@link Boolean} to set
	 */
	public void setWriteRequestStringToConsole(boolean writeRequestStringToConsole) {
		this.writeRequestStringToConsole = writeRequestStringToConsole;
	}

	// /**
	// * Sets the EPSG codes delivered by the server.
	// *
	// * @param existingEpsgCodes
	// * the {@link ArrayList} of {@link String}s to set
	// */
	// private void setExistingEpsgCodes(ArrayList<String> existingEpsgCodes) {
	// this.existingEpsgCodes = existingEpsgCodes;
	// }

	// OTHERS
}
