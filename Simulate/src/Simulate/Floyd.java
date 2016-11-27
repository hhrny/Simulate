package Simulate;

public class Floyd implements RoutePlanning {
	public int[][] A;
	public int[][] path;
	@Override
	public int getNextLocation(int s, int dest) {
		// TODO Auto-generated method stub
		return path[s][dest];
	}

	@Override
	public void genShortestPath(int [][] g, int num) {
		// TODO Auto-generated method stub
		int i,j,k;
		A = new int[num][num];
		path = new int[num][num];
		for(i = 0; i < num; i ++){
			for(j = 0; j < num; j ++){
				A[i][j] = g[i][j];
				path[i][j] = -1;
			}
		}
		for(i = 0; i < num; i ++ ){
			for(j = 0; j < num; j ++){
				for(k = 0; k < num; k ++){
					if(A[j][k] > (A[j][i] + A[i][k])){
						A[j][k] = A[j][i] + A[i][k];
						path[j][k] = i;
					}
				}
			}
		}
	}

	@Override
	public int getTime(int s, int e) {
		// TODO Auto-generated method stub
		return A[s][e];
	}
	
}
