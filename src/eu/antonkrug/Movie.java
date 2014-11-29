package eu.antonkrug;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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
	private String						plot;
	private String						coverImageURL;
	private String						actors;

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
	
	/**
	 * @return the plot
	 */
	public String getPlot() {
		return plot;
	}

	/**
	 * @param plot the plot to set
	 */
	public void setPlot(String plot) {
		this.plot = plot;
	}

	/**
	 * @return the coverImageURL
	 */
	public String getCoverImageURL() {
		return coverImageURL;
	}
	
	/**
	 * @return the actors
	 */
	public String getActors() {
		return actors;
	}

	/**
	 * @param actors the actors to set
	 */
	public void setActors(String actors) {
		this.actors = actors;
	}
	

	/**
	 * @param coverImageURL the coverImageURL to set
	 */
	public void setCoverImageURL(String coverImageURL) {
		this.coverImageURL = coverImageURL;
	}
	
	/**
	 * Get meta informations from IMDB and store some into our database
	 */
	public void populateIMDBmeta() {
		try {
			String result=IOUtils.toString(new URL("http://www.imdbapi.com/?i=&t="+URLEncoder.encode(name,"UTF-8")+"&y="+year));
			
			JSONObject json = (JSONObject) JSONValue.parse(result);
			if (json.get("Response").equals("True")) {
				plot=json.get("Plot").toString();
				actors=json.get("Actors").toString();
				coverImageURL=json.get("Poster").toString();
//				System.out.println(result);
//				System.out.println(json.get("Poster"));
//				System.out.println(json.get("Actors"));
//				System.out.println(json.get("Plot"));
				
			}
			
			
		} catch (MalformedURLException e) {
			System.out.println("Bad URL ");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Can't load the URL ");
			e.printStackTrace();
		}
		
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
    sb.append(",\"plot\":");
    sb.append("\""+JSONObject.escape(this.getPlot())+"\"");
    sb.append(",\"coverImageURL\":");
    sb.append("\""+JSONObject.escape(this.getCoverImageURL())+"\"");
    sb.append(",\"actors\":");
    sb.append("\""+JSONObject.escape(this.getActors())+"\"");
    sb.append("}");
    
    return sb.toString();		
	}
	
}
