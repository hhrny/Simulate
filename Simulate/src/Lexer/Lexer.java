package Lexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Vector;

public class Lexer {
	public static int line = 1;
	public static int col = 1;
	private char peek = ' ';
	private String filename;
	private Reader reader;
	private boolean isFinish = true;
	private Hashtable<String, Word> words = new Hashtable<String, Word>();
	
	private Vector<Token> tokens = new Vector<Token>();
	private int currentToken = 0;
	
	private void reserve(Word w) { words.put(w.lexeme, w); }
	
	public Lexer(){
		reserve(Word.lorryValume);
		reserve(Word.lorryMaxLoad);
		reserve(Word.binServiceTime);
		reserve(Word.binVolume);
		reserve(Word.disposalDistrRate);
		reserve(Word.disposalDistrShape);
		reserve(Word.bagVolume);
		reserve(Word.bagVolumeMin);
		reserve(Word.bagVolumeMax);
		reserve(Word.noAreas);
		reserve(Word.areaIdx);
		reserve(Word.serviceFreq);
		reserve(Word.thresholdVal);
		reserve(Word.noBins);
		reserve(Word.roadsLayout);
		reserve(Word.stopTime);
		reserve(Word.warmUpTime);
		reserve(Word.experiment);
	}
	
	void readch() {
		int tmp = -1;
		try {
			tmp = reader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(tmp == -1){
			isFinish = true;
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			tmp = ' ';
		}
		peek = (char)tmp;
		col ++;
	}
	public boolean isFinish() {
		return isFinish;
	}
	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
	public boolean isBlock(char c){
		if(c == '\n'){
			return true;
		}
		if(c == ' '){
			return true;
		}
		if(c == '\t'){
			return true;
		}
		if(c == '\r'){
			return true;
		}
		return false;
	}
	public Token scan(){
		Token token;
		while(! isFinish){
			// deal with the block
			if(peek == '\n'){
				col = 1;
				line ++;
				readch();
			}else if(peek == ' ' || peek == '\t' || peek == '\r'){
				readch();
			}else if(peek == '#'){
				do{
					readch();
				}while(peek != '\n' && (! isFinish));
				if(peek == '\n'){
					col = 1;
					line ++;
					readch();
				}
			}else{
				break;
			}
		}
		if(isFinish){
			//System.out.println(filename + " read finished!");
			return null;
		}
		StringBuffer b = new StringBuffer();
		String s;
		if(peek == '-'){
			b.append('-');
			readch();
		}		
		if(Character.isDigit(peek)){
			do{
				b.append(peek);
				readch();
			}while((! isFinish) && Character.isDigit(peek));
			
			if(peek != '.' && (! isBlock(peek))){
				while((! isFinish) && (! isBlock(peek))){
					b.append(peek);
					readch();
				}
				s = b.toString();
				System.err.println("Lexr Error : Incorrect word " + s + " in line " + line + " and " + (col-s.length()-1) + "th character");
				token = new Word(s, Tag.UNKNOWN);
				token.line = line;
				token.col = col-s.length()-1;
				return token;
			}
			else if(isBlock(peek)){
				s = b.toString();
				token = new SInt(Integer.valueOf(s).intValue());
				token.line = line;
				token.col = col-s.length()-1;
				return token;
			}
			else{
				// add '.' to b
				b.append(peek);
				readch();
			}
			while((! isFinish) && Character.isDigit(peek)){
				// add digit to b
				b.append(peek);
				readch();
			}
			if(! isBlock(peek)){
				// some character behind the number
				do{
					b.append(peek);
					readch();
				}while((! isFinish) && (! isBlock(peek)));
				s = b.toString();
				System.err.println("Lexr Error : Incorrect word " + s + " in line " + line + " and " + (col-s.length()-1) + "th character");
				token = new Word(s, Tag.UNKNOWN);
				token.line = line;
				token.col = col-s.length()-1;
				return token;
			}
			// the string is a number
			s = b.toString();
			token = new SFloat(Float.valueOf(b.toString()).floatValue());
			token.line = line;
			token.col = col-s.length()-1;
			return token;
		}

		if(Character.isLetter(peek)){
			do{
				b.append(peek);
				readch();
			}while((! isFinish) && (! isBlock(peek)));
			s = b.toString();
			Word w = (Word)words.get(s);
			if(w != null){
				token = new Word(w.lexeme, w.tag);
				token.line = line;
				token.col = col-s.length()-1;
				return token;
			}
			// Error : can not find the word
			System.err.println("Lexr Error : Incorrect word " + s + " in line " + line + " and " + (col-s.length()-1) + "th character");
			token = new Word(s, Tag.UNKNOWN);
			token.line = line;
			token.col = col-s.length()-1;
			return token;
		}
		token = new Token(peek);
		token.line = line;
		token.col = col-1;
		peek = ' ';
		return token;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
		File file = new File(filename);
		try {
			reader = new InputStreamReader(new FileInputStream(file));
			isFinish = false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("can not open the file " + filename);
			e.printStackTrace();
		}
		// analyse the tokens
		while(! isFinish){
			Token t = this.scan();			
			if(t != null){
				tokens.add(t);
			}
		}
		this.currentToken = 0;
	}
	// whether the lexer has next token
	public boolean hasNextToken(){
		return (currentToken < tokens.size());
	}
	// get the current token of lexer
	public Token getCurrentToken(){
		return tokens.get(currentToken);
	}
	// get the next token
	public Token getNextToken(){
		return tokens.get(currentToken);
	}
	// get the next token, and lexer token pointer point to next token
	public Token nextToken(){
		return tokens.get(currentToken++);
	}
	
	public static void main(String []args){
		Lexer lexer = new Lexer();
		if(args.length == 1){
			lexer.setFilename(args[0]);
		}else{
			lexer.setFilename("conf8.txt");
		}
		while(lexer.hasNextToken()){
			Token token = lexer.nextToken();
			if(token != null){
				System.out.println(token.toString());
			}
		}
	}
}
