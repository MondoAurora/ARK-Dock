function ArkModel () {
	this.data = {};

	this.dustSelect = function (... path) {
	    var ret = data;
	
		for (member of path) {
	        ret = ret[member];
			if (!ret) {
				break;
			}
		}
	
		return ret;
	}

	function dustGet(entity, key) {
		var e = data[entity];
		return e ? e[key] : null;
	}

	function dustSet(entity, key, val) {
		var e = data[entity];
		
		if ( !entity ) {
			entity = global;
		}
		var ret = entity[key];
		entity[key] = val;
		
		$('[comp-id=' + id + ']').detach();
	
		$(".rtmsValueEdit").trigger('rtmsEventReload', { e: entity, k : key, v: val } );
	
		return ret;
	}
	console.log("Hello, world from ArkModel.");
}