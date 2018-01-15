package io.github.dagri.GeospatialPDF4J.geo;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawGeometry;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawLineString;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawMultiLineString;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawMultiPolygon;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawPoint;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawPolygon;
import io.github.dagri.GeospatialPDF4J.res.AdditionalInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to store {@link DrawPoint}s, {@link DrawLineString}s,
 * {@link DrawPolygon}s, {@link DrawMultiLineString} and
 * {@link DrawMultiPolygon}s and provide various actions on them.
 * 
 * @author DaGri
 * @since 03.02.2017
 */
@Slf4j
public class GeometryContainer {

	// ATTRIBUTES

	/**
	 * The {@link ArrayList} of {@link DrawPolygon}s.
	 */
	private ArrayList<DrawPolygon>			drawPolygons			= new ArrayList<>();

	/**
	 * The {@link ArrayList} of {@link DrawMultiPolygon}s.
	 */
	private ArrayList<DrawMultiPolygon>		drawMultiPolygons		= new ArrayList<>();

	/**
	 * The {@link ArrayList} of {@link DrawLineString}s.
	 */
	private ArrayList<DrawLineString>		drawLineStrings			= new ArrayList<>();

	/**
	 * The {@link ArrayList} of {@link DrawMultiLineString}s.
	 */
	private ArrayList<DrawMultiLineString>	drawMultiLineStrings	= new ArrayList<>();

	/**
	 * The {@link ArrayList} of {@link DrawPoint}s.
	 */
	private ArrayList<DrawPoint>			drawPoints				= new ArrayList<>();

	// CONSTRUCTORS

	/**
	 * Empty constructor for a {@link GeometryContainer}.
	 */
	public GeometryContainer() {
		// NOTHING
	}

	// METHODS

	/**
	 * Adds a {@link DrawPolygon} to the internal {@link ArrayList} of
	 * {@link DrawPolygon}s.
	 *
	 * @param p
	 *            the {@link DrawPolygon} to add
	 */
	public void addDrawPolygon(DrawPolygon p) {
		if (p != null)
			this.getDrawPolygons().add(p);
	}

	/**
	 * Adds a {@link DrawLineString} to the internal {@link ArrayList} of
	 * {@link DrawLineString}s.
	 *
	 * @param ls
	 *            the {@link DrawLineString} to add
	 */
	public void addDrawLineString(DrawLineString ls) {
		if (ls != null)
			this.getDrawLineStrings().add(ls);
	}

	/**
	 * Adds a {@link DrawMultiPolygon} to the internal {@link ArrayList} of
	 * {@link DrawMultiPolygon}s.
	 *
	 * @param mp
	 *            the {@link DrawMultiPolygon} to add
	 */
	public void addDrawMultiPolygon(DrawMultiPolygon mp) {
		if (mp != null)
			this.getMultiPolygons().add(mp);
	}

	/**
	 * Adds a {@link DrawMultiLineString} to the internal {@link ArrayList} of
	 * {@link DrawMultiLineString}s.
	 *
	 * @param mls
	 *            the {@link DrawMultiLineString} to add
	 */
	public void addDrawMultiLineString(DrawMultiLineString mls) {
		if (mls != null)
			this.getMutliLineStrings().add(mls);
	}

	/**
	 * Adds a {@link DrawPoint} to the internal {@link ArrayList} of
	 * {@link DrawPoint}s.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to add
	 */
	public void addDrawPoint(DrawPoint dp) {
		if (dp != null)
			this.getDrawPoints().add(dp);
	}

	/**
	 * Adds a {@link DrawGeometry} to the internal {@link ArrayList}s of
	 * {@link DrawGeometry}s.
	 *
	 * @param g
	 *            the {@link DrawGeometry} to add
	 */
	public void addDrawGeometry(DrawGeometry g) {
		if (g instanceof DrawPoint) {
			this.addDrawPoint((DrawPoint) g);
		} else if (g instanceof DrawLineString) {
			this.addDrawLineString((DrawLineString) g);
		} else if (g instanceof DrawPolygon) {
			this.addDrawPolygon((DrawPolygon) g);
		} else if (g instanceof DrawMultiLineString) {
			this.addDrawMultiLineString((DrawMultiLineString) g);
		} else if (g instanceof DrawMultiPolygon) {
			this.addDrawMultiPolygon((DrawMultiPolygon) g);
		}
	}

	/**
	 * Adds all {@link DrawGeometry}s to the internal {@link ArrayList}s of
	 * {@link DrawGeometry}s.
	 *
	 * @param geomList
	 *            the {@link ArrayList} of {@link DrawGeometry}s.
	 */
	public void addDrawGeometrys(ArrayList<DrawGeometry> geomList) {
		for (int a = 0; a < geomList.size(); a++) {
			DrawGeometry temp = geomList.get(a);
			if (temp instanceof DrawPoint) {
				this.getDrawPoints().add((DrawPoint) temp);
			} else if (temp instanceof DrawLineString) {
				this.getDrawLineStrings().add((DrawLineString) temp);
			} else if (temp instanceof DrawMultiLineString) {
				this.getMutliLineStrings().add((DrawMultiLineString) temp);
			} else if (temp instanceof DrawPolygon) {
				this.getDrawPolygons().add((DrawPolygon) temp);
			} else if (temp instanceof DrawMultiPolygon) {
				this.getMultiPolygons().add((DrawMultiPolygon) temp);
			} else {
				log.warn("Could not sort in the DrawGeometry at index " + a + "!");
			}
		}
	}

	/**
	 * Intersects the internal stored {@link DrawPoint}s, {@link DrawLineString}
	 * and {@link DrawPolygon}s with the given {@link BoundingBox}.
	 * 
	 * {@link DrawGeometry}s that do not intersect the {@link BoundingBox}
	 * will not be touched. {@link DrawGeometry}s that intersect the
	 * {@link BoundingBox} will be removed from the {@link ArrayList}s,
	 * intersected and re-added to the right {@link ArrayList} of
	 * {@link DrawGeometry}s.
	 *
	 * @param bbox
	 *            the {@link BoundingBox} to intersect with
	 */
	public void intersect(BoundingBox bbox) {
		// CREATE A DRAW POLYGON BY THE COORDINATE2DS OF THE BOUNDINGBOX
		DrawPolygon intersect = new DrawPolygon(bbox.getCoordsAsArrayList());
		// FIRST : POLYGONS
		for (int a = 0; a < this.getDrawPolygons().size(); a++) {
			if (this.getDrawPolygons().get(a).intersectsGeometry(intersect.getJtsGeometry())) {
				// TRUE
				// CREATE A GEOMETRY BY THE INTERSECTION
				Geometry g = this.getDrawPolygons().get(a).getJtsGeometry().intersection(intersect.getJtsGeometry());
				// SORT IN THE GEOMETRY
				boolean countDown = this.sortInPolygons(a, g, this.getDrawPolygons().get(a).getInfo());
				if (countDown)
					a--;
			}
			// ELSE : FALSE = DO NOTHING
		}

		for (int a = 0; a < this.getDrawLineStrings().size(); a++) {
			if (this.getDrawLineStrings().get(a).intersectsGeometry(intersect.getJtsGeometry())) {
				// TRUE
				Geometry g = this.getDrawLineStrings().get(a).getJtsGeometry().intersection(intersect.getJtsGeometry());
				// SORT IN THE GEOMETRY
				boolean countDown = this.sortInLineStrings(a, g, this.getDrawPolygons().get(a).getInfo());
				if (countDown)
					a--;
			}
			// ELSE : FALSE = DO NOTHING
		}

		for (int a = 0; a < this.getDrawPoints().size(); a++) {
			if (this.getDrawPoints().get(a).intersectsGeometry(intersect.getJtsGeometry())) {
				// NOTHING
			} else {
				// POINT IS OUTSIDE BOUNDINGBOX
				this.getDrawPoints().remove(a);
				a--;
			}

		}

		// DRAWMULTIPOLYGONS ARE ALREADY OKAY AT THIS STATE
		// DRAWMULTILINESTRINGS ARE ALREADY OKAY AT THIS STATE
		// MULTIPOINTS ARE NOT PLANNED FOR NOW
	}

	/**
	 * Reduces all content, contained inside of this {@link GeometryContainer},
	 * about the lower left corner of the given {@link BoundingBox}.
	 *
	 * @param bbox
	 *            the {@link BoundingBox} to reduce with
	 */
	public void reduceContent(BoundingBox bbox) {
		for (int a = 0; a < this.getDrawPoints().size(); a++)
			this.getDrawPoints().get(a).reduce(bbox.getLl().getOrdinate(0), bbox.getLl().getOrdinate(1));
		for (int b = 0; b < this.getDrawLineStrings().size(); b++)
			this.getDrawLineStrings().get(b).reduce(bbox.getLl().getOrdinate(0), bbox.getLl().getOrdinate(1));
		for (int c = 0; c < this.getDrawPolygons().size(); c++)
			this.getDrawPolygons().get(c).reduce(bbox.getLl().getOrdinate(0), bbox.getLl().getOrdinate(1));
		for (int d = 0; d < this.getMutliLineStrings().size(); d++)
			this.getMutliLineStrings().get(d).reduce(bbox.getLl().getOrdinate(0), bbox.getLl().getOrdinate(1));
		for (int e = 0; e < this.getMultiPolygons().size(); e++)
			this.getMultiPolygons().get(e).reduce(bbox.getLl().getOrdinate(0), bbox.getLl().getOrdinate(1));
	}

	/**
	 * Scales all content, contained inside of this {@link GeometryContainer},
	 * with the given factor.
	 *
	 * @param factor
	 *            the factor to scale with
	 */
	public void scaleContent(double factor) {
		for (int a = 0; a < this.getDrawPoints().size(); a++)
			this.getDrawPoints().get(a).scale(factor);
		for (int b = 0; b < this.getDrawLineStrings().size(); b++)
			this.getDrawLineStrings().get(b).scale(factor);
		for (int c = 0; c < this.getDrawPolygons().size(); c++)
			this.getDrawPolygons().get(c).scale(factor);
		for (int d = 0; d < this.getMutliLineStrings().size(); d++)
			this.getMutliLineStrings().get(d).scale(factor);
		for (int e = 0; e < this.getMultiPolygons().size(); e++)
			this.getMultiPolygons().get(e).scale(factor);
	}

	/**
	 * Adds the offset of the margin in X- and Y- direction as well as the
	 * offset to the lower left corner of the map-image.
	 *
	 * @param marginX
	 *            the margin offset in X- direction
	 * @param marginY
	 *            the margin offset in Y- direction
	 * @param offsetX
	 *            the offset to the maps lower left corner in X- direction
	 * @param offsetY
	 *            the offset to the maps lower left corner in X- direction
	 */
	public void addMarginOffset(double marginX, double marginY, double offsetX, double offsetY) {
		for (int a = 0; a < this.getDrawPoints().size(); a++)
			this.getDrawPoints().get(a).reduce(-offsetX - marginX, -offsetY - marginY);
		for (int b = 0; b < this.getDrawLineStrings().size(); b++)
			this.getDrawLineStrings().get(b).reduce(-offsetX - marginX, -offsetY - marginY);
		for (int c = 0; c < this.getDrawPolygons().size(); c++)
			this.getDrawPolygons().get(c).reduce(-offsetX - marginX, -offsetY - marginY);
		for (int d = 0; d < this.getMutliLineStrings().size(); d++)
			this.getMutliLineStrings().get(d).reduce(-offsetX - marginX, -offsetY - marginY);
		for (int e = 0; e < this.getMultiPolygons().size(); e++)
			this.getMultiPolygons().get(e).reduce(-offsetX - marginX, -offsetY - marginY);
	}

	/**
	 * Sorts the given {@link Geometry} (which is supposed to be a
	 * {@link LineString} or {@link MultiLineString} in the right
	 * {@link ArrayList}.
	 * 
	 * Returns true, if the {@link Geometry} given was a {@link LineString} and
	 * the {@link DrawLineString} at index a in the {@link ArrayList} of
	 * {@link DrawLineString}s was replaced by a new, intersected,
	 * {@link DrawLineString}.
	 * 
	 * Returns false if the {@link Geometry} given was a {@link MultiLineString}
	 * or no longer any kind of {@link LineString}. If it was a
	 * {@link MultiLineString} the {@link DrawLineString} at the index a was
	 * removed and a new {@link DrawMultiLineString} was added to the
	 * {@link ArrayList} of {@link DrawMultiLineString}s. If the
	 * {@link Geometry} was no longer any kind of {@link LineString} the
	 * {@link DrawLineString} at index a was removed.
	 *
	 * @param a
	 *            the index as {@link Integer}
	 * @param g
	 *            the {@link Geometry}; {@link Polygon} or {@link MultiPolygon}
	 * @param info
	 *            the {@link AdditionalInfo} to set to new {@link DrawGeometry}
	 * @return true or false (look above)
	 */
	private boolean sortInLineStrings(int a, Geometry g, AdditionalInfo info) {
		if (g.getClass().getClass().getCanonicalName() == MultiLineString.class.getCanonicalName()) {
			// ADD A NEW DRAWMULTILINESTRING TO THE ARRAYLIST OF DML
			// CREATE A DRAWMULTILINESTRING WITH THE CASTED MULTILINESTRING
			DrawMultiLineString temp = new DrawMultiLineString((MultiLineString) g, info);
			// ADD DRAWMULTILINESTRING
			this.getMutliLineStrings().add(temp);
			// REMOVE OLD LINESTRING
			this.getDrawLineStrings().remove(a);
			// RETURN FALSE BECAUSE THE OLD DRAWLINESTRING WAS REMOVED
			return false;
		} else if (g.getClass().getClass().getCanonicalName() == LineString.class.getCanonicalName()) {
			this.getDrawLineStrings().remove(a);
			DrawLineString temp = new DrawLineString((LineString) g, info);
			this.getDrawLineStrings().add(a, temp);
			// RETURN TRUE BECAUSE THE OLD DRAWLINESTRING WAS REPLACED
			return true;
		} else {
			// NOT A POLYGON ANYMORE
			this.getDrawLineStrings().remove(a);
			// RETURN FALSE BECAUSE THE OLD DRAWLINESTRING WAS REMOVED
			return false;
		}
	}

	/**
	 * Sorts the given {@link Geometry} (which is supposed to be a
	 * {@link Polygon} or {@link MultiPolygon} in the right {@link ArrayList}.
	 * 
	 * Returns true, if the {@link Geometry} given was a {@link Polygon} and the
	 * {@link DrawPolygon} at index a in the {@link ArrayList} of
	 * {@link DrawPolygon}s was replaced by a new, intersected,
	 * {@link DrawPolygon}.
	 * 
	 * Returns false if the {@link Geometry} given was a {@link MultiPolygon} or
	 * no longer any kind of {@link Polygon}. If it was a {@link MultiPolygon}
	 * the {@link DrawPolygon} at the index a was removed and a new
	 * {@link DrawMultiPolygon} was added to the {@link ArrayList} of
	 * {@link DrawMultiPolygon}s. If the {@link Geometry} was no longer any kind
	 * of {@link Polygon} the {@link DrawPolygon} at index a was removed.
	 *
	 * @param a
	 *            the index as {@link Integer}
	 * @param g
	 *            the {@link Geometry}; {@link Polygon} or {@link MultiPolygon}
	 * @param info
	 *            the {@link AdditionalInfo} to set to new {@link DrawGeometry}
	 * @return true or false (look above)
	 */
	private boolean sortInPolygons(int a, Geometry g, AdditionalInfo info) {
		if (g.getClass().getClass().getCanonicalName() == MultiPolygon.class.getCanonicalName()) {
			// ADD A NEW DRAWMULTIPOLYGON TO THE ARRAYLIST OF DMP
			// CREATE A DRAWMULTIPOLYGON WITH THE CASTED MULTIPOLYGON
			DrawMultiPolygon temp = new DrawMultiPolygon((MultiPolygon) g, info);
			// ADD DRAWMULTIPOLYGON
			this.getMultiPolygons().add(temp);
			// REMOVE OLD POLYGON
			this.getDrawPolygons().remove(a);
			// RETURN FALSE BECAUSE THE OLD DRAWPOLYGON WAS REMOVED
			return false;
		} else if (g.getClass().getClass().getCanonicalName() == Polygon.class.getCanonicalName()) {
			this.getDrawPolygons().remove(a);
			DrawPolygon temp = new DrawPolygon((Polygon) g, info);
			this.getDrawPolygons().add(a, temp);
			// RETURN TRUE BECAUSE THE OLD DRAWPOLYGON WAS REPLACED
			return true;
		} else {
			// NOT A POLYGON ANYMORE
			this.getDrawPolygons().remove(a);
			// RETURN FALSE BECAUSE THE OLD DRAWPOLYGON WAS REMOVED
			return false;
		}
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link ArrayList} of {@link DrawPolygon}s.
	 *
	 * @return the {@link ArrayList} of {@link DrawPolygon}s
	 */
	public ArrayList<DrawPolygon> getDrawPolygons() {
		return drawPolygons;
	}

	/**
	 * Returns the {@link ArrayList} of {@link DrawMultiPolygon}s.
	 *
	 * @return the {@link ArrayList} of {@link DrawMultiPolygon}s
	 */
	public ArrayList<DrawMultiPolygon> getMultiPolygons() {
		return drawMultiPolygons;
	}

	/**
	 * Returns the {@link ArrayList} of {@link DrawLineString}s.
	 *
	 * @return the {@link ArrayList} of {@link DrawLineString}s
	 */
	public ArrayList<DrawLineString> getDrawLineStrings() {
		return drawLineStrings;
	}

	/**
	 * Returns the {@link ArrayList} of {@link DrawMultiLineString}s.
	 *
	 * @return the {@link ArrayList} of {@link DrawMultiLineString}s
	 */
	public ArrayList<DrawMultiLineString> getMutliLineStrings() {
		return drawMultiLineStrings;
	}

	/**
	 * Returns the {@link ArrayList} of {@link DrawPoint}s.
	 *
	 * @return the {@link ArrayList} of {@link DrawPoint}
	 */
	public ArrayList<DrawPoint> getDrawPoints() {
		return drawPoints;
	}

	// OTHERS
}
