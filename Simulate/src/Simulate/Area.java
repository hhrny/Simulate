package Simulate;

import java.util.HashSet;
import java.util.Set;


public class Area {
	public int areaIdx;
	public int serviceFreq;
	public int getServiceFreq() {
		return serviceFreq;
	}
	public void setServiceFreq(double sFreq) {
		this.serviceFreq = (int)(1.0/sFreq*360);
	}
	public float thresholdVal;
	public int noBins;
	public int[][] roadsLayout;    // road layout and distances between bin location ( in minutes)
	public Bin[] bins;
	public Lorry lorry;
	public Simulate simulate;
	public Set<Integer> binOverflow = new HashSet<Integer>();
	public Set<Integer> binExceeded = new HashSet<Integer>();
	
	// statistics parameter
	public int allTripDuration = 0;   // all trips duration; seconds
	public int allTripTimes = 0;     // number of trips
	
	public int noTrips = 0;
	
	public int noSchedule = 0;
	
	public float allWasteWeight = 0;    // kg
	public float allWasteVolume = 0;
	
//	public static Comparator<> comparator;
	
	public Area(){
		
	}
	public void configArea(AreaConfig acf){
		this.areaIdx = acf.getAreaIdx();
		this.serviceFreq = (int)(1.0/acf.getServiceFreq()*3600);
		this.thresholdVal = acf.getThresholdVal();
		this.noBins = acf.getNoBins();
		this.roadsLayout = new int[this.noBins+1][this.noBins+1];
		bins = new Bin[this.noBins];
		for(int i = 0; i <= this.noBins; i ++){
			for(int j = 0; j <= this.noBins; j ++){
				this.roadsLayout[i][j] = acf.getRoadsLayout().get(i*(this.noBins+1)+j);
				if(this.roadsLayout[i][j] != -1){
					this.roadsLayout[i][j] *= 60;
				}
				else{
					this.roadsLayout[i][j] = Integer.MAX_VALUE/2;
				}
			}
		}
		for(int i = 0; i < this.noBins; i ++){
			bins[i] = new Bin(this, i+1);		
		}
		lorry = new Lorry();
		lorry.area = this;
		lorry.rp.genShortestPath(this.roadsLayout, this.noBins+1);
	}
	public void genEvent(){
		for(int i = 0; i < this.noBins; i ++){
			SEvent e = User.genBagSEvent(bins[i]);
			this.addEventToQueue(e);
		}
		LorrySEvent lorryScheduleEvent = new LorrySEvent();
		lorryScheduleEvent.time = this.serviceFreq;
		lorryScheduleEvent.eventType = SEventType.LORRY_SCHEDULE;
		lorryScheduleEvent.area = this;
		lorryScheduleEvent.leftLocation = 0;
		this.addEventToQueue(lorryScheduleEvent);
	}
	public void addEventToQueue(SEvent event){
		simulate.eventsQueue.addEvent(event);
	}
	public int getAverageTripDuration(){
		// minutes:seconds
		if(this.allTripTimes == 0){
			return 0;
		}
		return this.allTripDuration/this.allTripTimes;
	}
	public int getNumberOfTrips(){
		// number of trips
		return this.noTrips;
	}
	public float getTripEfficiency(){
		// kg/min
		if(this.allTripDuration == 0){
			return 0;
		}
		return (float)this.allWasteWeight*60/this.allTripDuration;
	}
	public float getAverageVolumeCollected(){
		// m^3
		if(allTripTimes == 0){
			return 0;
		}
		return (float)allWasteVolume/allTripTimes;
	}
	public int getNoSchedule(){
		return noSchedule;
	}
	public float getNoTripsPerSchedule(){
		if(noSchedule == 0){
			return 0;
		}
		return (float)allTripTimes/noSchedule;
	}
	public int getNoBinOverflowed(){
		return binOverflow.size();
	}
	public float getPercentageOfBinOverflowed(){
		return (float)getNoBinOverflowed()/bins.length;
	}
}
