package eu.antonkrug;

import java.util.ArrayList;
import java.util.Collections;

//import java.util.Arrays;

public class MovieCategories {
	private ArrayList<MovieCategory>	list;

	// private Boolean dirty;

	public MovieCategories() {
		this.list = new ArrayList<MovieCategory>();
		// this.dirty = false;
	}

	public void addMovie(String name) {
		MovieCategory category = new MovieCategory(name);
		int index = list.indexOf(category);

		if (index >= 0) {
			list.get(index).incTimesUsed();
		} else {
			this.list.add(new MovieCategory(name));
			Collections.sort(list, MovieCategory.BY_NAME);
		}
	}
	

}
