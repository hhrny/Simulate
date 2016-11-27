package Simulate;

public class LorrySEvent extends SEvent {
	public Area area;
	public int leftLocation, arrivedLocation;
	public float weight, volume;
	public Bin bin;
	public String toString(){
		switch(this.eventType){
		case SEventType.LORRY_LEFT:
			return DateTime.toString(time)+" -> "+"lorry "+area.areaIdx+" left location "+area.areaIdx+"."+this.leftLocation;
		case SEventType.LORRY_ARRIVED_AT:
			return DateTime.toString(time)+" -> "+"lorry "+area.areaIdx+" arrived at location "+area.areaIdx+"."+this.arrivedLocation;
		case SEventType.LORRY_LOAD:
			return DateTime.toString(time)+" -> "+"load of lorry "+area.areaIdx+" became 0 kg and contents volume 0 m^3";
		case SEventType.LORRY_CONTENTS_VOLUME_CHANGED:
			return DateTime.toString(time)+" -> "+"load of lorry "+area.areaIdx+" became "+(bin.currentLoadWeight+this.weight)+" kg and contents volume "+(bin.currentLoadVolume+this.volume)+" m^3";
		}
		return "Event Type Error in LorryEvent!";
	}
}
