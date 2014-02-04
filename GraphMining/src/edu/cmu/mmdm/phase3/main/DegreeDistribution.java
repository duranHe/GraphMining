package edu.cmu.mmdm.phase3.main;

import edu.cmu.mmdm.phase3.util.*;

import java.sql.*;

/**
 * Desciption: Compute degree distribution for a graph (Task1)
 */
public class DegreeDistribution {

	public static void run() {

		Printer p = new Printer(Configuration.getDegreeOutput());
		System.out.println("Compute degree distribution...");
		String sql = "select degree, count(*) from (select p1 as nodeid, count(*) as degree from ((select src as p1,dst as p2 from advogato as T1) union all (select dst as p1,src as p2 from advogato as T2)) as T3 group by p1) as T4 group by degree order by degree desc";

		ResultSet rs = SqlManipulate.query(sql);
		try {
			while (rs.next()) {
				int pid = rs.getInt(1);
				int degree = rs.getInt(2);
				//System.out.println(pid + "\t" + degree);
				p.print(pid + "\t" + degree);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		DegreeDistribution.run();
	}

}
