var Fullscreencollectionview = function(options){
	Component.apply(this, arguments);
	var self = this;
	
	this.element = jQuery("#fullscreencollectionview");
	
	this.fullscreenButton = jQuery('#go-fullscreen');	
	this.fullscreenButton.on('click', function(){
		self.element.css('display', 'block');
		self.goFullScreen();
	});
};
Fullscreencollectionview.prototype = Object.create(Component.prototype);
Fullscreencollectionview.prototype.goFullScreen = function(){
	console.log("Fullscreen.goFullScreen()");
	var element = this.element[0];
	if(element.requestFullscreen) {
		console.log("requestFullscreen()");
	    element.requestFullscreen();
	  } else if(element.mozRequestFullScreen) {
		console.log("mozRequestFullScreen()");
	    element.mozRequestFullScreen();
	  } else if(element.webkitRequestFullscreen) {
		console.log("webkitRequestFullscreen()");
	    element.webkitRequestFullscreen();
	  } else if(element.msRequestFullscreen) {
		console.log("msRequestFullscreen()");
	    element.msRequestFullscreen();
	  }
};