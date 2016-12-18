package Simulate;

public interface RoutePlanning {
	public int getNextLocation(int s, int dest);
	public void genShortestPath(int [][]g, int num);
	public int getTime(int s, int e);
	public void routePlanning(Lorry lorry);
	public int getNextLocaltion();
	public int nextLocation();
}
