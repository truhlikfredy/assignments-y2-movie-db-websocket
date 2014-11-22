package eu.antonkrug;

import java.util.ArrayList;
import java.util.Collections;

public class Database {
	public ArrayList<Movie>					movies;
	public ArrayList<MovieCategory>	categories;

	public Database() {
			this.movies			= new ArrayList<Movie>();
			this.categories	= new ArrayList<MovieCategory>();
	}

	public void addMovie(String name) {
		MovieCategory category = new MovieCategory(name);
		int index = this.categories.indexOf(category);

		if (index >= 0) {
			this.categories.get(index).incTimesUsed();
		} else {
			this.categories.add(new MovieCategory(name));
			Collections.sort(this.categories, MovieCategory.BY_NAME);
		}
	}

}
