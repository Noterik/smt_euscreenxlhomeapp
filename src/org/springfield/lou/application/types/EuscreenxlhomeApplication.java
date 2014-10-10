package org.springfield.lou.application.types;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import org.dom4j.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.components.ComponentManager;
import org.springfield.lou.application.types.conditions.EqualsCondition;
import org.springfield.lou.application.types.conditions.FilterCondition;
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
		
		this.addReferid("mobilenav", "/euscreenxlelements/mobilenav");
		this.addReferid("header", "/euscreenxlelements/header");
		this.addReferid("footer", "/euscreenxlelements/footer");
		this.addReferid("linkinterceptor", "/euscreenxlelements/linkinterceptor");
		this.addReferid("warning", "/euscreenxlelements/warning");
		this.addReferid("videocopyright", "/euscreenxlelements/videocopyright");
		this.addReferid("fontawesome", "/euscreenxlelements/fontawesome");
		
		
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
	}
 	
 	 public String getFavicon() {
         return "/eddie/apps/euscreenxlelements/img/favicon.png";
     }
 	
 	public void initializeMode(Screen s){
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
			s.putMsg("template", "", "activateTooltips()");
		}else{
			removeContent(s, "footer");
		}
		s.putMsg("header", "", "setActivePage(home)");
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
		
		if(start == 0){
			stop = start + limit;
		}else{
			stop = start + limit - 1;
		}
		
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
			}else if(node.getName().equals("video")){
				item.put("id", node.getId());
				item.put("title", org.springfield.fs.FsEncoding.decode(node.getProperty(FieldMappings.getSystemFieldName("title"))));
				item.put("description", org.springfield.fs.FsEncoding.decode(node.getProperty(FieldMappings.getSystemFieldName("title"))));
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
			String subCategory = (String) data.get("subcategory");
			
			if(category.equals("video-highlights")){
				this.setVideoHighlights(s, collectionNodes, subCategory);
			}else if(category.equals("in-the-news")){
				this.setInTheNews(s, collectionNodes, subCategory);
			}else if(category.equals("series-picks")){
				this.setSeriesPicks(s, collectionNodes, subCategory);
			}
		}catch(Exception e){ 
			e.printStackTrace();
		}
		
	}
	
	private void setSeriesPicks(Screen s, FSList collectionNodes, String seriesId){
		List<FsNode> nodes = collectionNodes.getNodesFiltered(seriesId.toLowerCase()); // find the item
		if (nodes!=null && nodes.size()>0) {
			FsNode n = (FsNode)nodes.get(0);
			
			FSList episodes = FSListManager.get(n.getPath() + "/video", false);
			
			s.setProperty("chunkRange", null);
			
			setNodes(s, episodes.getNodes());
			s.putMsg("collectionviewer", "app", "createGrid()");
		}
	}
	
	private void setVideoHighlights(Screen s, FSList collectionNodes, String topic){
		System.out.println("setVideoHighLights()");
		List<FsNode> nodes = collectionNodes.getNodes();
		
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
	
	private void setInTheNews(Screen s, FSList collectionNodes, String topic){
		System.out.println("setInTheNews()");
		List<FsNode> nodes = collectionNodes.getNodes();
		
		Filter filter = new Filter();
		FilterCondition topicCondition = new EqualsCondition(FieldMappings.getSystemFieldName("topic"), topic);
		filter.addCondition(topicCondition);
		
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
	
	public void playVideo(Screen s, String path){
		System.out.println("CollectionrutgerApplication.playVideo(" +  path + ")");
		String uri = (String) s.getProperty("uri");
		System.out.println("URI: " + uri);
		String teaserPath = uri + "/" + path;
		FsNode teaser = Fs.getNode(teaserPath);
		String videoPath = null;
		String title = null;
		
		if(teaser != null){
			title = org.springfield.fs.FsEncoding.decode(teaser.getProperty("title"));
			videoPath = teaser.getProperty("basedon").replace("'", "");
		}else{
			String allURI = "/domain/euscreenxl/user/*/*";
			
			FSList fslist = FSListManager.get(allURI);
			List<FsNode> nodes = fslist.getNodesFiltered(path.toLowerCase()); // find the item
			if (nodes!=null && nodes.size()>0) {
				FsNode n = (FsNode)nodes.get(0);
				title = org.springfield.fs.FsEncoding.decode(n.getProperty(FieldMappings.getSystemFieldName("title")));
				videoPath = n.getPath();
			}
		}
		
		if(videoPath != null){
			String rawVideoPath = videoPath + "/rawvideo/1";
			FsNode video = Fs.getNode(videoPath);
			FsNode rawvideo = Fs.getNode(rawVideoPath);
					
			
			String mount = rawvideo.getProperty("mount");
			String poster = setEdnaMapping(video.getProperty("screenshot"));
			String[] splits = mount.split(",");		
			
			JSONObject titleMessage = new JSONObject();
			titleMessage.put("title", title);
			
			JSONObject videoMessage = new JSONObject();
			videoMessage.put("src", splits[0]);
			videoMessage.put("title", title);
			videoMessage.put("poster", poster);
			
			JSONObject linkMessage = new JSONObject();
			linkMessage.put("id", video.getId());
			
			s.putMsg("player", "app", "setTitle(" + titleMessage + ")");
			s.putMsg("player", "app", "setVideo(" + videoMessage + ")");
			s.putMsg("player", "app", "setLink(" + linkMessage + ")");
		}
	}
	
	public String setEdnaMapping(String screenshot) {
		if(screenshot != null){
			if (!wantedna) {
				screenshot = screenshot.replace("edna/", "");
			} else {
				int pos = screenshot.indexOf("edna/");
				if 	(pos!=-1) {
					screenshot = "http://images.euscreenxl.eu/"+screenshot.substring(pos+5);
				}
			}
			screenshot +="?script=euscreen640t";
			return screenshot;
		}else{
			return null;
		}
	}
}
