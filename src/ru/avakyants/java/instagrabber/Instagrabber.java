package ru.avakyants.java.instagrabber;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Instagrabber {

	public static void main(String[] args) {
		
		
		try {
			if(args.length<2) throw new IllegalArgumentException("Need 2 argument: path_to_save, url_download");
					
			String pageSource = null;
			List<String> results = null;
			URL url = new URL(args[1]);
			
			try(Scanner scanner = new Scanner(url.openStream())){
				pageSource = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			}
			
			results = findAndReturnList(pageSource,"<meta property=\"og:image\" content=\"(.+)\" />");
			
			if(results.size()>0) {
				for(String imageURL: results) {
					try(InputStream in = new URL(imageURL).openStream()){
					    try {
					    		Files.copy(in, Paths.get(args[0]));
					    }catch(FileAlreadyExistsException eae) {
					    		Files.delete(Paths.get(args[0]));
					    		Files.copy(in, Paths.get(args[0]));
					    }
					}
				}
			}
			
			System.out.print("Successfuly download image: "+args[1]+" to: "+args[0]);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static List<String> findAndReturnList(String txt, String patternString){
		
		List<String> res = new ArrayList<>();
		
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(txt);
		
		while(matcher.find()) {
			res.add(matcher.group(1));
		}		
		
		return res;
		
	}

}
