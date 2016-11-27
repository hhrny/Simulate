package Simulate;

import java.util.Random;

public class User {
	private static float disposalDistrRate;    // no per hour
	public static float getDisposalDistrRate() {
		return disposalDistrRate;
	}
	public static void setDisposalDistrRate(float disposalDistrRate) {
		User.disposalDistrRate = disposalDistrRate;
	}
	public static int getDisposalDistrShape() {
		return disposalDistrShape;
	}
	public static void setDisposalDistrShape(int disposalDistrShape) {
		User.disposalDistrShape = disposalDistrShape;
	}
	private static int disposalDistrShape;   // shape of the Erlang distribution
	private static float bagVolume;     // cubic metres
	private static float bagWeightMin;
	private static float bagWeightMax;
	private static Random rd;
	
	public static boolean configUser(Config cf){
		if(cf == null){
			return false;
		}
		// get the system current time as the seek of rand
		long t = System.currentTimeMillis();
		rd = new Random(t);
		User.disposalDistrRate = cf.getDisposalDistrRate();
		User.disposalDistrShape = cf.getDisposalDistrShape();
		User.bagVolume = cf.getBagVolume();
		User.bagWeightMin = cf.getBagWeightMin();
		User.bagWeightMax = cf.getBagWeightMax();
		return true;
	}
	public static BagSEvent genBagSEvent(Bin bin){
		BagSEvent bevent = new BagSEvent();		
		Bag bag = new Bag();
		bag.bagVolume = User.bagVolume;
		bag.bagWeight = (User.bagWeightMax-User.bagWeightMin)*rd.nextFloat()+User.bagWeightMin;
		double tmp = 1.0;
		for(int i = 0; i < User.disposalDistrShape; i ++){
			tmp *= rd.nextDouble();
		}
		int timeInterval = (int)(-1.0/User.disposalDistrRate*3600*Math.log(tmp));
		//System.out.println("time interval: " + timeInterval);
		bag.disposalTime = Simulate.currentTime+timeInterval;
		bevent.eventType = SEventType.BAG_DISPOSED;
		bevent.time = bag.disposalTime;
		bevent.bin = bin;
		bevent.bag = bag;
		return bevent;
	}
}
