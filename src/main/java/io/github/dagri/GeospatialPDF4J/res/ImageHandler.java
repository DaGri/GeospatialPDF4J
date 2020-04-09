package io.github.dagri.GeospatialPDF4J.res;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

import io.github.dagri.GeospatialPDF4J.exceptions.ImageCovertingException;
import io.github.dagri.GeospatialPDF4J.map.Map;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * Class to provide various types of {@link Image} and {@link BufferedImage}
 * operations.
 * 
 * @author DaGri
 * @since 17.01.2017
 *
 */
@Slf4j
public class ImageHandler {

	// ATTRIBUTES

	/**
	 * The instance of an {@link ImageHandler} according to the
	 * singleton-pattern.
	 */
	private static ImageHandler instance;

	// CONSTRUCTORS

	/**
	 * Empty constructor for an {@link ImageHandler}.
	 */
	private ImageHandler() {
		// NOTHING
	}

	// METHODS

	/**
	 * Returns an instance of an {@link ImageHandler} according to the
	 * singleton-pattern.
	 *
	 * @return an instance of and {@link ImageHandler}
	 */
	public static ImageHandler getInstance() {
		if (instance == null) {
			log.debug("Instance was null. Creating new Imagehandler.");
			instance = new ImageHandler();
		} 
		return instance;
	}

	/**
	 * Creates a {@link BufferedImage} that fits the standard DPI value of 72
	 * PDI (inside a PDF-file) in height and width by using the given height and
	 * width in inches.
	 *
	 * @param width
	 *            the width the image shall fit as {@link Double} in inches
	 * @param height
	 *            the height the image shall fit as {@link Double} in inches
	 * @return a {@link BufferedImage}
	 */
	public BufferedImage createStandardDPIImage(double inchesWidth, double inchesHeight) {
		log.debug("Multiplying the inch values with the standard DPI value of a PDF-file.");
		int width = Math.abs((int) Math.round(inchesWidth * 72));
		int height = Math.abs((int) Math.round(inchesHeight * 72));
		log.debug("Creating a new BufferedImage...");
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		log.debug("Painting the BufferedImage...");
		this.fillStdColor(buffImg);
		log.debug("BufferedImage created!");
		return buffImg;
	}

	/**
	 * Fills the given {@link BufferedImage} with the standard background color.
	 *
	 * @param buff
	 *            the {@link BufferedImage} to fill
	 */
	public void fillStdColor(BufferedImage buffImg) {
		log.debug("Creating Graphics for the BufferedImage...");
		Graphics2D graphics = buffImg.createGraphics();
		graphics.setColor(new Color(255, 185, 185, 0));
		log.debug("Filling the BufferedImage...");
		graphics.fillRect(0, 0, buffImg.getWidth(), buffImg.getHeight());
		graphics.finalize();
		log.debug("Buffered Image filled.");
	}

	/**
	 * Creates an empty {@link BufferedImage} and returns it.
	 *
	 * @param width
	 *            the width of the image to create
	 * @param height
	 *            the height of the image to create
	 * @return the generated {@link BufferedImage}
	 */
	public BufferedImage createEmptyImage(int width, int height) {
		log.debug("Absoluting the values...");
		width = (int) Math.abs(width);
		height = (int) Math.abs(height);

		log.debug("Creating a new BufferedImage...");
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		log.debug("Setting the standard background color to the created BufferedImage...");
		this.fillStdColor(buffImg);

		log.debug("Returning the new BufferedImage...");

		return buffImg;
	}

	/**
	 * Converts a {@link BufferedImage} to an iText {@link Image}
	 *
	 * @param buffImg
	 *            the {@link BufferedImage} to convert
	 * @param transparency
	 *            the transparency of the {@link Image} to create as a value
	 *            between 0 and 255.
	 * @return the converted iText {@link Image}
	 * @throws ImageCovertingException
	 */
	public Image convertToImage(BufferedImage buffImg, int transparency) throws ImageCovertingException {
		try {
			if (transparency > 255)
				transparency = 255;

			log.debug("Converting the image and returning it...");

			BufferedImage buffImg2 = new BufferedImage(buffImg.getWidth(), buffImg.getHeight(), BufferedImage.TYPE_INT_ARGB);

			buffImg2 = Thumbnails.of(buffImg2).size(buffImg.getWidth(), buffImg.getHeight()).imageType(BufferedImage.TYPE_INT_ARGB)
					.watermark(Positions.BOTTOM_LEFT, buffImg, (float) transparency / 255.0f).asBufferedImage();

			buffImg = null;

			return Image.getInstance(buffImg2, null);

		} catch (BadElementException | IOException e) {
			log.error(e.getMessage().toString());
			throw new ImageCovertingException();
		}
	}

	/**
	 * Scales the given iText {@link Image} with the given factor in percent and
	 * returns it.
	 *
	 * @param img
	 *            the {@link Image} to scale
	 * @param factor
	 *            the factor to scale about
	 * @return the {@link Image}
	 */
	public Image scale(Image img, float factor) {
		log.debug("Scaling the BufferedImage by the given factor...");
		img.scalePercent(factor);
		log.debug("Returning the scaled Image...");
		return img;
	}

	/**
	 * Scales the given {@link Image} to fit the given inch-values in width and
	 * height.
	 *
	 * @param img
	 *            the {@link Image} to scale
	 * @param inchesHeight
	 *            the height to fit in inches
	 * @param inchesWidth
	 *            the width to fit in inches
	 * @return
	 */
	public Image scaleToFitInches(Image img, double inchesWidth, double inchesHeight) {
		log.debug("Rounding the given inch values after converting them to standard-pixel values...");
		float w = (int) Math.round(inchesWidth * 72);
		float h = (int) Math.round(inchesHeight * 72);
		log.debug("Scaling the image to the given pixel size...");
		img.scaleAbsolute(w, h);
		log.debug("Returning th scaled image...");
		return img;
	}

	// TODO : TEST METHOD
	/**
	 * Scales the given {@link Image} to fit the given maximal inch-value; in X-
	 * or Y- direction.
	 *
	 * @param img
	 *            the {@link Image} to scale
	 * @param inchesMax
	 *            the maximum value in inches as {@link Double}
	 * @return the scaled {@link Image}
	 */
	public Image scaleToFitInchesMax(Image img, double inchesMax) {
		log.debug("Rounding the given inch values after converting them to standard-pixel values...");
		float w_px = (int) Math.round(inchesMax * 72);
		float h_px = (int) Math.round(inchesMax * 72);

		if (w_px < h_px) {
			log.debug("Width is smaller than the height. Using the width to calculate the factor...");
			float w_factor = w_px / img.getWidth();
			log.debug("Scaling the image...");
			img.scalePercent(w_factor);
		} else {
			log.debug("Width is smaller than the height. Using the width to calculate the factor...");
			float h_factor = (h_px / img.getHeight())*100;
			log.debug("Scaling the image...");
			img.scalePercent(h_factor);
		}
		log.debug("Returning th scaled image...");
		return img;
	}

	/**
	 * Returns a {@link LayerImage} computed from the parted images of the
	 * {@link TileArray}.
	 *
	 * @param t
	 *            the {@link TileArray} containing the parted image.
	 * @return a {@link LayerImage}
	 */
	public BufferedImage computeImage(TileArray t) {
		log.debug("Creating an empty BufferedImage to be filled from the Tile's BufferedImages...");
		BufferedImage buffImg = this.createEmptyImage(t.getImgWidth(), t.getImgHeight());

		// THE START VALUES
		int actX = 0;
		int actY = 0;

		// IN X DIRECTION
		for (int cols = 0; cols < t.getColumns(); cols++) {
			// IN Y DIRECTION
			for (int rows = 0; rows < t.getRows(); rows++) {
				log.debug("Creating graphics for the Tile " + rows + " (row), " + cols + "(column...)");
				log.debug("Drawing the Tile's BufferedImage into the combined BufferedImage...");
				buffImg.createGraphics().drawImage(t.getTiles()[cols][rows].getTileImage(), actX, actY, t.getTiles()[cols][rows].getImageWidth(), t.getTiles()[cols][rows].getImageHeight(), null);

				// DISPOSE THE GRAPHICS
				buffImg.getGraphics().dispose();

				// COUNT ON IN X DIRECTION
				actY = actY + t.getTiles()[cols][rows].getImageHeight();

				// IF THE END IS REACHED
				if (rows == t.getRows() - 1) {
					// COUNT ON IN Y DIRECTION
					actX = actX + t.getTiles()[cols][rows].getImageWidth();
					// RESET X DIRECTION
					actY = 0;
				}
			}
		}
		// MAYBE WRITE THE IMAGE TO THE FILE SYSTEM TO CONTROL IT
		// this.writeToFileSystem(buffImg);
		log.debug("Returning the computed BufferedImage...");
		return buffImg;
	}

	/**
	 * Writes a {@link BufferedImage} to the file system.
	 * 
	 * Gives it the current system time in milliseconds as name.
	 *
	 * @param buff
	 *            the {@link BufferedImage} to write
	 */
	public void writeToFileSystem(BufferedImage buff) {
		try {
			log.info("Writing a BufferedImage to the filesystem...");
			File outputfile = new File("./output/" + System.currentTimeMillis() + ".png");
			ImageIO.write(buff, "png", outputfile);
			log.info("BufferedImage written.");
		} catch (IOException e) {
			log.error("Could not write image to the file system!");
		}
	}

	/**
	 * Takes a {@link BufferedImage} and some other values to create a
	 * {@link LayerImage} and returns it.
	 * 
	 * o the offsets to the {@link Map} will be set to the {@link LayerImage} o
	 * the inches to cover-values are used to resize the {@link Image}
	 *
	 * @param buffImg
	 *            the {@link BufferedImage}
	 * @param xOffset
	 *            the offset to the {@link Map} in X-direction
	 * @param yOffset
	 *            the offset to the {@link Map} in Y-direction
	 * @param inchesToCoverWidth
	 *            the inches to cover in width
	 * @param inchesToCoverHeight
	 *            the inches to cover in height
	 * @return a new {@link LayerImage}
	 * @throws ImageCovertingException
	 */
	public LayerImage convertToLayerImage(BufferedImage buffImg, double xOffset, double yOffset, double inchesToCoverWidth, double inchesToCoverHeight, int transparency)
			throws ImageCovertingException {
		// CREATE THE LAYERIMAGE
		LayerImage erg = new LayerImage();

		// SET THE IMAGE BY CONVERTING AND RESIZING IT
		erg.setImage(this.convertAndScale(buffImg, inchesToCoverWidth, inchesToCoverHeight, transparency));

		// SET THE OFFSET
		erg.setxOffset(xOffset);
		erg.setyOffset(yOffset);

		return erg;
	}

	/**
	 * Converts a {@link BufferedImage} to an {@link Image} and scales it to fit
	 * the given inch-values in width and height.
	 *
	 * @param buffImg
	 *            the {@link BufferedImage} to convert
	 * @param inchesToCoverWidth
	 *            the inches to cover in width
	 * @param inchesToCoverHeight
	 *            the inches to cover in height
	 * @return the converted and scaled {@link Image}
	 * @throws ImageCovertingException
	 */
	private Image convertAndScale(BufferedImage buffImg, double inchesToCoverWidth, double inchesToCoverHeight, int transparency) throws ImageCovertingException {
		log.debug("Converting the BufferedImage to an iText Image...");
		Image i = this.convertToImage(buffImg, transparency);
		log.debug("Scaling the image...");
		i = this.scaleToFitInches(i, inchesToCoverWidth, inchesToCoverHeight);
		log.debug("Returning the Image...");
		return i;
	}

	// GETTERS AND SETTERS

	// OTHERS
}
