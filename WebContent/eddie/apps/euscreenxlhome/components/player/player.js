var Player = function(options){
	var self = this;
	Component.apply(this, arguments);
	
	this.element = jQuery("#player");
	this.template = this.element.find('#overlay-player-video-template').text();
	this.element.find('.modal').on('hide.bs.modal', function(){
		self.element.find('video')[0].pause();
	});
};

Player.prototype = Object.create(Component.prototype);
Player.prototype.setTitle = function(data){
	var params = JSON.parse(data);
	this.element.find('h4.modal-title').html(params.title);
};

Player.prototype.setVideo = function(data){
	console.log("SETVIDEO CALLED IN HOME2");
	var self = this;
	var params = JSON.parse(data);
	console.log(params);
	this.element.find('video').remove();
	
	
	
	console.log("VIDEO2 DANIEL");
	console.log(params);
	console.log("END2 VIDEO2 "+params.src);
	
	var videoid=params.src;
	var pos=videoid.indexOf("?");
	var ticket = "";
	if (pos!=-1) {
		ticket = videoid.substring(pos+1);
		videoid=videoid.substring(0,pos);
	}
	var duration=params.duration;
	var maggieid=params.maggieid;
	
	console.log("TICKET="+ticket);
	console.log("SRC="+videoid);

//	console.log("END2 VIDEO2 "+params..src+" "+params.screenshot+" duration="+params.sources[0].duration);
	
	//var filledTemplate = _.template(this.template, {video: params});
	//this.element.find('.modal-body.media-player').html(filledTemplate);
	
	
	//var manurl = "https://videoeditor.noterik.com/manifest/createmanifest.php?src=http://openbeelden.nl/files/09/9983.9970.WEEKNUMMER403-HRE0001578C.mp4&duration=86360&id=http://openbeelden.nl/files/09/9983.9970.WEEKNUMMER403-HRE0001578C.mp4";
	var manurl = "https://beta.qandr.eu/euscreenxlmanifestservlet/?videoid="+videoid+"&"+ticket+"&duration="+duration+"&maggieid="+maggieid;

	
	var html = "<div class=\"player\" id=\"viewer\"></div><script>new europeanamediaplayer.default(document.getElementById(\"viewer\"), {}, {editor: \"http://video-editor.eu\", manifest: \""+manurl+"\"});</script>";

	
	this.element.find('.modal-body.media-player').html(html);
	this.element.find('.modal').modal('show');
	
	if(jQuery('body').hasClass('fullscreenactive')){
		var modalDialog = self.element.find('.modal-dialog');
		this.element.find('.modal').off('show.bs.modal').on('shown.bs.modal', function(){
			var modalHeight = self.element.find('.modal').height();
			var dialogHeight = modalDialog.height();
			
			if((dialogHeight + 60) > modalHeight){
				var wantedHeight = modalHeight - 200;
				var wantedWidth = wantedHeight / 3 * 4;
			    modalDialog.width(wantedWidth);
			}
			
		});
	}
	
};


Player.prototype.setLink = function(data){
	console.log("Player.prototype.setLink(" + data + ")");
	var params = JSON.parse(data);
	
	jQuery('#visit-item-page').attr('href', '/item.html?id=' + params.id);
};