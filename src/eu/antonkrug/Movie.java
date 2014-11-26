package eu.antonkrug;

import java.io.Serializable;

/**
 * Movie class containg name, year and genre
 * 
 * @author Anton Krug
 * 
 */
public class Movie implements Serializable {

	/**
	 * Generate new ID if you will change anything in this class, if you change
	 * stuff often then comment it out and let the compiler generate one for you
	 * which will change automaticly if you will do any modifications to the class
	 */
	private static final long	serialVersionUID	= -2527051303955958396L;

	private MovieGenre				category;
	private String						name;
	private int								ratingCount;
	private int								ratingSum;
	private Integer						year;

	public Movie(String name, Integer year, MovieGenre category) {
		this.name = name;
		this.year = year;
		this.category = category;
		this.ratingSum = 0;
		this.ratingCount = 0;
	}

	/**
	 * Adds new rating to the sum and increments counter as well
	 * 
	 * @param value
	 */
	public void addRating(byte value) {
		this.ratingSum += value;
		this.ratingCount++;
	}

	/**
	 * Calculates and returns the average rating for this movie
	 * @return
	 */
	public double getAverageRating() {
		return (double) this.ratingSum / this.ratingCount;
	}

	public MovieGenre getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the ratingCount
	 */
	public int getRatingCount() {
		return ratingCount;
	}

	/**
	 * @return the ratingSum
	 */
	public int getRatingSum() {
		return ratingSum;
	}

	public Integer getYear() {
		return year;
	}

	public void setCategory(MovieGenre category) {
		this.category = category;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param ratingCount
	 *          the ratingCount to set
	 */
	public void setRatingCount(int ratingCount) {
		this.ratingCount = ratingCount;
	}

	/**
	 * @param ratingSum
	 *          the ratingSum to set
	 */
	public void setRatingSum(int ratingSum) {
		this.ratingSum = ratingSum;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "Movie [name=" + name + ", year=" + year + ", category=" + category + "]";
	}

}
