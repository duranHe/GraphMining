package edu.cmu.mmdm.phase3.main;

import edu.cmu.mmdm.phase3.util.*;

import java.util.*;
import java.sql.*;

/**
 * Decription: Compute Connected Components for a graph(Task3)
 */
public class ConnectedComponents {

	private static HashMap<Integer, Integer> nidGid = new HashMap<Integer, Integer>();
	
	public static void main(String[] args) {
		ConnectedComponents.run();
	}

	public static void run() {
		System.out.println("Compute components");
		System.out.println("Initialize...");
		ConnectedComponents.initialize();

		int cnt = 0;
		while (!ConnectedComponents.compute()) {
			System.out.println("\tloop " + (++cnt) + "...");
		}

		ConnectedComponents.print();
	}

	public static void initialize() {
		String sql = "update advogatovec set cc=id";
		SqlManipulate.update(sql);
		sql = "select id, cc from advogatovec";
		ResultSet rs = SqlManipulate.query(sql);
		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				int cc = rs.getInt(2);
				nidGid.put(id, cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static boolean compute() {

		boolean judge = true;

		String sql = "select E.p2, min(E.CC*V.cc) from ((select src as p1, dst as p2, CC from advogato) union (select dst as p1, src as p2, CC from advogato)) as E, advogatovec as V where E.p1=V.Id group by E.p2";
		ResultSet rs = SqlManipulate.query(sql);

		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				int mini = rs.getInt(2);
				if (nidGid.get(id) != mini) {
					judge = false;
					SqlManipulate.update(
							"update advogatovec set cc=? where id=?",
							Math.min(mini, nidGid.get(id)), id);
					nidGid.put(id, Math.min(mini, nidGid.get(id)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return judge;
	}

	public static void print() {
		String sql = "select fre, count(fre) from (select count(*) as fre from advogatovec group by cc) as T group by fre";
		ResultSet rs = SqlManipulate.query(sql);
		Printer p = new Printer(Configuration.getCcOutput());

		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				int cnt = rs.getInt(2);
				p.print(id + "\t" + cnt);
				//System.out.println(id + "\t" + cnt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
