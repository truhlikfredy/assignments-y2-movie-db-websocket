package eu.antonkrug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

import mws.WebSocket;
import mws.handshake.ClientHandshake;
import mws.server.WebSocketServer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * Main Class which is running server instance of the application
 * 
 * @author Anton Krug
 * 
 */
public class Server extends WebSocketServer {

	public final static Boolean	VERBOSE		= true;

	public Boolean							runServer	= true;

	/**
	 * Outputs logging message on the console, will print out web socket
	 * connection and time as well.
	 * 
	 * @param ws
	 * @param txt
	 */
	private void log(WebSocket ws, String txt) {
		System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " : "
				+ ws.getRemoteSocketAddress() + " " + txt);
	}

	/**
	 * Checking if the port is open and not used
	 * 
	 * @param port
	 * @return
	 */
	public static boolean available(int port) {
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	static class Message extends Thread {

		public void run() {
			System.out.println("Exiting...");
			if (DB.obj().isLoaded()) {
				// data was loaded, saving it

				// TODO do rotation of files
				String fileName = DB.obj().getLoadedFileName();
				DBInputOutputEnum data = DBInputOutputEnum.getInstance(fileName);
				data.save();
			}
			System.out.println("Bey.");
		}
	}

	/**
	 * The main method to kick off the server
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException {

		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Message());

		// The port number. It's over 9000!! But let's check if it's free first.
		int port = 9001;
		if (Server.available(port)) {
			Server svr = new Server(port);
			svr.start();

			BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String in;
				try {
					in = sysin.readLine();
					if (in.equals("exit") || !svr.runServer) {
						svr.stop();
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} else {
			System.out.println("ERROR: Port " + port + " taken!");
		}
	}

	private DB	db;

	public Server(int port) {
		super(new InetSocketAddress(port));

		db = DB.getInstance();
		System.out.println("WS on port " + port + " listening ... ");
		System.out.println("To shutdown the server type: exit and press return. ");
	}

	private Boolean RLogIn(String name, String pass) {

	}

	public void importData() {
		// FileCSV importer = new FileCSV("films_fx.csv","ratings_fx.csv");
		// importer.loadMovies();
		// importer.loadRatings();

		// DBInputOutput xml = new DBInputOutput();
		// xml.saveXML("sasa");
		// xml.loadXML("sasa");
		// xml.loadDat("test.dat");
		// DB.obj().compatibilityForEachUser();
		// DB.obj().purgeCacheForEachUser();
		// xml.saveXML("sasa2");
		// xml.saveDat("test.dat");

		DBInputOutputEnum data = DBInputOutputEnum.getInstance("movies.csv");
		data.load();

		// System.out.println(DB.obj().users.get(0).ratingMovie.get(0));
		// User usr = DB.obj().getUsers().get(0);
		User usr = DB.obj().getUsers().get(0);
		usr.calculateAll();
		System.out.println("Done.");

	}

	@Override
	public void onOpen(WebSocket ws, ClientHandshake ch) {
		this.log(ws, "connected");
	}

	@Override
	public void onClose(WebSocket ws, int i, String string, boolean bln) {
		this.log(ws, "disconnected");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onMessage(WebSocket ws, String string) {
		this.log(ws, "got " + string);

		JSONObject json = (JSONObject) JSONValue.parse(string);

		API request = API.getEnum(json.get("t"));
		System.out.println(request);

		switch (request) {

		// will ignore answer types and non valid ones
			case A_IS_ADMIN:
			case A_LIST_MOVIES:
			case A_LIST_RATINGS:
			case A_LIST_USERS:
			case A_MOVIE:
			case A_OK_DIALOG:
			case A_PASS_FAIL:
			case A_USER:
			case NOT_VALID:
				if (VERBOSE) System.out.print("Got wrong request type: " + request);
				break;

			case R_ADD_MOVIE:
				break;
			case R_ADD_USER:
				break;
			case R_CACHE_DIRTY:
				break;
			case R_EDIT_USER:
				break;
			case R_GET_MOVIE:
				break;
			case R_GET_USER:
				break;
			case R_LIST_GENRES:
				break;
			case R_LIST_MOVIES:
				break;
			case R_LIST_RATINGS:
				break;
			case R_LIST_USERS:
				break;

			case R_LOAD:
				if (ws.isAdmin()) {
					DBInputOutputEnum data = DBInputOutputEnum.getInstance(json.get("file").toString());
					data.load();
				}
				break;

			case R_LOGIN:
				if (VERBOSE)
					this.log(ws, "Tries to log in:" + json.get("name") + " password " + json.get("pass"));

				JSONObject jsonR = new JSONObject();
				jsonR.put("t", API.A_PASS_FAIL);

				if (this.RLogIn(json.get("name").toString(), json.get("pass").toString())) {
					jsonR.put("val", true);				
				} else {
					jsonR.put("val", false);
				}
				
				ws.send(jsonR.toString());

				break;

			case R_LOGOUT:
				break;
			case R_PURGE_CACHE:
				break;
			case R_RATE:
				break;
			case R_REMOVE_MOVIE:
				break;
			case R_REMOVE_USER:
				break;
			case R_SAVE:
				break;
			default:
				break;

		}
	}

	@Override
	public void onError(WebSocket ws, Exception excptn) {
		this.log(ws, " ERROR! " + excptn.toString());
	}

}
