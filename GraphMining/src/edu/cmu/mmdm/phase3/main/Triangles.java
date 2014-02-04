package edu.cmu.mmdm.phase3.main;

import java.sql.ResultSet;
import java.util.Random;

import edu.cmu.mmdm.phase3.util.*;

/*
 * Description: count the total number of triangles in a graph
 */
public class Triangles {

	public static void main(String[] args) {
		Triangles.run();
	}

	public static void run() {
		Printer p = new Printer(Configuration.getTriangles());
		int totalWedge = cumulant();
		p.print("total number of wedge is: " + totalWedge);

		int k = 100;
		double coeff = fraction(k, totalWedge);
		double triangle = coeff * totalWedge / 3;
		p.print("coeff C is: " + coeff);
		p.print("total number of triangles is: " + triangle);
	}

	public static double fraction(int k, int totalWedge) {
		String sql = "";
		try {
			Random r = new Random();
			ResultSet rs;
			int num, node, degree, wedge = 0, closedWedge = 0;
			for (int i = 0; i < k; i++) {
				num = r.nextInt(totalWedge + 1);
				sql = "select node from wedge where cumuWedge = (select max(cumuWedge) from wedge where cumuWedge < "
						+ num + ")";
				rs = SqlManipulate.query(sql);
				rs.next();
				node = rs.getInt(1);

				sql = "select degree from wedge where node = " + node;
				rs = SqlManipulate.query(sql);
				rs.next();
				degree = rs.getInt(1);
				wedge += degree * (degree - 1) / 2;

				sql = "select count(*) from advogato, "
						+ "(select src as node from advogato where dst = "
						+ node
						+ " and src != "
						+ node
						+ " union select dst as node from advogato where src = "
						+ node
						+ " and dst != "
						+ node
						+ ") as p1,"
						+ "(select src as node from advogato where dst = "
						+ node
						+ " and src != "
						+ node
						+ " union select dst as node from advogato where src = "
						+ node
						+ " and dst != "
						+ node
						+ ") as p2 "
						+ "where advogato.src = p1.node and advogato.dst = p2.node and p1.node > p2.node";
				rs = SqlManipulate.query(sql);
				rs.next();
				closedWedge += rs.getInt(1);
			}
			return (double) closedWedge / (double) wedge;
		} catch (Exception e) {
			System.out.println(sql);
			e.printStackTrace();
		}
		return 0;
	}

	public static int cumulant() {
		try {
			String sql = "select node, count(*) from (select src as node from advogato union all select dst as node from advogato) as tmp group by node";
			ResultSet rs = SqlManipulate.query(sql);
			int cumulativeWedge = 0;
			while (rs.next()) {
				int id = rs.getInt(1);
				int degree = rs.getInt(2);

				cumulativeWedge += degree * (degree - 1) / 2;
				sql = "insert into wedge values (" + id + ", "
						+ cumulativeWedge + ", " + degree + ")";
				SqlManipulate.insert(sql);
			}

			return cumulativeWedge;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}
}
