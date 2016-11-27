package Simulate;

import java.util.Vector;

import Lexer.*;

public class AreaConfig {
	private int areaIdx;    // area index
	private float serviceFreq;   // trips per hour
	private float thresholdVal;   // bins serviced if occupacy is more or equal threshold value
	private int noBins;    // nunber of bins
	private Vector<Integer> roadsLayout;   // roads layout
	
	public AreaConfig(){
		this.roadsLayout = new Vector<Integer>();
	}
	public AreaConfig(AreaConfig ac){
		this.areaIdx = ac.getAreaIdx();
		this.serviceFreq = ac.getServiceFreq();
		this.thresholdVal = ac.getThresholdVal();
		this.noBins = ac.getNoBins();
		this.roadsLayout = new Vector<Integer>(ac.getRoadsLayout());
	}
	public int getAreaIdx() {
		return areaIdx;
	}
	public void setAreaIdx(int areaIdx) {
		this.areaIdx = areaIdx;
	}
	public float getServiceFreq() {
		return serviceFreq;
	}
	public void setServiceFreq(float serviceFreq) {
		this.serviceFreq = serviceFreq;
	}
	public float getThresholdVal() {
		return thresholdVal;
	}
	public void setThresholdVal(float thresholdVal) {
		this.thresholdVal = thresholdVal;
	}
	public int getNoBins() {
		return noBins;
	}
	public void setNoBins(int noBins) {
		this.noBins = noBins;
	}
	public Vector<Integer> getRoadsLayout() {
		return roadsLayout;
	}
	public void setRoadsLayout(Vector<Integer> roadsLayout) {
		this.roadsLayout = roadsLayout;
	}
	public void addRoadsLayout(int v){
		this.roadsLayout.add(v);
	}
	public boolean isLayoutAvaid(){
		int noLayoutLine = (int) Math.sqrt(this.roadsLayout.size());
		return (noLayoutLine == (noBins+1));
	}
	public void print(){
		System.out.println("areaIdx: " + this.getAreaIdx() + "\tserviceFreq: " + this.getServiceFreq() + "\tthresholdVal: " + this.getThresholdVal() + "\tnoBins: " + this.getNoBins());
		System.out.println("roadsLayout:");
		for(int i = 0; i <= this.getNoBins(); i ++){
			for(int j = 0; j <= this.getNoBins(); j ++){
				System.out.print(this.roadsLayout.get(i*(this.getNoBins()+1)+j) + "\t");
			}
			System.out.print("\n");
		}
	}
	// produce area configure from lexer
	public static AreaConfig produceAreaConfig(Token t, Lexer lexer){
		AreaConfig acf = new AreaConfig();
		Token tmp;
		int i,j;
		boolean flag = true;
		// set the area index
		if(t.tag != Tag.AREAIDX){
			// Error
			return AreaConfig.dealRestParam(lexer);
		}
		int line = t.line;
		boolean isSameLine = true;
		tmp = lexer.getNextToken();
		if(tmp.isInteger() && (! ((Num)tmp).isUINT8())){
			// Error
			System.err.println("Error: Parameter areaIdx is not uint8_t type!");
			return AreaConfig.dealRestParam(lexer);
		}
		// check the same line
		isSameLine = isSameLine && (tmp.line == line);
		acf.setAreaIdx(Integer.valueOf(((Num)tmp).val));
		lexer.nextToken();
		// set the service frequent
		tmp = lexer.getNextToken();
		if(tmp.tag != Tag.SERVICEFREQ){
			// Error
			System.err.println("Error: Wrong order configure in area "+acf.getAreaIdx() + " in line " + line + ".");
			return AreaConfig.dealRestParam(lexer);
		}
		// check the same line
		isSameLine = isSameLine && (tmp.line == line);
		lexer.nextToken();
		tmp = lexer.getNextToken();
		if(tmp.tag != Tag.FLOAT && tmp.tag != Tag.INT && (! ((Num)tmp).isFLOAT())){
			// Error, is not float
			System.err.println("Error: Parameter serviceFreq is not float type in area "+acf.getAreaIdx() + ".");
			return AreaConfig.dealRestParam(lexer);
		}
		// check the same line
		isSameLine = isSameLine && (tmp.line == line);
		acf.setServiceFreq(Float.valueOf(((Num)tmp).val));
		lexer.nextToken();
		// set the threshold value
		tmp = lexer.getNextToken();
		if(tmp.tag != Tag.THRESHOLDVAL){
			// Error
			System.err.println("Error: Wrong order configure in area "+acf.getAreaIdx() + " in line " + line + ".");
			return AreaConfig.dealRestParam(lexer);
		}
		// check the same line
		isSameLine = isSameLine && (tmp.line == line);
		lexer.nextToken();
		tmp = lexer.getNextToken();
		if(tmp.tag != Tag.FLOAT && tmp.tag != Tag.INT && (! ((Num)tmp).isFLOAT())){
			System.err.println("Error: Parameter thresholdVal is not float type in area "+acf.getAreaIdx() + ".");
			return AreaConfig.dealRestParam(lexer);
		}
		// check the same line
		isSameLine = isSameLine && (tmp.line == line);
		acf.setThresholdVal(Float.valueOf(((Num)tmp).val));
		if(acf.getThresholdVal() >= 1.0){
			System.err.println("Warning: Parameter thresholdVal bigger than 1.0 in area " + acf.getAreaIdx() + ".");
		}
		lexer.nextToken();
		// set the number of bins
		tmp = lexer.getNextToken();
		if(tmp.tag != Tag.NOBINS){
			// Error
			System.err.println("Error: Wrong order configure in area "+acf.getAreaIdx() + " in line " + line + ".");
			return AreaConfig.dealRestParam(lexer);
		}
		// check the same line
		isSameLine = isSameLine && (tmp.line == line);
		lexer.nextToken();
		tmp = lexer.getNextToken();
		if(tmp.tag != Tag.INT && (! ((Num)tmp).isUINT16())){
			System.err.println("Error: Parameter noBins is not uint16_t type in area "+acf.getAreaIdx() + ".");
			return AreaConfig.dealRestParam(lexer);
		}
		// check the same line
		isSameLine = isSameLine && (tmp.line == line);
		acf.setNoBins(Integer.valueOf(((Num)tmp).val));
		lexer.nextToken();
		if(! isSameLine){
			System.err.println("Error: Parameters of area "+acf.getAreaIdx() + " not in same line.");
			return AreaConfig.dealRestParam(lexer);
		}
		// set the road layout
		tmp = lexer.getNextToken();
		if(tmp.tag != Tag.ROADSLAYOUT){
			// Error
			System.err.println("Error: Wrong order configure in area "+acf.getAreaIdx() + " in line " + line + ".");
			return AreaConfig.dealRestParam(lexer);
		}
		for(i = 0; i <= acf.getNoBins(); i ++){
			for(j = 0; j <= acf.getNoBins(); j ++){
				lexer.nextToken();
				tmp = lexer.getNextToken();
				if(tmp.tag != Tag.INT || (! ((Num)tmp).isINT8())){
					// Error
					System.err.println("Error: Value of parameter roadsLayout in ("+(i+1)+","+(j+1)+") is not int8_t type in area "+acf.getAreaIdx() + ".");
					return AreaConfig.dealRestParam(lexer);
				}
				if(i == j && Integer.valueOf(((Num)tmp).val) != 0){
					System.err.println("Error: Value of parameter roadsLayout in ("+(i+1)+","+(j+1)+") is not zero in area "+acf.getAreaIdx() + ".");
					flag = false;
				}
				if(i != j && Integer.valueOf(((Num)tmp).val) < 0 && Integer.valueOf(((Num)tmp).val) != -1){
					System.err.println("Error: Value of parameter roadsLayout in ("+(i+1)+","+(j+1)+") is negative in area "+acf.getAreaIdx() + ".");
					flag = false;
				}
				acf.addRoadsLayout(Integer.valueOf(((Num)tmp).val));
			}
		}
		lexer.nextToken();
		if(flag){    // flag of value of parameter in roadslayout isnegative
			return acf;
		}
		else{
			return null;
		}
	}
	// when generate configure of AreaConfig is wrong, deal with the rest word input
	public static AreaConfig dealRestParam(Lexer lexer){
		while(lexer.hasNextToken()){
			Token tmp = lexer.getNextToken();
			if(tmp.tag == Tag.FLOAT || tmp.tag == Tag.INT || tmp.tag == Tag.NOBINS || tmp.tag == Tag.ROADSLAYOUT || tmp.tag == Tag.THRESHOLDVAL){
				tmp = lexer.nextToken();
			}
			else{
				break;
			}
		}
		return null;
	}
}
