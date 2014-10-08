package org.springfield.lou.application.types;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

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
	private String observingUri = "/domain/euscreenxl/user/eu_agency/collection/highlights/teaser";
	
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
		
		this.addReferidCSS("elements", "/euscreenxlelements/generic");
		this.addReferidCSS("bootstrap", "/euscreenxlelements/bootstrap");
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
		allNodes = FSListManager.get(this.observingUri, false);
		
		s.putMsg("collectionviewer", "app", "createGrid()");
		
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
		
		List<FsNode> nodes = allNodes.getNodes();
		
		Iterator<FsNode> ii = nodes.iterator();
		
		while(ii.hasNext()){
			FsNode node = ii.next();
			System.out.println("--------NODE------------");
			System.out.println(node.asXML());
			System.out.println("-------/NODE------------");
		}
				
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
				stop = nodes.size() - 1;
			}else{
				stop = 1;
			}
			s.putMsg("collectionviewer", "app", "endReached()");
		}
		
		nodes = nodes.subList(start, stop);
		
		JSONArray objectToSend = new JSONArray();
		
		for(Iterator<FsNode> i = nodes.iterator(); i.hasNext();){
			FsNode node = i.next();
			System.out.println(node.asXML());
			JSONObject item = new JSONObject();
			item.put("id", node.getId());
			item.put("title", org.springfield.fs.FsEncoding.decode(node.getProperty("title")));
			item.put("screenshot", node.getProperty("screenshot"));
			item.put("description", org.springfield.fs.FsEncoding.decode(node.getProperty("title")));
			objectToSend.add(item);
		}
		s.putMsg("collectionviewer", "app", "appendItems(" + objectToSend + ")");
	}
	
	public void setTopic(Screen s, String topic){
		FilterCondition condition = new EqualsCondition(FieldMappings.getSystemFieldName("topic"), topic, ",");
		Filter filter = new Filter();
		filter.addCondition(condition);
		s.setProperty("filter", filter);
		s.setProperty("chunkRange", null);
		s.putMsg("collectionviewer", "app", "createGrid()");
		getNextChunk(s);
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
		String teaserPath = this.observingUri + "/" + path;
		FsNode teaser = Fs.getNode(teaserPath);
		String videoPath = teaser.getProperty("basedon").replace("'", "");
		String rawVideoPath = videoPath + "/rawvideo/1";
		FsNode video = Fs.getNode(videoPath);
		FsNode rawvideo = Fs.getNode(rawVideoPath);
				
		String title = org.springfield.fs.FsEncoding.decode(teaser.getProperty("title"));
		String mount = rawvideo.getProperty("mount");
		String poster = video.getProperty("screenshot");
		String[] splits = mount.split(",");		
		
		JSONObject posterMessage = new JSONObject();
		posterMessage.put("poster", poster);
		
		JSONObject titleMessage = new JSONObject();
		titleMessage.put("title", title);
		
		JSONObject videoMessage = new JSONObject();
		videoMessage.put("video", splits[0]);
		
		JSONObject linkMessage = new JSONObject();
		linkMessage.put("id", video.getId());
		
		s.putMsg("player", "app", "setPoster(" + posterMessage + ")");
		s.putMsg("player", "app", "setTitle(" + titleMessage + ")");
		s.putMsg("player", "app", "setVideo(" + videoMessage + ")");
		s.putMsg("player", "app", "setLink(" + linkMessage + ")");
	}
}
