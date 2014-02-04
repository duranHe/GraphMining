package edu.cmu.mmdm.phase3.main;

import java.sql.ResultSet;

import edu.cmu.mmdm.phase3.util.*;

/*
 * Description: calculate single-source shortest path using Dijkstra algorithm
 */
public class Dijkstra {
	static int srcid = 1;

	public static void main(String[] args) {
		Dijkstra.run();
	}

	public static void run() {
		int nodeNum = initNode(srcid);

		dijkstra(nodeNum);
		print();
	}

	public static void dijkstra(int nodeNum) {
		try {
			String sql;
			ResultSet rs, costRS;
			int nodeid, dstid;
			double cost, weight, currentCost, newCost, high, low;

			for (int i = 0; i < nodeNum; i++) {
				sql = "select min(cost) from node where flag = false and id != "
						+ srcid;
				rs = SqlManipulate.query(sql);
				rs.next();
				cost = rs.getDouble(1);

				high = cost + 0.005;
				low = cost - 0.005;
				sql = "select id from node where id != " + srcid
						+ " and flag = false and cost between " + low + " and "
						+ high;
				rs = SqlManipulate.query(sql);
				rs.next();
				nodeid = rs.getInt(1);

				sql = "update node set flag = true where id = " + nodeid;
				SqlManipulate.update(sql);

				sql = "select dst, weight from advogato where src = " + nodeid;
				rs = SqlManipulate.query(sql);
				while (rs.next()) {
					dstid = rs.getInt(1);
					weight = rs.getDouble(2);

					sql = "select cost from node where id = " + dstid;
					costRS = SqlManipulate.query(sql);
					costRS.next();
					currentCost = costRS.getDouble(1);

					newCost = weight + cost;
					if (newCost < currentCost) {
						sql = "update node set cost = " + newCost
								+ " where id = " + dstid;
						SqlManipulate.update(sql);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int initNode(int srcid) {
		int count = 0;
		try {
			ResultSet rs, costrs;
			String sql = "select * from (select src from advogato union select dst from advogato) as tmp";
			rs = SqlManipulate.query(sql);
			int id;
			double cost;
			while (rs.next()) {
				id = rs.getInt(1);
				count++;

				sql = "select weight from advogato where src = " + srcid
						+ " and dst = " + id;
				costrs = SqlManipulate.query(sql);
				if (costrs.next())
					cost = costrs.getDouble(1);
				else
					cost = 99999;

				sql = "insert into node values (" + id + ", " + cost + ", "
						+ "false)";
				SqlManipulate.insert(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count - 1;
	}

	public static void print() {
		Printer p = new Printer(Configuration.getDijkstra());
		try {
			String sql = "select * from node order by id";
			ResultSet rs = SqlManipulate.query(sql);
			int id;
			double cost;
			while (rs.next()) {
				id = rs.getInt(1);
				cost = rs.getDouble(2);
				p.print(Integer.toString(id) + '\t' + Double.toString(cost));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
