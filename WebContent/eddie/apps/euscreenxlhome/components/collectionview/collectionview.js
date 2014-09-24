var Collectionview = function(options){
	var self = this;
	console.log("Collectionview()");
	Component.apply(this, arguments);
	
	this.element = jQuery('#collectionview');
	this.collectionElement = this.element.find('.items');
	this.itemTemplate = this.element.find('#item-template').text();
	this.chunkContainerTemplate = jQuery('#chunk-template').text();
	this.showMoreButton = jQuery('body .row .more a');
	this.lock = false;
	
	this.currentChunk = null;
	this.currentGrid = null;
		
	this.showMoreButton.on('click', function(event){
		event.preventDefault();
		self.createGrid();
	});
};
Collectionview.prototype = Object.create(Component.prototype);
Collectionview.prototype.device = "desktop";
Collectionview.prototype.setDevice = function(device){
	var self = this;
	console.log("setDevice(" + device + ")");
	this.device = device;
	if(device == "tablet"){
		jQuery(".more a").remove();
		jQuery(".more").addClass('loading')
		
		jQuery(window).on('scroll', function(){
			if(!self.lock){
				var _docHeight = (document.height !== undefined) ? document.height : document.body.offsetHeight;
				var difference = _docHeight - jQuery(window).scrollTop();
				var wHeight = jQuery(window).height();
				
				if((difference / 2) <= wHeight){
					self.lock = true;
					self.createGrid();
				}
			}
		});
	}
};
Collectionview.prototype.createGrid = function(){
	console.log("Collectionview.prototype.createGrid()");
	this.currentChunk = jQuery(this.chunkContainerTemplate);
	new noterik.layout.squared({element: this.currentChunk});
	this.currentGrid = this.currentChunk.data('layout').createGrid(4);
	var size = 0;
	
	for(var i = 0; i < this.currentGrid.length; i++){
		var row = this.currentGrid[i];
		size += row.length;
	}
	
	this.collectionElement.append(this.currentChunk);
	eddie.putLou('', 'setGridSize(' + size + ')');
	
};
Collectionview.prototype.appendItems = function(data){
	var self = this;
	var items = JSON.parse(data);
	
	for(var i = 0; i < items.length; i++){
		var item = items[i];
		var itemElement = jQuery(_.template(this.itemTemplate, {item: item}));
		itemElement.on('click', 
			(function(item){
				return function(){
					if(self.device != "tablet"){
						eddie.putLou("", "playVideo(" + item.id + ")");
					}else{
						if(jQuery(this).data('touched')) {
		                    eddie.putLou("", "playVideo(" + item.id + ")");
		                }else{
		                	self.element.find('.media-item').data('touched', false);
		                	jQuery(this).data('touched', true);
		                }
					}
				}
			})(item)
		);
		this.currentChunk.append(itemElement);
	}
	
	this.lock = false;
	this.currentChunk.data('layout').create(this.currentGrid);
	$('#screen').css('visibility','visible');
};
Collectionview.prototype.endReached = function(){
	jQuery('body .row .more').hide();
};