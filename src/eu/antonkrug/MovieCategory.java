package eu.antonkrug;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Movie category class.
 * 
 * @author Anton Krug
 * 
 */

public class MovieCategory implements Serializable {

	public static final Comparator<MovieCategory>	BY_NAME						= new ByName();
	public static final Comparator<MovieCategory>	BY_USAGE					= new ByUsage();

	/**
	 * Generate new ID if you will change anything in this class, if you change
	 * stuff often then comment it out and let the compiler generate one for you
	 * which will change automaticly if you will do any modifications to the class
	 */
	private static final long											serialVersionUID	= 5470272780131654013L;

	private String																name;
	private int																		timesUsed;

	public MovieCategory(String name) {
		this.name = name;
		this.timesUsed = 0;
	}

	private static class ByName implements Comparator<MovieCategory> {
		public int compare(MovieCategory left, MovieCategory right) {
			return left.name.compareTo(right.name);
		}
	}

	private static class ByUsage implements Comparator<MovieCategory> {
		public int compare(MovieCategory left, MovieCategory right) {
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MovieCategory other = (MovieCategory) obj;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	

}
