package Simulate;

public class Bin {
	public Area area;
	public Simulate simulate;
	public int binId;
	public static float binServiceTime;
	public static float binVolume;
	public float currentLoadWeight;
	public float currentLoadVolume;
	public boolean thresholdExceeded;
	public boolean overflowed;
	
	public Bin(Area a, int bid){
		this.area = a;
		this.simulate = a.simulate;
		this.binId = bid;
		this.currentLoadVolume = 0;
		this.currentLoadWeight = 0;
		this.overflowed = false;
		this.thresholdExceeded = false;
	}
	public void configBin(Config cf){
		Bin.binServiceTime = cf.getBinServiceTime();
		Bin.binVolume = cf.getBinVolume();
	}
	
	public void addBag(Bag bag){
		this.currentLoadVolume += bag.bagVolume;
		this.currentLoadWeight += bag.bagWeight;
		BinSEvent binEvent = new BinSEvent();
		binEvent.bin = this;
		binEvent.bag = bag;
		binEvent.time = Simulate.currentTime;		
		if(this.currentLoadVolume >= Bin.binVolume){
			binEvent.eventType = SEventType.BIN_OVERFLOWED;
			this.area.binOverflow.add(this.binId);
		}
		else if(this.currentLoadVolume >= Bin.binVolume*this.area.thresholdVal){
			binEvent.eventType = SEventType.BIN_OCCUPANCY_THRESHOLD_EXCEEDED;
			this.area.binExceeded.add(this.binId);
		}
		else{
			binEvent.eventType = SEventType.BIN_CONTENTS_VOLUME_CHANGED;
		}
		this.simulate.eventsQueue.addEvent(binEvent);
	}
	
	public void clearBag(){
		this.currentLoadVolume = 0;
		this.currentLoadWeight = 0;
		this.area.binOverflow.remove(Integer.valueOf(this.binId));
		this.area.binExceeded.remove(Integer.valueOf(this.binId));
		this.overflowed = false;
		this.thresholdExceeded = false;
	}
	public boolean isThresholdExceeded() {
		return thresholdExceeded;
	}
	public void setThresholdExceeded(boolean thresholdExceeded) {
		this.thresholdExceeded = thresholdExceeded;
	}
	public boolean isOverflowed() {
		return overflowed;
	}
	public void setOverflowed(boolean overflowed) {
		this.overflowed = overflowed;
	}
}
