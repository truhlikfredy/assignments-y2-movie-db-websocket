package eu.antonkrug;

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
		importer.loadMovies();
		importer.loadRatings();
	}




}
