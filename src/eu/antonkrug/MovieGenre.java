package eu.antonkrug;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Movie genre category class. It keeps tracks how many each category was used.
 * Renaming of a Category can be done from one place and will affect all Movies
 * using this Genre
 * 
 * @author Anton Krug
 * 
 */

public class MovieGenre implements Serializable {

	public static final Comparator<MovieGenre>	BY_NAME						= new ByName();
	public static final Comparator<MovieGenre>	BY_USAGE					= new ByUsage();

	/**
	 * Generate new ID if you will change anything in this class, if you change
	 * stuff often then comment it out and let the compiler generate one for you
	 * which will change automaticly if you will do any modifications to the class
	 */
	private static final long										serialVersionUID	= 5470272780131654013L;

	private String															name;
	private int																	timesUsed;

	public MovieGenre(String name) {
		this.name = name;
		this.timesUsed = 0;
	}

	private static class ByName implements Comparator<MovieGenre> {
		public int compare(MovieGenre left, MovieGenre right) {
			return left.name.compareTo(right.name);
		}
	}

	private static class ByUsage implements Comparator<MovieGenre> {
		public int compare(MovieGenre left, MovieGenre right) {
			return left.timesUsed - right.timesUsed;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTimesUsed() {
		return timesUsed;
	}

	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}

	public void incTimesUsed() {
		this.timesUsed++;
	}

	@Override
	public String toString() {
		return "MovieCategory [name=" + name + ", timesUsed=" + timesUsed + "]";
	}

}
