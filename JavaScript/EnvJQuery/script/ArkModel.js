function ArkModel () {
	this.data = {};

	this.selectEntity = function (... path) {
	    var ret = data;
	
		for (member of path) {
	        ret = ret[member];
			if (!ret) {
				break;
			}
		}
	
		return ret;
	}

	function getValue(entity, key) {
		var e = data[entity];
		return e ? e[key] : null;
	}

	function setValue(entity, key, val) {
		var e = data[entity];
		
		if ( !entity ) {
			entity = global;
		}
		var ret = entity[key];
		entity[key] = val;
		
		$('[ark-dataEntity=' + entity + ']').trigger('arkEventReload', { e: entity, k : key, v: val } );
//		$('[ark-dataEntity=' + entity + ']').('[ark-dataKey=' + key + ']').trigger('arkEventReload', { e: entity, k : key, v: val } );
	
		return ret;
	}
	
	console.log("Hello, world from ArkModel. 01");
}