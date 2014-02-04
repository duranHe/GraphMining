package edu.cmu.mmdm.phase3.util;

/**
 * Description: Configuration Settings
 */
public class Configuration {

	/** Settings for database **/
	private static String driver = "org.postgresql.Driver";
	private static String url = "jdbc:postgresql://127.0.0.1:5432/15826phase3";
	private static String username = "LK";
	private static String password = "123";

	private static int eigenvalueCount = 30;
	private static int qriteration = 50;
	private static int pageRankIteration = 20;
	
	/** Settings for file output **/
	private static String filePath = "E:/826/advogato/out.advogato";
	private static String degreeOutput = "./file/degree_output.txt";
	private static String prOutput = "./file/pagerank.txt";
	private static String randomWalkOutput = "./file/rwr.txt";
	private static String ccOutput = "./file/ccOutput.txt";
	private static String radiiOutput = "./file/radii.txt";
	private static String eigenvalueOutput = "./file/eigenvalue.txt";
	private static String fabp = "./file/fabp.txt";
	private static String triangles = "./file/triangles.txt";
	private static String dijkstra = "./file/dijkstra.txt";

	/** getters and setters for the configuration settings **/
	public static String getFilePath() {
		return filePath;
	}

	public static String getUrl() {
		return url;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static String getDegreeOutput() {
		return degreeOutput;
	}

	public static String getPrOutput() {
		return prOutput;
	}

	public static String getCcOutput() {
		return ccOutput;
	}

	public static String getDriver() {
		return driver;
	}

	public static void setDriver(String driver) {
		Configuration.driver = driver;
	}

	public static String getRadiiOutput() {
		return radiiOutput;
	}

	public static void setRadiiOutput(String radiiOutput) {
		Configuration.radiiOutput = radiiOutput;
	}

	public static int getEigenvalueCount() {
		return eigenvalueCount;
	}

	public static void setEigenvalueCount(int eigenvalueCount) {
		Configuration.eigenvalueCount = eigenvalueCount;
	}

	public static String getEigenvalueOutput() {
		return eigenvalueOutput;
	}

	public static void setEigenvalueOutput(String eigenvalueOutput) {
		Configuration.eigenvalueOutput = eigenvalueOutput;
	}

	public static String getRandomWalkOutput() {
		return randomWalkOutput;
	}

	public static void setRandomWalkOutput(String randomWalkOutput) {
		Configuration.randomWalkOutput = randomWalkOutput;
	}

	public static int getQriteration() {
		return qriteration;
	}

	public static void setQriteration(int qriteration) {
		Configuration.qriteration = qriteration;
	}

	public static int getPageRankIteration() {
		return pageRankIteration;
	}

	public static void setPageRankIteration(int pageRankIteration) {
		Configuration.pageRankIteration = pageRankIteration;
	}

	public static String getFabp() {
		return fabp;
	}

	public static void setFabp(String fabp) {
		Configuration.fabp = fabp;
	}

	public static String getTriangles() {
		return triangles;
	}

	public static void setTriangles(String triangles) {
		Configuration.triangles = triangles;
	}

	public static String getDijkstra() {
		return dijkstra;
	}

	public static void setDijkstra(String dijkstra) {
		Configuration.dijkstra = dijkstra;
	}
}
