
if ( !('ARKBase' in window) ){
	console.error('ArkBase init error. You should load ArkBase.js first!');
} else {
	function ArkView () {
		this.domFragments = {};
	}
	
	ARKBase.View = new ArkView();

	console.log('ArkView 01 initialized.');
}