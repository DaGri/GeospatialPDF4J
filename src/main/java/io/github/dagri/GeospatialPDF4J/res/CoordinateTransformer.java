package io.github.dagri.GeospatialPDF4J.res;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.MissingCrsException;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to be used for transformation of {@link DirectPosition}s implemented in
 * singleton pattern. Use the getInstance()-method to get an usable instance of
 * this class.
 * 
 * You can pick the correct axis order(s) by checking out
 * http://www.epsg-registry.org : In the top of the page, witch to the right
 * rider and seach for the EPSG-Code you are looking for. Take a look into the
 * informations below and you will find a table of axis orders.
 * 
 * @author DaGri
 * @since 10.01.2017
 */
@Slf4j
public class CoordinateTransformer {

	// ATTRIBUTES

	/**
	 * The instance of this {@link CoordinateTransformer} (singleton pattern).
	 */
	private static CoordinateTransformer instance;

	// CONSTRUCTORS

	/**
	 * Empty constructor for a {@link CoordinateTransformer}.
	 */
	private CoordinateTransformer() {
		// NOTHING
	}

	// METHODS

	/**
	 * Returns an instance of this {@link CoordinateTransformer}.
	 *
	 * @return an instance of {@link CoordinateTransformer}
	 */
	public static CoordinateTransformer getInstance() {
		if (instance == null) {
			log.debug("Creating instance for the CoordinateTransformer.");
			instance = new CoordinateTransformer();
		}
		log.debug("Returning instance.");
		return instance;
	}

	/**
	 * Transforms a {@link DirectPosition2D} to the desired
	 * {@link CoordinateReferenceSystem}.
	 * 
	 * Throws a {@link CoordinateTransformException} if the transforming could
	 * not be processed.
	 *
	 * @param sourcePos
	 *            the {@link DirectPosition2D} to transform
	 * @param destinationCRS
	 *            the {@link CoordinateReferenceSystem} to transform to
	 * @return the transformed {@link DirectPosition2D}
	 * @throws CoordinateTransformException
	 *             if the transformation could not be processed
	 * @throws MissingCrsException 
	 */
	public DirectPosition2D transform(DirectPosition2D sourcePos, CoordinateReferenceSystem destinationCRS) throws CoordinateTransformException, MissingCrsException {
		log.debug("Starting to convert DirectPosition to another CRS");
		
		if(sourcePos.getCoordinateReferenceSystem() == null || destinationCRS.getCoordinateSystem() == null)
			throw new MissingCrsException();
		
		if (sourcePos.getCoordinateReferenceSystem() == destinationCRS) {
			// DONE BECAUSE THE SOURCE DIRECT POSITION IS ALREADY IN THE TARGET
			// CRS
			log.debug("Source DirectPosition is already in the desired CRS.");

			// RETURN THE SOURCE POSITION
			return sourcePos;
		}

		try {

			log.debug("Searching for transformation.");
			// GET THE TRANSFORMATION
			MathTransform transform = CRS.findMathTransform(sourcePos.getCoordinateReferenceSystem(), destinationCRS);
			log.debug("Transforming CRS...");
			// TRANSFORM TO NEW 'TEMP' DIRECT POSITION
			DirectPosition temp = transform.transform(sourcePos, null);
			log.debug("Transformation done.");

			DirectPosition2D erg = new DirectPosition2D(temp.getCoordinateReferenceSystem(), temp.getOrdinate(0), // temp.getCoordinate()[0],
					temp.getOrdinate(1) // temp.getCoordinate()[1]
			);

			// RETURN THE TRANSFORMED DIRECT POSITION
			return erg;

		} catch (FactoryException e) {
			log.error(e.getMessage().toString());
			throw new CoordinateTransformException();
		} catch (MismatchedDimensionException e) {
			log.error(e.getMessage().toString());
			throw new CoordinateTransformException();
		} catch (TransformException e) {
			log.error(e.getMessage().toString());
			throw new CoordinateTransformException();
		}
	}

	/**
	 * Transforms a given EPSG-code (integer) and a given coordinate (northing
	 * and easting) (in any by GeoTools supported
	 * {@link CoordinateReferenceSystem}) directly in a {@link Coordinate2D} in
	 * the UTM zone 32 {@link CoordinateReferenceSystem}.
	 * 
	 * Throws a {@link CoordinateTransformException} if the transforming could
	 * not be processed.
	 *
	 * @param epsg
	 *            the EPSG-code of the source-coordinate
	 * @param ordinate0
	 *            the northing of the coordinate to transform
	 * @param ordinate1
	 *            the easting of the coordinate to transform
	 * @return a {@link Coordinate2D} in UTM32
	 * @throws CoordinateTransformException
	 *             if the transformation could not be processed
	 */
	public DirectPosition2D transformUTM(int epsg, double ordinate0, double ordinate1) throws CoordinateTransformException {
		log.debug("Starting to convert DirectPosition to another CRS");

		try {
			CoordinateReferenceSystem destinationCrs = CRS.decode("EPSG:25832");
			CoordinateReferenceSystem sourceCrs = CRS.decode("EPSG:" + epsg);

			DirectPosition2D sourcePos = new DirectPosition2D(sourceCrs, ordinate0, ordinate1);

			if (sourcePos.getCoordinateReferenceSystem() == destinationCrs) {
				// DONE BECAUSE THE SOURCE DIRECT POSITION IS ALREADY IN THE
				// TARGET CRS
				log.debug("Source DirectPosition is already in the desired CRS.");

				// RETURN THE SOURCE POSITION
				return sourcePos;
			}

			log.debug("Searching for transformation.");
			// GET THE TRANSFORMATION
			MathTransform transform = CRS.findMathTransform(sourcePos.getCoordinateReferenceSystem(), destinationCrs);
			log.debug("Transforming CRS...");
			// TRANSFORM TO NEW 'TEMP' DIRECT POSITION
			DirectPosition temp = transform.transform(sourcePos, null);
			log.debug("Transformation done.");

			DirectPosition2D erg = new DirectPosition2D(destinationCrs, temp.getCoordinate()[0], temp.getCoordinate()[1]);

			// LOOK AT THE OTHER DIMENSIONS
			log.debug("Addding higher dimensions.");

			// RETURN THE TRANSFORMED DIRECT POSITION
			return erg;
		} catch (FactoryException e) {
			log.error(e.getMessage().toString());
			throw new CoordinateTransformException();
		} catch (MismatchedDimensionException e) {
			log.error(e.getMessage().toString());
			throw new CoordinateTransformException();
		} catch (TransformException e) {
			log.error(e.getMessage().toString());
			throw new CoordinateTransformException();
		}
	}

	/**
	 * Transforms a {@link DirectPosition2D} from its
	 * {@link CoordinateReferenceSystem} to another
	 * {@link CoordinateReferenceSystem} identified by its EPSG code.
	 * 
	 * Returns the same {@link DirectPosition2D} if the source and the target
	 * CRS are the same.
	 *
	 * @param dp2D
	 *            the {@link DirectPosition2D} to transform.
	 * @param epsg
	 *            the EPSG-code of the CRS to transform to
	 * @return the transformed {@link DirectPosition2D}
	 * @throws CoordinateTransformException
	 */
	public DirectPosition transform(DirectPosition dp, int epsg) throws CoordinateTransformException {
		try {
			log.info("Transforming DirectPosition2D to another CRS...");
			log.debug("Extracting source and target CRS...");
			CoordinateReferenceSystem targetCrs = CRS.decode("EPSG:" + epsg);
			CoordinateReferenceSystem sourceCrs = dp.getCoordinateReferenceSystem();
			log.debug("Source and target CRS extracted.");

			if (dp.getCoordinateReferenceSystem() == targetCrs) {
				log.debug("Source DirectPosition2D is already in the desired CRS. Returning source Position2D.");
				return dp;
			}

			log.debug("Finding Math transform and transforming source position...");
			MathTransform trans = CRS.findMathTransform(sourceCrs, targetCrs);
			DirectPosition temp = trans.transform(dp, null);
			log.debug("Source position transformed.");

			log.debug("Creating new DirectPosition2D to be returned...");
			DirectPosition2D erg = new DirectPosition2D(targetCrs, temp.getOrdinate(0), temp.getOrdinate(1));
			log.debug("DirectPosition2D created.");

			log.debug("Adding higher dimensions...");
			for (int a = 2; a < dp.getDimension(); a++)
				erg.setOrdinate(a, dp.getOrdinate(a));
			log.debug("Higher dimensions added.");

			log.info("Transformation done.");
			return erg;
		} catch (NoSuchAuthorityCodeException e) {
			log.error(e.getMessage());
			throw new CoordinateTransformException();
		} catch (FactoryException e) {
			log.error(e.getMessage());
			throw new CoordinateTransformException();
		} catch (MismatchedDimensionException e) {
			log.error(e.getMessage());
			throw new CoordinateTransformException();
		} catch (TransformException e) {
			log.error(e.getMessage());
			throw new CoordinateTransformException();
		}
	}

	/**
	 * Creates a {@link DirectPosition} using the given EPSG-code to identify
	 * the CRS and the given ordinates.
	 *
	 * @param epsg
	 *            the EPSG-code as {@link Integer}
	 * @param ordinate0
	 *            the first ordinate as {@link Double}
	 * @param ordinate1
	 *            the second ordinate as {@link Double}
	 * @return a new created {@link DirectPosition}
	 */
	public DirectPosition2D createDirectPosition2D(int epsg, double ordinate0, double ordinate1) {
		try {
			CoordinateReferenceSystem targetCrs = CRS.decode("EPSG:" + epsg);
			DirectPosition2D erg = new DirectPosition2D(targetCrs, ordinate0, ordinate1);
			return erg;
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CoordinateReferenceSystem getUtmCrs() throws CoordinateTransformException {
		try {
			return CRS.decode("EPSG:25832");
		} catch (NoSuchAuthorityCodeException e) {
			log.error(e.getMessage());
			throw new CoordinateTransformException();
		} catch (FactoryException e) {
			log.error(e.getMessage());
			throw new CoordinateTransformException();
		}
	}

	// GETTERS AND SETTERS

	// OTHERS
}
