package io.github.dagri.GeospatialPDF4J.draw.drawers;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfWriter;
import com.vividsolutions.jts.geom.LineString;

import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawLineString;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawMultiLineString;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawPoint;
import io.github.dagri.GeospatialPDF4J.draw.geometries.DrawPolygon;
import io.github.dagri.GeospatialPDF4J.draw.styles.Icon;
import io.github.dagri.GeospatialPDF4J.draw.styles.LineStringStyle;
import io.github.dagri.GeospatialPDF4J.draw.styles.PointStyle;
import io.github.dagri.GeospatialPDF4J.draw.styles.PolygonStyle;
import io.github.dagri.GeospatialPDF4J.exceptions.ImageCovertingException;
import io.github.dagri.GeospatialPDF4J.res.AdditionalInfo;
import io.github.dagri.GeospatialPDF4J.res.ImageHandler;

/**
 * Abstract class to be used as parental class for all drawers for a PDF file.
 * 
 * This class already provides multiple methods and extension-support for its
 * children!
 * 
 * @author DaGri
 * @since 20.02.2017
 */
public abstract class PdfDrawer {

	// ATTRIBUTES

	/**
	 * The {@link PdfWriter} of this {@link PdfDrawer}.
	 */
	private PdfWriter			writer;

	/**
	 * The {@link PdfContentByte} of this {@link PdfDrawer} gained from the
	 * {@link PdfWriter} given in the constructor.
	 */
	private PdfContentByte		contByte;

	/**
	 * The {@link PdfStructureElement} to add the PDF-content below.
	 */
	private PdfStructureElement	top;

	/**
	 * The parental {@link PdfLayer} this {@link PdfDrawer} shall add its
	 * content below.
	 */
	private PdfLayer			parentalLayer;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link DataDrawer} using a {@link PdfWriter}, a
	 * {@link PdfStructureElement} and an parental {@link PdfLayer} to draw
	 * below.
	 * 
	 * @param writer
	 *            the {@link PdfWriter} to use
	 * @param top
	 *            the top element to structure below
	 * @param parentalLayer
	 *            the {@link PdfLayer} to draw below
	 */
	public PdfDrawer(PdfWriter writer, PdfStructureElement top, PdfLayer parentalLayer) {
		this.setWriter(writer);
		this.setContByte(writer.getDirectContent());
		this.setTop(top);
		this.setParentalLayer(parentalLayer);
	}

	/**
	 * Draws a {@link DrawPoint} with the given {@link PointStyle}.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle}
	 */
	public void drawDrawPoint(DrawPoint dp, PointStyle style) {
		if (dp == null)
			return;
		if (style == null)
			style = new PointStyle();

		if (style.pointIcon != null) {
			// THERE IS A POINT ICON GIVEN
			try {
				this.drawIcon(dp, style);
			} catch (ImageCovertingException | DocumentException e) {
				// ICON ERROR : DRAW GEOMETRY INSTEAD
				e.printStackTrace();
				drawPointGeometry(dp, style);
			}
		} else {
			// THERE IS NO POINT ICON GIVEN : DRAW A GEOMETRY INSTEAD
			drawPointGeometry(dp, style);
		}
	}

	/**
	 * Draws a {@link DrawLineString} with the given {@link LineStringStyle}.
	 *
	 * @param dls
	 *            the {@link DrawLineString} to draw
	 * @param style
	 *            the {@link LineStringStyle}
	 */
	public void drawLineString(DrawLineString dls, LineStringStyle style) {

		if (dls == null)
			return;
		if (style == null)
			style = new LineStringStyle();

		this.getContByte().setLineWidth(style.lineStringStrength);
		this.getContByte().setColorStroke(style.lineStringColor);

		if (dls.getJtsPointNumber() > 0)
			this.getContByte().moveTo((float) dls.getJtsGeometry().getPointN(0).getX(), (float) dls.getJtsGeometry().getPointN(0).getY());

		for (int a = 0; a < dls.getJtsPointNumber(); a++) {
			this.getContByte().lineTo((float) dls.getJtsGeometry().getPointN(a).getX(), (float) dls.getJtsGeometry().getPointN(a).getY());
		}

		this.getContByte().stroke();
	}

	/**
	 * Draws a {@link DrawPolygon} with the given {@link PolygonStyle}.
	 *
	 * @param dp
	 *            the {@link DrawPolygon} to be drawn
	 * @param style
	 *            the {@link PolygonStyle} to draw the {@link DrawPolygon} with
	 */
	public void drawPolygon(DrawPolygon dp, PolygonStyle style) {
		if (dp == null)
			return;
		if (style == null)
			style = new PolygonStyle();

		this.getContByte().setLineWidth(style.polygonStrokeWidth);
		this.getContByte().setColorStroke(style.polygonColor);
		this.getContByte().setColorFill(style.polygonFillColor);

		if (dp.getJtsGeometry().getCoordinates().length > 0)
			this.getContByte().moveTo((float) dp.getJtsGeometry().getCoordinates()[0].x, (float) dp.getJtsGeometry().getCoordinates()[0].x);

		for (int a = 0; a < dp.getJtsGeometry().getCoordinates().length; a++) {
			this.getContByte().lineTo((float) dp.getJtsGeometry().getCoordinates()[a].x, (float) dp.getJtsGeometry().getCoordinates()[a].y);
		}

		this.getContByte().closePath();
		if (style.polygonFilled)
			this.getContByte().fill();

		this.getContByte().stroke();

	}

	/**
	 * Draws a {@link DrawMultiLineString} with the given
	 * {@link LineStringStyle}.
	 *
	 * @param dls
	 *            the {@link DrawMultiLineString} to draw
	 * @param style
	 *            the {@link LineStringStyle}
	 */
	public void drawMultiLineString(DrawMultiLineString dmls, LineStringStyle style) {
		if (dmls == null)
			return;
		if (style == null)
			style = new LineStringStyle();

		this.getContByte().setLineWidth(style.lineStringStrength);
		this.getContByte().setColorStroke(style.lineStringColor);

		for (int a = 0; a < dmls.getJtsGeometry().getNumGeometries(); a++) {
			try {
				LineString actLS = (LineString) dmls.getJtsGeometry().getGeometryN(a);
				if (actLS.getNumPoints() > 0)
					this.getContByte().lineTo((float) actLS.getPointN(0).getX(), (float) actLS.getPointN(0).getY());

				for (int b = 0; b < actLS.getNumPoints(); b++) {
					this.getContByte().lineTo((float) actLS.getPointN(b).getX(), (float) actLS.getPointN(b).getY());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Draw a geometry for the {@link DrawPoint} indicated by the
	 * {@link PointStyle}.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} containing the style informations
	 */
	private void drawPointGeometry(DrawPoint dp, PointStyle style) {
		// SET CONTENTBYTE PROPERTIES
		this.getContByte().setLineWidth(style.pointLineWidth);
		this.getContByte().setColorStroke(style.pointColor);
		this.getContByte().setColorFill(style.pointFillColor);

		// GEOMETRY
		/*
		 * Replaces the previous if-else-blocks (still below).
		 */
		switch (style.pointSymbol) {
			case CIRCLE:
				this.drawCircle(dp, style);
				break;
			case SQUARE:
				this.drawSquare(dp, style);
				break;
			case TRIANGLE:
				this.drawTriangle(dp, style);
				break;
			case TRIANGLE_UPSIDE:
				this.drawTriangleUpside(dp, style);
				break;
			case TRIANGLE_LYING_LEFT:
				this.drawTriangleLyingLeft(dp, style);
				break;
			case DIAMOND:
				this.drawDiamond(dp, style);
				break;
			case CROSS:
				this.drawCross(dp, style);
				break;
			case STAR:
				this.drawStar(dp, style);
				break;
			case X:
				this.drawX(dp, style);
				break;
			case HOURGLASS:
				this.drawHourglass(dp, style);
				break;
			case HOURGLASS_LYING:
				this.drawHourglassLying(dp, style);
				break;
			case HOURGLASS_LEFT:
				this.drawHourglassLeft(dp, style);
				break;
			case HOURGLASS_RIGHT:
				this.drawHourglassRight(dp, style);
				break;

			default:
				this.drawCircle(dp, style);
		}

		/*
		 * Replaces by the switch-statemend above, but still listed here until
		 * the next version.
		 */
		// if (style.pointSymbol == EPointAppearance.CIRCLE) {
		// this.drawCircle(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.SQUARE) {
		// this.drawSquare(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.TRIANGLE) {
		// this.drawTriangle(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.TRIANGLE_UPSIDE) {
		// this.drawTriangleUpside(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.TRIANGLE_LYING_LEFT)
		// {
		// this.drawTriangleLyingLeft(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.DIAMOND) {
		// this.drawDiamond(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.CROSS) {
		// this.drawCross(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.STAR) {
		// this.drawStar(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.X) {
		// this.drawX(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.HOURGLASS) {
		// this.drawHourglass(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.HOURGLASS_LYING) {
		// this.drawHourglassLying(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.HOURGLASS_LEFT) {
		// this.drawHourglassLeft(dp, style);
		// } else if (style.pointSymbol == EPointAppearance.HOURGLASS_RIGHT) {
		// this.drawHourglassRight(dp, style);
		// } else
		// this.drawCircle(dp, style);

		// FINISH THE DRAWING
		this.getContByte().stroke();
	}

	/**
	 * Draws the {@link Icon}, contained in the {@link PointStyle}, with the
	 * maximum width or height indicated by the point-radius from the
	 * {@link PointStyle}, centered or not centered.
	 *
	 * Throws an {@link ImageCovertingException} if the image could not be
	 * converted to an iText-Image Throws an {@link DocumentException} if the
	 * converted iText-Image could not be added to the document
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 * 
	 * @throws ImageCovertingException
	 *             if the image could not be converted to an iText-Image
	 * @throws DocumentException
	 *             if the converted iText-Image could not be added to the
	 *             document
	 */
	private void drawIcon(DrawPoint dp, PointStyle style) throws ImageCovertingException, DocumentException {
		ImageHandler handler = ImageHandler.getInstance();
		// TODO : ICONS MIT OPACITY
		Image img = handler.convertToImage(style.pointIcon.getImage(), 255);
		// IF THE POINT ICON HAS NOT ESPECIALLY SET WIDTH
		if (style.pointIcon.getWidth() == Float.MIN_VALUE)
			// USE THE WIDTH OF THE STYLE
			img.scaleToFit(style.pointRadius, style.pointRadius);
		else
			// ELSE USE THE POINT ICON WIDTH INSIDE THE POINT ICON
			img.scaleToFit(style.pointIcon.getWidth(), style.pointIcon.getWidth());
		if (style.centered) {
			// CENTERED
			img.setAbsolutePosition((float) (dp.getJtsGeometry().getX() - img.getScaledWidth() / 2), (float) (dp.getJtsGeometry().getY() - img.getScaledHeight() / 2));
		} else {
			// NOT CENTERED
			img.setAbsolutePosition((float) (dp.getJtsGeometry().getX()), (float) (dp.getJtsGeometry().getY()));
		}
		this.getContByte().addImage(img);
	}

	/**
	 * Draws a star.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawStar(DrawPoint dp, PointStyle style) {
		this.drawX(dp, style);
		this.drawCross(dp, style);
	}

	/**
	 * Draws a standing hourglass.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawHourglass(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY());
		}
		this.getContByte().closePath();
		this.fillOrNot(style);
	}

	/**
	 * Draws a lying hourglass.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawHourglassLying(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY());
		}
		this.getContByte().closePath();
		this.fillOrNot(style);

	}

	/**
	 * Draws a left lying hourglass.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawHourglassLeft(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
		}
		this.getContByte().closePath();
		this.fillOrNot(style);
	}

	/**
	 * Draws an right lying hourglass.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawHourglassRight(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius);
		}
		this.getContByte().closePath();
		this.fillOrNot(style);

	}

	/**
	 * Draws an X.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawX(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY());
		}
	}

	/**
	 * Draws a cross.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawCross(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
		}
	}

	/**
	 * Draws a diamond.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawDiamond(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
		}
		this.getContByte().closePath();
		this.fillOrNot(style);
	}

	/**
	 * Draws a left lying triangle.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawTriangleLyingLeft(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY());
		}
		this.getContByte().closePath();
		this.fillOrNot(style);
	}

	/**
	 * Draws a upside triangle.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawTriangleUpside(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius);
		}
		this.getContByte().closePath();
		this.fillOrNot(style);
	}

	/**
	 * Draws a standing triangle.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawTriangle(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY() + style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().moveTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius / 2, (float) dp.getJtsGeometry().getY() + style.pointRadius);
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX() + style.pointRadius, (float) dp.getJtsGeometry().getY());
			this.getContByte().lineTo((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY());
		}
		this.getContByte().closePath();
		this.fillOrNot(style);
	}

	/**
	 * Draws a square.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawSquare(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().rectangle(
					// X
					(float) dp.getJtsGeometry().getX() - style.pointRadius / 2,
					// Y
					(float) dp.getJtsGeometry().getY() - style.pointRadius / 2,
					// W
					style.pointRadius,
					// H
					style.pointRadius);
		} else {
			// NOT CENTERED
			this.getContByte().rectangle(
					// X
					(float) dp.getJtsGeometry().getX(),
					// Y
					(float) dp.getJtsGeometry().getY(),
					// W
					style.pointRadius,
					// H
					style.pointRadius);
		}
		this.getContByte().closePath();
		this.fillOrNot(style);
	}

	/**
	 * Draws a circle.
	 *
	 * @param dp
	 *            the {@link DrawPoint} to draw
	 * @param style
	 *            the {@link PointStyle} to draw the {@link DrawPoint} with
	 */
	private void drawCircle(DrawPoint dp, PointStyle style) {
		if (style.centered) {
			// CENTERED
			this.getContByte().circle((float) dp.getJtsGeometry().getX() - style.pointRadius / 2, (float) dp.getJtsGeometry().getY() - style.pointRadius / 2, style.pointRadius / 2);
		} else {
			// NOT CENTERED
			this.getContByte().circle((float) dp.getJtsGeometry().getX(), (float) dp.getJtsGeometry().getY(), style.pointRadius / 2);
		}
		// this.getContByte().closePath();
		this.fillOrNot(style);
	}

	/**
	 * Fills a draw geometry if the given {@link PointStyle} indicates that it
	 * should be filled, or not.
	 *
	 * @param style
	 *            the {@link PointStyle} to use
	 */
	private void fillOrNot(PointStyle style) {
		// FILLED OR NOT
		if (style.pointFilled) {
			// FILLED
			this.getContByte().fill();
		} else {
			// NOT FILLED
		}
	}

	// METHODS

	/**
	 * Mathod to call all internal draw-methods.
	 */
	public abstract void drawAll();

	/**
	 * Creates a {@link PdfStructureElement} filled with the informations
	 * contained in the {@link AdditionalInfo}-object and with the given
	 * {@link String} as name.
	 *
	 * @param info
	 *            the {@link AdditionalInfo} containing the informations
	 * @param name
	 *            the name to set
	 * @return a {@link PdfStructureElement}
	 */
	protected PdfStructureElement createStructureElement(AdditionalInfo info, String name) {
		PdfDictionary partentDic = new PdfDictionary();
		partentDic.put(PdfName.O, PdfName.USERPROPERTIES);
		PdfStructureElement elem = new PdfStructureElement(this.getTop(), new PdfName(name));
		PdfArray array = new PdfArray();
		for (int a = 0; a < info.infoCount(); a++) {
			PdfDictionary childDict = new PdfDictionary();
			childDict.put(PdfName.N, new PdfName(info.getInfo(a).getKey()));
			childDict.put(PdfName.V, new PdfString(info.getInfo(a).getValue()));
			array.add(childDict);
		}
		partentDic.put(PdfName.P, array);
		elem.put(PdfName.A, partentDic);
		return elem;
	}

	// GETTERS AND SETTERS

	/**
	 * Returns the {@link PdfWriter} of this {@link PdfDrawer}.
	 *
	 * @return the {@link PdfWriter} of this {@link PdfDrawer}
	 */
	public PdfWriter getWriter() {
		return writer;
	}

	/**
	 * Sets the {@link PdfWriter} of this {@link PdfDrawer}.
	 *
	 * @param writer
	 *            the {@link PdfWriter} to set
	 */
	public void setWriter(PdfWriter writer) {
		this.writer = writer;
	}

	/**
	 * Returns the {@link PdfContentByte} of this {@link PdfDrawer}.
	 *
	 * @return the {@link PdfContentByte} of this {@link PdfDrawer}
	 */
	public PdfContentByte getContByte() {
		return contByte;
	}

	/**
	 * Sets the {@link PdfContentByte} of this {@link PdfDrawer}
	 *
	 * @param contByte
	 *            the {@link PdfContentByte} to set
	 */
	public void setContByte(PdfContentByte contByte) {
		this.contByte = contByte;
	}

	/**
	 * Returns the top {@link PdfStructureElement} of this {@link PdfDrawer}.
	 *
	 * @return the top {@link PdfStructureElement}
	 */
	public PdfStructureElement getTop() {
		return top;
	}

	/**
	 * Sets the top {@link PdfStructureElement} of this {@link PdfDrawer}.
	 *
	 * @param top
	 *            the top {@link PdfStructureElement} to set
	 */
	public void setTop(PdfStructureElement top) {
		this.top = top;
	}

	/**
	 * Returns the parental {@link PdfLayer} of this {@link PdfDrawer}.
	 *
	 * @return the parental {@link PdfLayer} of this {@link PdfDrawer}
	 */
	public PdfLayer getParentalLayer() {
		return parentalLayer;
	}

	/**
	 * Sets the parental {@link PdfLayer} of this {@link PdfDrawer}.
	 *
	 * @param parentalLayer
	 *            the {@link PdfLayer} to set
	 */
	public void setParentalLayer(PdfLayer parentalLayer) {
		this.parentalLayer = parentalLayer;
	}

	// OTHERS
}
