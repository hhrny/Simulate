package Simulate;

import java.io.File;
import java.text.DecimalFormat;
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
		// add stop event
		SEvent stopEvent = new SEvent();
		stopEvent.time = Simulate.stopTime;
		stopEvent.eventType = SEventType.SIMULATE_STOP;
		this.eventsQueue.addEvent(stopEvent);
	}
	private void bagDisposedEvent(SEvent e){
		// display the bag disposed message
		BagSEvent currentBagEvent = (BagSEvent)e;
		if(Simulate.isAbleLogging){
			// if display the message
			System.out.println(e.toString());
		}
		// check the bin is overflowed
		if(! currentBagEvent.bin.isOverflowed()){
			// the bin is not full
			// generate next bin contents volume changed event
			BinSEvent nextBinEvent = new BinSEvent();
			nextBinEvent.bag = currentBagEvent.bag;
			nextBinEvent.bin = currentBagEvent.bin;
			nextBinEvent.eventType = SEventType.BIN_CONTENTS_VOLUME_CHANGED;
			nextBinEvent.time = currentBagEvent.time;
			nextBinEvent.volume = currentBagEvent.bag.bagVolume;
			nextBinEvent.weight = currentBagEvent.bag.bagWeight;
			this.eventsQueue.addEvent(nextBinEvent);
		}
		// generate next event of bag disposed
		BagSEvent nextBagEvent = User.genBagSEvent(currentBagEvent.bin);
		this.eventsQueue.addEvent(nextBagEvent);
	}
	private void lorryLeftEvent(SEvent e){
		// display current lorry left message
		LorrySEvent currentLorryEvent = (LorrySEvent)e;
		if(Simulate.isAbleLogging){
			// if display the message
			System.out.println(e.toString());
		}
		// route planning, to get next destination
		currentLorryEvent.area.lorry.routePlanning();
		
		// generate next lorry arrived event
		LorrySEvent nextLorryArrivedEvent = new LorrySEvent();
		nextLorryArrivedEvent.eventType = SEventType.LORRY_ARRIVED_AT;
		nextLorryArrivedEvent.time = currentLorryEvent.time + currentLorryEvent.area.lorry.rp.getTime(currentLorryEvent.area.lorry.currentLocation, currentLorryEvent.area.lorry.destLocation);
		nextLorryArrivedEvent.area = currentLorryEvent.area;
		nextLorryArrivedEvent.leftLocation = currentLorryEvent.area.lorry.currentLocation;
		if(currentLorryEvent.area.lorry.destLocation > 0){
			// go to next bin
			//System.err.println(nextLorryArrivedEvent.arrivedLocation);
			nextLorryArrivedEvent.arrivedLocation = currentLorryEvent.area.lorry.destLocation;
			//System.err.println(nextLorryArrivedEvent.arrivedLocation);
			nextLorryArrivedEvent.bin = currentLorryEvent.area.bins[nextLorryArrivedEvent.arrivedLocation-1];
		}
		else{
			// back to location 0
			nextLorryArrivedEvent.arrivedLocation = 0;
			nextLorryArrivedEvent.bin = null;
		}
		this.eventsQueue.addEvent(nextLorryArrivedEvent);
	}
	private void lorryArrivedAtEvent(SEvent e){
		// display current lorry arrived at event
		LorrySEvent currentLorryEvent = (LorrySEvent)e;
		if(Simulate.isAbleLogging){
			// if display the message
			System.out.println(e.toString());
		}
		// arrived at the destination
		currentLorryEvent.area.lorry.currentLocation = currentLorryEvent.arrivedLocation;
		// statistic
		currentLorryEvent.area.allTripTimes ++;
		currentLorryEvent.area.allTripDuration += currentLorryEvent.area.lorry.rp.getTime(currentLorryEvent.leftLocation, currentLorryEvent.arrivedLocation);
		// generate next events
		if(currentLorryEvent.arrivedLocation == 0){ 
			// back to the location 0
			LorrySEvent nextLorryEvent = new LorrySEvent();
			nextLorryEvent.area = currentLorryEvent.area;
			nextLorryEvent.time = currentLorryEvent.time + 5*(int)Bin.binServiceTime;
			nextLorryEvent.eventType = SEventType.LORRY_LOAD;
			this.eventsQueue.addEvent(nextLorryEvent);
		}
		else{
			// arrived at destination bin, load the bin than add a load bin event
			BinSEvent nextBinLoadEvent = new BinSEvent();
			nextBinLoadEvent.time = currentLorryEvent.time + (int)Bin.binServiceTime;
			nextBinLoadEvent.eventType = SEventType.BIN_LOAD;
			nextBinLoadEvent.bin = currentLorryEvent.bin;
			nextBinLoadEvent.bag = null;
			this.eventsQueue.addEvent(nextBinLoadEvent);
			// and generate a lorry contents volume changed event
			LorrySEvent nextLorryEvent = new LorrySEvent();
			nextLorryEvent.eventType = SEventType.LORRY_CONTENTS_VOLUME_CHANGED;
			nextLorryEvent.time = nextBinLoadEvent.time;
			nextLorryEvent.area = currentLorryEvent.area;
			nextLorryEvent.bin = currentLorryEvent.bin;
			nextLorryEvent.weight = nextBinLoadEvent.bin.currentLoadWeight;
			nextLorryEvent.volume = nextBinLoadEvent.bin.currentLoadVolume/2;
			this.eventsQueue.addEvent(nextLorryEvent);
		}
	}
	private void lorryContentsVolumeChangedEvent(SEvent e){
		// display the current lorry event
		LorrySEvent currentLorryEvent = (LorrySEvent)e;
		currentLorryEvent.area.lorry.currentLoadVolume += currentLorryEvent.volume;
		currentLorryEvent.area.lorry.currentLoadWeight += currentLorryEvent.weight;
		// statistic
		// add the waste weight to the all waste weight
		currentLorryEvent.area.allWasteWeight += currentLorryEvent.weight;
		// add the waste volume to the all waste volume
		currentLorryEvent.area.allWasteVolume += currentLorryEvent.volume;
		
		// generate next lorry left event
		LorrySEvent nextLorryLeftEvent = new LorrySEvent();
		nextLorryLeftEvent.time = currentLorryEvent.time;
		nextLorryLeftEvent.eventType = SEventType.LORRY_LEFT;
		nextLorryLeftEvent.area = currentLorryEvent.area;
		nextLorryLeftEvent.bin = currentLorryEvent.bin;
		nextLorryLeftEvent.leftLocation = currentLorryEvent.area.lorry.currentLocation;
		this.eventsQueue.addEvent(nextLorryLeftEvent);
	}
	private void lorryLoad(SEvent e){
		// display lorry load message
		LorrySEvent currentLorryEvent = (LorrySEvent)e;
		if(Simulate.isAbleLogging){
			// if display the message
			System.out.println(e.toString());
		}
		// clean the lorry
		currentLorryEvent.area.lorry.currentLoadVolume = 0;
		currentLorryEvent.area.lorry.currentLoadWeight = 0;
		// route planning
		currentLorryEvent.area.lorry.routePlanning();
		if(currentLorryEvent.area.lorry.destLocation != 0){
			// need to collect the rubbish again
			LorrySEvent nextLorryLeftEvent = new LorrySEvent();
			nextLorryLeftEvent.eventType = SEventType.LORRY_LEFT;
			nextLorryLeftEvent.time = currentLorryEvent.time;
			nextLorryLeftEvent.area = currentLorryEvent.area;
			nextLorryLeftEvent.leftLocation = 0;
			this.eventsQueue.addEvent(nextLorryLeftEvent);
		}
	}
	private void binLoad(SEvent e){
		// display bin load message
		BinSEvent currentBinEvent = (BinSEvent)e;
		if(Simulate.isAbleLogging){
			// if display the message
			System.out.println(e.toString());
		}
		// clear the bin
		if(currentBinEvent.bin.currentLoadVolume >= Bin.binVolume){
			currentBinEvent.bin.area.binExceeded.remove(currentBinEvent.bin.binId);
			currentBinEvent.bin.setThresholdExceeded(false);
		}
		if(currentBinEvent.bin.currentLoadVolume >= Bin.binVolume*currentBinEvent.bin.area.thresholdVal){
			currentBinEvent.bin.area.binOverflow.remove(currentBinEvent.bin.binId);
			currentBinEvent.bin.setOverflowed(false);
		}
		currentBinEvent.bin.currentLoadVolume = 0;
		currentBinEvent.bin.currentLoadWeight = 0;
	}
	private void binContentsVolumeChangedEvent(SEvent e){
		// display bin contents volume changed message
		BinSEvent nextBinEvent;
		BinSEvent currentBinEvent = (BinSEvent)e;
		if(Simulate.isAbleLogging){
			// if display the message
			System.out.println(e.toString());
		}
		currentBinEvent.bin.currentLoadVolume += currentBinEvent.volume;
		currentBinEvent.bin.currentLoadWeight += currentBinEvent.weight;
		// generate a bin event if overflowed or exceeded
		if(currentBinEvent.bin.currentLoadVolume > Bin.binVolume){
			nextBinEvent = new BinSEvent();
			nextBinEvent.time = currentBinEvent.time;
			nextBinEvent.eventType = SEventType.BIN_OVERFLOWED;
			nextBinEvent.bin = currentBinEvent.bin;
			nextBinEvent.bag = currentBinEvent.bag;
			this.eventsQueue.addEvent(nextBinEvent);
		}else if(currentBinEvent.bin.currentLoadVolume > Bin.binVolume*currentBinEvent.bin.area.thresholdVal){
			nextBinEvent = new BinSEvent();
			nextBinEvent.time = currentBinEvent.time;
			nextBinEvent.eventType = SEventType.BIN_OCCUPANCY_THRESHOLD_EXCEEDED;
			nextBinEvent.bin = currentBinEvent.bin;
			nextBinEvent.bag = currentBinEvent.bag;
			this.eventsQueue.addEvent(nextBinEvent);
		}
	}
	private void binOccupancyThresholdExceededEvent(SEvent e){
		// display bin occupancy threshold exceeded message
		BinSEvent currentBinEvent = (BinSEvent)e;
		if(Simulate.isAbleLogging){
			// if display the message
			System.out.println(e.toString());
		}
		currentBinEvent.bin.area.binExceeded.add(currentBinEvent.bin.binId);
	}
	private void binOverflowed(SEvent e){
		// display bin overflowed message
		BinSEvent currentBinEvent = (BinSEvent)e;
		if(Simulate.isAbleLogging){
			// if display the message
			System.out.println(e.toString());
		}
		currentBinEvent.bin.area.binOverflow.add(currentBinEvent.bin.binId);
	}
	private void modifySystemState(SEvent e){
		// modify system state according to event
		switch(e.eventType){
		case SEventType.BAG_DISPOSED:
			// display the bag disposed message and generate next event of this disposed bag
			// and bin contents volume changed event
			this.bagDisposedEvent(e);
			break;
		case SEventType.LORRY_LEFT:
			// display the message of lorry left
			this.lorryLeftEvent(e);
			break;
		case SEventType.LORRY_ARRIVED_AT:
			// display a lorry arrived at message
			this.lorryArrivedAtEvent(e);
			break;
		case SEventType.LORRY_CONTENTS_VOLUME_CHANGED:
			// display a message of lorry contents volume changed and change the contents of lorry
			this.lorryContentsVolumeChangedEvent(e);
			break;
		case SEventType.LORRY_LOAD:
			// load and check whether there is a next collect rubbish schedule
			this.lorryLoad(e);
			break;
		case SEventType.BIN_LOAD:
			// load bin
			this.binLoad(e);
			break;
		case SEventType.BIN_CONTENTS_VOLUME_CHANGED:
			// display a bin contents volume changed message and change the contents of bin
			this.binContentsVolumeChangedEvent(e);
			break;
		case SEventType.BIN_OCCUPANCY_THRESHOLD_EXCEEDED:
			// bin overflowed and the bin to occupancy threshold exceeded list
			this.binOccupancyThresholdExceededEvent(e);
			break;
		case SEventType.BIN_OVERFLOWED:
			// bin overflowed and the bin to overflowed list
			this.binOverflowed(e);
			break;
		}
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
			if(nextEvent.eventType == SEventType.SIMULATE_STOP){
				// simulate is finished
				break;
			}
			this.modifySystemState(nextEvent);
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
		DecimalFormat df = new DecimalFormat("########0.00");  
		System.out.println("---");
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": average trip duration "+DateTime.toMinSecString(areas.get(i).getAverageTripDuration()));
			alltripduration += areas.get(i).allTripDuration;
			alltriptimes += areas.get(i).allTripTimes;
		}
		System.out.println("overall average trip duration "+DateTime.toMinSecString((alltripduration/alltriptimes)));
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": average no. trip "+df.format(areas.get(i).getNoTripsPerSchedule()));
			allnoschedule += areas.get(i).getNoSchedule();
		}
		System.out.println("overall average no. trip " + df.format(((float)alltriptimes/allnoschedule)));
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": trip efficiency "+df.format(areas.get(i).getTripEfficiency()));// kg/min
			allwasteweight += areas.get(i).allWasteWeight;
		}
		System.out.println("overall trip efficiency "+df.format(allwasteweight*60/alltripduration));   // kg/min
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": average volume collected "+df.format(areas.get(i).getAverageVolumeCollected()));
			allwastevolume += areas.get(i).allWasteVolume;
		}
		System.out.println("overall average volume collected "+df.format(allwastevolume/alltriptimes));
		for(i = 0; i < Simulate.noAreas; i ++){
			System.out.println("area " + i + ": percentage of bins overflowed "+df.format(areas.get(i).getPercentageOfBinOverflowed()));
			allbins += areas.get(i).bins.length;
			allbinoverflowed += areas.get(i).getNoBinOverflowed();
		}
		System.out.println("overall percentage of bins overflowed "+df.format((float)allbinoverflowed/allbins));
		System.out.println("---");
	}
	public static void main(String []args){
		int i;
		String filename;
		Lexer lexer;
		// lexer analyze
		if(args.length >= 1){
			filename = args[0];
		}else{
			filename = "conf.txt";
		}
		// check the file is exist
		File f = new File(filename);
		if(! f.exists()){
			System.out.println("The input file " + filename + " no exist!");
			return;
		}
		// set the parameter disable logging
		if(args.length == 2){
			if(args[1].equals("-disablelogging")){
				Simulate.setAbleLogging(false);
			}
		}
		lexer = new Lexer();
		lexer.setFilename(filename);
		// debug
		//Simulate.setAbleLogging(false);
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
