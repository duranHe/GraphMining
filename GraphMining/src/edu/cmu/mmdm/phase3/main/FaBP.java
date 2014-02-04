package edu.cmu.mmdm.phase3.main;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.cmu.mmdm.phase3.util.*;

/*
 * Description: calculate the belief value of each node
 */
public class FaBP {
	public static void main(String[] args) {
		FaBP.run();
	}

	public static void run() {
		double h = norm();
		double a = 4 * h * h / (1 - 4 * h * h);
		double c = 2 * h / (1 - 4 * h * h);
		System.out.println("a is " + a + "; c is " + c);
		init(a, c);
		propagate(a, c);
		print();
	}

	public static void init(double a, double c) {
		try {
			String sql = "select max(num) from (select count(*) as num from (select src as node from advogato union all select dst as node from advogato) as temp group by node) as tmp2";
			ResultSet rs = SqlManipulate.query(sql);
			rs.next();
			int maxDegree = rs.getInt(1);

			sql = "select node, count(*) from (select src as node from advogato union all select dst as node from advogato) as tmp group by node";
			rs = SqlManipulate.query(sql);
			while (rs.next()) {
				int id = rs.getInt(1);
				int degree = rs.getInt(2);
				double coeff = (double) degree / (double) maxDegree;

				sql = "insert into vector values (" + id + ", " + coeff + ")";
				SqlManipulate.insert(sql);
				sql = "insert into result values (" + id + ", " + coeff + ")";
				SqlManipulate.insert(sql);

				sql = "select * from advogato where src = " + id
						+ " and dst = " + id;
				ResultSet self = SqlManipulate.query(sql);
				double value = 0 - (double) degree / c * a;
				if (self.next())
					sql = "update advogato set degree = " + value
							+ "where src = " + id + " and dst = " + id;
				else
					sql = "insert into advogato values (" + id + ", " + value
							+ ")";
				SqlManipulate.update(sql);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double norm() {
		try {
			String sql = "select max(num) from (select count(*) as num from (select src as node from advogato union all select dst as node from advogato) as temp group by node) as tmp2";
			ResultSet rs = SqlManipulate.query(sql);
			rs.next();
			int maxDegree = rs.getInt(1);
			double oneNorm = 1 / (double) (2 + 2 * maxDegree);

			sql = "select sum(num) from (select count(*) as num from (select src as node from advogato union all select dst as node from advogato) as tmp group by node) as tmp2";
			rs = SqlManipulate.query(sql);
			rs.next();
			double c1 = 2 + rs.getInt(1);

			sql = "select sum(num) from (select count(*) * count(*) as num from (select src as node from advogato union all select dst as node from advogato) as tmp group by node) as tmp2";
			rs = SqlManipulate.query(sql);
			rs.next();
			double c2 = rs.getInt(1) - 1;

			double tmp = Math.sqrt(c1 * c1 + 4 * c2);
			double froNorm = Math.sqrt((tmp - c1) / 8 / c2);

			if (oneNorm > froNorm)
				return oneNorm;
			else
				return froNorm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void propagate(double a, double c) {
		try {
			String sql = "select * from (select src from advogato union select dst from advogato) as tmp";
			ResultSet rs = SqlManipulate.query(sql);
			while (rs.next()) {
				int id = rs.getInt(1);
				sql = "select sum(degree * "
						+ c
						+ " * coeff + coeff) from advogato, vector where src = "
						+ id + " and dst = node";
				ResultSet sum = SqlManipulate.query(sql);
				sum.next();
				double value = sum.getDouble(1);

				sql = "update result set coeff = " + value + " where node = "
						+ id;
				SqlManipulate.update(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void print() {
		Printer p = new Printer(Configuration.getFabp());
		try {
			p.print("final belief is:");
			String sql = "select * from result order by node";
			ResultSet rs = SqlManipulate.query(sql);

			while (rs.next()) {
				p.print(rs.getInt(1) + "\t" + rs.getDouble(2));
			}

			/*
			 * sql = "select * from vector order by node"; rs =
			 * SqlManipulate.query(sql); while(rs.next())
			 * System.out.println(rs.getInt(1) + "\t" + rs.getDouble(2));
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
