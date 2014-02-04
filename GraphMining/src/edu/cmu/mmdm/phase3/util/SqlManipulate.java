package edu.cmu.mmdm.phase3.util;

import java.sql.*;

/**
 * Description: Basic Sql Manipulations
 */
public class SqlManipulate {

	private static Connection conn = null;
	private static PreparedStatement pstmt = null;
	private static PreparedStatement pstmtBatch = null;
	private static PreparedStatement pstmtBatch1 = null;
	private static PreparedStatement pstmtBatch2 = null;
	private static PreparedStatement pstmtBatch3 = null;

	public SqlManipulate() {
	}

	/** initialize the connection to the database **/
	static {
		try {
			Class.forName(Configuration.getDriver());
			conn = DriverManager.getConnection(Configuration.getUrl(),
					Configuration.getUsername(), Configuration.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** reconnect to database **/
	public static void reconnect() {
		try {
			conn = DriverManager.getConnection(Configuration.getUrl(),
					Configuration.getUsername(), Configuration.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** basic operation: create table **/
	public static boolean createTable(String sql, Object... args) {
		pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			if (!pstmt.execute())
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/** basic operation: insertion into table **/
	public static boolean insert(String sql, Object... args) {
		pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; ++i) {
				pstmt.setObject(i + 1, args[i]);
			}
			if (pstmt.executeUpdate() != 1) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static void closeAutoCommit() {
		try {
			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openAutoCommit() {
		try {
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initBatch(String sql) {
		try {
			pstmtBatch = conn.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initBatch(String sql1, String sql2, String sql3) {
		try {
			pstmtBatch1 = conn.prepareStatement(sql1);
			pstmtBatch2 = conn.prepareStatement(sql2);
			pstmtBatch3 = conn.prepareStatement(sql3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void commitBatch() {
		try {
			pstmtBatch.executeBatch();
			conn.commit();
			pstmtBatch.clearBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void commitBatch2() {
		try {
			pstmtBatch1.executeBatch();
			pstmtBatch2.executeBatch();
			pstmtBatch3.executeBatch();
			conn.commit();
			pstmtBatch1.clearBatch();
			pstmtBatch2.clearBatch();
			pstmtBatch3.clearBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** basic operation: insert in batch **/
	public static void insertBatch(Object... args) {
		try {
			for (int i = 0; i < args.length; ++i) {
				pstmtBatch.setObject(i + 1, args[i]);
			}
			pstmtBatch.addBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertBatch1(Object... args) {
		try {
			for (int i = 0; i < args.length; ++i) {
				pstmtBatch1.setObject(i + 1, args[i]);
			}
			pstmtBatch1.addBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertBatch2(Object... args) {
		try {
			for (int i = 0; i < args.length; ++i) {
				pstmtBatch2.setObject(i + 1, args[i]);
			}
			pstmtBatch2.addBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertBatch3(Object... args) {
		try {
			for (int i = 0; i < args.length; ++i) {
				pstmtBatch3.setObject(i + 1, args[i]);
			}
			pstmtBatch3.addBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** basic operation: update table **/
	public static boolean update(String sql, Object... args) {
		pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; ++i) {
				pstmt.setObject(i + 1, args[i]);
			}
			if (pstmt.executeUpdate() != 1) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/** basic operation: query on table **/
	public static ResultSet query(String sql, Object... args) {
		pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; ++i) {
				pstmt.setObject(i + 1, args[i]);
			}
			rs = pstmt.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/** basic operation: query one integer from database **/
	public static int queryInt(String sql, Object... args) {
		pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; ++i) {
				pstmt.setObject(i + 1, args[i]);
			}
			rs = pstmt.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int result = 0;

		try {
			if (rs.next()) {
				result = rs.getInt(1);
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
		return result;
	}

	/** basic operation: query one float from database **/
	public static float queryFloat(String sql, Object... args) {
		pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; ++i) {
				pstmt.setObject(i + 1, args[i]);
			}
			rs = pstmt.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}

		float result = 0;

		try {
			if (rs.next()) {
				result = rs.getFloat(1);
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
		return result;
	}

	public static void delete(String sql, Object... args) {
		pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; ++i) {
				pstmt.setObject(i + 1, args[i]);
			}
			pstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeStmt() {
		try {
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeBatchStmt() {
		try {
			pstmtBatch.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeBatchStmt2() {
		try {
			pstmtBatch1.close();
			pstmtBatch2.close();
			pstmtBatch3.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}