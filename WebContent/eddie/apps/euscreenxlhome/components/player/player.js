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
	this.element.find('h4.modal-title').text(params.title);
};
Player.prototype.setVideo = function(data){
	var params = JSON.parse(data);
	console.log(params);
	this.element.find('video').remove();
	
	var filledTemplate = _.template(this.template, {video: params});
	console.log(filledTemplate);
	this.element.find('.modal-body.media-player').html(filledTemplate);
	this.element.find('.modal').modal('show');
};
Player.prototype.setLink = function(data){
	console.log("Player.prototype.setLink(" + data + ")");
	var params = JSON.parse(data);
	
	jQuery('#visit-item-page').attr('href', '/item.html?id=' + params.id);
};