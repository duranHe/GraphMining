package edu.cmu.mmdm.phase3.main;

import java.sql.ResultSet;

import edu.cmu.mmdm.phase3.util.Configuration;
import edu.cmu.mmdm.phase3.util.Printer;
import edu.cmu.mmdm.phase3.util.SqlManipulate;

/**
 * Description: RWR on a graph(Extra task 1)
 */
public class RandomWalk {

	private static int cnt = 0;
	private static int k = 100;

	public static void run(){
		System.out.println("Normalize adjacency matrix");
		RandomWalk.normalizeMatrix();
		System.out.println("Normalize page rank vector");
		RandomWalk.initializeVec();
		System.out.println("Compute rwr score");
		for (int i = 0; i < 20; ++i) {
			System.out.println("\tloop " + i + "...");
			RandomWalk.calculate();
		}
		RandomWalk.printRwr();
	}
	
	public static void main(String[] args) {
		RandomWalk.run();
	}

	public static void normalizeMatrix() {
		String sql = "select src as begin, count(*) as outdegree from advogato group by src";
		String update = "with temp as ("
				+ sql
				+ ") update advogato set normadjm=(1.0/temp.outdegree) from temp where src=temp.begin";
		SqlManipulate.update(update);
	}

	public static void initializeVec() {
		String sql = "select count(*) from advogatovec";
		cnt = SqlManipulate.queryInt(sql);
		SqlManipulate
				.update("update advogatovec set rwr=?", 1.0f / (float) cnt);
	}

	public static void calculate() {
		String sql = "select advogato.dst as dst, sum(0.85*advogato.normadjm*advogatovec.rwr) as val from advogato, advogatovec where advogato.src=advogatovec.id group by advogato.dst";
		String update = "with temp as ("
				+ sql
				+ ") update advogatovec set rwr=temp.val from temp where id=temp.dst";
		SqlManipulate.update(update);
		update = "update advogatovec set rwr=rwr+0.15 where id=?";
		SqlManipulate.update(update, k);
	}

	public static void printRwr() {
		String sql = "select * from advogatovec";
		ResultSet rs = SqlManipulate.query(sql);
		Printer p = new Printer(Configuration.getRandomWalkOutput());
		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				float rwr = rs.getFloat(4);
				p.print(id + "\t" + rwr);
				//System.out.println(id + "\t" + rwr);
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
