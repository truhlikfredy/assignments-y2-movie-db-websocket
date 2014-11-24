package eu.antonkrug;

import java.io.Serializable;

/**
 * Movie class containg name, year and genre
 * 
 * @author Anton Krug
 * 
 */
public class Movie implements Serializable{

	/**
	 * Generate new ID if you will change anything in this class, if you change
	 * stuff often then comment it out and let the compiler generate one for you
	 * which will change automaticly if you will do any modifications to the class
	 */
	private static final long	serialVersionUID	= -2527051303955958396L;
	
	private String			name;
	private Integer			year;
	private MovieGenre	category;

	public Movie(String name, Integer year, MovieGenre category) {
		this.name = name;
		this.year = year;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public MovieGenre getCategory() {
		return category;
	}

	public void setCategory(MovieGenre category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Movie [name=" + name + ", year=" + year + ", category=" + category + "]";
	}

}
