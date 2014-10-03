var Player = function(options){
	var self = this;
	Component.apply(this, arguments);
	
	this.element = jQuery("#player");
	this.element.find('.modal').on('hide.bs.modal', function(){
		self.element.find('video')[0].pause();
	});
};

Player.prototype = Object.create(Component.prototype);
Player.prototype.setTitle = function(data){
	var params = JSON.parse(data);
	this.element.find('h4.modal-title').text(params.title);
};
Player.prototype.setVideo = function(data){
	var params = JSON.parse(data);
	var source = jQuery('<source src="' + params.video + '" type="video/mp4">');
	this.element.find('video').html(source);
	this.element.find('.modal').modal('show');
};
Player.prototype.setPoster = function(data){
	console.log("Player.prototype.setPoster(" + data + ")");
	var params = JSON.parse(data);
	this.element.find('video').attr('poster', params.poster);
};
Player.prototype.setLink = function(data){
	console.log("Player.prototype.setLink(" + data + ")");
	var params = JSON.parse(data);
	
	jQuery('#visit-item-page').attr('href', '/item.html?id=' + params.id);
};