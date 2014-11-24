package eu.antonkrug;

public enum DBInputOutputEnum {

	CSV(1) {
		public void load() {
			this.getDbio().loadCVS(this.getFileName());
		}
	},
	DAT(3) {
		public void load() {
			this.getDbio().loadDAT(this.getFileName());
		}
	},
	NULL(0) {
		public void load() {
			System.out.println("ERROR: Tried to load unknow file!");
		}
	},
	XML(2) {
		public void load() {
			this.getDbio().loadXML(this.getFileName());
		}
	};

	/**
	 * Will get extensions from filename and decide what enum will return
	 * 
	 * @param fileName
	 * @return
	 */
	public static DBInputOutputEnum getInstance(String fileName) {
		// detect extension of file
		String extension = "";

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
			extension = fileName.substring(i + 1).toLowerCase();
		}

		// by extensions return correct ENUM
		DBInputOutputEnum ret = DBInputOutputEnum.NULL;

		if (extension.equals("cvs")) ret = DBInputOutputEnum.CSV;
		if (extension.equals("xml")) ret = DBInputOutputEnum.XML;
		if (extension.equals("dat")) ret = DBInputOutputEnum.DAT;

		ret.setFileName(fileName);
		return ret;
	}

	private DBInputOutput	dbio;
	private String				fileName;

	private int						type;

	/**
	 * Constructor for enum
	 * @param type
	 */
	private DBInputOutputEnum(int type) {
		this.type = type;
		this.dbio = new DBInputOutput();
	}

	/**
	 * @return the dbio
	 */
	public DBInputOutput getDbio() {
		return dbio;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the type
	 */
	public int getValue() {
		return type;
	}

	/**
	 * Abstract method for load, depedning on enum it will be doing something else
	 */
	public abstract void load();

	/**
	 * @param dbio
	 *          the dbio to set
	 */
	public void setDbio(DBInputOutput dbio) {
		this.dbio = dbio;
	}

	/**
	 * @param fileName
	 *          the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @param type
	 *          the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

}
