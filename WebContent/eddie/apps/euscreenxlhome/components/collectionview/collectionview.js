var Collectionview = function(options){
	var self = this;
	console.log("Collectionview()");
	Component.apply(this, arguments);
	
	this.element = jQuery('#collectionview');
	this.collectionElement = this.element.find('.items');
	this.itemTemplate = this.element.find('#item-template').text();
	this.chunkContainerTemplate = jQuery('#chunk-template').text();
	this.showMoreButton = jQuery('body .row .more a');
	
	this.currentChunk = null;
	this.currentGrid = null;
		
	this.showMoreButton.on('click', function(event){
		event.preventDefault();
		self.createGrid();
	});
	
	
};
Collectionview.prototype = Object.create(Component.prototype);
Collectionview.prototype.createGrid = function(){
	$('#screen').css('visibility','hidden');
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
	console.log("Collectionview.appendItems(" + data + ")");
	
	var items = JSON.parse(data);
	
	for(var i = 0; i < items.length; i++){
		var item = items[i];
		this.currentChunk.append(_.template(this.itemTemplate, {item: item}));
	}
	
	this.currentChunk.data('layout').create(this.currentGrid);
		$('#screen').css('visibility','visible');
};
Collectionview.prototype.endReached = function(){
	this.showMoreButton.hide();
};