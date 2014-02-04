package edu.cmu.mmdm.phase3.util;

import java.io.*;
import java.util.*;

/**
 * Description: insert the data of file to database
 */
public class ParseFile {

	public static String insert = "insert into advogato values(?,?)";
	private static HashMap<Integer, HashSet<Integer>> map = new HashMap<Integer, HashSet<Integer>>();

	/** parse on string **/
	public static void parse(String str) {
		if (!str.startsWith("%")) {
			String[] strs = str.split("\\s+");
			int from = Integer.parseInt(strs[0]);
			int to = Integer.parseInt(strs[1]);
			if (!map.containsKey(from)
					|| (map.containsKey(from) && !map.get(from).contains(to)))
				SqlManipulate.insertBatch(from, to);
			// SqlManipulate.insert(insert, from, to);
			if (map.containsKey(from)) {
				map.get(from).add(to);
			} else {
				map.put(from, new HashSet<Integer>());
				map.get(from).add(to);
			}
		}
	}

	/** translate one file **/
	public static void readFile(String file) {

		int cnt = 0;
		Scanner sc = null;
		try {
			sc = new Scanner(new File(file));
		} catch (Exception e) {
			e.printStackTrace();
		}

		SqlManipulate.closeAutoCommit();
		SqlManipulate.initBatch(insert);
		while (sc.hasNextLine()) {
			String str = sc.nextLine();
			parse(str);
			if (++cnt % 10000 == 0) {
				SqlManipulate.commitBatch();
			}
		}
		SqlManipulate.commitBatch();
		SqlManipulate.closeBatchStmt();
		SqlManipulate.openAutoCommit();
	}
}
