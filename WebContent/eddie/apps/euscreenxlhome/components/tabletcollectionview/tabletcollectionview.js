var Tabletcollectionview = function(options){
	console.log("Tabletcollectionview()");
	var self = this;
	this.lock = false;
	
	Component.apply(this, arguments);
	jQuery(".more a").remove();
	jQuery(".more").addClass('loading')
	
	jQuery(window).on('scroll', function(){
		if(!self.lock){
			var _docHeight = (document.height !== undefined) ? document.height : document.body.offsetHeight;
			var difference = _docHeight - jQuery(window).scrollTop();
			var wHeight = jQuery(window).height();
			console.log("DIFFERENCE: " + difference);
			if(difference < wHeight){
				self.lock = true;
				eddie.putLou('collectionview', 'createGrid()');
			}
		}
	});
};
Tabletcollectionview.prototype = Object.create(Component.prototype);
Tabletcollectionview.prototype.unlock = function(){
	this.lock = false;
}
