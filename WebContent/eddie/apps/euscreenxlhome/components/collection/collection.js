var Collection = function(options){
	console.log("Collection()");
};

Collection.prototype = Object.create(Component.prototype);
Collection.prototype.items = [];
Collection.prototype.getByFilter = function(filterFn){
	return this.items.filter(filterFn)[0];
};
Collection.prototype.getByIndex = function(index){
	return this.items[index];
};
Collection.prototype.add = function(index, item){
	console.log("Collection.prototype.add(" + index + ", " + JSON.stringify(item) + ")");
	var params = {
		index: index,
		item: item
	};
	eddie.putLou('', 'addItem(' + JSON.stringify(params) + ')');
};
Collection.prototype.remove = function(item){
	console.log("Collection.prototype.remove()");
	eddie.putLou('', 'removeItem(' + JSON.stringify(item) + ')');
};
Collection.prototype.update = function(json){
	console.log("Collection.update(" + json + ")");
	var self = this;
	var params = JSON.parse(json);
	
	for(key in params){
		switch(key){
			case "added":
				var update = params[key];
				if(update.index > -1){
					var tail = this.items.splice(update.index, this.items.length - update.index);
					this.items.push.apply(this.items, update.items);
					this.items.push.apply(this.items, tail);
				}else{
					this.items.push.apply(this.items, update.items);
				}
				$(this).trigger('items-added', [update.index, update.items]);
				break;
			case "removed":
				var update = params[key];
				this.items.splice(update.index, 1);
				$(this).trigger('items-removed', [update.items]);
				break;
		}
	}
};
