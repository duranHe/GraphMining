package edu.cmu.mmdm.phase3.util;

import java.util.*;
import java.io.*;
import java.util.Map.*;

public class StatRadii {

	public static void statRadii(String file) {

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		Scanner sc = null;
		try {
			sc = new Scanner(new File(file));
			while (sc.hasNextLine()) {
				String str = sc.nextLine();
				String[] strs = str.split("\t");
				int radius = Integer.parseInt(strs[1]);
				if (map.containsKey(radius)) {
					map.put(radius, map.get(radius) + 1);
				} else {
					map.put(radius, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Printer p = new Printer(Configuration.getRadiiOutput());
		Iterator<Entry<Integer, Integer>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, Integer> e = iter.next();
			p.print(e.getKey() + "\t" + e.getValue());
			//System.out.println(e.getKey() + "\t" + e.getValue());
		}
	}

	public static void main(String[] args) {
		StatRadii.statRadii("./file/radii.txt");
	}
}
