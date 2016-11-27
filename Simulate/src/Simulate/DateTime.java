package Simulate;

public class DateTime {
	public static String toString(int dt){
		int d,h,m,s;
		String result = "";
		s = dt%60;
		dt = dt/60;
		m = dt%60;
		dt = dt/60;
		h = dt%24;
		d = dt/24;
		if(d<10){
			result += "0";
		}
		result += (d+":");
		if(h<10){
			result += "0";
		}
		result += (h+":");
		if(m<10){
			result += "0";
		}
		result += (m+":");
		if(s<10){
			result += "0";
		}
		result += s;
		return result;
	}
	public static String toMinSecString(int dt){
		int m,s;
		String result;
		m = dt/60;
		s = dt%60;
		result = m + ":";
		if(s<10){
			result += "0";
		}
		result += s;
		return result;
	}
}
