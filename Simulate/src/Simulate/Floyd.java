package Simulate;

import java.util.Iterator;
import java.util.Vector;

public class Floyd implements RoutePlanning {
	public int[][] A;
	public int[][] path;
	public Vector<Integer> p = new Vector<Integer>();
	public int nobins = 0;
	public int pointer = 0;
	@Override
	public int getNextLocation(int s, int dest) {
		return path[s][dest];
	}

	@Override
	public void genShortestPath(int [][] g, int num) {
		int i,j,k;
		this.nobins = num;
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
	public int getTime(int s, int e) {
		return A[s][e];
	}
	public void routePlanning(Lorry lorry){
		int mindist = Integer.MAX_VALUE, minbin = -1, tmppos;
		int [] flag = new int[nobins];
		for(int i = 0; i < nobins; i ++){
			flag[i] = 0;
		}
		int currentLoc = 0;
		for(int i = 0; i < lorry.area.binExceeded.size(); i ++){
			mindist = Integer.MAX_VALUE;
			minbin = -1;
			Iterator<Integer> it = lorry.area.binExceeded.iterator();
			while(it.hasNext()){
				tmppos = it.next().intValue();
				if(flag[tmppos] == 0 && getTime(currentLoc, tmppos) < mindist){
					mindist = getTime(currentLoc, tmppos);
					minbin = tmppos;
				}
			}
			if(minbin != -1){
				flag[minbin] = 1;
				currentLoc = minbin;
				p.add(minbin);
			}else{
				break;
			}
		}
		pointer = 0;
	}

	public int getNextLocaltion() {
		if(pointer == p.size()){
			return 0;
		}
		return p.get(pointer);
	}

	public int nextLocation() {
		if(pointer == p.size()){
			return 0;
		}
		return p.get(pointer++);
	}
}
