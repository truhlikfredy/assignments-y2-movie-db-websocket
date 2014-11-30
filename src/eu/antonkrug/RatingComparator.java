package eu.antonkrug;

import java.util.Comparator;
import java.util.Map;

class RatingComparator implements Comparator<Movie> {

  Map<Integer, Movie> base;
  
  public RatingComparator(Map<Integer, Movie> base) {
      this.base = base;
  }

  // ordering is inconsistent with equals    
  public int compare(Movie a, Movie b) {
      if (a.getAverageRating() >= b.getAverageRating()) {
          return -1;
      } else {
          return 1;
      } // returning 0 would merge keys
  }
}