package edu.cmu.mmdm.phase3.main;

import edu.cmu.mmdm.phase3.util.*;

import java.sql.*;

/**
 * Description: Compute pagerank score for a graph(Task2)
 */
public class PageRank {

	public static int cnt = 0;

	public static void main(String[] args) {
		PageRank.run();
	}

	public static void run() {
		System.out.println("Normalize adjacency matrix");
		PageRank.normalizeMatrix();
		System.out.println("Normalize page rank vector");
		PageRank.initializeVec();
		System.out.println("Compute page rank score");
		for (int i = 0; i < Configuration.getPageRankIteration(); ++i) {
			System.out.println("\tloop " + i + "...");
			PageRank.calculate();
		}
		PageRank.printPr();
	}

	/** doing normalization on ajacency matrix **/
	public static void normalizeMatrix() {
		String sql = "select src as begin, count(*) as outdegree from advogato group by src";
		String update = "with temp as ("
				+ sql
				+ ") update advogato set normadjm=(1.0/temp.outdegree) from temp where src=temp.begin";
		SqlManipulate.update(update);
	}

	/** initialize pagerank score vector **/
	public static void initializeVec() {
		String sql = "select count(*) from advogatovec";
		cnt = SqlManipulate.queryInt(sql);
		SqlManipulate.update("update advogatovec set pr=?", 1.0f / (float) cnt);
	}

	/** compute page rank score in one loop **/
	public static void calculate() {
		String sql = "select advogato.dst as dst, "
				+ 0.15f
				/ (float) cnt
				+ " + sum(0.85*advogato.normadjm*advogatovec.pr) as pr from advogato, advogatovec where advogato.src=advogatovec.id group by advogato.dst";
		String update = "with temp as ("
				+ sql
				+ ") update advogatovec set pr=temp.pr from temp where id=temp.dst";
		SqlManipulate.update(update);
	}

	/** print page rank score to file **/
	public static void printPr() {
		String sql = "select * from advogatovec";
		ResultSet rs = SqlManipulate.query(sql);
		Printer p = new Printer(Configuration.getPrOutput());
		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				float pr = rs.getFloat(2);
				p.print(id + "\t" + pr);
				//System.out.println(id + "\t" + pr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
