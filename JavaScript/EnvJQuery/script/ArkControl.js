
if ( !('ARKBase' in window) ){
	console.error('ArkBase init error. You should load ArkBase.js first!');
} else {
	function ArkControl () {
		var Logics = {};
	}
	
	ARKBase.Control = new ArkControl();

	console.log('ArkControl 01 initialized.');
}