package edu.cmu.mmdm.phase3.util;

import java.sql.*;

/**
 * Description: QR decomposition on matrix
 */
public class QR {

	private static int m = 30;

	/** Build tridiagonal matrix using alpha and beta **/
	public static void buildTridiagonalMatrix() {
		String query = "select count(*) from alpha";
		m = SqlManipulate.queryInt(query);
		String insert = "insert into A values(?,?,?)";

		query = "select val from alpha where id=?";
		for (int i = 0; i < m; ++i) {
			float val = SqlManipulate.queryFloat(query, i + 1);
			SqlManipulate.insert(insert, i, i, val);
		}

		query = "select val from beta where id=?";
		--m;
		for (int i = 0; i < m; ++i) {
			float val = SqlManipulate.queryFloat(query, i + 1);
			SqlManipulate.insert(insert, i, i + 1, val);
			SqlManipulate.insert(insert, i + 1, i, val);
		}
		++m;

		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < i - 1; ++j) {
				SqlManipulate.insert(insert, i, j, 0.0f);
			}
			for (int j = i + 2; j < m; ++j) {
				SqlManipulate.insert(insert, i, j, 0.0f);
			}
		}
	}

	/** initialize Q matrix and R matrix **/
	public static void initialize() {
		String delete = "delete from t_q_qr";
		SqlManipulate.delete(delete);

		String insert = "insert into t_q_qr (select row_id, col_id, val from A)";
		SqlManipulate.insert(insert);
		
		delete = "delete from t_r";
		SqlManipulate.delete(delete);
	}

	public static void compute() {
		for (int i = 0; i < Configuration.getQriteration(); ++i) {
			System.out.println("\tQR loop " + i + " ...");
			initialize();
			decomposition();
			reformulate();
		}
	}

	/** decompose A=QR **/
	public static void decomposition() {
		String insert1 = "insert into t_r (select ?, ?, sqrt(sum(Power(val,2))) from t_q_qr where col_id=?)";
		String update1 = "update t_q_qr set val=val/(select val from t_r where col_id=? and row_id=?) where col_id=?";

		String query2 = "select sum(tableQ1.val * tableQ2.val) from t_q_qr tableQ1, t_q_qr tableQ2 where tableQ1.row_id=tableQ2.row_id and tableQ1.col_id=? and tableQ2.col_id=?";
		String insert2 = "insert into t_r values(?,?,?)";
		String update2 = "update t_q_qr tableQ1 set val=val-(select val from t_r where col_id=? and row_id=?)*(select val from t_q_qr tableQ where tableQ.col_id=? and tableQ.row_id=tableQ1.row_id) where tableQ1.col_id=?";
		for (int j = 0; j < m; ++j) {

			SqlManipulate.insert(insert1, j, j, j);
			SqlManipulate.update(update1, j, j, j);

			for (int i = j + 1; i < m; ++i) {
				float val = SqlManipulate.queryFloat(query2, j, i);
				if (Math.abs(val) < 0.000001)
					SqlManipulate.insert(insert2, j, i, 0.0f);
				else
					SqlManipulate.insert(insert2, j, i, val);
				SqlManipulate.update(update2, i, j, j, i);
			}
		}
	}

	/** A = RQ **/
	public static void reformulate() {
		String delete = "delete from A";
		SqlManipulate.delete(delete);
		String query = "select t_r.row_id, t_q_qr.col_id, sum(t_r.val*t_q_qr.val) from t_r, t_q_qr where t_r.col_id=t_q_qr.row_id group by t_r.row_id, t_q_qr.col_id";
		ResultSet rs = SqlManipulate.query(query);
		String insert = "insert into A values(?,?,?)";
		try {
			while (rs.next()) {
				int row = rs.getInt(1);
				int col = rs.getInt(2);
				float val = rs.getFloat(3);
				if (Math.abs(val) < 0.000001)
					val = 0.0f;
				SqlManipulate.insert(insert, row, col, val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			SqlManipulate.closeStmt();
		}
	}

}
