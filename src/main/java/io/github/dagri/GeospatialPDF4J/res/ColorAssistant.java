package io.github.dagri.GeospatialPDF4J.res;

import java.awt.Color;

/**
 * Class to help the user to retrieve {@link Color}s depending on various input
 * types as gradients.
 * 
 * The class is implemented in singleton-pattern to save RAM-space if it is used
 * at many different places.
 * 
 * @author DaGri
 * @since 06.09.2017
 */
public class ColorAssistant {

	// ATTRIBUTES

	/**
	 * The private {@link ColorAssistant} instance, according to the
	 * singleton-pattern.
	 */
	private static ColorAssistant instance = null;

	/**
	 * {@link Enum} to pick a channel of the RGBA-Color-model.
	 * 
	 * @author DaGri
	 * @since 10.09.2017
	 */
	public enum channel {
		RED, GREEN, BLUE, ALPHA
	};

	// CONSTRUCTORS

	/**
	 * Private constructor for a {@link ColorAssistant} according to the
	 * singleton design pattern.
	 */
	private ColorAssistant() {
		// NOTHING TO DO YET
	}

	// METHODS

	/**
	 * Returns an instance of a {@link ColorAssistant}.
	 *
	 * @return a {@link ColorAssistant}-instance
	 */
	public static ColorAssistant getInstance() {
		if (instance == null)
			instance = new ColorAssistant();
		return instance;
	}

	/**
	 * Returns a {@link Color} gradienting in the given channel from black to the maximum value channel.
	 * 
	 * Choosing alpha-channel will result in a gradient from fully opaque (black) to fully transparent.
	 *
	 * @param min the minimal value of the range
	 * @param max the maximum value of the range
	 * @param act the actual value 
	 * @param channel the channel to change
	 * @return  a new {@link Color}
	 */
	public Color getGradientBlack2Channel(double min, double max, double act, ColorAssistant.channel channel) {
		int r = 0, g = 0, b = 0, a = 0;

		int add = this.calcValue((int) max, (int) min, (int) act);

		if (channel != null)
			if (channel == ColorAssistant.channel.RED) {
				r = add;
			} else if (channel == ColorAssistant.channel.GREEN) {
				g = add;
			} else if (channel == ColorAssistant.channel.BLUE) {
				b = add;
			} else if (channel == ColorAssistant.channel.ALPHA) {
				a = add;
			}
		this.checkRGBA(r, g, b, a);
		
		return new Color(r, g, b, a);
	}

	/**
	 * Returns a {@link Color} gradienting in the given channel from white to the maximum value channel.
	 * 
	 * Choosing alpha-channel will result in a gradient from fully opaque (white) to fully transparent.
	 *
	 * @param min the minimal value of the range
	 * @param max the maximum value of the range
	 * @param act the actual value 
	 * @param channel the channel to change
	 * @return a new {@link Color}
	 */
	public Color getGradientWhite2Channel(double min, double max, double act, ColorAssistant.channel channel) {
		int r = 255, g = 255, b = 255, a = 0;

		int red = this.calcValue((int) max, (int) min, (int) act);

		if (channel != null)
			if (channel == ColorAssistant.channel.RED) {
				g = g - red;
				b = b - red;
			} else if (channel == ColorAssistant.channel.GREEN) {
				r=r-red;
				b=b-red;
			} else if (channel == ColorAssistant.channel.BLUE) {
				r=r-red;
				g=g-red;
			} else if (channel == ColorAssistant.channel.ALPHA) {
				a = a + red;
			}
		this.checkRGBA(r, g, b, a);

		return new Color(r, g, b, a);
	}

	/**
	 * Returns a {@link Color}, defined by the R-G-B values, gradienting in the alpha channel from 0 (act = min value) to 255 (act = max value).
	 *
	 * @param r the red-value of the given color
	 * @param g the green-value of the given color
	 * @param b the blue-value of the given color
	 * @param min the minimal value of the range
	 * @param max the maximum value of the range
	 * @param act the actual value 
	 * @return a new {@link Color}
	 */
	public Color getGradientGivenColorUp(int r, int g, int b, double min, double max, double act) {
		// CALCULATE THE VALUE
		int a = Math.abs(this.calcValue((int) max, (int) min, (int) act));
		this.checkRGBA(r, g, b, a);

		return new Color(r, g, b, a);
	}

	/**
	 * Returns a {@link Color}, defined by the R-G-B values, gradienting in the alpha channel from 255 (act = min value) to 0 (act = max value).
	 *
	 * @param r the red-value of the given color
	 * @param g the green-value of the given color
	 * @param b the blue-value of the given color
	 * @param min the minimal value of the range
	 * @param max the maximum value of the range
	 * @param act the actual value 
	 * @return a new {@link Color}
	 */
	public Color getGradientGivenColorDown(int r, int g, int b, double min, double max, double act) {
		// CALCULATE THE VALUE TO REDUCE
		int red = this.calcValue((int) max, (int) min, (int) act);
		int a = 255 - red;
		this.checkRGBA(r, g, b, a);

		return new Color(r, g, b, a);
	}

	/**
	 * Returns a (positive!) integer-value to be subtracted from 255 to receive
	 * a color gradient to be used on any channel.
	 *
	 * @param min the minimal value of the range
	 * @param max the maximum value of the range
	 * @param act the actual value 
	 * @return an integer value to be used
	 */
	private int calcValue(int max, int min, int act) {
		int erg = (int) (((double) act - (double) min) / ((double) max - (double) min) * 255.0);
		return Math.abs(erg);
	}
	
	/**
	 * Checks if a calculated value is between 0 and 255. Sets the value to zero
	 * if it is < 0 and to 255 if it is > 255.
	 *
	 * @param val the value to check
	 * @return val between 0 and 255
	 */
	private int checkColorValue(int val) {
		if (val < 0)
			val = 0;
		if (val > 255)
			val = 255;
		return val;
	}
	
	/**
	 * Checks if the calculated values (Red, Green, Blue, Alpha) are in the range between 0 and 255. Sets the values to the minimum / maximum if they are outside. 
	 *
	 * @param r the Red channel
	 * @param g the Green channel
	 * @param b the Blue channel
	 * @param a the Alpha channel
	 */
	private void checkRGBA(int r, int g, int b, int a){
		r = this.checkColorValue(r);
		g = this.checkColorValue(g);
		b = this.checkColorValue(b);
		a = this.checkColorValue(a);
	}

	// GETTERS AND SETTERS

	// OTHERS
}
