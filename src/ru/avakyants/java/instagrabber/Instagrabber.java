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

import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.cookie.CookieHashSet;
import me.postaddict.instagram.scraper.cookie.DefaultCookieJar;
import me.postaddict.instagram.scraper.interceptor.ErrorInterceptor;
import me.postaddict.instagram.scraper.model.Media;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class Instagrabber {

	public static void main(String[] args) {
		
		
		try {
			if(args.length<2) throw new IllegalArgumentException("Need 2 argument: path_to_save, url_download");
			
			Instagrabber instgrb = new Instagrabber();					
			List<String> results = instgrb.findAndReturnList(instgrb.getSourceByURL(args[1]),"<meta property=\"og:image\" content=\"(.+)\" />");
			instgrb.saveToFiles(results, args[0]);
			instgrb.printAccountInfoByURL(args[1]);
			
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
	
	public void printAccountInfoByURL(String URL) throws IOException {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		OkHttpClient httpClient = new OkHttpClient.Builder()
		        .addNetworkInterceptor(loggingInterceptor)
		        .addInterceptor(new ErrorInterceptor())
		        .cookieJar((CookieJar) new DefaultCookieJar(new CookieHashSet()))
		        .build();
		
		Instagram instagram = new Instagram(httpClient);
		
		/*Get media by url*/		
		Media media = instagram.getMediaByUrl(URL);
		System.out.println("Username: "+media.getOwner().getUsername());
	}

}
