package eu.antonkrug;

/**
 * Main Class which is running server instance of the application
 * 
 * @author Anton Krug
 * 
 */
public class Server {

	public final static Boolean	VERBOSE	= true;

	public static void main(String[] args) {
		Server svr = new Server();
		svr.importData();
	}

	private DB db;
	
	public Server() {
		db=DB.getInstance();
	}
	
	public void importData() {
		FileCSV importer = new FileCSV("films_fx.csv","ratings_fx.csv");
//		importer.loadMovies();
//		importer.loadRatings();
		
		DBInputOutput xml = new DBInputOutput();
//		xml.saveXML("sasa");
//		xml.loadXML("sasa");
		xml.loadDat("test.dat");
		DB.obj().compatibilityForEachUser();
		DB.obj().purgeCacheForEachUser();
		xml.saveXML("sasa2");
//		xml.saveDat("test.dat");
		
		
//		System.out.println(DB.obj().users.get(0).ratingMovie.get(0));
//		User usr = DB.obj().getUsers().get(0);
		User usr = DB.obj().getUsers().get(0);
		usr.calculateAll();
		System.out.println("Done.");		
		
	}




}
