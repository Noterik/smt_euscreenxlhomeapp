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
	var self = this;
	var params = JSON.parse(data);
	console.log(params);
	this.element.find('video').remove();
	
	
	var videoid=params.src;
	var pos=videoid.indexOf("?");
	var ticket = "";
	if (pos!=-1) {
		ticket = videoid.substring(pos+1);
		videoid=videoid.substring(0,pos);
	}
	var duration=params.duration;
	var maggieid=params.maggieid;
	
	//var manurl = "https://videoeditor.noterik.com/manifest/createmanifest.php?src=http://openbeelden.nl/files/09/9983.9970.WEEKNUMMER403-HRE0001578C.mp4&duration=86360&id=http://openbeelden.nl/files/09/9983.9970.WEEKNUMMER403-HRE0001578C.mp4";
	var manurl = "https://embd.eu/euscreenxlmanifestservlet/?videoid="+videoid+"&"+ticket+"&duration="+duration+"&maggieid="+maggieid;

	var user = "";

	var cookie = document.cookie;
	var re = /smt_euscreenxlapp_user=[a-zA-Z0-9]+/;
	var rs;

	if ((rs = re.exec(cookie)) !== null) {
		if (rs.index === re.lastIndex) {
			re.lastIndex++;
		}
		// eg m[0] etc.
	}
	if (rs) {
		var splitedRegexResult = rs[0].split('=');
		user = splitedRegexResult[1];
	}
	
	var hash = "";
	re2 = /smt_euscreenxlapp_hash=[a-zA-Z0-9]+/;
	var rs2;
	
	if ((rs2 = re2.exec(cookie)) !== null) {
		if (rs2.index === re2.lastIndex) {
			re2.lastIndex++;
		}
		// eg m[0] etc.
	}
	if (rs2) {
		var splitedRegexResult2 = rs2[0].split('=');
		hash = splitedRegexResult2[1];
	}

	if (user != "" && hash != "") {
		var html = "<div class=\"player\" id=\"viewer\"></div><script>new EuropeanaMediaPlayer(document.getElementById(\"viewer\"), {manifest: \""
				+ manurl + "\"}, {editor: \"http://video-editor.eu/user/"
				+ user
				+ "/hash/"
				+ hash
				+ "\", manifest: \""
				+ manurl
				+ "\"});</script>";

	} else {
		var html = "<div class=\"player\" id=\"viewer\"></div><script>new EuropeanaMediaPlayer(document.getElementById(\"viewer\"), {manifest: \""
				+ manurl + "\"}, {editor: \"http://euscreen.eu/myeuscreen.html\"});</script>";
	}
	
	
	//var html = "<div class=\"player\" id=\"viewer\"></div><script>new europeanamediaplayer.default(document.getElementById(\"viewer\"), {}, {editor: \"http://video-editor.eu\", manifest: \""+manurl+"\"});</script>";

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