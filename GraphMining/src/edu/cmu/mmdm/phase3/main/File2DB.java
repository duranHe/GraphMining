package edu.cmu.mmdm.phase3.main;

import edu.cmu.mmdm.phase3.util.*;

/**
 * Description: Create table and insert the data
 */
public class File2DB {

	public static void main(String[] args) {
		File2DB.run();
	}

	public static void run() {
		File2DB.createTables();
		File2DB.insertEdge();
		File2DB.insertVec();
	}

	public static void createTables() {
		String sql = "";
		//basic
		sql = "CREATE TABLE IF NOT EXISTS advogato (src integer NOT NULL DEFAULT '0',dst integer NOT NULL DEFAULT '0',normadjm double precision DEFAULT '0',CC integer DEFAULT '1', weight real DEFAULT '1', degree real DEFAULT '1', PRIMARY KEY (src, dst))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS advogatovec (Id integer NOT NULL,pr double precision DEFAULT '0',cc integer DEFAULT '0', rwr double precision DEFAULT '0', PRIMARY KEY (Id))";
		SqlManipulate.createTable(sql);
		
		//radii
		sql = "CREATE TABLE IF NOT EXISTS radii_fm1 (Id integer NOT NULL, k integer NOT NULL, bitstr integer, PRIMARY KEY (Id, k))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS radii_fm2 (Id integer NOT NULL, k integer NOT NULL, bitstr integer, PRIMARY KEY (Id, k))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS radii_n  (Id integer NOT NULL, k integer NOT NULL, n integer, PRIMARY KEY (Id, k))";
		SqlManipulate.createTable(sql);

		//Eigen
		sql = "CREATE TABLE IF NOT EXISTS alpha (Id integer, val real, PRIMARY KEY (Id))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS beta (Id integer, val real, PRIMARY KEY (Id))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS v(id integer, val real, PRIMARY KEY(id))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS v1(id integer, val real, PRIMARY KEY(id))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS v2(id integer, val real, PRIMARY KEY(id))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS t_q_qr (row_id integer, col_id integer, val double precision DEFAULT '0', PRIMARY KEY (row_id, col_id))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS t_r (row_id integer, col_id integer, val double precision DEFAULT '0', PRIMARY KEY (row_id, col_id))";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS A (row_id integer, col_id integer, val double precision DEFAULT '0', PRIMARY KEY (row_id, col_id))";
		SqlManipulate.createTable(sql);
		
		//FaBP
		sql = "CREATE TABLE IF NOT EXISTS vector (node bigint NOT NULL DEFAULT '0', coeff real NOT NULL DEFAULT '0')";
		SqlManipulate.createTable(sql);
		sql = "CREATE TABLE IF NOT EXISTS result (node bigint NOT NULL DEFAULT '0', coeff real NOT NULL DEFAULT '0')";
		SqlManipulate.createTable(sql);
		
		//Triangle
		sql = "CREATE TABLE IF NOT EXISTS wedge (node bigint NOT NULL DEFAULT '0',cumuWedge bigint NOT NULL DEFAULT '0', degree bigint NOT NULL DEFAULT '0')";
		SqlManipulate.createTable(sql);

		//Dijkstra
		sql = "CREATE TABLE IF NOT EXISTS node (id bigint NOT NULL DEFAULT '0', cost real NOT NULL DEFAULT '0', flag boolean DEFAULT FALSE)";
		SqlManipulate.createTable(sql);
	}

	public static void insertEdge() {
		System.out.println("start insert ...");
		SqlManipulate.reconnect();
		ParseFile.readFile(Configuration.getFilePath());
		System.out.println("finish");
		
		String sql = "create index idx_src on advogato(src)";
		SqlManipulate.createTable(sql);
		sql = "create index idx_dst on advogato(dst)";
		SqlManipulate.createTable(sql);
	}

	public static void insertVec() {
		System.out.println("start insert ...");
		String insert = "insert into advogatovec(id) (select src from advogato) union (select dst from advogato)";
		SqlManipulate.insert(insert);
		System.out.println("finish");
	}
}