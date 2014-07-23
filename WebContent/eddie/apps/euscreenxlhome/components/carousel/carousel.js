var Carousel = function(options){
	Component.apply(this, arguments);
	
	this.carouselElement = jQuery("#carousel-example-generic");
	this.carouselElement.carousel({
		interval: 8000
	});
};
Carousel.prototype = Object.create(Component.prototype);