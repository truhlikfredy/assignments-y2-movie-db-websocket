package eu.antonkrug.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.antonkrug.DB;
import eu.antonkrug.DBInputOutputEnum;
import eu.antonkrug.Movie;
import eu.antonkrug.Rating;
import eu.antonkrug.User;

public class RatingTest {

	private DBInputOutputEnum	data;
	private DB								db;
	private User							user;

	@Before
	public void setUp() throws Exception {
		data = DBInputOutputEnum.getInstance("JUnitTestData/database.dat");
		data.load();
		this.db = DB.getInstance();
		this.user = db.getUsers().get(1);
	}

	// this will test User & sorting as well
	@Test
	public void testRating() {

		db.compatibilityForEachUser();
		user.calculateAll();
		assertEquals(55, db.getMovies().size());

		Movie movie = db.getMovies().get(1);

		assertEquals(3.833, movie.getAverageRating(), 0.01);

		// is already rated by this user
		assertEquals(true, user.ratedMovie(1));

		// how many movies this user already rated
		assertEquals(19, user.getRatingMovie().size());

		// rate already rated
		user.rateMovie(1, (byte) 5);
		assertEquals(3.833, movie.getAverageRating(), 0.01);

		// update that rating
		user.rateMovie(1, (byte) -5);
		assertEquals(3.555, movie.getAverageRating(), 0.01);

		// add anonymous rating without user
		movie.addRating((byte) 5);
		assertEquals(3.594, movie.getAverageRating(), 0.01);

		// add rating for diffrent movie
		user.rateMovie(3, (byte) -5);

		// ratings collection size should change
		assertEquals(20, user.getRatingMovie().size());
	}

	@Test
	public void ratingConvertion() {

		//from string
		assertEquals(Rating.LIKED, Rating.getRating("3"));
		
		//from int
		assertEquals(Rating.DIDNT_LIKED, Rating.getRating(-3));
		
		//convert from 1-5 to our rating system		
		// wrong one
		assertEquals(Rating.NOT_SEEN, Rating.getFromOneToFiveRating((byte) -5));		
		//correct ones
		assertEquals(Rating.TERRIBLE, Rating.getFromOneToFiveRating((byte) 1));
		assertEquals(Rating.REALLY_LIKED, Rating.getFromOneToFiveRating((byte) 5));
		
		//and converting back
		assertEquals((byte) 1,Rating.toZeroToFive((byte)-5));
		assertEquals((byte) 3,Rating.toZeroToFive((byte)1));
	}

}
