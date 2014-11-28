package eu.antonkrug;

import java.io.Serializable;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

//TODO description
/**
 * Movie class containg name, year and genre
 * 
 * @author Anton Krug
 * 
 */
public class Movie implements Serializable, JSONAware {

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
		if (this.ratingCount>0) {
			return (double) this.ratingSum / this.ratingCount;
		} else {
			return 0.0;
		}
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
		return "Movie [category=" + category + ", name=" + name + ", year=" + year
				+ ", averageRating=" + getAverageRating() + "]";
	}
	
	

	@Override
	public String toJSONString() {
    StringBuffer sb = new StringBuffer();
    
    sb.append("{");
    sb.append("\"name\":");
    sb.append("\"" + JSONObject.escape(this.name) + "\"");
    sb.append(",\"year\":");
    sb.append(this.year);
    sb.append(",\"genre\":");
    sb.append(this.getCategory().toJSONString());
    sb.append(",\"averageRating\":");
    sb.append(this.getAverageRating());
    sb.append("}");
    
    return sb.toString();		
	}
	
}
