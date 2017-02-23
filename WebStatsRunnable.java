
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import java.util.TreeMap;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class WebStatsRunnable {
	
	public static String currentUrl = "http://www.google.ca";
	public static String currentTag = "";
	public static String webLine;
	public static String webLine2;
	public static int c = 0;
	public static int nUrls = 0;
	public static int nPath,argPath  = 0;
	public static URL url;
	public static Map<String, Integer> Tags = new ConcurrentHashMap<String, Integer>();
	public static Map<String, Integer> GlobalTags = new ConcurrentHashMap<String, Integer>();
	public static Queue<String> pages = new LinkedList();

	 public static  void main(String[] args) throws IOException {

		 
		 // collect all urls until reach page total 
		 // if no more urls and need more to match total pages
		 // then look for url while not exceeding path length on another page
		 //loop
	
		 int argPage = args.length!= 0? Integer.parseInt(args[1]): 10;
		 argPath = args.length!= 0? Integer.parseInt(args[3]): 3;
		 String argUrl = args.length!= 0? args[4]:"http://www.w3schools.com/html/html_urlencode.asp" ;
		 c=argPage;
		 currentUrl = argUrl;
		 System.out.println (argPage);
		 pages.add(currentUrl);
	
	
		 	 url = new URL(currentUrl);
   	   
	    	 
	    	 URLConnection con = url.openConnection();
	  
	         InputStream is =con.getInputStream();
 
	         BufferedReader br = new BufferedReader(new InputStreamReader(is));

	         String line = null;
	         while (((line = br.readLine()) != null) ) {
		    	webLine = line;
	        	//getPages(line);
		    	threads();
		       }
	     
	        	 
	      
	         for (String e: pages){
	        	
	        		  url = new URL(e);
	        		  con =url.openConnection();
	        		  is =con.getInputStream();
	        		  br = new BufferedReader(new InputStreamReader(is));

	        		  line = null;
	        		  while ((line = br.readLine()) != null) {
	        			  checkForTags(line);
		      
		       }
	        		  System.out.println("\n Current URL (stats below): "+ e + "\n"); 
	        		  TreeMap<String, Integer> sortedTags= new TreeMap<String,Integer>(Tags);
	        		  Set set = sortedTags.entrySet();
	        		  Iterator i = set.iterator();
	        		  // Display elements
	        		  while(i.hasNext()) {
	        			  Map.Entry me = (Map.Entry)i.next();
	        			  System.out.print(me.getKey() + ": ");
	        			  System.out.println(me.getValue());
	        		  }
	        		  nPath++;
	        		  Tags.clear();
	       
	         }
	        
	        System.out.println("\n----------GLOBAL TAGS BELOW-----------\n");
	        TreeMap<String, Integer> sortedGlobalTags= new TreeMap<String,Integer>(GlobalTags);
	        Set s = sortedGlobalTags.entrySet();
	       
		       Iterator ii = s.iterator();
		       // Display elements
		       while(ii.hasNext()) {
		          Map.Entry mm = (Map.Entry)ii.next();
		          System.out.print(mm.getKey() + ": ");
		          System.out.println(mm.getValue());
		       }

	 }// end of main method
	 public static synchronized void threads() //throws InterruptedException
		{
			try{
			final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
			
		getPages(webLine);
				}
			});
			thread.run();
			
			}
			
			catch(Exception e)
			{
				System.out.println(e);	
			}
		}
	 public static synchronized void threads2() //throws InterruptedException
		{
			try{
			final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
			
				pages.add(currentUrl);
				}
			});
			thread.run();
			
			}
			
			catch(Exception e)
			{
				System.out.println(e);	
			}
		}
	 public static synchronized void checkForTags(String tag)
	    {
		
		 tag.toLowerCase(); 
		 char[] str = tag.toCharArray();
		 String aTag;
		 aTag = "";
		 for(int i=0;i< str.length; i++){
			 if (str[i] ==  '<' && (str[i+1] != '/') && (str[i+1] != '>')&& ("abcdefghijklmnopqrstuvwxyz".indexOf(str[i+1]) != -1)){
				 for(int j=i;j< str.length; j++){
					 
					 // Obtain the tag and send it to tagCounter()
					 if (str[j] !=  '>' && (str[j] !=  ' '))
						 aTag = aTag + str[j]; 
					 else {
						 aTag += ">";
						 currentTag = aTag;
					
						 tagCounter(aTag);
						 aTag = "";
						 break;
			   }
			 } 
				 
		   }
	 
		 }
		 
		 
	   }//end of checkfortags

	 public static synchronized void tagCounter(String tag){
		 //Tags for each URL
		 int count = Tags.containsKey(currentTag) ? Tags.get(currentTag) : 0;
		 Tags.put(currentTag, count + 1);	 
		 //Global tags
		 int coun = GlobalTags.containsKey(currentTag) ? GlobalTags.get(currentTag) : 0;
		 GlobalTags.put(currentTag, coun + 1);
		 
		 
	 }
	 public static synchronized void getPages(String lines)
	    {
		 String pattern = "href=\"(http.+?)\"";

	  if (lines.contains("<a"))
	  {
		 
		  Pattern r = Pattern.compile(pattern);
 		 Matcher m = r.matcher(lines);
 		 if (m.find()) {
 		     currentUrl = m.group(1);
 		 if (!currentUrl.contains("=")&& !currentUrl.contains("validator") ){
 		    System.out.println("####    " + currentUrl);
 			 
 			 addPage();
 			 }
		  
 		 } }
 }
	 public static synchronized void addPage(){
		 if (nUrls < c && !(pages.contains(currentUrl)))
			 threads2();
		 nUrls++;
	
	 
	 }

}// end of class
