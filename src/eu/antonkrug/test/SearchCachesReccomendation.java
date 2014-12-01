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

	/*
	@Test
	public void testRecomended() {
		// this will test sorting and caches as well

		// how many recomendings
		assertEquals(35, user.reccomendations().size());

		// purge precalculated data
		db.purgeCacheForEachUser();
		db.compatibilityForEachUser();
		user.calculateAll();

		// check if get same result
		assertEquals(35, user.reccomendations().size());

		//check and rate first recomended movie
		Movie movie = user.reccomendations().get(0);
		assertEquals("War", movie.getName());
		assertEquals(36,movie.getId());
		user.rateMovie(movie.getId(),(byte)5);

		// check if it's excluded
		assertEquals(34, user.reccomendations().size());
		
		// remove this users ratings
		user.removeAllRatings();

		// can't match your trend with anybody so is not recomending anything
		assertEquals(0, user.reccomendations().size());
	}
	*/
	
	@Test
	public void testRecomendedTrend() {
		//checks if recomending changes in the way it's desired
		
		assertEquals("John Moose", user.getFullName());
		
		User otherUser = db.getUsers().get(7);
		assertEquals("Irene Baker", otherUser.getFullName());

		//no cache calculated
		assertEquals(null, user.getTopCache());
		
		//now who is compatible when it's calculated
		user.calculateAll();
		assertEquals("Andrew Smores", user.getTopCache().get(0).getUser().getFullName());

		//and got reccomended War as first choice 
		assertEquals("Girl Interrupted", user.reccomendations().get(0).getName());		
		
		//make johns rating the same as irene's so they should be very compatible
		user.removeAllRatings();
		for (int i=0;i<otherUser.getRatingMovie().size();i++) {
			int movieID = otherUser.getRatingMovie().get(i);
			byte movieRating = otherUser.getRatingRating().get(i);
			
			user.rateMovie( movieID, movieRating);
		}
		
		//see if most compatible person now is Irene instead of Andrew Smores
		user.calculateAll();
		assertEquals(otherUser,user.getTopCache().get(0).getUser());
		
		//and we should get different recomendation
		user.calculateAll();
		assertEquals("Empire Records", user.reccomendations().get(0).getName());
//		int movieForDownVote = user.reccomendations().get(0).getId();
				
		//Irene will downvote our top recomended movie.		
//		otherUser.rateMovie(movieForDownVote,(byte)-5);

		//And John's top recomedation should change
//		user.calculateAll();
//		assertEquals("Empire Records", user.reccomendations().get(0).getName());
	
		
		//Irene will now rate 
		//BTW IMO whe reading the plot, this movie doesn't sound very good !
		//Really this is hypotethical situation that we will give it 5 rating.
		int movieForUpVote = user.reccomendations().get(1).getId();
		assertEquals("Hairspray",db.getMovie(movieForUpVote).getName());
		
		for (Cache compatibleUser:user.getTopCache()) {
			compatibleUser.getUser().rateMovie(movieForUpVote, (byte)5);
		}
		
		otherUser.rateMovie(movieForUpVote, (byte)5 );
		
		user.calculateAll();
		assertEquals("Fever Pitch", user.reccomendations().get(0).getName());
		
		
		
		
		// purge precalculated data
		db.purgeCacheForEachUser();
		db.compatibilityForEachUser();
		user.calculateAll();
		
		// check if get same result
		assertEquals(35, user.reccomendations().size());
		
		//check and rate first recomended movie
		Movie movie = user.reccomendations().get(0);
		assertEquals("War", movie.getName());
		assertEquals(36,movie.getId());
		user.rateMovie(movie.getId(),(byte)5);
		
		// check if it's excluded
		assertEquals(34, user.reccomendations().size());
		
		// remove this users ratings
		user.removeAllRatings();
		
		// can't match your trend with anybody so is not recomending anything
		assertEquals(0, user.reccomendations().size());
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
