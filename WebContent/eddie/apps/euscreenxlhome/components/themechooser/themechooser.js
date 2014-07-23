var Themechooser = function(options){
	Component.apply(this, arguments);
	
	this.element = jQuery('#themechooser');
	
	this.element.find('a').on('click', function(event){
		event.preventDefault();
		eddie.putLou('setTopic(' + jQuery(this).data('topic') + ')');
	});
};
Themechooser.prototype = Object.create(Component.prototype);