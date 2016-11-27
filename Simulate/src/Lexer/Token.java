package Lexer;

public class Token {
	public final int tag;
	public int line,col;
	public Token(int t) {tag = t; }
	public String toString() { return "(value : " + tag + ") in line:"+line+" col:"+col;}
	public boolean isInteger(){
		if(tag == Tag.INT){
			return true;
		}
		return false;
	}
	public boolean isFloat(){
		if(tag == Tag.INT || tag == Tag.FLOAT){
			return true;
		}
		return false;
	}
	public boolean isWord(){
		if(tag != Tag.UNKNOWN){
			return true;
		}
		return false;
	}
}
