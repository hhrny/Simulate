package Simulate;

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
	// set the next destination location
	public void routePlanning(){
		this.rp.routePlanning(this);
	}
}
