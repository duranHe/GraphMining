package edu.cmu.mmdm.phase3.main;

import edu.cmu.mmdm.phase3.util.*;

import java.sql.*;
/**
 * Description: Compute Radii distribution for a graph(Taks4)
 */
public class Radii {

	private static int MaxIter = 15;
	private static int K = 1;
	private static int hMax = 30;

	public static void main(String[] args) {
		Radii.run();
	}

	public static void run() {
		Radii.initialize();
		Radii.iterate();
		Radii.computeRadii();
	}

	public Radii() {
	}

	public static void initialize() {
		SqlManipulate.delete("delete from radii_fm1");
		SqlManipulate.delete("delete from radii_fm2");
		SqlManipulate.delete("delete from radii_n");
		System.out.println("Initialize...");
		String sql = "select Id from advogatovec";
		ResultSet rs = SqlManipulate.query(sql);
		int temp = 0;

		String insert1 = "insert into radii_fm1(id, k, bitstr) values(?,?,?)";
		String insert2 = "insert into radii_fm2(id, k, bitstr) values(?,?,?)";
		String insert = "insert into radii_n(id, k, n) values(?,?,?)";

		SqlManipulate.closeAutoCommit();
		SqlManipulate.initBatch(insert1, insert2, insert);
		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				int[] bitstrs = FlajoletMartin.getKBitString(id, K);
				int len = bitstrs.length;

				++temp;
				for (int i = 0; i < len; ++i) {
					SqlManipulate.insertBatch1(id, i, bitstrs[i]);
					SqlManipulate.insertBatch2(id, i, bitstrs[i]);
				}

				int cnt = FlajoletMartin.getNum(bitstrs);

				SqlManipulate.insertBatch3(id, 0, cnt);
				if (temp % 10000 == 0) {
					SqlManipulate.commitBatch2();
				}
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
		SqlManipulate.commitBatch2();
		SqlManipulate.closeBatchStmt2();
		SqlManipulate.openAutoCommit();
	}

	public static void iterate() {
		for (int h = 0; h < MaxIter; ++h) {
			String sql1 = "select advogato.src as src, radii_fm1.k as k, bit_or(radii_fm1.bitstr) as bitstr from advogato, radii_fm1 where advogato.dst=radii_fm1.Id group by advogato.src, radii_fm1.k";
			String update1 = "with temp as ("
					+ sql1
					+ ") update radii_fm2 set bitstr=temp.bitstr from temp where radii_fm2.id=temp.src and radii_fm2.k=temp.k";
			int iter = 2 * h + 1;
			System.out.println("iter:\t" + iter);

			update(update1, "radii_fm2", iter);
			if (!ifChange()) {
				hMax = iter;
				break;
			}

			String sql2 = "select advogato.src as src, radii_fm2.k as k, bit_or(radii_fm2.bitstr) as bitstr from advogato, radii_fm2 where advogato.dst=radii_fm2.Id group by advogato.src, radii_fm2.k";
			String update2 = "with temp as ("
					+ sql2
					+ ") update radii_fm1 set bitstr=temp.bitstr from temp where radii_fm1.id=temp.src and radii_fm1.k=temp.k";
			iter = 2 * h + 2;
			System.out.println("iter:\t" + iter);

			update(update2, "radii_fm1", iter);
			if (!ifChange()) {
				hMax = iter;
				break;
			}
		}
	}

	private static void update(String sql, String table, int iter) {

		System.out.println("\tupdate...");
		SqlManipulate.update(sql);

		ResultSet rs = null;
		int[] bitstrs = new int[K];
		
		String sql1 = "select id, bitstr from " + table + " order by id asc";
		rs = SqlManipulate.query(sql1);
		int temp = 1;
		int cnt = 0;
		int i = 0;
		String insert2 = "insert into radii_n values(?,?,?)";
		SqlManipulate.closeAutoCommit();
		SqlManipulate.initBatch(insert2);
		try{
			while(rs.next()){
				int id = rs.getInt(1);
				int bitstr = rs.getInt(2);
				if(id == temp){
					bitstrs[i++] = bitstr;
				}else{
					int num = FlajoletMartin.getNum(bitstrs);
					SqlManipulate.insertBatch(id, iter, num);
					i = 0;
					temp = id;
					bitstrs[i++] = bitstr;
					++ cnt;
					if (cnt % 10000 == 0)
						SqlManipulate.commitBatch();
				}
			}
			//int num = FlajoletMartin.getNum(bitstrs);
			//SqlManipulate.insertBatch(temp, iter, num);
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
		
		SqlManipulate.commitBatch();
		SqlManipulate.closeBatchStmt();
		SqlManipulate.openAutoCommit();
	}

	private static boolean ifChange() {
		System.out.println("\tJudging...");
		String join = "select radii_fm1.bitstr, radii_fm2.bitstr from radii_fm1 INNER JOIN radii_fm2 ON (radii_fm1.id=radii_fm2.id and radii_fm1.k=radii_fm2.k)";
		ResultSet rs = SqlManipulate.query(join);
		try {
			while (rs.next()) {
				if (rs.getInt(1) != rs.getInt(2))
					return true;
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
		return false;
	}

	public static void computeRadii() {
		Printer p = new Printer(Configuration.getRadiiOutput());
		
		String sql = "select Id from advogatovec";
		ResultSet rs = SqlManipulate.query(sql);
		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				String sqlMax = "select N from radii_n where Id=? and k=?";
				int maxNi = SqlManipulate.queryInt(sqlMax, id, hMax);
				maxNi *= 0.9f;
				String sqlN = "select Id, k, N from radii_n where Id=? order by k asc";

				int radius = 0;
				ResultSet rsN = SqlManipulate.query(sqlN, id);
				try {
					while (rsN.next()) {
						radius = rsN.getInt(2);
						float tempN = (float) rsN.getInt(3);
						if (tempN >= maxNi) {
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (rsN != null)
							rsN.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				p.print(id + "\t" + radius);
				//System.out.println(id + "\t" + radius);
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

		StatRadii.statRadii(Configuration.getRadiiOutput());
	}
}
