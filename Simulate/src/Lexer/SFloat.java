package Lexer;

public class SFloat extends Num {

	public SFloat(float v) {
		super(Float.valueOf(v).toString(), Tag.FLOAT);
		// TODO Auto-generated constructor stub
	}
	public String toString() {
		return "(FLOAT : " + val + ") in line:"+line+" col:"+col; 
	}
	public boolean isFLOAT(){
		return true;
	}
}
