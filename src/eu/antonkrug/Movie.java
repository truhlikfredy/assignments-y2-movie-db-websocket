package eu.antonkrug;

public class Movie {

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
