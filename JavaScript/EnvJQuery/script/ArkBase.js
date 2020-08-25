
if ( !('ARKBase' in window) ){
	function ArkBase () {
		
		var Tokens = {};
		var TokenInfo = {};
		var Paths = {};
		
		function getPath(root, rel) {
			var path = null;
			
			if ( root ) {
				path = new URL(root + rel).href;
				if (path.slice(-1) === '/') {
					path = path.slice(0, -1);
				}
			}
			
			return path;
		}

		var r = document.currentScript.src;
		
		if ( r ) {
			Paths.script = getPath(r, '/..');
			Paths.res = getPath(Paths.script, '/../res');
		} else {
			console.error('ArkBase path init error.');
		}
		
		this.getTokens = function(module) {
			return Tokens[module];
		}
		
		this.addModule = function(module, ... tokens) {
			var mod = Tokens[module];
			
			if ( !mod ) {
				mod = {};
				Tokens[module] = mod;
				
				for (t of tokens) {
					var id = t.type + '_' + module + '_' + t.id;
					var entity = t.entity ? t.entity : id;
					mod[id] = entity;
					
					if ( t.info ) {
						TokenInfo[id] = t.info;
					}
				}
			} else {
				console.error('Multiple declaration of module ' + module);
			}	
		}		
	}
	
	ARKBase = new ArkBase();
	
	console.log('ArkBase 01 initialized.');
} else {
	console.error('ArkBase init error. You should load ArkBase.js only once!');
}
