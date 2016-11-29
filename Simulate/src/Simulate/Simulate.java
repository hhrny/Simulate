package Simulate;

import java.util.Vector;

import Lexer.Lexer;

public class Simulate {
	public static int currentTime;
	public static int stopTime;
	
	public static int noAreas;
	public Vector<Area> areas = new Vector<Area>();
	public static float warmUpTime;

	public EventsQueue eventsQueue = new EventsQueue();
	
	private static boolean isAbleLogging = true;
	
	public void initSimulate(Config cf){
		// set the param of config
		Simulate.currentTime = 0;
		Simulate.stopTime = (int)cf.getStopTime()*3600;
		Simulate.noAreas = cf.getNoAreas();
		Simulate.warmUpTime = (int)cf.getWarmUpTime()*3600;
		Bin.binServiceTime = cf.getBinServiceTime();
		Bin.binVolume = cf.getBinVolume();
		Lorry.lorryMaxLoad = cf.getLorryMaxLoad();
		Lorry.lorryVolume = cf.getLorryVolume();
		User.configUser(cf);
		// clear events queue
		this.eventsQueue.clear();
		// generate simulate area
		areas.clear();
		for(int i = 0; i < Simulate.noAreas; i ++){
			Area tmp = new Area();
			tmp.simulate = this;
			tmp.configArea(cf.getAreas().get(i));
			tmp.genEvent();
			areas.add(tmp);
		}
	}
	public void modifySystemState(SEvent e){
		// modify system state according to event
		LorrySEvent le,le2;
		BagSEvent bage;
		BinSEvent bine, bine2;
		
		switch(e.eventType){
		case SEventType.BAG_DISPOSED:
			// display the bag disposed message and generate next event of this bag and bin
			bage = (BagSEvent)e;
			bine = new BinSEvent();
			bine.bag = bage.bag;
			bine.bin = bage.bin;
			bine.eventType = SEventType.BIN_CONTENTS_VOLUME_CHANGED;
			bine.time = bage.time;
			bine.volume = bage.bag.bagVolume;
			bine.weight = bage.bag.bagWeight;
			this.eventsQueue.addEvent(bine);
			break;
		case SEventType.LORRY_LEFT:
			le = (LorrySEvent)e;
			if(le.area.lorry.currentLoadVolume >= Lorry.lorryVolume || le.area.lorry.currentLoadWeight >= Lorry.lorryMaxLoad){
				le.area.lorry.destLocation = 0;
			}else{
				le.area.lorry.routePlanning();
			}
			le2 = new LorrySEvent();
			le2.time = Simulate.currentTime + (le.area.lorry.rp.getTime(le.area.lorry.currentLocation, le.area.lorry.destLocation));
			le2.leftLocation = le.area.lorry.currentLocation;
			le2.arrivedLocation = le.area.lorry.destLocation;
			le2.area = le.area;
			//System.out.println(le2.arrivedLocation-1);
			if(le2.arrivedLocation != 0){
				le2.bin = le.area.bins[le2.arrivedLocation-1];
			}
			le2.eventType = SEventType.LORRY_ARRIVED_AT;
			this.eventsQueue.addEvent(le2);
			break;
		case SEventType.LORRY_ARRIVED_AT:
			le = (LorrySEvent)e;
			le.area.allTripTimes ++;
			le.area.allTripDuration += le.area.lorry.rp.getTime(le.leftLocation, le.arrivedLocation);
			if(le.arrivedLocation == 0){ 
				// back to the location 0
				le2 = new LorrySEvent();
				le2.area = le.area;
				le2.time = Simulate.currentTime + 5*(int)Bin.binServiceTime;
				le2.eventType = SEventType.LORRY_LOAD;
				this.eventsQueue.addEvent(le2);
			}
			else{
				// arrived at destination bin, load the bin
				// add a load bin event
				bine = new BinSEvent();
				bine.time = Simulate.currentTime + (int)Bin.binServiceTime;
				bine.eventType = SEventType.BIN_LOAD;
				bine.bin = le.area.bins[le.arrivedLocation-1];
				bine.bag = null;
				this.eventsQueue.addEvent(bine);
				bine.bin.area.lorry.currentLocation = le.arrivedLocation;
			}
			break;
		case SEventType.LORRY_CONTENTS_VOLUME_CHANGED:
			le = (LorrySEvent)e;
			le.area.lorry.currentLoadVolume += le.volume;
			le.area.lorry.currentLoadWeight += le.weight;
			// add the waste weight to the all waste weight
			le.area.allWasteWeight += le.weight;
			// add the waste volume to the all waste volume
			le.area.allWasteVolume += le.volume;
			le2 = new LorrySEvent();
			le2.time = Simulate.currentTime;
			le2.eventType = SEventType.LORRY_LEFT;
			le2.area = le.area;
			le2.bin = le.bin;
			le2.leftLocation = le.area.lorry.currentLocation;
			this.eventsQueue.addEvent(le2);
			break;
		case SEventType.LORRY_LOAD:
			break;
		case SEventType.BIN_LOAD:
			bine = (BinSEvent)e;
			if((bine.bin.area.lorry.currentLoadVolume + bine.bin.currentLoadVolume/2.0 <= Lorry.lorryVolume) && (bine.bin.area.lorry.currentLoadWeight+bine.bin.currentLoadWeight<=Lorry.lorryMaxLoad)){
				bine.volume = (float)(bine.bin.currentLoadVolume/2.0);
				bine.weight = bine.bin.currentLoadWeight;
			}else if(bine.bin.area.lorry.currentLoadVolume + bine.bin.currentLoadVolume/2.0 <= Lorry.lorryVolume){
				bine.weight = Lorry.lorryMaxLoad - bine.bin.area.lorry.currentLoadWeight;
				bine.volume = bine.bin.currentLoadVolume/bine.bin.currentLoadWeight*bine.weight;
			}else if(bine.bin.area.lorry.currentLoadWeight+bine.bin.currentLoadWeight<=Lorry.lorryMaxLoad){
				bine.volume = Lorry.lorryVolume - bine.bin.area.lorry.currentLoadVolume;
				bine.weight = bine.bin.currentLoadWeight/bine.bin.currentLoadVolume*bine.volume;
			}else{
				float tmpvolume, tmpweight;
				tmpvolume = Lorry.lorryVolume - bine.bin.area.lorry.currentLoadVolume;
				tmpweight = Lorry.lorryMaxLoad - bine.bin.area.lorry.currentLoadWeight;
				if(bine.bin.currentLoadVolume/bine.bin.currentLoadWeight*tmpweight <= tmpvolume){
					bine.weight = tmpweight;
					bine.volume = (float)(bine.bin.currentLoadVolume/bine.bin.currentLoadWeight*tmpweight/2.0);
				}else{
					bine.volume = (float)(tmpvolume/2.0);
					bine.weight = bine.bin.currentLoadWeight/bine.bin.currentLoadVolume*tmpvolume;
				}
			}
			bine.bin.currentLoadVolume -= bine.volume;
			bine.bin.currentLoadWeight -= bine.weight;
			if(bine.bin.currentLoadVolume < Bin.binVolume){
				bine.bin.area.binExceeded.remove(bine.bin.binId);
			}
			if(bine.bin.currentLoadVolume < Bin.binVolume*bine.bin.area.thresholdVal){
				bine.bin.area.binOverflow.remove(bine.bin.binId);
			}
			// add a lorry volume changed event
			le2 = new LorrySEvent();
			le2.volume = bine.volume;
			le2.weight = bine.weight;
			le2.eventType = SEventType.LORRY_CONTENTS_VOLUME_CHANGED;
			le2.bin = bine.bin;
			le2.area = bine.bin.area;
			le2.time = bine.time;
			this.eventsQueue.addEvent(le2);
			break;
		case SEventType.BIN_CONTENTS_VOLUME_CHANGED:
			bine = (BinSEvent)e;
			bine.bin.currentLoadVolume += bine.volume;
			bine.bin.currentLoadWeight += bine.weight;
			// generate a bin event if overflowed or exceeded
			if(bine.bin.currentLoadVolume > Bin.binVolume){
				bine2 = new BinSEvent();
				bine2.time = bine.time;
				bine2.eventType = SEventType.BIN_OVERFLOWED;
				bine2.bin = bine.bin;
				bine2.bag = bine.bag;
				this.eventsQueue.addEvent(bine2);
			}else if(bine.bin.currentLoadVolume > Bin.binVolume*bine.bin.area.thresholdVal){
				bine2 = new BinSEvent();
				bine2.time = bine.time;
				bine2.eventType = SEventType.BIN_OCCUPANCY_THRESHOLD_EXCEEDED;
				bine2.bin = bine.bin;
				bine2.bag = bine.bag;
				this.eventsQueue.addEvent(bine2);
			}
			bage = User.genBagSEvent(bine.bin);
			this.eventsQueue.addEvent(bage);
			break;
		case SEventType.BIN_OCCUPANCY_THRESHOLD_EXCEEDED:
			bine = (BinSEvent)e;
			bine.bin.area.binExceeded.add(bine.bin.binId);
			break;
		case SEventType.BIN_OVERFLOWED:
			bine = (BinSEvent)e;
			bine.bin.area.binOverflow.add(bine.bin.binId);
			break;
		}
		if(Simulate.isAbleLogging){
			System.out.println(e.toString());
		}
		//System.out.println("Error Event Type!");
	}
	public void runSimulate(){
		Simulate.currentTime = 0;
		SEvent nextEvent;
		while(Simulate.currentTime <= Simulate.stopTime){
			nextEvent = this.eventsQueue.getNextEvent();
			if(nextEvent == null){
				System.out.println("Events Queue is null!");
				return;
			}
			Simulate.currentTime = nextEvent.time;
			this.modifySystemState(nextEvent);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static boolean isAbleLogging() {
		return isAbleLogging;
	}
	public static void setAbleLogging(boolean isAbleLogging) {
		Simulate.isAbleLogging = isAbleLogging;
	}

	public void printSummaryStatistics(){
		int i, alltripduration=0, alltriptimes=0, allnoschedule = 0, allbinoverflowed= 0,allbins= 0;
		float allwasteweight= 0, allwastevolume=0;
		System.out.println("---");
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": average trip duration "+DateTime.toMinSecString(areas.get(i).getAverageTripDuration()));
			alltripduration += areas.get(i).allTripDuration;
			alltriptimes += areas.get(i).allTripTimes;
		}
		System.out.println("overall average trip duration "+DateTime.toMinSecString((alltripduration/alltriptimes)));
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": average no. trip "+areas.get(i).getNoTripsPerSchedule());
			allnoschedule += areas.get(i).getNoSchedule();
		}
		System.out.println("overall average no. trip " + ((float)alltriptimes/allnoschedule));
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": trip efficiency "+areas.get(i).getTripEfficiency());// kg/min
			allwasteweight += areas.get(i).allWasteWeight;
		}
		System.out.println("overall trip efficiency "+allwasteweight*60/alltripduration);   // kg/min
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": average volume collected "+areas.get(i).getAverageVolumeCollected());
			allwastevolume += areas.get(i).allWasteVolume;
		}
		System.out.println("overall average volume collected "+allwastevolume/alltriptimes);
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": percentage of bins overflowed "+areas.get(i).getPercentageOfBinOverflowed());
			allbins += areas.get(i).bins.length;
			allbinoverflowed += areas.get(i).getNoBinOverflowed();
		}
		System.out.println("overall percentage of bins overflowed "+(float)allbinoverflowed/allbins);
		System.out.println("---");
	}
	public static void main(String []args){
		int i;
		Lexer lexer = new Lexer();
		// lexer analyze
		if(args.length >= 1){
			lexer.setFilename(args[0]);
		}else{
			lexer.setFilename("conf3.txt");
		}
		// set the parameter disable logging
		if(args.length == 2){
			if(args[1].equals("-disablelogging")){
				Simulate.setAbleLogging(false);
			}
		}
		// debug
		Simulate.setAbleLogging(false);
		// generate configure from lexer
		Config cf = Config.genFromLexer(lexer);
		if(cf != null){
			System.out.println("The simulation will continue.");
		}
		else{
			System.out.println("The simulation will terminate.");
			return;
		}
		Simulate simulate = new Simulate();
		// experiment
		if(cf.isExperiment()){
			// if there is a experiment parameter
			for(i = 0; i < cf.noExp; i ++){
			//for(i = 0; i < 1; i ++){
				simulate.initSimulate(cf.getExperiment(i));
				// print the parameter of configure
				cf.getExperiment(i).printConfigureParameter(i);
				// run simulate
				simulate.runSimulate();
				// print summary statistics
				simulate.printSummaryStatistics();
			}
		}
		else
		{
			simulate.initSimulate(cf);
			// run simulate
			simulate.runSimulate();
			// print summary statistics
			simulate.printSummaryStatistics();
		}
	}
}
