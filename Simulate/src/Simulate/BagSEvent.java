package Simulate;

public class BagSEvent extends SEvent {
	public Bin bin;
	public Bag bag;
	
	public String toString(){
		return DateTime.toString(time)+" -> "+"bag weighing "+bag.bagWeight+" kg disposed of at bin "+bin.area.areaIdx+"."+bin.binId;
	}
}
