package org.springfield.lou.application.types;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.json.simple.JSONObject;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.components.ComponentManager;
import org.springfield.fs.FSXMLStrainer;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.fs.IncorrectFilterException;
import org.springfield.lou.screen.Screen;

public class EuscreenxlhomeApplication extends Html5Application implements Observer{

	private FSXMLStrainer collectionStrainer;
	private Map<String, Integer[]> chunksForScreens;
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
		
		this.addReferid("header", "/euscreenxlelements/header");
		this.addReferid("footer", "/euscreenxlelements/footer");
				
		chunksForScreens = new HashMap<String, Integer[]>();
		try {
			Document filterDocument = DocumentHelper.parseText("<filter><include><node id=\"video\"><include><property id=\"description\" /><property id=\"title\" /><property id=\"screenshot\" /></include></node></include></filter>");
			collectionStrainer = new FSXMLStrainer(filterDocument);
		} catch (IncorrectFilterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void startScreen(Screen s) {
		System.out.println("-----------------------NEWSCREEN-------------------------");
		System.out.println("CollectionrutgerApplication.onNewScreen()");
		this.unsubscribe(this.observingUri);
		System.out.println("OBSERVING URI:" + observingUri);
		System.out.println("COLLECTION STRAINER: " + collectionStrainer);
		this.subscribe(this.observingUri, collectionStrainer);
		System.out.println("----------------------END NEWSCREEN----------------------");
		initializeScreen(s);
	}
	
	private void initializeScreen(Screen s){
		/*
		System.out.println("EuscreenxlhomeApplication.initializeScreen()");
		this.chunksForScreens.put(s.getId(), 0);
		getNextChunk(s);
		*/
		s.putMsg("collectionview", "app", "createGrid()");
	}
	
	public void setGridSize(Screen s, String content){
		System.out.println("EuscreenxlhomeApplication.setGridSize(" + content + ")");
		int limit = Integer.parseInt(content);
		int start = 0;
		int stop;
		if(this.chunksForScreens.containsKey(s.getId())){
			Integer[] range = this.chunksForScreens.get(s.getId());
			start = range[1];
		}
		
		stop = start + limit;
		
		Integer[] newRange = {start, stop};
		this.chunksForScreens.put(s.getId(), newRange);
		getNextChunk(s);
	}
	
	public void getNextChunk(Screen s){
		Integer[] range = this.chunksForScreens.get(s.getId());
		s.putMsg("collectionview", "app", "appendItems(" + this.observingNodes.get(this.observingUri).get("//video[position() >= " + range[0] + " and position() < " + (range[1] + 1) + "]").asXML() + ")");
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
