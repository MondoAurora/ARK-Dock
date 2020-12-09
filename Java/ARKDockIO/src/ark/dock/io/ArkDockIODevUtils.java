package ark.dock.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import dust.gen.DustGenUtils;

public class ArkDockIODevUtils extends DustGenUtils implements ArkDockIOConsts {
	static int COUNT = 0;
	static int COUNT_REAL = 0;

	static void collectInfo(File root, int len) throws Exception {
		if ( 0 == len ) {
			len = root.getAbsolutePath().length();
		}
		File[] fs = root.listFiles();
		for (File f : fs) {
			if ( f.isFile()) {
				try (BufferedReader br = new BufferedReader(new FileReader(f))) {
					int count = 0;
			    for(String line; (line = br.readLine()) != null; ) {
			    	++count;
			    	++COUNT;
			    	if ( !line.trim().isEmpty() ) {
			    		++COUNT_REAL;
			    	}
			    }
			    
			    System.out.println(f.getAbsolutePath().substring(len) + " (" + count + ")");
//			    DustGenLog.log(f.getAbsolutePath().substring(len), countReal, "/", count);
				}
			}
		}		
		
		for (File f : fs) {
			if ( f.isDirectory() ){
				collectInfo(f, len);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		File fSrc = new File(".");
		collectInfo(fSrc, 0);
		
    System.out.println(fSrc.getAbsolutePath() + " lines total:" + COUNT + ", real: " + COUNT_REAL);

	}
}
