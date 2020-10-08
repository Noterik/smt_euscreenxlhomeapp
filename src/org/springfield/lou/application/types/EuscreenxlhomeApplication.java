package org.springfield.lou.application.types;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.components.ComponentManager;
import org.springfield.lou.application.types.conditions.AndCondition;
import org.springfield.lou.application.types.conditions.EqualsCondition;
import org.springfield.lou.application.types.conditions.FilterCondition;
import org.springfield.lou.euscreen.config.Config;
import org.springfield.lou.euscreen.config.ConfigEnvironment;
import org.springfield.lou.euscreen.config.SettingNotExistException;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.screen.Screen;

public class EuscreenxlhomeApplication extends Html5Application implements Observer{

	private FSList allNodes;
	private static Boolean wantedna = true;
	private HashMap<String, String> categoryURLMappings;
	private HashMap<String, String> subtitleMappings;
	public String ipAddress="";
	public static boolean isAndroid;
	private Config config;
	
	private Comparator<Node> titleComparator = new Comparator<Node>(){
		@Override
		public int compare(Node arg0, Node arg1) {
			try{
				String title0 = arg0.selectSingleNode("properties/title").getText();
				String title1 = arg1.selectSingleNode("properties/title").getText();
				return title0.compareTo(title1);
			}catch(Exception e){
				return -1;
			}
		}
	};
		
 	public EuscreenxlhomeApplication(String id) {
		super(id); 
		
		try{
			if(this.inDevelMode()){
				config = new Config(ConfigEnvironment.DEVEL);
			}else{
				config = new Config();
			}
		}catch(SettingNotExistException snee){
			snee.printStackTrace();
		}
		
		this.addReferid("config", "/euscreenxlelements/config");
		this.addReferid("mobilenav", "/euscreenxlelements/mobilenav");
		this.addReferid("header", "/euscreenxlelements/header");
		this.addReferid("footer", "/euscreenxlelements/footer");
		this.addReferid("linkinterceptor", "/euscreenxlelements/linkinterceptor");
		this.addReferid("warning", "/euscreenxlelements/warning");
		this.addReferid("videocopyright", "/euscreenxlelements/videocopyright");
		this.addReferid("fontawesome", "/euscreenxlelements/fontawesome");
		this.addReferid("analytics", "/euscreenxlelements/analytics");
		this.addReferid("urltransformer", "/euscreenxlelements/urltransformer");
		
		this.addReferidCSS("fontawesome", "/euscreenxlelements/fontawesome");
		this.addReferidCSS("bootstrap", "/euscreenxlelements/bootstrap");
		this.addReferidCSS("theme", "/euscreenxlelements/theme");
		this.addReferidCSS("genericadditions", "/euscreenxlelements/generic");
		this.addReferidCSS("all", "/euscreenxlelements/all");
		this.addReferidCSS("terms", "/euscreenxlelements/terms");
		
		
		this.categoryURLMappings = new HashMap<String, String>();
		this.categoryURLMappings.put("video-highlights", "/domain/euscreenxl/user/eu_agency/collection/highlights/teaser");
		this.categoryURLMappings.put("in-the-news", "/domain/euscreenxl/user/eu_agency/collection/inthenews/teaser");
		this.categoryURLMappings.put("series-picks", "/domain/euscreenxl/user/*/*");
		this.categoryURLMappings.put("video-posters", "/domain/euscreenxl/user/eu_agency/collection/videoposters/teaser");
		
		this.subtitleMappings = new HashMap<String, String>();
		this.subtitleMappings.put("EUS_9BD29393D326CB022C154E86A2371BF37143C42B", "/eddie/apps/euscreenxlhome/img/subtitles/EUS_9BD29393D326CB022C154E86A2371BF37143C42B.vtt");
		this.subtitleMappings.put("EUS_22A13730C1527DF4811A4D1CAAA6B1AAA2337A7D", "/eddie/apps/euscreenxlhome/img/subtitles/EUS_22A13730C1527DF4811A4D1CAAA6B1AAA2337A7D.vtt");
		this.subtitleMappings.put("EUS_29D477BDBB6AADDE415446ED863DBF6CD8E469BF", "/eddie/apps/euscreenxlhome/img/subtitles/EUS_29D477BDBB6AADDE415446ED863DBF6CD8E469BF.vtt");
		this.subtitleMappings.put("EUS_61C0D194C09E1258818041CDB9EB1F997FB4BCF2", "/eddie/apps/euscreenxlhome/img/subtitles/EUS_61C0D194C09E1258818041CDB9EB1F997FB4BCF2.vtt");
		this.subtitleMappings.put("EUS_756ADC59FC8F1A3942764B609E97783616056779", "/eddie/apps/euscreenxlhome/img/subtitles/EUS_756ADC59FC8F1A3942764B609E97783616056779.vtt");
		this.subtitleMappings.put("EUS_ECF997B524B9315CE0954D189954A909352103F1", "/eddie/apps/euscreenxlhome/img/subtitles/EUS_ECF997B524B9315CE0954D189954A909352103F1.vtt");
 	}
 	
 	 public String getFavicon() {
         return "/eddie/apps/euscreenxlelements/img/favicon.png";
     }
 	
 	public void initializeMode(Screen s){
 		this.loadContent(s, "config", "config");
 		this.loadContent(s, "urltransformer", "urltransformer");
 		this.loadContent(s, "cookiesnotification");
 		try {
			s.putMsg("config", "", "update(" + config.getSettingsJSON() + ")");
		} catch (SettingNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
 		if(!this.inDevelMode()){
			s.putMsg("linkinterceptor", "", "interceptLinks()");
		} else {
			s.removeContent("linkinterceptor");
		}
 	}
 	
 	public void initTooltips(Screen s){
 		s.putMsg("collectionviewer", "", "initTooltips()");
 	}
	
	public void initializeScreen(Screen s){				
		if(s.getCapabilities() != null && s.getCapabilities().getDeviceModeName() == null){
			loadContent(s, "footer");
			
		}else{
			removeContent(s, "footer");
		}
		s.putMsg("header", "", "setActivePage(home)");
		loadContent(s, "overlaydialog");
		loadContent(s, "analytics");
	}
	
	public void setDeviceTablet(Screen s){
		s.putMsg("collectionviewer", "app", "setDevice(tablet)");
	}
	
	private boolean inDevelMode() {
    	return LazyHomer.inDeveloperMode();
    }
	
	public void setGridSize(Screen s, String content){
		System.out.println("EuscreenxlhomeApplication.setGridSize(" + content + ")");
		int limit = Integer.parseInt(content);
		int start = 0;
		int stop;
		
		if(s.getProperty("chunkRange") != null){
			Integer[] chunkRange = (Integer[]) s.getProperty("chunkRange");
			start = chunkRange[1] + 1;
		}
		
		stop = start + limit;
		
		Integer[] newRange = {start, stop};
		s.setProperty("chunkRange", newRange);
		getNextChunk(s);
	}
	
	public void daniellog(Screen s){
	}
	
	public void fullscreenChanged(Screen s){
		s.setProperty("chunkRange", null);
	}
	
	public void getNextChunk(Screen s){
		Integer[] range = (Integer[]) s.getProperty("chunkRange");
		
		List<FsNode> nodes = (List<FsNode>) s.getProperty("nodes");

		if(!this.inDevelMode()){ // Production mode
			Filter approvedFilter = new Filter();
			EqualsCondition condition = new EqualsCondition("public", "true");
			approvedFilter.addCondition(condition);
			nodes = approvedFilter.apply(nodes);
		}
		
		if(s.getProperty("filter") != null){
			Filter filter = (Filter) s.getProperty("filter");
			nodes = filter.apply(nodes);
		}
		
		int start = range[0];
		int stop = range[1];
		
		if(stop >= nodes.size()){
			if(stop > nodes.size() && nodes.size() > 1){
				stop = nodes.size();
			}else{
				stop = 1;
			}
			s.putMsg("collectionviewer", "app", "endReached()");
		}
		
		if(nodes.size() > 0)
			nodes = nodes.subList(start, stop);
		
		JSONArray objectToSend = new JSONArray();
		
		for(Iterator<FsNode> i = nodes.iterator(); i.hasNext();){
			FsNode node = i.next();
			System.out.println(node.asXML());
			JSONObject item = new JSONObject();
			if(node.getName().equals("teaser")){
				item.put("id", node.getId());
				item.put("title", org.springfield.fs.FsEncoding.decode(node.getProperty("title")));
				try{
					item.put("screenshot", setEdnaMapping(node.getProperty("screenshot")));
				}catch(Exception e){
					item.put("screenshot", null);
				}
				item.put("description", org.springfield.fs.FsEncoding.decode(node.getProperty("title")));
				item.put("type", org.springfield.fs.FsEncoding.decode(node.getProperty("basedontype")));
				item.put("path", node.getPath());
			}else if(node.getName().equals("video")){
				item.put("id", node.getId());
				item.put("title", org.springfield.fs.FsEncoding.decode(node.getProperty(FieldMappings.getSystemFieldName("title"))));
				item.put("description", org.springfield.fs.FsEncoding.decode(node.getProperty(FieldMappings.getSystemFieldName("title"))));
				item.put("type", "video");
				item.put("path", node.getPath());
				try{
					item.put("screenshot", setEdnaMapping(node.getProperty("screenshot")));
				}catch(Exception e){
					item.put("screenshot", null);
				}
			}
			objectToSend.add(item);
		}
		s.putMsg("collectionviewer", "app", "appendItems(" + objectToSend + ")");
	}
	
	public void changeSelection(Screen s, String message){
		JSONObject data = (JSONObject) JSONValue.parse(message);
		String category = (String) data.get("category");
		
		s.putMsg("collectionviewer", "", "moreAvailable()");
		System.out.println("category: " + category);
		
		try{
			String uri = this.categoryURLMappings.get(category);
			s.setProperty("uri", uri);
			FSList collectionNodes;
			if(uri.contains("*")){
				collectionNodes = FSListManager.get(uri);
			}else{
				collectionNodes = FSListManager.get(uri, false);
			}
			System.out.println(collectionNodes);
			
			JSONObject params = (JSONObject) data.get("params");
		
			
			if(category.equals("video-highlights")){
				this.setVideoHighlights(s, collectionNodes, params);
			}else if(category.equals("in-the-news")){
				this.setInTheNews(s, collectionNodes, params);
			}else if(category.equals("series-picks")){
				this.setSeriesPicks(s, collectionNodes, params);
			}else if(category.equals("video-posters")){
				this.setVideoPosters(s, collectionNodes, params);
			}
		}catch(Exception e){ 
			e.printStackTrace();
		}
		
	}
	
	private void setVideoPosters(Screen s, FSList collectionNodes, JSONObject params){
		List<FsNode> nodes = collectionNodes.getNodes();	
		s.setProperty("chunkRange", null);
		setNodes(s, nodes, true);
		s.putMsg("collectionviewer", "app", "createGrid()");
	}
	
	private void setSeriesPicks(Screen s, FSList collectionNodes, JSONObject searchParameters){
		System.out.println("setSeriesPicks(" + searchParameters + ")");
		List<FsNode> nodes = new ArrayList<FsNode>();
		s.setProperty("chunkRange", null);
		
		
		if(searchParameters.containsKey("id")){
			String id = (String) searchParameters.get("id");
			
			nodes = collectionNodes.getNodesFiltered(id.toLowerCase()); // find the item
			if (nodes!=null && nodes.size()>0) {
				FsNode n = (FsNode)nodes.get(0);
				
				FSList episodes = FSListManager.get(n.getPath() + "/video", false);
				
				nodes = episodes.getNodes();
				
			}
		}else if(searchParameters.containsKey("series")){
			String series = (String) searchParameters.get("series");
			String provider = (String) searchParameters.get("provider");
			EqualsCondition seriesCondition = new EqualsCondition(FieldMappings.getSystemFieldName("series"), series);
			EqualsCondition providerCondition = new EqualsCondition(FieldMappings.getSystemFieldName("provider"), provider);
			Filter filter = new Filter();
			AndCondition andCondition = new AndCondition();
			andCondition.add(seriesCondition);
			andCondition.add(providerCondition);
			filter.addCondition(andCondition);
			nodes = filter.apply(collectionNodes.getNodes());
		}
		
		setNodes(s, nodes);
		
		s.putMsg("collectionviewer", "app", "createGrid()");
		
	}
	
	private void setVideoHighlights(Screen s, FSList collectionNodes, JSONObject params){
		System.out.println("setVideoHighLights()");
		List<FsNode> nodes = collectionNodes.getNodes();
		String topic = (String) params.get("topic");
		
		if(!topic.equals("*")){
			Filter filter = new Filter();
			FilterCondition topicCondition = new EqualsCondition(FieldMappings.getSystemFieldName("topic"), topic);
			filter.addCondition(topicCondition);
			nodes = filter.apply(nodes);
		}
		
		s.setProperty("chunkRange", null);
		setNodes(s, nodes, true);
		s.putMsg("collectionviewer", "app", "createGrid()");
	}
	
	private void setInTheNews(Screen s, FSList collectionNodes, JSONObject params){
		System.out.println("setInTheNews()");
		List<FsNode> nodes = collectionNodes.getNodes();
		
		String topic = (String) params.get("topic");
		
		Filter filter = new Filter();
		FilterCondition topicCondition = new EqualsCondition(FieldMappings.getSystemFieldName("topic"), topic);
		filter.addCondition(topicCondition);
		
		nodes = filter.apply(nodes);
		
		s.setProperty("chunkRange", null);
		setNodes(s, nodes, true);
		s.putMsg("collectionviewer", "app", "createGrid()");
	}
	
	private void setNodes(Screen s, List<FsNode> nodes){
		setNodes(s, nodes, false);
	}
	
	private void setNodes(Screen s, List<FsNode> nodes, boolean random){
		if(random){
			long seed = System.nanoTime();
			Collections.shuffle(nodes, new Random(seed));
		}
		s.setProperty("nodes", nodes);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("Collection changed!");
		// TODO Auto-generated method stub
		ComponentManager manager = this.componentmanager;
		JSONObject updateParams = (JSONObject) arg;
		System.out.println(updateParams);
		if(arg != null){
			//manager.getComponent("collection").put("app", "update(" + updateParams + ")");
		}
	}
	
	public void actionPlayitem(Screen s, String message){
		System.out.println("playItem( " + message + " )");
		JSONObject data = (JSONObject) JSONValue.parse(message);
		if(data != null){
			String type = (String) data.get("type");
			String path = (String) data.get("path");
			if(type.equals("video")){
				playVideo(s, path);
			}else if(type.equals("videoposter")){
				playVideoPoster(s, path);
			}
		}
	}
	
	private void playVideo(Screen s, String path){
		
		String title;
		FsNode node;
		if(path.contains("/video/")){
			node = Fs.getNode(path);
			title = org.springfield.fs.FsEncoding.decode(node.getProperty(FieldMappings.getSystemFieldName("title")));
		}else{
			FsNode teaserNode = Fs.getNode(path);
			String videoPath = teaserNode.getProperty("basedon").replace("'", "");
			node = Fs.getNode(videoPath);
			title = org.springfield.fs.FsEncoding.decode(teaserNode.getProperty(FieldMappings.getSystemFieldName("title")));
		}
		
		if(node != null){
			String rawVideoPath = node.getPath() + "/rawvideo/1";
			FsNode rawvideo = Fs.getNode(rawVideoPath);
			
			String videoFilePath = rawvideo.getProperty("mount");
			String[] videos = videoFilePath.split(",");
			String[] videoFilePathWithTicket = new String[videos.length];			
			
			//for(int i = 0; i < videos.length; i++){ //Temp workaround to only have 1 video instead of multiple
			//This to prevent downloading of the second stream as the browser only plays out the first stream.
			for (int i = 0; i < 1; i++) { 			
				String video = videos[i];
				
				if (video.indexOf("http://") == -1 && video.indexOf("https://") == -1) {
					Random randomGenerator = new Random();
					Integer random= randomGenerator.nextInt(100000000);
					String ticket = Integer.toString(random);
	
					String videoFile= "/"+video+"/"+node.getPath()+ "/rawvideo/1/raw.mp4";
					videoFile = videoFile.replace("//","/");	
					try{						
						System.out.println("CallingSendTicket="+videoFile);						
						sendTicket(videoFile,ipAddress,ticket);}
					catch (Exception e){}

					videoFilePathWithTicket[i] = "https://" + video + ".noterik.com/progressive/" + video + "/" + node.getPath() + "/rawvideo/1/raw.mp4?ticket="+ticket;
				} else if (videoFilePath.indexOf(".noterik.com/progressive/") > -1) {
					Random randomGenerator = new Random();
					Integer random= randomGenerator.nextInt(100000000);
					String ticket = Integer.toString(random);
					
					String videoFile = video.substring(video.indexOf("progressive")+11);
					videoFile = videoFile.startsWith("http://") ? videoFile.replaceFirst("http", "https") : videoFile;
					
					try{						
						//System.out.println("CallingSendTicket");						
						sendTicket(videoFile,ipAddress,ticket);}
					catch (Exception e){}
					
					video = video.startsWith("http://") ? video.replaceFirst("http", "https") : video;
					videoFilePathWithTicket[i] = video+"?ticket="+ticket;	
				} else {
					videoFilePathWithTicket[i] = video;
				}
			} 
			
			
			String poster = setEdnaMapping(node.getProperty("screenshot"));
	
			JSONObject titleMessage = new JSONObject();
			titleMessage.put("title", title);
			
			JSONObject videoMessage = new JSONObject();
			
			
			videoMessage.put("src", videoFilePathWithTicket[0]); //TODO: fix to take both videos into account!
			videoMessage.put("title", title);

			videoMessage.put("poster", poster);
			
			FsNode maggieNode = Fs.getNode(node.getPath());
		//	System.out.println("MAGGIENODE="+maggieNode.asXML());
			String duration = maggieNode.getProperty(FieldMappings.getSystemFieldName("duration"));
			System.out.println("DURATION="+timeToSeconds(duration));
			videoMessage.put("duration",""+timeToSeconds(duration));
			videoMessage.put("maggieid", maggieNode.getPath());
			// daniel 
			
			System.out.println("ID: " + id);
			
			if(this.subtitleMappings.containsKey(id)){
				videoMessage.put("subtitles", this.subtitleMappings.get(id));
			}
			
			JSONObject linkMessage = new JSONObject();
			linkMessage.put("id", node.getId());
			System.out.println("WWWWOOOOO2="+node.getPath());
			
			s.putMsg("player", "app", "setTitle(" + titleMessage + ")");
			s.putMsg("player", "app", "setVideo(" + videoMessage + ")");
			s.putMsg("player", "app", "setLink(" + linkMessage + ")");
		}else{
			System.out.println("This video doesn't exist!");
		}
	}
	
	private void playVideoPoster(Screen s, String path){
		System.out.println("playVideoPoster(" + path + ")");
		FsNode teaserNode = Fs.getNode(path);
		if(teaserNode != null){
			FsNode videoPosterNode = Fs.getNode(teaserNode.getProperty("basedon"));
			JSONObject message = new JSONObject();
			message.put("html", videoPosterNode.getProperty("html"));
			message.put("wrap", true);
			message.put("visible", true);
			s.putMsg("overlaydialog", "", "update(" + message + ")");
			System.out.println("VIDEO POSTER XML: " + videoPosterNode.asXML());
		}
	}
	
	public String setEdnaMapping(String screenshot) {
		if(screenshot != null){
			if (!wantedna) {
				screenshot = screenshot.replace("edna/", "");
				screenshot = screenshot.startsWith("http://") ? screenshot.replaceFirst("http", "https")  : screenshot ;
			} else {
				int pos = screenshot.indexOf("edna/");
				if 	(pos!=-1) {
					screenshot = "https://images.euscreenxl.eu/"+screenshot.substring(pos+5);
				} else {
					screenshot = screenshot.startsWith("http://") ? screenshot.replaceFirst("http", "https")  : screenshot ;
				}
			}
			screenshot +="?script=euscreen640t";
			return screenshot;
		}else{
			return null;
		}
	}
	
	public String getMetaHeaders(HttpServletRequest request) {
		ipAddress=getClientIpAddress(request);
		
		String browserType = request.getHeader("User-Agent");
		if(browserType.indexOf("Mobile") != -1) {
			String ua = request.getHeader("User-Agent").toLowerCase();
			isAndroid = ua.indexOf("android") > -1; //&& ua.indexOf("mobile");	
		}
		
		return "";
	}
	
	private static void sendTicket(String videoFile, String ipAddress, String ticket) throws IOException {
		URL serverUrl = new URL("http://ticket.noterik.com:8080/lenny/acl/ticket");
		HttpURLConnection urlConnection = (HttpURLConnection)serverUrl.openConnection();
		
		Long Sytime = System.currentTimeMillis();
		Sytime = Sytime / 1000;
		String expiry = Long.toString(Sytime+(15*60));
		
		// Indicate that we want to write to the HTTP request body
		
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("Content-Type", "text/xml");
		urlConnection.setRequestMethod("POST");
		videoFile=videoFile.substring(1);
		
		//System.out.println("I send this video address to the ticket server:"+videoFile);
		//System.out.println("And this ticket:"+ticket);
		//System.out.println("And this EXPIRY:"+expiry);
		
		// Writing the post data to the HTTP request body
		BufferedWriter httpRequestBodyWriter = 
		new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		String content="";
		if (isAndroid){
			content = "<fsxml><properties><ticket>"+ticket+"</ticket>"
			+ "<uri>/"+videoFile+"</uri><ip>"+ipAddress+"</ip> "
			+ "<role>user</role>"
			+ "<expiry>"+expiry+"</expiry><maxRequests>4</maxRequests></properties></fsxml>";
			isAndroid=false;
			//System.out.println("Android ticket!");
		}
		else {
			content = "<fsxml><properties><ticket>"+ticket+"</ticket>"
			+ "<uri>/"+videoFile+"</uri><ip>"+ipAddress+"</ip> "
			+ "<role>user</role>"
			+ "<expiry>"+expiry+"</expiry><maxRequests>1</maxRequests></properties></fsxml>";
		}
		System.out.println(getCurrentTimeStamp()+" sending content "+content);
		httpRequestBodyWriter.write(content);
		httpRequestBodyWriter.close();
		
		System.out.println("response code = "+urlConnection.getResponseCode());
		
		// Reading from the HTTP response body
		Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
		while(httpResponseScanner.hasNextLine()) {
			System.out.println(httpResponseScanner.nextLine());
		}
		httpResponseScanner.close();		
	}
	
	private static final String[] HEADERS_TO_TRY = { 
		"X-Forwarded-For",
		"Proxy-Client-IP",
		"WL-Proxy-Client-IP",
		"HTTP_X_FORWARDED_FOR",
		"HTTP_X_FORWARDED",
		"HTTP_X_CLUSTER_CLIENT_IP",
		"HTTP_CLIENT_IP",
		"HTTP_FORWARDED_FOR",
		"HTTP_FORWARDED",
		"HTTP_VIA",
		"REMOTE_ADDR" };
		
	public static String getClientIpAddress(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
		String ip = request.getHeader(header);
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
		return ip;
		}
		}
		return request.getRemoteAddr();
	}
	
	public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	private int timeToSeconds(String time) {
		String[] parts = time.split(":");
		if (parts.length==3) {
			try {
				int sec = Integer.parseInt(parts[2]);
				int min = Integer.parseInt(parts[1]);
				int hour = Integer.parseInt(parts[0]);
				return (sec+(min*60)+(hour*3600));
			} catch(Exception e) {
				return 3600; // default to a hour?
			}
		} else if (parts.length==2) {
			try {
				int sec = Integer.parseInt(parts[1]);
				int min = Integer.parseInt(parts[0]);
				return (sec+(min*60));
			} catch(Exception e) {
				return 3600; // default to a hour?
			}
		}
		return 3600;
	}
}
