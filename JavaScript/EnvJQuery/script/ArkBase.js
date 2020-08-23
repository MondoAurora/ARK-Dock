
if ( !("ARKBase" in window) ){
	function ArkBase () {
		this.getPath = function(root, rel) {
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
			this.scriptPath = this.getPath(r, '/..');
			this.resourcePath = this.getPath(this.scriptPath, '/../res');
		} else {
			console.error('ArkBase path init error.');
		}
		
		this.Tokens = {};
		
		this.addModule = function(module, ... tokens) {
			var mod = this.Tokens[module];
			
			if ( !mod ) {
				mod = {};
				this.Tokens[module] = mod;
				
				for (t of tokens) {
					var id = t.type + '_' + t.id;
					var entity = t.entity ? t.entity : id;
					mod[id] = entity;
				}
			} else {
				console.error('Multiple declaration of module ' + module);
			}	
		}		
	}
	
	ARKBase = new ArkBase();
} else {
	console.error('ArkBase init error. You should load ArkBase.js only once!');
}
