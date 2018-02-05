package ru.avakyants.java.instagrabber;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
			
			Instagrabber instgrb = new Instagrabber();					
			List<String> results = instgrb.findAndReturnList(instgrb.getSourceByURL(args[1]),"<meta property=\"og:image\" content=\"(.+)\" />");
			instgrb.saveToFiles(results, args[0]);			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String getSourceByURL(String urlString) throws IOException {
		String source = null;
		
		URL url = new URL(urlString);
		try(Scanner scanner = new Scanner(url.openStream())){
			source = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
		}
		
		return source;
	}
	
	public List<String> findAndReturnList(String txt, String patternString){
		
		List<String> res = new ArrayList<>();
		
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(txt);
		
		while(matcher.find()) {
			res.add(matcher.group(1));
		}		
		
		return res;
		
	}
	
	public void saveToFiles(List<String> imageList, String pathToSave) throws MalformedURLException, IOException {
		int cnt = 1;
		String path = null;
		if(imageList.size()>0) {
			for(String imageURL: imageList) {
				try(InputStream in = new URL(imageURL).openStream()){
					path = pathToSave+"/"+cnt+".jpg";
				    try {
				    		Files.copy(in, Paths.get(path));
				    }catch(FileAlreadyExistsException eae) {
				    		Files.delete(Paths.get(path));
				    		Files.copy(in, Paths.get(path));
				    }
				}
				cnt++;
				System.out.println("Successfuly download image: "+imageURL+" to: "+path);
			}
		}
	}

}
