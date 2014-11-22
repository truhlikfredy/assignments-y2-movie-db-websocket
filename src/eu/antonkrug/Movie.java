package eu.antonkrug;

import java.util.ArrayList;

public class Movie {

	private String									name;
	private Integer									year;
	private MovieCategory						category;

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

	public MovieCategory getCategory() {
		return category;
	}

	public void setCategory(MovieCategory category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Movie [name=" + name + ", year=" + year + ", category=" + category + "]";
	}

	
}
