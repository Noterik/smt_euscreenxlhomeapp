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
	
	var filledTemplate = _.template(this.template, {video: params});
	this.element.find('.modal-body.media-player').html(filledTemplate);
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