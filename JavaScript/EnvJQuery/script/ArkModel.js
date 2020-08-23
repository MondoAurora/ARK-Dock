
if ( !("ARKBase" in window) ){
	console.error('ArkBase init error. You should load ArkBase.js first!');
} else {
	ARKBase.addModule('ArkModel', 
		{ type: 'ArkCmd', id: 'ArkDialogChk'},
		{ type: 'ArkCmd', id: 'ArkDialogGet'},
		{ type: 'ArkCmd', id: 'ArkDialogSet'},
		{ type: 'ArkCmd', id: 'ArkDialogAdd'},
		{ type: 'ArkCmd', id: 'ArkDialogDel'},
		
		{ type: 'ArkCmd', id: 'ArkLogicInit'},
		{ type: 'ArkCmd', id: 'ArkLogicBegin'},
		{ type: 'ArkCmd', id: 'ArkLogicProcess'},
		{ type: 'ArkCmd', id: 'ArkLogicEnd'},
		{ type: 'ArkCmd', id: 'ArkLogicRelease'},
		
		{ type: 'ArkCmd', id: 'ArkStoreIsChanged'},
		{ type: 'ArkCmd', id: 'ArkStoreUndo'},
		{ type: 'ArkCmd', id: 'ArkStoreRedo'},
		{ type: 'ArkCmd', id: 'ArkStoreCommit'},
		{ type: 'ArkCmd', id: 'ArkStoreRollback'},
	);
}

function ArkModel () {
	this.data = {};
	this.orig = {};
	this.undo = [];
	
	this.Tokens = ARKBase.Tokens.ArkModel;

	this.selectEntity = function (... path) {
	    var entity = this.data;
		var coll = null;
		var val = null;
	
		for (member of path) {
			if ( !entity && !coll ) {
				return null;
			}
			
	        val = coll ? coll[member] : entity[member];
			if ( !val ) {
				return null;
			}
			
			coll = ( (typeof val === 'object') || $.isArray(val)) ? val : null;
			entity = coll ? null : this.data[val];
		}
	
		return val;
	}

	this.getValue = function (entity, key) {
		var e = this.data[entity];
		return e ? e[key] : null;
	}
	
	this.setValue = function (entity, key, val) {
		var e = this.data[entity];
		var ret = null;
		
		if ( e ) {
			ret = e[key];
			
			if ( val != ret ) {
				e[key] = val;
				
				$('[ark-dataEntity=' + entity + ']').trigger('arkEventReload', { e: entity, k : key, v: val } );
		//		$('[ark-dataEntity=' + entity + ']').('[ark-dataKey=' + key + ']').trigger('arkEventReload', { e: entity, k : key, v: val } );
			}
		}
		return ret;
	}
	
	console.log("Hello, world from ArkModel. 03");
}