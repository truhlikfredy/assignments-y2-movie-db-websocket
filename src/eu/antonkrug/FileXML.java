package eu.antonkrug;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class FileXML {

	public ArrayList<MovieGenre>		genres;
	public Boolean									genresDirty;
	public HashMap<Integer, Movie>	movies;
	public ArrayList<User>					users;

	public void saveXML(String fileName) {
		DB db = DB.obj();

		this.genres = db.getGenres();
		this.genresDirty = db.getGenresDirty();
		this.movies = db.getMovies();
		this.users = db.getUsers();
		
		XStream xstream = new XStream(new DomDriver());
//		System.out.println(xstream.toXML(this));
		
		PrintWriter out;
		
		try {
			out = new PrintWriter(fileName);
			out.println(xstream.toXML(this));
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to write to file.");
			e.printStackTrace();
		}

	}
	
	public void loadXML(String fileName) {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(fileName));
			XStream xstream = new XStream(new DomDriver());
			FileXML tmp = (FileXML)xstream.fromXML(new String(encoded, "UTF-8"));

			DB db = DB.obj();
			db.setGenres(tmp.genres);
			db.setGenresDirty(tmp.genresDirty);
			db.setMovies(tmp.movies);
			db.setUsers(tmp.users);
			
		} catch (IOException e) {
			System.out.println("ERROR: XML Load failed!");
			e.printStackTrace();
		}
		
		
	}
}
