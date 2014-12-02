package eu.antonkrug.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.antonkrug.Cache;
import eu.antonkrug.DB;
import eu.antonkrug.DBInputOutputEnum;
import eu.antonkrug.Movie;
import eu.antonkrug.User;

public class SearchCachesReccomendation {

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

	@Test
	public void testRecomended() {
		// this will test sorting and caches as well

		// how many recomendings
		assertEquals(30, user.reccomendations().size());

		// purge precalculated data
		db.purgeCacheForEachUser();
		db.compatibilityForEachUser();
		user.calculateAll();

		// check if get same result
		assertEquals(30, user.reccomendations().size());

		// get cached reccomendation results without performance penalty
		assertEquals(30, user.getReccomended().size());

		// check and rate first recomended movie top #1
		Movie movie = user.reccomendations().get(0);
		assertEquals("Once Upon a Time in Mexico", movie.getName());
		assertEquals(44, movie.getId());
		user.rateMovie(movie.getId(), (byte) 5);

		// check if after you rated it, it's not recomended to you anymore and top#1
		// is something else
		assertEquals("Girl Interrupted", user.reccomendations().get(0).getName());

		// remove this users ratings
		user.removeAllRatings();

		// can't match your trend with anybody so is not recomending anything
		assertEquals(0, user.reccomendations().size());
	}

	@Test
	public void testRecomendedUpDownVoting() {
		// checks if recomending changes in the way it's desired

		assertEquals("John Moose", user.getFullName());

		User otherUser = db.getUsers().get(7);
		assertEquals("Irene Baker", otherUser.getFullName());

		// no cache calculated
		assertEquals(null, user.getTopCache());

		// now who is compatible when cache is calculated
		user.calculateAll();
		assertEquals("Andrew Smores", user.getTopCache().get(0).getUser().getFullName());

		// and got reccomended as first choice
		assertEquals("Once Upon a Time in Mexico", user.reccomendations().get(0).getName());

		// make johns rating the same as irene's so they should be very compatible
		user.removeAllRatings();
		for (int i = 0; i < otherUser.getRatingMovie().size(); i++) {
			int movieID = otherUser.getRatingMovie().get(i);
			byte movieRating = otherUser.getRatingRating().get(i);

			user.rateMovie(movieID, movieRating);
		}

		// see if most compatible person now is Irene instead of Andrew Smores
		user.calculateAll();
		assertEquals(otherUser, user.getTopCache().get(0).getUser());

		// recomendations will recalculate user compatibility cache if needed
		// and we should get different recomendation
		assertEquals("Stick It", user.reccomendations().get(0).getName());
		assertEquals("Girl Interrupted", user.reccomendations().get(1).getName());

		// Irene will downvote top #1 and upvote top #2
		otherUser.rateMovie(user.reccomendations().get(0).getId(), (byte) -5);
		otherUser.rateMovie(user.reccomendations().get(1).getId(), (byte) 5);

		// And john's recomendations should reflect the change
		assertEquals("Girl Interrupted", user.reccomendations().get(0).getName());
		assertEquals("Stick It", user.reccomendations().get(1).getName());

		// Now everybody who is compatible with john will rate high his top 5 movie
		int movieForUpVote = user.reccomendations().get(5).getId();
		assertEquals("The Limey", db.getMovie(movieForUpVote).getName());

		for (Cache compatibleUser : user.getTopCache()) {
			compatibleUser.getUser().rateMovie(movieForUpVote, (byte) 5);
		}

		// And should be enough to promote that movie from top 5 to top 1 for john
		assertEquals("The Limey", user.reccomendations().get(0).getName());
	}

	@Test
	public void testSearch() {
		// case sensitivity
		assertEquals(1, db.searchMovie("baTman", user).size());

		// invalid query
		assertEquals(0, db.searchMovie("bewqewq EWQEWQEW", user).size());

		// single movie
		assertEquals(1, db.searchMovie("grape", user).size());

		// single movie & category (bigraphy)
		assertEquals(3, db.searchMovie("grap", user).size());

		// single category
		assertEquals(2, db.searchMovie("graph", user).size());
	}

}
