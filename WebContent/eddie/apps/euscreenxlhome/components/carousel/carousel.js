var Carousel = function(options){
	Component.apply(this, arguments);
	
	this.contentLoader = eddie.getComponent('contentloader');
	this.carouselElement = jQuery("#carousel-example-generic");
	
	this.contentLoader.on('contents-changed', function(contents){
		if(contents['carousel']){
			this.renderCarousel();
		}
	});
	
	this.renderCarousel();
	
};
Carousel.prototype = Object.create(Component.prototype);
Carousel.prototype.renderCarousel = function(){
	console.log("renderCarousel()");
	var self = this;
	var contents = this.contentLoader.get('contents').carousel;
	console.log("CONTENTS: " + contents);
	var $contents = jQuery(contents);
	
	var buttons = [];
	var i = 0;
	this.carouselElement.find('.carousel-indicators').html('');
	
	$contents.each(function(key, item){
		if(jQuery(item).hasClass('item')){
			var button = jQuery('<li data-target="#carousel-example-generic" data-slide-to="' + i + '" ></li>');
			if(i === 0)
				button.addClass('active');
			
			self.carouselElement.find('.carousel-indicators').append(button);
	        i++;
		}
	});
	
	this.carouselElement.find('.carousel-inner').html(contents);
	this.carouselElement.carousel({
		interval: 16000
	});
};

