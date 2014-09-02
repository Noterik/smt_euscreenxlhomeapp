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
	private String observingUri = "/domain/springfieldwebtv/user/david/video";
	
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
		this.addReferid("terms", "/euscreenxlelements/terms");
		this.addReferid("linkinterceptor", "/euscreenxlelements/linkinterceptor");
		
		System.out.println("DANIEL1: START LOADING NODES");
		allNodes = FSListManager.get(this.observingUri);
		System.out.println("DANIEL2: END LOADING NODES");
	}
 	
 	public void initializeMode(Screen s){
		System.out.println("DANIEL3: INIT MODE");
 		if(!this.inDevelMode()){
			s.putMsg("terms", "", "show()");
			s.putMsg("linkinterceptor", "", "interceptLinks()");
		}
		System.out.println("DANIEL4: END INIT MODE");
 	}
	
	public void initializeScreen(Screen s){
		System.out.println("DANIEL5: INIT SCREEN");
		s.putMsg("collectionview", "app", "createGrid()");
		
		if(s.getCapabilities() != null && s.getCapabilities().getDeviceModeName() == null){
			loadContent(s, "footer");
			s.putMsg("template", "", "activateTooltips()");
		}else{
			removeContent(s, "footer");
		}
		System.out.println("DANIEL6: INIT SCREEN");
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
		System.out.println("DANIEL START ACTIONLIST");
	}
	
	public void getNextChunk(Screen s){
		Integer[] range = (Integer[]) s.getProperty("chunkRange");
		
		List<FsNode> nodes = allNodes.getNodes();
		if(s.getProperty("filter") != null){
			Filter filter = (Filter) s.getProperty("filter");
			nodes = filter.apply(nodes);
		}
		
		int start = range[0];
		int stop = range[1];
		
		if(start != 0){
			stop += 1;
		}
		
		if(stop >= nodes.size()){
			if(stop > nodes.size()){
				stop = nodes.size() - 1;
			}
			s.putMsg("collectionview", "app", "endReached()");
		}
		
		nodes = nodes.subList(start, stop);
		
		JSONArray objectToSend = new JSONArray();
		
		for(Iterator<FsNode> i = nodes.iterator(); i.hasNext();){
			FsNode node = i.next();
			JSONObject item = new JSONObject();
			item.put("id", node.getId());
			item.put("title", node.getProperty("title"));
			item.put("screenshot", node.getProperty("screenshot"));
			item.put("description", node.getProperty("description"));
			objectToSend.add(item);
		}
		s.putMsg("collectionview", "app", "appendItems(" + objectToSend + ")");
	}
	
	public void setTopic(Screen s, String topic){
		FilterCondition condition = new EqualsCondition(FieldMappings.getSystemFieldName("topic"), topic, ",");
		Filter filter = new Filter();
		filter.addCondition(condition);
		s.setProperty("filter", filter);
		s.setProperty("chunkRange", null);
		s.putMsg("collectionview", "app", "createGrid()");
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
	
	public void playVideo(Screen s, JSONObject params){
		System.out.println("CollectionrutgerApplication.playVideo()");
		String path = (String) params.get("path");
		String fullPath = this.observingUri + "/" +path + "/rawvideo/1";
		FsNode node = Fs.getNode(fullPath);
		String mount = node.getProperty("mount");
		String[] splits = mount.split(",");
		
		JSONObject message = new JSONObject();
		message.put("video", splits[0]);
		
		String command = "setVideo(" + message + ")";
		s.putMsg("playeroverlay", "", command);
	}
}
