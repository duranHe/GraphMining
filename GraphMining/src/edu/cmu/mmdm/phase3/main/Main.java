package edu.cmu.mmdm.phase3.main;

public class Main {

	public static void main(String[] args) {

		// Create table and read file into database
		System.out.println("Initialize Graph Mining");
		File2DB.run();

		// Compute Degree Distribution
		System.out.println("Task1: Degree Distribution");
		DegreeDistribution.run();

		// Compute PageRank
		System.out.println("Task2: Page Rank");
		PageRank.run();

		// Compute Connected Component
		System.out.println("Task3: Connected Component");
		ConnectedComponents.run();

		// Radii
		System.out.println("Task4: Compute Radii");
		Radii.run();

		// Eigenvalue
		System.out.println("Task5: Compute Eigenvalues");
		Eigenvalue.run();

		// RWR
		System.out.println("Extra Task for Kuo: RWR");
		RandomWalk.run();

		// Triangles
		System.out.println("Task7: Triangle");
		Triangles.run();

		// Dijkstra
		System.out.println("Extra Task for Yuning: Dijkstra");
		Dijkstra.run();

		// FaBP
		System.out.println("Task6: FaBP");
		FaBP.run();
	}
}
