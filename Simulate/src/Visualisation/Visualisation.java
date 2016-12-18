package Visualisation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Visualisation {
	public static void main(String [] args){
		File file;
		Scanner scanner;
		FileWriter fw1 = null, fw2 = null, fw3 = null;
		int nodr, nods, nosq;
		float fdr, fds, fsq, fval;
		float dr, ds, sq, val;
//		if(args.length != 1){
//			return;
//		}
//		file = new File(args[0]);
		file = new File("./Statistic/s1");
		if(! file.exists()){
			System.err.println("Error: the file "+ args[0] + " no exist!");
			return;
		}
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			scanner = null;
			e1.printStackTrace();
		}
		String path = file.getParent();
		//
		file = new File(path+"/v1");
		if(! file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fw1 = new FileWriter(file);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//
		file = new File(path+"/v2");
		if(! file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fw2 = new FileWriter(file);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//
		file = new File(path+"/v3");
		if(! file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fw3 = new FileWriter(file);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		nodr = scanner.nextInt();
		nods = scanner.nextInt();
		nosq = scanner.nextInt();
		// get the first experiment result
		fdr = scanner.nextFloat();
		fds = scanner.nextFloat();
		fsq = scanner.nextFloat();
		fval = scanner.nextFloat();
		try {
			fw1.write("" + fdr + "\t" + fval + "\n");
			fw2.write("" + fds + "\t" + fval + "\n");
			fw3.write("" + fsq + "\t" + fval + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(scanner.hasNext()){
			dr = scanner.nextFloat();
			ds = scanner.nextFloat();
			sq = scanner.nextFloat();
			val = scanner.nextFloat();
			try {
				if(nodr != 0 && fds == ds && fsq == sq){
					fw1.write("" + dr + "\t" + val + "\n");
				}else if(nods != 0 && fdr == dr && fsq == sq){
					fw2.write("" + ds + "\t" + val + "\n");
				}else if(nosq != 0 && fdr == dr && fds == ds){
					fw3.write("" + sq + "\t" + val + "\n");
				}else{
					continue;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fw1.close();
			fw2.close();
			fw3.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
