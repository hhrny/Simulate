package Lexer;

public class SInt extends Num {
	
	public SInt(int t) {
		super(Integer.valueOf(t).toString(), Tag.INT);
		// TODO Auto-generated constructor stub
	}
	
	public String toString() {
		return "(INT : " + val + ") in line:"+line+" col:"+col; 
	}
	public boolean isUINT8(){
		int tmp = Integer.valueOf(val, 10);
		if(tmp >= 0 && tmp <= 255){
			return true;
		}
		return false;
	}
	public boolean isINT8(){
		int tmp = Integer.valueOf(val, 10);
		if(tmp >= -127 && tmp <= 127){
			return true;
		}
		return false;
	}
	public boolean isUINT16(){
		int tmp = Integer.valueOf(val, 10);
		if(tmp >= 0 && tmp <= 65535){
			return true;
		}
		return false;
	}
}
