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
	this.loadingElement = this.element.find('.loading');
	this.useNativeFullScreen = true;
	
	this.currentChunk = null;
	this.currentGrid = null;
	this.currentSize = null;
		
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
	this.exitFullscreenButton.on('click', function(event){
		event.preventDefault();
		jQuery(this).removeClass("active");
		self.leaveFullScreen();
	});
	
	this.infoContent = jQuery("#info-content");
	
	this.element.find('a[data-toggle="tab"]').on('shown.bs.tab', function(event){
		self.selectionChanged.apply(self, [event]);
	});
	
	this.element.find('.subcats a').on('click', function(event){
		event.preventDefault();
		self.element.find('.subcats .tab-pane.active li.active').removeClass('active');
		jQuery(this).parent().addClass('active');
		self.selectionChanged.apply(self, [event]);
	});
	
	this.selectionChanged();
	
	jQuery('button[data-overlay]').popupOverlayJS({
		$overlayContents : jQuery('.overlaycontent'),
		contentOverlayIdAttr : 'data-overlay'
	});
};
Collectionviewer.prototype = Object.create(Component.prototype);
Collectionviewer.prototype.device = "desktop";
Collectionviewer.prototype.selectionChanged = function(event){
	this.currentlyActiveCategory = jQuery('ul.nav-tabs li.active a').attr('href');
	this.infoContent.html(jQuery(this.currentlyActiveCategory + '-text').html());
	
	this.element.find('.item-chunk').remove();
	
	var subparams = {};
	
	this.currentlyActiveCategory = this.currentlyActiveCategory.replace("#", "");
	var field = 'topic';
	
	if(this.element.find('.subcats .tab-pane.active li.active a').data('topic')){
		subparams['topic'] = this.element.find('.subcats .tab-pane.active li.active a').data('topic');
	}
	
	if(this.element.find('.subcats .tab-pane.active li.active a').data('id')){
		subparams['id'] = this.element.find('.subcats .tab-pane.active li.active a').data('id');
	}
	
	if(this.element.find('.subcats .tab-pane.active li.active a').data('series')){
		subparams['series'] = this.element.find('.subcats .tab-pane.active li.active a').data('series');
	}
	
	if(this.element.find('.subcats .tab-pane.active li.active a').data('provider')){
		subparams['provider'] = this.element.find('.subcats .tab-pane.active li.active a').data('provider');
	}
	
	
	var params = {
		'category': this.currentlyActiveCategory,
		'params': subparams
	};
	
	this.loadingElement.show();
	eddie.putLou('', 'changeSelection(' + JSON.stringify(params) + ')')
};
Collectionviewer.prototype.initTooltips = function(){
	console.log("Collectionviewer.initTooltips()");
	this.element.find('button[data-toggle]').tooltip();
};
Collectionviewer.prototype.listenToScroll = function(element){
	var self = this;
	this.showMoreButton.find('a').remove();
	this.showMoreButton.addClass('loading');
	
	console.log(element);
	
	element.off('scroll').on('scroll', function(){
		if(!self.lock){
			var _docHeight = (document.height !== undefined) ? document.height : document.body.offsetHeight;
			var difference = _docHeight - jQuery(element).scrollTop();
			var wHeight = element.height();
			
			if((difference / 2) <= wHeight){
				self.lock = true;
				self.createGrid();
			}
		}
	});
};
Collectionviewer.prototype.stopListeningToScroll = function(element){
	this.showMoreButton.removeClass('loading');
	this.showMoreButton.append('<a href="#">SHOW MORE</a>');
	element.off('scroll');
};
Collectionviewer.prototype.setDevice = function(device){
	var self = this;
	console.log("setDevice(" + device + ")");
	this.device = device;
	if(device == "tablet"){
		this.listenToScroll(jQuery(window));
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
	this.currentsize = size;
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
	this.loadingElement.hide();
};
Collectionviewer.prototype.moreAvailable = function(){
	this.showMoreButton.show();
};
Collectionviewer.prototype.endReached = function(){
	this.showMoreButton.hide();
};
Collectionviewer.prototype.leaveFullScreen = function(){
	var element = this.element[0];
	this.element.removeClass('fullscreen');
	jQuery('body').removeClass('fullscreenactive');
	this.stopListeningToScroll(this.element);
	if(this.device != "tablet" && this.useNativeFullScreen && (element.requestFullscreen || element.mozRequestFullScreen || element.webkitRequestFullscreen || element.msRequestFullscreen)){
		
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
	}
	this.collectionElement.html('');
	this.loadingElement.show();
	this.gridSize = 4;
	this.createGrid();
	this.fullscreenButton.removeClass("active");
};
Collectionviewer.prototype.goFullScreen = function(){
	this.loadingElement.show();
	var self = this;
	var element = this.element[0];
	
	this.element.addClass('fullscreen');
	jQuery('body').addClass('fullscreenactive');
	
	this.listenToScroll(this.element);
	
	if(this.device != "tablet" && this.useNativeFullScreen && (element.requestFullscreen || element.mozRequestFullScreen || element.webkitRequestFullscreen || element.msRequestFullscreen)){
		this.element.on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function(event){
			
			jQuery(this).off('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange');
			eddie.putLou('', 'fullscreenChanged()');
			self.collectionElement.html('');
			self.gridSize = 6;
			self.createGrid();
			
			var element = this;
					
			//Weird hack for this, event is triggered twice after each other in some browser because they support both the prefixed and the none-prefixed event.
			//So I start listening after the second event has triggered.
			setTimeout(function(){
				jQuery(element).on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function(event){
					self.element.removeClass('fullscreen');
					jQuery(element).off('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange');
					eddie.putLou('', 'fullscreenChanged()');
					self.collectionElement.html('');
					self.gridSize = 4;
					setTimeout(function(){
						self.createGrid();
					}, 1000)
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
		
		if(this.device == "tablet"){
			self.gridSize = 4;
		}else{
			self.gridSize = 6;
		}
		self.createGrid();
	}
};