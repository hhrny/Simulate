package Simulate;

public class BinSEvent extends SEvent {
	public Bin bin;
	public Bag bag;
	public float volume, weight;
	public String toString(){
		switch(this.eventType){
		case SEventType.BIN_LOAD:
			return DateTime.toString(time)+" -> "+"load of bin "+bin.area.areaIdx+"."+bin.binId+" became 0 kg and contents volume 0 m^3";
		case SEventType.BIN_CONTENTS_VOLUME_CHANGED:
			return DateTime.toString(time)+" -> "+"load of bin "+bin.area.areaIdx+"."+bin.binId+" became "+(bin.currentLoadWeight+weight)+" kg and contents volume "+(bin.currentLoadVolume+volume)+" m^3";
		case SEventType.BIN_OCCUPANCY_THRESHOLD_EXCEEDED:
			return DateTime.toString(time)+" -> "+"occupancy threshold of bin "+bin.area.areaIdx+"."+bin.binId+" exceeded";
		case SEventType.BIN_OVERFLOWED:
			return DateTime.toString(time)+" -> "+"bin "+bin.area.areaIdx+"."+bin.binId+" overflowed";
		}
		return "Event Type Error in BinEvent!";
	}
}
