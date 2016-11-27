package Lexer;

public class Word extends Token {
	public String lexeme = "";
	public Word(String a, int tag) { super(tag); lexeme = a; }
	public String toString() { return "(WORD : " + lexeme + ") in line:"+line+" col:"+col; }
	public final static Word
		lorryValume = new Word("lorryVolume", Tag.LORRYVOLUME), 
		lorryMaxLoad = new Word("lorryMaxLoad", Tag.LORRYMAXLOAD),
		binServiceTime = new Word("binServiceTime", Tag.BINSERVICETIME), 
		binVolume = new Word("binVolume", Tag.BINVOLUME),
		disposalDistrRate = new Word("disposalDistrRate", Tag.DISPOSALDISTRRATE), 
		disposalDistrShape = new Word("disposalDistrShape", Tag.DISPOSALDISTRSHAPE),
		bagVolume = new Word("bagVolume", Tag.BAGVOLUME), 
		bagVolumeMin = new Word("bagWeightMin", Tag.BAGWEIGHTMIN), 
		bagVolumeMax = new Word("bagWeightMax", Tag.BAGWEIGHTMAX),
		noAreas = new Word("noAreas", Tag.NOAREAS), 
		areaIdx = new Word("areaIdx", Tag.AREAIDX), 
		serviceFreq = new Word("serviceFreq", Tag.SERVICEFREQ),
		thresholdVal = new Word("thresholdVal", Tag.THRESHOLDVAL), 
		noBins = new Word("noBins", Tag.NOBINS), 
		roadsLayout = new Word("roadsLayout", Tag.ROADSLAYOUT),
		stopTime = new Word("stopTime", Tag.STOPTIME), 
		warmUpTime = new Word("warmUpTime", Tag.WARMUPTIME),
		experiment = new Word("experiment", Tag.EXPERIMENT);
}
