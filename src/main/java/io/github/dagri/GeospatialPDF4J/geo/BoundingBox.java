package io.github.dagri.GeospatialPDF4J.geo;

import java.util.ArrayList;

import org.geotools.geometry.DirectPosition2D;

import io.github.dagri.GeospatialPDF4J.exceptions.BoundingboxNotCreatableException;
import io.github.dagri.GeospatialPDF4J.exceptions.ChangedCoordinateOrderException;
import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.ImpossibleCoordinateOrderException;
import io.github.dagri.GeospatialPDF4J.exceptions.MissingCrsException;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import io.github.dagri.GeospatialPDF4J.res.GeoCalculator;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to represent a bounding-box in a two dimensional room.
 * 
 * This class can be used to define an area to be requested from a server, to
 * display it inside a PDF document.
 * 
 * @author DaGri
 * @since 10.01.2017
 */
@Slf4j
public class BoundingBox {

	// ATTRIBUTES

	/**
	 * The lower left {@link DirectPosition2D} of this {@link BoundingBox}
	 */
	private DirectPosition2D	ll;

	/**
	 * The upper right {@link DirectPosition2D} of this {@link BoundingBox}.
	 */
	private DirectPosition2D	ur;

	/**
	 * The upper left {@link DirectPosition2D} of this {@link BoundingBox}.
	 */
	private DirectPosition2D	ul;

	/**
	 * The lower right {@link DirectPosition2D} of this {@link BoundingBox}.
	 */
	private DirectPosition2D	lr;

	/**
	 * The centered {@link DirectPosition2D} of this {@link BoundingBox}.
	 */
	private DirectPosition2D	center;

	/**
	 * The geographical width of this {@link BoundingBox} in meters.
	 */
	private double				geoWidth;

	/**
	 * The geographical height of this {@link BoundingBox} in meters.
	 */
	private double				geoHeight;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link BoundingBox} using two {@link DirectPosition2D}
	 * s: The lower left and the upper right.
	 * 
	 * Throws {@link BoundingboxNotCreatableException} if the
	 * {@link BoundingBox} could not be created.
	 * 
	 * @param ll
	 *            the lower left {@link DirectPosition2D}
	 * @param ur
	 *            the upper right {@link DirectPosition2D}
	 * @throws BoundingboxNotCreatableException
	 */
	public BoundingBox(DirectPosition2D ll, DirectPosition2D ur) throws BoundingboxNotCreatableException {
		log.debug("Creating a new BoundingBox2D from two DirectPosition2Ds...");
		log.debug("Checking the CRS of the Coordinate2Ds...");
		CoordinateTransformer t = CoordinateTransformer.getInstance();
		try {
			log.debug("Looking at the lower left DirectPosition...");
			if (ll.getCoordinateReferenceSystem() != t.getUtmCrs())
				t.transform(ll, 25832);
			log.debug("Looking at the upper right DirectPosition...");
			if (ur.getCoordinateReferenceSystem() != t.getUtmCrs())
				t.transform(ur, 25832);
		} catch (CoordinateTransformException e) {
			log.error(e.getMessage());
			throw new BoundingboxNotCreatableException();
		}
		log.debug("DirectPosition2Ds transformed to internal used UTM-CRS!");

		try {
			log.debug("Checking the coordinate order...");
			this.checkCoordinateOrder(ll, ur);
		} catch (ImpossibleCoordinateOrderException e) {
			log.error(e.getMessage().toString());
			throw new BoundingboxNotCreatableException();
		} catch (ChangedCoordinateOrderException e) {
			log.debug("Coordinates need to be exchanged...");
			DirectPosition2D temp = ll;
			ll = ur;
			ur = temp;
			log.debug("Coordinates exchanged. Checking again...");
			try {
				this.checkCoordinateOrder(ll, ur);
			} catch (ImpossibleCoordinateOrderException | ChangedCoordinateOrderException e1) {
				log.error(e.getMessage().toString());
				throw new BoundingboxNotCreatableException();
			}
		}
		log.debug("Coordinate order checked.");

		log.debug("Setting lower left Coordinate2D");
		this.setLl(ll);
		log.debug("Setting upper right Coordinate2D");
		this.setUr(ur);

		log.debug("Calculating other values...");
		try {
			this.calcValues();
		} catch (CoordinateTransformException | MissingCrsException e) {
			log.error(e.getMessage());
			throw new BoundingboxNotCreatableException();
		}
		log.debug("BoundingBox created.");
	}

	// METHODS

	/**
	 * Sets the lower right and the upper left {@link DirectPosition2D},
	 * calculates the centered {@link DirectPosition2D}, and calculates the
	 * geographical width and height by calling the internal methods
	 * 'calcCenterCoordinate()' and 'calcgeoWidthAndHeight()'.
	 * 
	 * @throws CoordinateTransformException
	 * @throws MissingCrsException
	 */
	private void calcValues() throws CoordinateTransformException, MissingCrsException {
		log.debug("Setting lower right and upper left Coordinate2D...");
		this.setLrAndUl();
		log.debug("Calculating width and height in meters...");
		this.calcWidthAndHeight();
		log.debug("Calculating centered Coordinate2D...");
		this.calcCenterCoordinate();
	}

	/**
	 * Calculates the values for the lower right and the upper left
	 * {@link DirectPosition2D} of this {@link BoundingBox}.
	 */
	private void setLrAndUl() {
		log.debug("Setting lower right DirectPosition2D...");
		this.setLr(new DirectPosition2D(
				// CRS (UTM)
				this.getLl().getCoordinateReferenceSystem(),
				// ORDINATE 0 FROM UR (UTM --> EAST)
				this.getUr().getOrdinate(0),
				// ORDIANTE 1 FROM LL (UTM --> NORTH
				this.getLl().getOrdinate(1)));
		log.debug("Setting upper left DirectPosition2D...");
		this.setUl(new DirectPosition2D(this.getLl().getCoordinateReferenceSystem(), this.getLl().getOrdinate(0), this.getUr().getOrdinate(1)));

		log.debug("Lower right and upper left Coordinate2D set.");
	}

	/**
	 * Checks the order of the given {@link DirectPosition2D}s.
	 * 
	 * The initial {@link DirectPosition2D}s have to be in a specific geometry:
	 * The lower left {@link DirectPosition2D} has to lay below and to the left
	 * against the upper right one.
	 * 
	 * Throws {@link ImpossibleCoordinateOrderException} if the geometry of the
	 * {@link DirectPosition2D}s is impossible, even if they would be exchanged
	 * with one another. Throws {@link ChangedCoordinateOrderException} if the
	 * geometry of the {@link DirectPosition2D}s is possible, if they would be
	 * exchanged with one another.
	 *
	 * @param ll
	 *            the lower left {@link DirectPosition2D}
	 * @param ur
	 *            the upper right {@link DirectPosition2D}
	 * @throws ImpossibleCoordinateOrderException
	 * @throws ChangedCoordinateOrderException
	 */
	private void checkCoordinateOrder(DirectPosition2D ll, DirectPosition2D ur) throws ImpossibleCoordinateOrderException, ChangedCoordinateOrderException {
		// ALL DIRECTPOSITIONS ARE IN UTM SYSTEM ALREADY
		// ORDINATE 0 --> EAST VALUE
		// ORDINATE 1 --> NORTH VALUE
		if (ll.getOrdinate(1) < ur.getOrdinate(1) && ll.getOrdinate(0) < ur.getOrdinate(0)) {
			log.debug("Coordinate2D order okay.");
		}
		// ELSE IF THEY ARE EXCANGED: LL ABOVE RIGHT UR
		else if (ll.getOrdinate(1) > ur.getOrdinate(1) && ll.getOrdinate(0) > ur.getOrdinate(0)) {
			log.error("Coordinate2D order exchanged. ");
			throw new ChangedCoordinateOrderException();
		}
		// ELSE THROW EXCEPTION
		else {
			log.error("Coordinate2D order impossible!");
			throw new ImpossibleCoordinateOrderException();
		}
	}

	/**
	 * Calculates and sets the centered {@link DirectPosition2D} of this
	 * {@link BoundingBox}.
	 */
	private void calcCenterCoordinate() {
		this.setCenter(new DirectPosition2D(
				// CRS
				this.getLl().getCoordinateReferenceSystem(),
				// ORDAINTE 0 (UTM --> EAST)
				(this.getLl().getOrdinate(0) + this.getUr().getOrdinate(0)) / 2,
				// ORDINATE 1 (UTM --> NORTH)
				(this.getLl().getOrdinate(1) + this.getUr().getOrdinate(1)) / 2));
		log.debug("Centered Coordinate2D set.");
	}

	/**
	 * Calculates the geographical width and height of this {@link BoundingBox}
	 * in meters and sets it.
	 * 
	 * @throws CoordinateTransformException
	 * @throws MissingCrsException
	 */
	private void calcWidthAndHeight() throws CoordinateTransformException, MissingCrsException {
		// SET THE GEOWIDTH
		this.setGeoWidth(GeoCalculator.getInstance().pythagoras2D(this.getLl(), this.getLr()));
		// SET THE GEOHEIGHTS
		this.setGeoHeight(GeoCalculator.getInstance().pythagoras2D(this.getLl(), this.getUl()));
		log.debug("Width and height set.");
	}

	/**
	 * Returns a {@link BoundingBox} with the given width (in meters) aligning
	 * to the right at this {@link BoundingBox}.
	 * 
	 * Throws {@link BoundingboxNotCreatableException} if the
	 * {@link BoundingBox} could not be created.
	 *
	 * @param width
	 *            the width of the new {@link BoundingBox} in meters.
	 * @return a new {@link BoundingBox}
	 * @throws BoundingboxNotCreatableException
	 */
	public BoundingBox getBboxRight(double width) throws BoundingboxNotCreatableException {
		return new BoundingBox(
				// LOWER LEFT CORNER
				this.getLr(),
				// UPPER RIGHT CORNER
				new DirectPosition2D(
						// CRS
						this.getLl().getCoordinateReferenceSystem(),
						// UPPER RIGHT ORDINATE 0 (UTM --> EAST)
						this.getUr().getOrdinate(0) + width,
						// UPPER RIGHT ORDINATE 1 (UTM --> NORTH)
						this.getUr().getOrdinate(1)));
	}

	/**
	 * Returns a {@link BoundingBox} with the given width (in meters) aligning
	 * to the left at this {@link BoundingBox}.
	 * 
	 * Throws {@link BoundingboxNotCreatableException} if the
	 * {@link BoundingBox} could not be created.
	 *
	 * @param width
	 *            the width of the new {@link BoundingBox} in meters.
	 * @return a new {@link BoundingBox}
	 * @throws BoundingboxNotCreatableException
	 */
	public BoundingBox getBboxLeft(double width) throws BoundingboxNotCreatableException {
		return new BoundingBox(new DirectPosition2D(this.getLl().getCoordinateReferenceSystem(), this.getLl().getOrdinate(0) - width, this.getLl().getOrdinate(1)), this.getUl());
	}

	/**
	 * Returns a {@link BoundingBox} with the given height (in meters) aligning
	 * above at this {@link BoundingBox}.
	 * 
	 * Throws {@link BoundingboxNotCreatableException} if the
	 * {@link BoundingBox} could not be created.
	 *
	 * @param height
	 *            the height of the new {@link BoundingboxNotCreatableException}
	 *            in meters.
	 * @return a new {@link BoundingBox}
	 * @throws BoundingboxNotCreatableException
	 */
	public BoundingBox getBboxAbove(double height) throws BoundingboxNotCreatableException {
		return new BoundingBox(this.getUl(), new DirectPosition2D(this.getLl().getCoordinateReferenceSystem(), this.getUr().getOrdinate(0), this.getUr().getOrdinate(1) + height));
	}

	/**
	 * Returns a {@link BoundingBox} with the given height (in meters) aligning
	 * below at this {@link BoundingBox}.
	 * 
	 * Throws {@link BoundingboxNotCreatableException} if the
	 * {@link BoundingBox} could not be created.
	 *
	 * @param height
	 *            the height of the new {@link BoundingboxNotCreatableException}
	 *            in meters.
	 * @return a new {@link BoundingBox}
	 * @throws BoundingboxNotCreatableException
	 */
	public BoundingBox getBboxBelow(double height) throws BoundingboxNotCreatableException {
		return new BoundingBox(new DirectPosition2D(this.getLl().getCoordinateReferenceSystem(), this.getLl().getOrdinate(0), this.getLl().getOrdinate(1) - height), this.getLr());

	}

	/**
	 * Returns a {@link String} containing these informations:
	 * 
	 * o lower left northing + "," o lower left easting + "," o upper right
	 * northing + "," o upper right easting
	 *
	 * @return a {@link String}
	 */
	public String getCornersForRequestUTM() {
		return this.getLl().getOrdinate(0) + "," + this.getLl().getOrdinate(1) + "," + this.getUr().getOrdinate(0) + "," + this.getUr().getOrdinate(1);
	}

	/**
	 * Returns the {@link DirectPosition2D}s of this {@link BoundingBox} in an
	 * {@link ArrayList} of {@link DirectPosition2D}s.
	 * 
	 * o the lower left {@link DirectPosition2D} at index 0 o the upper left
	 * {@link DirectPosition2D} at index 1 o the upper right
	 * {@link DirectPosition2D} at index 2 o the lower right
	 * {@link DirectPosition2D} at index 3
	 *
	 * @return an {@link ArrayList} containing all four {@link DirectPosition2D}
	 */
	public ArrayList<DirectPosition2D> getCoordsAsArrayList() {
		ArrayList<DirectPosition2D> list = new ArrayList<>();
		list.add(0, this.getLl());
		list.add(1, this.getUl());
		list.add(2, this.getUr());
		list.add(3, this.getLr());
		return list;
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the geographical width of this {@link BoundingBox} as
	 * {@link Double} in meters.
	 *
	 * @return the geoWidth as {@link Double} in meters
	 */
	public double getGeoWidth() {
		return geoWidth;
	}

	/**
	 * Sets the geographical width of this {@link BoundingBox}.
	 *
	 * @param geoWidth
	 *            the geoWidth to set
	 */
	private void setGeoWidth(double geoWidth) {
		this.geoWidth = geoWidth;
	}

	/**
	 * Returns the geographical height of this {@link BoundingBox} as
	 * {@link Double} in meters.
	 *
	 * @return the geoHeight as {@link Double} in meters
	 */
	public double getGeoHeight() {
		return geoHeight;
	}

	/**
	 * Sets the geographical height of this {@link BoundingBox}.
	 *
	 * @param geoHeight
	 *            the geoHeight to set
	 */
	private void setGeoHeight(double geoHeight) {
		this.geoHeight = geoHeight;
	}

	/**
	 * Returns the lower left {@link DirectPosition2D} of this
	 * {@link BoundingBox}.
	 *
	 * @return the lower left {@link DirectPosition2D} of this
	 *         {@link BoundingBox}
	 */
	public DirectPosition2D getLl() {
		return ll;
	}

	/**
	 * Sets the lower left {@link DirectPosition2D} of this {@link BoundingBox}.
	 *
	 * @param ll
	 *            the {@link DirectPosition2D} to set
	 */
	private void setLl(DirectPosition2D ll) {
		this.ll = ll;
	}

	/**
	 * Returns the upper right {@link DirectPosition2D} of this
	 * {@link BoundingBox}.
	 *
	 * @return the upper right {@link DirectPosition2D} of this
	 *         {@link BoundingBox}
	 */
	public DirectPosition2D getUr() {
		return ur;
	}

	/**
	 * Sets the upper right {@link DirectPosition2D} of this {@link BoundingBox}
	 * .
	 *
	 * @param ur
	 *            the {@link DirectPosition2D} to set
	 */
	private void setUr(DirectPosition2D ur) {
		this.ur = ur;
	}

	/**
	 * Returns the upper left {@link DirectPosition2D} of this
	 * {@link BoundingBox}.
	 *
	 * @return the upper left {@link DirectPosition2D} of this
	 *         {@link BoundingBox}
	 */
	public DirectPosition2D getUl() {
		return ul;
	}

	/**
	 * Sets the upper left {@link DirectPosition2D} of this {@link BoundingBox}.
	 *
	 * @param ul
	 *            the {@link DirectPosition2D} to set
	 */
	private void setUl(DirectPosition2D ul) {
		this.ul = ul;
	}

	/**
	 * Returns the lower right {@link DirectPosition2D} of this
	 * {@link BoundingBox}.
	 *
	 * @return the lower right {@link DirectPosition2D} of this
	 *         {@link BoundingBox}
	 */
	public DirectPosition2D getLr() {
		return lr;
	}

	/**
	 * Sets the lower right {@link DirectPosition2D} if this {@link BoundingBox}
	 * .
	 *
	 * @param lr
	 *            the {@link DirectPosition2D} to set
	 */
	private void setLr(DirectPosition2D lr) {
		this.lr = lr;
	}

	/**
	 * Returns the centered {@link DirectPosition2D} of this {@link BoundingBox}
	 * .
	 *
	 * @return the centered {@link DirectPosition2D} of this {@link BoundingBox}
	 */
	public DirectPosition2D getCenter() {
		return center;
	}

	/**
	 * Sets the centered {@link DirectPosition2D} of this {@link BoundingBox}.
	 *
	 * @param center
	 *            the {@link DirectPosition2D} to set
	 */
	private void setCenter(DirectPosition2D center) {
		this.center = center;
	}

	// OTHERS
}
