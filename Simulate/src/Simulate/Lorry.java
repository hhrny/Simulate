package Simulate;

import java.util.Iterator;

public class Lorry {
	public static int lorryVolume;
	public static int lorryMaxLoad;
	public float currentLoadWeight;
	public float currentLoadVolume;
	public Area area;
	public int currentLocation;
	public int nextLocation;
	public int destLocation;
	
	public RoutePlanning rp = new Floyd();
	
	public Lorry(){
		this.currentLoadVolume = 0;
		this.currentLoadWeight = 0;
		this.currentLocation = 0;
		this.nextLocation = 0;
		this.destLocation = 0;
	}
	public void configLorry(Config cf){
		Lorry.lorryMaxLoad = cf.getLorryMaxLoad();
		Lorry.lorryVolume = cf.getLorryVolume();
	}

	public static int getLorryVolume() {
		return lorryVolume;
	}

	public static void setLorryVolume(int lVolume) {
		lorryVolume = lVolume;
	}

	public static int getLorryMaxLoad() {
		return lorryMaxLoad;
	}
	
	public void setRoutePlanning(RoutePlanning rp){
		this.rp = rp;
	}

	public static void setLorryMaxLoad(int lMaxLoad) {
		lorryMaxLoad = lMaxLoad;
	}
	public void routePlanning(){
		int mindist = Integer.MAX_VALUE, minbin = -1, tmppos;
		float leftVolume = Lorry.lorryVolume - this.currentLoadVolume, leftWeight = Lorry.lorryMaxLoad - this.currentLoadWeight;
		Iterator<Integer> it = this.area.binExceeded.iterator();
		while(it.hasNext()){
			tmppos = it.next().intValue();
			if(this.area.bins[tmppos-1].currentLoadVolume <= leftVolume*2 && this.area.bins[tmppos-1].currentLoadWeight <= leftWeight){
				if(rp.getTime(this.currentLocation, tmppos) < mindist){
					mindist = rp.getTime(this.currentLocation, tmppos);
					minbin = tmppos;
				}
			}
		}
		if(minbin != -1){
			this.destLocation = minbin;
		}else{
			this.destLocation = 0;
		}
	}
}
