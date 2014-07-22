var Collectionview = function(options){
	var self = this;
	console.log("Collectionview()");
	Component.apply(this, arguments);
	
	this.element = jQuery('#collectionview');
	this.collectionElement = this.element.find('.items');
	this.itemTemplate = this.element.find('#item-template').text();
	this.showMoreButton = jQuery('body .row .more a');
	this.grid = null;
	
	console.log(this.showMoreButton);
	
	this.showMoreButton.on('click', function(event){
		event.preventDefault();
		self.createGrid();
	});

	new noterik.layout.squared({element: this.collectionElement});
};
Collectionview.prototype = Object.create(Component.prototype);
Collectionview.prototype.createGrid = function(){
	console.log("Collectionview.prototype.createGrid()");
	this.grid = this.collectionElement.data('layout').createGrid(4);
	var size = 0;;
	
	for(var i = 0; i < this.grid.length; i++){
		var row = this.grid[i];
		size += row.length;
	}
	eddie.putLou('', 'setGridSize(' + size + ')');
};
Collectionview.prototype.appendItems = function(data){
	console.log("Collectionview.appendItems(" + data + ")");
	
	var self = this;
	data = $(data);
	var children = data.children(":not(properties)");
	
	for(var i = 0; i < children.length; i++){
		var xml = children[i];
		var item = {
			title: jQuery(xml).find('title').text(),
			description: jQuery(xml).find('description').text(),
			screenshot: jQuery(xml).find('screenshot').text()
		}
		var html = _.template(this.itemTemplate, {item: item});
		this.collectionElement.append(html);
	}
	this.collectionElement.data('layout').create(this.grid);
};