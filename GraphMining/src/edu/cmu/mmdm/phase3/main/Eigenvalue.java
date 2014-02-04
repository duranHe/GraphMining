package edu.cmu.mmdm.phase3.main;

import edu.cmu.mmdm.phase3.util.*;
import java.sql.*;

/**
 * Description: Compute first k eigenvalues (Task5)
 */
public class Eigenvalue {

	public static void main(String[] args) {
		Eigenvalue.run();
	}

	public static void run() {
		System.out.println("lanczos initialization...");
		Lanczos.initialize();
		Lanczos.compute();
		System.out.println("QR decomposition begin...");
		QR.buildTridiagonalMatrix();
		QR.compute();

		Printer p = new Printer(Configuration.getEigenvalueOutput());
		String query = "select val from A where row_id=col_id";
		ResultSet rs = SqlManipulate.query(query);
		try {
			while (rs.next()) {
				float eigenv = rs.getFloat(1);
				p.print(Float.toString(eigenv));
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
		}
	}
}
