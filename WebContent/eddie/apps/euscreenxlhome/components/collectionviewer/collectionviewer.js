var Collectionviewer = function(options){
	var self = this;
	console.log("Collectionview()");
	Component.apply(this, arguments);
	
	this.element = jQuery('#collectionviewer');
	this.collectionElement = this.element.find('.items');
	this.itemTemplate = this.element.find('#item-template').text();
	this.chunkContainerTemplate = jQuery('#chunk-template').text();
	this.showMoreButton = this.element.find('.more');
	this.lock = false;
	this.gridSize = 4;
	
	this.currentChunk = null;
	this.currentGrid = null;
		
	this.showMoreButton.on('click', function(event){
		event.preventDefault();
		self.createGrid();
	});
	
	this.fullscreenButton = jQuery('#go-fullscreen');	
	this.fullscreenButton.on('click', function(){
		self.element.css('display', 'block');
		self.goFullScreen();
	});
	
	this.exitFullscreenButton = jQuery('#exit-fullscreen');
	this.exitFullscreenButton.on('click', function(){
		self.leaveFullScreen();
	});
	
	this.infoContent = jQuery("#info-content");
	
	this.element.find('ul.nav-tabs li a').on('click', function(){
		var id = jQuery(this).attr('href');
		self.infoContent.html(jQuery(id + "-text").html());
	});
	
	var currentlyActiveTabId = jQuery('ul.nav-tabs li a').attr('href');
	this.infoContent.html(jQuery(currentlyActiveTabId + '-text').html());
	
	jQuery('button[data-overlay]').popupOverlayJS({
		$overlayContents : jQuery('.overlaycontent'),
		contentOverlayIdAttr : 'data-overlay'
	});
};
Collectionviewer.prototype = Object.create(Component.prototype);
Collectionviewer.prototype.device = "desktop";
Collectionviewer.prototype.initTooltips = function(){
	console.log("Collectionviewer.initTooltips()");
	this.element.find('button[data-toggle]').tooltip();
};
Collectionviewer.prototype.setDevice = function(device){
	var self = this;
	console.log("setDevice(" + device + ")");
	this.device = device;
	if(device == "tablet"){
		this.showMoreButton.find('a').remove();
		this.showMoreButton.addClass('loading')
		
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
Collectionviewer.prototype.createGrid = function(){
	console.log("Collectionviewer.prototype.createGrid()");
	this.currentChunk = jQuery(this.chunkContainerTemplate);
	this.currentChunk.attr('data-ntk-columns', this.gridSize);
	new noterik.layout.squared({element: this.currentChunk});
	console.log("GRID SIZE: " + this.gridSize);
	
	this.currentGrid = this.currentChunk.data('layout').createGrid(this.gridSize);
	
	console.log(this.currentGrid);
	var size = 0;
	
	for(var i = 0; i < this.currentGrid.length; i++){
		var row = this.currentGrid[i];
		size += row.length;
	}
	
	this.collectionElement.append(this.currentChunk);
	eddie.putLou('', 'setGridSize(' + size + ')');
	
};
Collectionviewer.prototype.appendItems = function(data){
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
Collectionviewer.prototype.endReached = function(){
	this.showMoreButton.hide();
};
Collectionviewer.prototype.leaveFullScreen = function(){
	if(this.device != "tablet"){
		if(document.webkitExitFullscreen) {
		    document.webkitExitFullscreen();
		} else if(document.mozCancelFullScreen) {
		    document.mozCancelFullScreen();
		} else if(document.exitFullscreen) {
		    document.exitFullscreen();
		} else if(document.msExitFullscreen) {
		    document.msExitFullscreen();
		}
	}else{
		eddie.putLou('', 'fullscreenChanged()');
		this.element.removeClass('fullscreentablet');
		self.collectionElement.html('');
		self.gridSize = 4;
		self.createGrid();
	}
};
Collectionviewer.prototype.goFullScreen = function(){
	var self = this;
	var element = this.element[0];
	
	if(this.device != "tablet"){
		this.element.on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function(event){
			
			jQuery(this).off('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange');
			eddie.putLou('', 'fullscreenChanged()');
			self.collectionElement.html('');
			self.gridSize = 8;
			self.createGrid();
			
			var element = this;
					
			//Weird hack for this, event is triggered twice after each other in some browser because they support both the prefixed and the none-prefixed event.
			//So I start listening after the second event has triggered.
			setTimeout(function(){
				jQuery(element).on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function(event){
					jQuery(element).off('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange');
					eddie.putLou('', 'fullscreenChanged()');
					self.collectionElement.html('');
					self.gridSize = 4;
					self.createGrid();
				});
			}, 1);
			
		});
		
		if(element.requestFullscreen) {
			element.requestFullscreen();
		} else if(element.mozRequestFullScreen) {
		    element.mozRequestFullScreen();
		} else if(element.webkitRequestFullscreen) {
		    element.webkitRequestFullscreen();
		} else if(element.msRequestFullscreen) {
			element.msRequestFullscreen();
		}
	}else{
		this.element.addClass('fullscreentablet');
		eddie.putLou('', 'fullscreenChanged()');
		self.collectionElement.html('');
		self.gridSize = 4;
		self.createGrid();
	}
};