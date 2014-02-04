package edu.cmu.mmdm.phase3.util;

/**
 * Description: Lanczos-NO Algorithm 
 */
public class Lanczos {

	public static float beta = 0.0f;
	public static int count = 0;

	/** initialize v0 v1 beta0 **/
	public static void initialize() {
		String sql = "select count(*) from advogatovec";
		count = SqlManipulate.queryInt(sql);

		int temp = 0;
		String insertv1 = "insert into v1 values(?,?)";
		String insertv2 = "insert into v2 values(?,?)";
		String insertv = "insert into v values(?,?)";
		SqlManipulate.closeAutoCommit();
		SqlManipulate.initBatch(insertv1, insertv2, insertv);
		float sum = 0.0f;
		for (int i = 1; i <= count; ++i) {
			SqlManipulate.insertBatch1(i, 0);
			//SqlManipulate.insert(insertv1, i, 0);
			float val = (float) Math.random();
			SqlManipulate.insertBatch2(i, val);
			//SqlManipulate.insert(insertv2, i, val);
			SqlManipulate.insertBatch3(i, 0);
			//SqlManipulate.insert(insertv, i, 0);
			sum += val * val;
			if (++ temp % 10000 == 0) {
				SqlManipulate.commitBatch2();
			}
		}
		SqlManipulate.commitBatch2();
		SqlManipulate.closeBatchStmt2();
		SqlManipulate.openAutoCommit();
		String update = "update v2 set val=val/" + sum;
		SqlManipulate.update(update);
	}

	/** Compute the tri-diagonal matrix for QR **/
	public static void compute() {

		int eigenValueCnt = Configuration.getEigenvalueCount();
		for (int i = 1; i <= eigenValueCnt; ++i) {
			System.out.println("\tlanczos loop " + i + " ...");
			
			String sql = "select advogato.src as src, sum(v2.val) as value from advogato, v2 where advogato.dst=v2.id group by advogato.src";
			
			String update = "with temp as (" + sql + ") update v set val=temp.value from temp where id=temp.src";
			SqlManipulate.update(update);
			
			String query = "select sum(v.val*v2.val) from v, v2 where v.Id = v2.Id";
			float alpha = SqlManipulate.queryFloat(query);

			update = "update v set val=v.val-" + alpha
					+ "*v2.val from v2 where v2.Id=v.Id";
			SqlManipulate.update(update);

			update = "update v set val=v.val-" + beta
					+ "*v1.val from v1 where v1.Id=v.Id";
			SqlManipulate.update(update);

			query = "select sum(v.val*v.val) from v";
			beta = SqlManipulate.queryFloat(query);
			beta = (float) Math.sqrt(beta);
			if (beta == 0.0f)
				break;

			update = "update v1 set val=v2.val from v2 where v1.Id=v2.Id";
			SqlManipulate.update(update);

			update = "update v2 set val=v.val/" + beta + " from v where v2.Id=v.Id";
			SqlManipulate.update(update);

			String insert = "insert into alpha values(?,?)";
			SqlManipulate.insert(insert, i, alpha);

			insert = "insert into beta values(?,?)";
			SqlManipulate.insert(insert, i, beta);
		}
	}
}
