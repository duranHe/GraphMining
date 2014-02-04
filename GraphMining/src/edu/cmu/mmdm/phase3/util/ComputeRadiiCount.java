package edu.cmu.mmdm.phase3.util;

import java.io.*;
import java.util.*;
import java.util.Map.*;

/**
 * Used in task4 
 */
public class ComputeRadiiCount {

	public static void main(String[] args){
		
		Scanner sc = null;
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		try{
			sc = new Scanner(new File(Configuration.getRadiiOutput()));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		while(sc.hasNextLine()){
			String str = sc.nextLine();
			String[] strs = str.split("\t");
			int radius = Integer.parseInt(strs[1]);
			if(map.containsKey(radius)){
				int cnt = map.get(radius);
				++ cnt;
				map.put(radius, cnt);
			}else{
				map.put(radius, 1);
			}
		}
		
		Iterator<Entry<Integer, Integer>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Integer, Integer> e = iter.next();
			System.out.println(e.getKey() + "\t" + e.getValue());
		}
	}
	
}
