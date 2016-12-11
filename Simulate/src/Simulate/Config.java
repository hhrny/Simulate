package Simulate;

import java.util.Vector;

import Lexer.*;

public class Config {
	private int lorryVolume;  // cubic metres
	private int lorryMaxLoad;   // kg
	private float binServiceTime;   //seconds, time required to empty a bin
	private float binVolume;      // cubic metres
	private float disposalDistrRate;    // no per hour
	private int disposalDistrShape;   // shape of the Erlang distribution
	private float bagVolume;     // cubic metres
	private float bagWeightMin;
	private float bagWeightMax;
	private int noAreas;    
	private Vector<AreaConfig> areasConfig = new Vector<AreaConfig>();
	private float stopTime;     // hours
	private float warmUpTime;     // hours, Warm-up time allowed before collecting statistics
	
	public int      noExp;
	private boolean isExperiment;
       
	public Vector<Float>   expDisposalDistrRate;
	public boolean         isDDRexp;
	public Vector<Integer> expDisposalDistrShape;
	public boolean         isDDSexp;
	public Vector<Float>   expServiceFreq;
	public boolean         isSFexp;
	
	public Vector<Config> experiments;
	
	public Config(){
		lorryVolume = -1;
		lorryMaxLoad = -1;
		binServiceTime = (float) -1;
		binVolume = (float) -1;
		disposalDistrRate = (float) -1;
		disposalDistrShape = -1;
		bagVolume = (float) -1;
		bagWeightMin = (float) -1;
		bagWeightMax = (float) -1;
		noAreas = -1;
		stopTime = (float) -1;
		warmUpTime = (float) -1;
		this.noExp = 0;
		this.isExperiment = false;
		this.isDDRexp = false;
		this.isDDSexp = false;
		this.isSFexp = false;
		this.expDisposalDistrRate = null;
		this.expDisposalDistrShape = null;
		this.expServiceFreq = null;
	}
	public Config(Config c){
		this.lorryVolume = c.getLorryVolume();
		this.lorryMaxLoad = c.getLorryMaxLoad();
		this.binServiceTime = c.getBinServiceTime();
		this.binVolume = c.getBinVolume();
		this.disposalDistrRate = c.getDisposalDistrRate();
		this.disposalDistrShape = c.getDisposalDistrShape();
		this.bagVolume = c.getBagVolume();
		this.bagWeightMin = c.getBagWeightMin();
		this.bagWeightMax = c.getBagWeightMax();
		this.noAreas = c.getNoAreas();
		this.stopTime = c.getStopTime();
		this.warmUpTime = c.getWarmUpTime();
		this.areasConfig = new Vector<AreaConfig>();
		for(int i = 0; i < this.noAreas; i ++){
			this.areasConfig.add(new AreaConfig(c.getAreas().get(i)));
		}
		this.noExp = c.noExp;
		this.isExperiment = c.isExperiment;
		this.expDisposalDistrRate = c.expDisposalDistrRate;
		this.expDisposalDistrShape = c.expDisposalDistrShape;
		this.expServiceFreq = c.expServiceFreq;
		this.isDDRexp = c.isDDRexp;
		this.isDDSexp = c.isDDSexp;
		this.isSFexp = c.isSFexp;
	}
	//
	public boolean checkNoArea(){
		if(areasConfig.size() != noAreas){
			System.err.println("Error: Parameter noAreas is not equal to number of areas!");
			return false;
		}
		return true;
	}
	// check the whether the configure is available
	public boolean isVaild(){
		boolean vaild = true;
		if(lorryVolume == -1){
			System.err.println("Error: Parameter lorryVolume is missed!");
			vaild = false;
		}
		if(lorryMaxLoad == -1){
			System.err.println("Error: Parameter lorryMaxLoad is missed!");
			vaild = false;
		}
		if(noAreas == -1){
			System.err.println("Error: Parameter noAreas is missed!");
			vaild = false;
		}
		if(warmUpTime == (float) -1){
			System.err.println("Error: Parameter warmUpTime is missed!");
			vaild = false;
		}
		if(binServiceTime == (float) -1){
			System.err.println("Error: Parameter binServiceTime is missed!");
			vaild = false;
		}
		if(binVolume == (float) -1){
			System.err.println("Error: Parameter binVolume is missed!");
			vaild = false;
		}
		if(disposalDistrRate == (float) -1 && expDisposalDistrRate == null){
			System.err.println("Error: Parameter disposalDistrRate is missed!");
			vaild = false;
		}
		if(disposalDistrShape == -1 && expDisposalDistrShape == null){
			System.err.println("Error: Parameter disposalDistrShape is missed!");
			vaild = false;
		}
		if(bagVolume == (float) -1){
			System.err.println("Error: Parameter bagVolume is missed!");
			vaild = false;
		}
		if(bagWeightMin == (float) -1){
			System.err.println("Error: Parameter bagWeightMin is missed!");
			vaild = false;
		}
		if(bagWeightMax == (float) -1){
			System.err.println("Error: Parameter bagWeightMax is missed!");
			vaild = false;
		}
		if(stopTime == (float) -1){
			System.err.println("Error: Parameter stopTime is missed!");
			vaild = false;
		}
		// check the bagWeightMin and bagWeightMax
		if((bagWeightMin != (float) -1) && (bagWeightMax == (float) -1) && (bagWeightMin >= bagWeightMax)){
			System.err.println("Error: Parameter bagWeightMin is bigger than bagWeightMax!");
			vaild = false;
		}
		// check the stop time and warm up time
		if((stopTime != (float) -1) && (warmUpTime == (float) -1) && (stopTime < warmUpTime)){
			System.err.println("Warning: Parameter stopTime smaller than warmUpTime.");
		}
		// check the lorry volume and bin volume
		if((lorryVolume != -1) && (binVolume == -1) && lorryVolume < binVolume){
			System.err.println("Warning: lorry capacity smaller than bin capacity.");
		}
		// check the disposalDistrRate and serviceFreq
		int i,j;
		if((! this.isDDRexp) && (! this.isSFexp)){
			for(i = 0; i < this.getAreas().size(); i ++){
				if(this.disposalDistrRate < this.getAreas().get(i).getServiceFreq()){
					System.err.println("Warning: disposal rate exceeds service rate in area "+i+".");
				}
			}
		}else if(this.isDDRexp && (! this.isSFexp)){
			for(i = 0; i < this.noExp; i ++){
				for(j = 0; j < this.getAreas().size(); j ++){
					if(this.expDisposalDistrRate.get(i) < this.getAreas().get(j).getServiceFreq()){
						System.err.println("Warning: disposal rate exceeds service rate in experiment "+(i+1)+" and area "+ j +".");
					}
				}
			}	
		}else if((! this.isDDRexp) && this.isSFexp){
			for(i = 0; i < this.noExp; i ++){
				if(this.disposalDistrRate < this.expServiceFreq.get(i)){
					System.err.println("Warning: disposal rate exceeds service rate in experiment "+(i+1)+".");
				}
			}	
		}else{
			for(i = 0; i < this.noExp; i ++){
				if(this.expDisposalDistrRate.get(i) < this.expServiceFreq.get(i)){
					System.err.println("Warning: disposal rate exceeds service rate in experiment "+(i+1)+".");
				}
			}
		}
		return vaild;
	}
	public int getLorryVolume() {
		return lorryVolume;
	}
	public void setLorryVolume(int lorryVolume) {
		this.lorryVolume = lorryVolume;
	}
	public int getLorryMaxLoad() {
		return lorryMaxLoad;
	}
	public void setLorryMaxLoad(int looryMaxLoad) {
		this.lorryMaxLoad = looryMaxLoad;
	}
	public float getBinServiceTime() {
		return binServiceTime;
	}
	public void setBinServiceTime(float binServiceTime) {
		this.binServiceTime = binServiceTime;
	}
	public float getBinVolume() {
		return binVolume;
	}
	public void setBinVolume(float binVolume) {
		this.binVolume = binVolume;
	}
	public float getDisposalDistrRate() {
		return disposalDistrRate;
	}
	public void setDisposalDistrRate(float disposalDistrRate) {
		this.disposalDistrRate = disposalDistrRate;
	}
	public int getDisposalDistrShape() {
		return disposalDistrShape;
	}
	public void setDisposalDistrShape(int disposalDistrShape) {
		this.disposalDistrShape = disposalDistrShape;
	}
	public float getBagVolume() {
		return bagVolume;
	}
	public void setBagVolume(float bagVolume) {
		this.bagVolume = bagVolume;
	}
	public float getBagWeightMin() {
		return bagWeightMin;
	}
	public void setBagWeightMin(float bagWeightMin) {
		this.bagWeightMin = bagWeightMin;
	}
	public float getBagWeightMax() {
		return bagWeightMax;
	}
	public void setBagWeightMax(float bagWeightMax) {
		this.bagWeightMax = bagWeightMax;
	}
	public int getNoAreas() {
		return noAreas;
	}
	public void setNoAreas(int noAreas) {
		this.noAreas = noAreas;
	}
	public Vector<AreaConfig> getAreas() {
		return areasConfig;
	}
	public void setAreas(Vector<AreaConfig> areasConfig) {
		this.areasConfig = areasConfig;
	}
	public float getStopTime() {
		return stopTime;
	}
	public void setStopTime(float stopTime) {
		this.stopTime = stopTime;
	}
	public float getWarmUpTime() {
		return warmUpTime;
	}
	public void setWarmUpTime(float warmUpTime) {
		this.warmUpTime = warmUpTime;
	}
	public void setServiceFreq(float serviceFreq){
		for(AreaConfig ac : this.areasConfig){
			ac.setServiceFreq(serviceFreq);
		}
	}
	// settting the paremeter
	public boolean setParam(Token paramName, Lexer lexer){
		Token tmp;
		switch(paramName.tag){
		case Tag.LORRYVOLUME:
			tmp = lexer.nextToken();
			if(tmp.isInteger() && ((Num)tmp).isUINT8()){
				// check the parameter whether is configure again
				if(this.lorryVolume != -1){
					System.err.println("Warning: Parameter lorryVolume is configure again!");
					//return false;
				}
				this.setLorryVolume(Integer.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter lorryVolume and value is not at same line!");
					return false;
				}
				// check the max lorry volume bigger than 0.0
				if(this.getLorryVolume() < 0.0){
					System.err.println("Error: Parameter lorryVolume is less than 0.0!");
					return false;
				}
				else if(this.getLorryVolume() == 0.0){
					System.err.println("Error: Parameter lorryVolume is equal to 0.0!");
				}
				return true;
			}
			System.err.println("Error: Parameter lorryVolume is not uint8_t type!");
			return false;
		case Tag.LORRYMAXLOAD:
			tmp = lexer.nextToken();
			if(tmp.isInteger() && ((Num)tmp).isUINT16()){
				// check the parameter whether is configure again
				if(this.lorryMaxLoad != -1){
					System.err.println("Warning: Parameter lorryMaxLoad is configure again!");
					//return false;
				}
				this.setLorryMaxLoad(Integer.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter lorryMaxLoad and value is not at same line!");
					return false;
				}
				// check the max lorry load bigger than 0.0
				if(this.getLorryMaxLoad() < 0.0){
					System.err.println("Error: Parameter lorryMaxLoad is less than 0.0!");
					return false;
				}
				else if(this.getLorryMaxLoad() == 0.0){
					System.err.println("Error: Parameter lorryMaxLoad is equal to 0.0!");
				}
				return true;
			}
			System.err.println("Error: Parameter lorryMaxLoad is not uint16_t type!");
			return false;
		case Tag.BINSERVICETIME:
			tmp = lexer.nextToken();
			if(tmp.isFloat()){
				// check the parameter whether is configure again
				if(this.binServiceTime != -1){
					System.err.println("Warning: Parameter binServiceTime is configure again!");
					//return false;
				}
				this.setBinServiceTime(Float.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter binServiceTime and value is not at same line!");
					return false;
				}
				// check the bin service time bigger than 0.0
				if(this.getBinServiceTime() < 0.0){
					System.err.println("Error: Parameter binServiceTime is less than 0.0!");
					return false;
				}
				return true;
			}
			System.err.println("Error: Parameter binServiceTime is not float type!");
			return false;
		case Tag.BINVOLUME:
			tmp = lexer.nextToken();
			if(tmp.isFloat()){
				// check the parameter whether is configure again
				if(this.binVolume != -1){
					System.err.println("Warning: Parameter binVolume is configure again!");
					//return false;
				}
				this.setBinVolume(Float.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter binVolume and value is not at same line!");
					return false;
				}
				// check the bin volume bigger than 0.0
				if(this.getBinVolume() < 0.0){
					System.err.println("Error: Parameter binVolume is less than 0.0!");
					return false;
				}
				else if(this.getBinVolume() == 0.0){
					System.err.println("Error: Parameter binVolume is equal to 0.0!");
				}
				return true;
			}
			System.err.println("Error: Parameter binVolume is not float type!");
			return false;
		case Tag.DISPOSALDISTRRATE:
			tmp = lexer.nextToken();
			if(tmp.tag == Tag.EXPERIMENT){
				// check the parameter whether is configure again
				if(this.expDisposalDistrRate != null){
					System.err.println("Warning: Parameter disposalDistrRate is configure again!");
//					while(lexer.hasNextToken() && (lexer.getNextToken().tag == Tag.FLOAT || lexer.getNextToken().tag == Tag.INT)){
//						tmp = lexer.nextToken();
//					}
//					return false;
				}
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter DisposalDistrRate and 'experiment' is not at same line!");
					return false;
				}
				this.expDisposalDistrRate = new Vector<Float>();
				while(lexer.hasNextToken() && lexer.getNextToken().isFloat()){
					tmp = lexer.nextToken();
					this.expDisposalDistrRate.add(Float.valueOf(((Num)tmp).val));
				}
				// check the experiment parameter
				for(int i = 0; i < this.expDisposalDistrRate.size(); i ++){
					// check the bag disposalDistrRate bigger than 0.0
					if(this.expDisposalDistrRate.get(i) < 0.0){
						System.err.println("Error: Parameter bag disposalDistrRate is less than 0.0!");
						return false;
					}
					else if(this.expDisposalDistrRate.get(i) == 0.0){
						System.err.println("Warning: Parameter bag disposalDistrRate is equal to 0.0!");
					}
				}
				this.isExperiment = true;
				this.isDDRexp = true;
				this.setDisposalDistrRate(Float.valueOf(((Num)tmp).val));
				return true;
			}
			else if(tmp.isFloat()){
				// check the parameter whether is configure again
				if(this.disposalDistrRate != -1){
					System.err.println("Warning: Parameter disposalDistrRate is configure again!");
					//return false;
				}
				this.setDisposalDistrRate(Float.valueOf(((Num)tmp).val));
				// check the bag disposalDistrRate bigger than 0.0
				if(this.getDisposalDistrRate() < 0.0){
					System.err.println("Error: Parameter bag disposalDistrRate is less than 0.0!");
					return false;
				}
				else if(this.getDisposalDistrRate() == 0.0){
					System.err.println("Warning: Parameter bag disposalDistrRate is equal to 0.0!");
				}
				//
				if(lexer.hasNextToken() && (lexer.getNextToken().tag == Tag.FLOAT || lexer.getNextToken().tag == Tag.INT)){
					// lose keyword 'experiment'
					System.err.println("Error: When configure parameter disposalDistrRate, loss keyword 'experiment'!");
					while(lexer.hasNextToken() && (lexer.getNextToken().tag == Tag.FLOAT || lexer.getNextToken().tag == Tag.INT)){
						tmp = lexer.nextToken();
					}
					return false;
				}
				this.expDisposalDistrRate = new Vector<Float>();
				this.expDisposalDistrRate.add(this.getDisposalDistrRate());
				return true;
			}
			System.err.println("Error: Parameter disposalDistrRate is not float type!");
			return false;
		case Tag.DISPOSALDISTRSHAPE:
			tmp = lexer.nextToken();
			if(tmp.tag == Tag.EXPERIMENT){
				// check the parameter whether is configure again
				if(this.expDisposalDistrShape != null){
					System.err.println("Warning: Parameter disposalDistrShape is configure again!");
//					while(lexer.getNextToken().tag == Tag.INT){
//						tmp = lexer.nextToken();
//					}
//					return false;
				}
				this.expDisposalDistrShape = new Vector<Integer>();
				while(lexer.hasNextToken() && lexer.getNextToken().isInteger()){
					tmp = lexer.nextToken();
					this.expDisposalDistrShape.add(Integer.valueOf(((Num)tmp).val));
				}
				this.isExperiment = true;
				this.isDDSexp = true;
				this.setDisposalDistrShape(Integer.valueOf(((Num)tmp).val));
				return true;
			}
			else if(tmp.isInteger() && ((Num)tmp).isUINT8()){
				// check the parameter whether is configure again
				if(this.disposalDistrShape != -1){
					System.err.println("Warning: Parameter disposalDistrShape is configure again!");
					//return false;
				}
				this.setDisposalDistrShape(Integer.valueOf(((Num)tmp).val));
				
				if(lexer.hasNextToken() && lexer.getNextToken().tag == Tag.INT){
					// lose keyword 'experiment'
					System.err.println("Error: When configure parameter disposalDistrShape, loss keyword 'experiment'!");
					while(lexer.hasNextToken() && lexer.getNextToken().isInteger()){
						tmp = lexer.nextToken();
					}
					return false;
				}
				this.expDisposalDistrShape = new Vector<Integer>();
				this.expDisposalDistrShape.add(this.getDisposalDistrShape());
				return true;
			}
			System.err.println("Error: Parameter disposalDistrShape is not uint8_t type!");
			return false;
		case Tag.SERVICEFREQ:
			tmp = lexer.nextToken();
			if(tmp.tag == Tag.EXPERIMENT){
				// check the parameter whether is configure again
				if(this.expServiceFreq != null){
					System.err.println("Warning: Parameter serviceFreq is configure again!");
//					while(lexer.hasNextToken() && (lexer.getNextToken().tag == Tag.FLOAT || lexer.getNextToken().tag == Tag.INT)){
//						tmp = lexer.nextToken();
//					}
//					return false;
				}
				// configure the experiment serviceFreq
				this.expServiceFreq = new Vector<Float>();
				while(lexer.hasNextToken() && lexer.getNextToken().isFloat()){
					tmp = lexer.nextToken();
					this.expServiceFreq.add(Float.valueOf(((Num)tmp).val));
				}
				if(this.noExp == 0){
					this.noExp = this.expServiceFreq.size();
				}
				for(int i = 0; i < this.expServiceFreq.size(); i ++){
					// check the lorry serviceFreq bigger than 0.0
					if(this.expServiceFreq.get(i) < 0.0){
						System.err.println("Error: Parameter serviceFreq of lorry is less than 0.0!");
						return false;
					}
					else if(this.expServiceFreq.get(i) == 0.0){
						System.err.println("Warning: Parameter serviceFreq of lorry is equal to 0.0!");
					}
				}
				this.isExperiment = true;
				this.isSFexp = true;
				return true;
			}
			else{
				// lose keyword 'experiment'
				int counter = 1;
				while(lexer.hasNextToken() && (lexer.getNextToken().tag == Tag.FLOAT || lexer.getNextToken().tag == Tag.INT)){
					tmp = lexer.nextToken();
					counter ++;
				}
				if(counter != 1){
					System.err.println("Error: When configure parameter serviceFreq, loss keyword 'experiment'!");
				}
				return false;
			}
		case Tag.BAGVOLUME:
			tmp = lexer.nextToken();
			if(tmp.isFloat()){
				// check the parameter whether is configure again
				if(this.bagVolume != -1){
					System.err.println("Warning: Parameter bagVolume is configure again!");
//					return false;
				}
				this.setBagVolume(Float.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter bagVolume and value is not at same line!");
					return false;
				}
				// check the bag weight bigger than 0.0
				if(this.getBagVolume() < 0.0){
					System.err.println("Error: Parameter bagVolume is less than 0.0!");
					return false;
				}
				else if(this.getBagVolume() == 0.0){
					System.err.println("Error: Parameter bagVolume is equal to 0.0!");
				}
				return true;
			}
			System.err.println("Error: Parameter bagVolume is not float type!");
			return false;
		case Tag.BAGWEIGHTMIN:
			tmp = lexer.nextToken();
			if(tmp.isFloat()){
				// check the parameter whether is configure again
				if(this.bagWeightMin != -1){
					System.err.println("Warning: Parameter bagWeightMin is configure again!");
//					return false;
				}
				this.setBagWeightMin(Float.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter bagWeightMin and value is not at same line!");
					return false;
				}
				// check the minimal bag weight bigger than 0.0
				if(this.getBagWeightMin() < 0.0){
					System.err.println("Error: Parameter bagWegithMin is less than 0.0!");
					return false;
				}
				return true;
			}
			System.err.println("Error: Parameter bagWegithMin is not float type!");
			return false;
		case Tag.BAGWEIGHTMAX:
			tmp = lexer.nextToken();
			if(tmp.isFloat()){
				// check the parameter whether is configure again
				if(this.bagWeightMax != -1){
					System.err.println("Warning: Parameter bagWeightMax is configure again!");
//					return false;
				}
				this.setBagWeightMax(Float.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter bagWeightMax and value is not at same line!");
					return false;
				}
				// check the max bag weight bigger than 0.0
				if(this.getBagWeightMax() < 0.0){
					System.err.println("Error: Parameter bagWegithMax is less than 0.0!");
					return false;
				}else if(this.getBagWeightMax() == 0.0){
					System.err.println("Error: Parameter bagWeightMax is equal to 0.0!");
				}
				return true;
			}
			System.err.println("Error: Parameter bagWeightMax is not float type!");
			return false;
		case Tag.NOAREAS:
			tmp = lexer.nextToken();
			if(tmp.isInteger() && ((Num)tmp).isUINT8()){
				// check the parameter whether is configure again
				if(this.noAreas != -1){
					System.err.println("Warning: Parameter noAreas is configure again!");
//					return false;
				}
				this.setNoAreas(Integer.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter noAreas and value is not at same line!");
					return false;
				}
				return true;
			}
			System.err.println("Error: Parameter noAreas is not uint8_t type!");
			return false;
		case Tag.AREAIDX:
			AreaConfig areacf;
			if(this.getNoAreas() == -1){
				System.err.println("Warning: You should setting the parameter 'noAreas' before setting the area configure.");
				//return false;
			}
			areacf = AreaConfig.produceAreaConfig(paramName, lexer);
			if(areacf == null){
				return false;
			}
			this.areasConfig.add(areacf);
			return true;
		case Tag.STOPTIME:
			tmp = lexer.nextToken();
			if(tmp.isFloat()){
				// check the parameter whether is configure again
				if(this.stopTime != -1){
					System.err.println("Warning: Parameter stopTime is configure again!");
//					return false;
				}
				this.setStopTime(Float.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter stopTime and value is not at same line!");
					return false;
				}
				// check the stop time bigger than 0.0
				if(this.getStopTime() < 0.0){
					System.err.println("Error: Parameter stopTime is less than 0.0!");
					return false;
				}else if(this.getStopTime() == 0.0){
					System.err.println("Warning: Parameter stopTime is equal to 0.0!");
				}
				return true;
			}
			System.err.println("Error: Parameter stopTime is not float type!");
			return false;
		case Tag.WARMUPTIME:
			tmp = lexer.nextToken();
			if(tmp.isFloat()){
				// check the parameter whether is configure again
				if(this.warmUpTime != -1){
					System.err.println("Warning: Parameter warmUpTime is configure again!");
//					return false;
				}
				this.setWarmUpTime(Float.valueOf(((Num)tmp).val));
				// check the parameter and value in same line
				if(paramName.line != tmp.line){
					System.err.println("Error: Parameter warmUpTime and value is not at same line!");
					return false;
				}
				// check the warm up time bigger than 0.0
				if(this.getWarmUpTime() < 0.0){
					System.err.println("Error: Parameter warmUpTime is less than 0.0!");
					return false;
				}
				else if(this.getWarmUpTime() == 0.0){
					System.err.println("Error: Parameter warmUpTime is equal to 0.0!");
				}
				return true;
			}
			System.err.println("Error: Parameter warmUpTime is not float type!");
			return false;
		case Tag.THRESHOLDVAL:
		case Tag.NOBINS:
		case Tag.ROADSLAYOUT:
			while(lexer.hasNextToken() && (lexer.getNextToken().tag == Tag.FLOAT || lexer.getNextToken().tag == Tag.INT)){
				tmp = lexer.nextToken();
			}
			return false;
		default:
			System.err.println("Error: Can not deal word: " + paramName.toString());
			while(lexer.hasNextToken() && (lexer.getNextToken().tag == Tag.FLOAT || lexer.getNextToken().tag == Tag.INT)){
				tmp = lexer.nextToken();
			}
		}
		return false;
	}
	// print the parameter of configure
	public void print(){
		System.out.println("lorryVolume: " + this.getLorryVolume());
		System.out.println("lorryMaxLoad: " + this.getLorryMaxLoad());
		System.out.println("binServiceTime: " + this.getBinServiceTime());
		System.out.println("binVolume: " + this.getBinVolume());
		System.out.println("disposalDistrRate: " + this.getDisposalDistrRate());
		System.out.println("disposalDistrShape: " + this.getDisposalDistrShape());
		System.out.println("bagVolume: " + this.getBagVolume());
		System.out.println("bagWeightMin: " + this.getBagWeightMin());
		System.out.println("bagweightMax: " + this.getBagWeightMax());
		System.out.println("noAreas: " + this.getNoAreas());
		for(int i = 0; i < areasConfig.size(); i ++){
			areasConfig.get(i).print();
		}
		System.out.println("stopTime: " + this.getStopTime());
		System.out.println("warmUpTime: " + this.getWarmUpTime());
	}
	// generate configure from lexer
	public static Config genFromLexer(Lexer lexer){
		Config cf = new Config();
		Token tmp;
		boolean isvaild = true, flag;
		while(lexer.hasNextToken()){
			tmp = lexer.nextToken();
			flag = cf.setParam(tmp, lexer);
			isvaild = isvaild && flag;
		}
		flag = cf.isVaild();
		if(isvaild && flag && cf.checkNoArea()){
			if(cf.isExperiment()){
				cf.experiments = new Vector<Config>();
				if(cf.expServiceFreq == null){
					// experiment param serviceFreq is null
					cf.noExp = cf.expDisposalDistrRate.size() * cf.expDisposalDistrShape.size();
					for(Float f : cf.expDisposalDistrRate){
						for(Integer i : cf.expDisposalDistrShape){
							Config exp = new Config(cf);
							exp.setDisposalDistrRate(f);
							exp.setDisposalDistrShape(i);
							cf.experiments.add(exp);
						}
					}
				}
				else{
					cf.noExp = cf.expDisposalDistrRate.size() * cf.expDisposalDistrShape.size() * cf.expServiceFreq.size();
					for(Float f : cf.expDisposalDistrRate){
						for(Integer i : cf.expDisposalDistrShape){
							for(Float sf : cf.expServiceFreq){
								Config exp = new Config(cf);
								exp.setDisposalDistrRate(f);
								exp.setDisposalDistrShape(i);
								exp.setServiceFreq(sf);
								cf.experiments.add(exp);
							}
						}
					}
				}
			}
			return cf;
		}
		return null;
	}
	// test main function of generate configure
	public static void main(String []args){
		Lexer lexer = new Lexer();
		if(args.length == 1){
			lexer.setFilename(args[0]);
		}else{
			lexer.setFilename("conf4.txt");
		}
		Config cf = Config.genFromLexer(lexer);
		if(cf != null){
			cf.print();
		}
		else{
			System.out.println("can not generate config!");
		}
	}
	public boolean isExperiment() {
		return isExperiment;
	}
	public void setExperiment(boolean isExperiment) {
		this.isExperiment = isExperiment;
	}
	// get experiment configure of index
	public Config getExperiment(int index){
		if(this.isExperiment == false){
			System.out.println("Warming: There is no experiments!");
			return null;
		}
		if(index < 0 || index >= this.noExp){
			System.out.println("Warming: Index of experiment is out of range!");
			return null;
		}
		return this.experiments.get(index);
	}
	// print experiment configure parameter of index
	void printConfigureParameter(int index){
		if(this.isExperiment == false){
			System.out.println("Warming: There is no experiments!");
			return;
		}
		if(index < 0 || index >= this.noExp){
			System.out.println("Warming: Index of experiment is out of range!");
			return;
		}
		System.out.print("\nExperiment #"+(index+1));
		if(this.isDDRexp){
			System.out.print(" disposalDistrRate "+this.disposalDistrRate);
		}
		if(this.isDDSexp){
			System.out.print(" disposalDistrShape "+this.disposalDistrShape);
		}
		if(this.isSFexp){
			System.out.print(" serviceFreq "+this.areasConfig.get(0).getServiceFreq());
		}
		System.out.println("\n");
	}
}
