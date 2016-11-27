package Lexer;

public class Num extends Token{
	public String val;
	public Num(String v, int t) {
		super(t);
		val = v;
	}
	public String toString() { 
		return "(NUM : " + val + ") in line:"+line+" col:"+col; 
	}
	public boolean isUINT8(){
		return false;
	}
	public boolean isINT8(){
		return false;
	}
	public boolean isUINT16(){
		return false;
	}
	public boolean isFLOAT(){
		return true;
	}
}
