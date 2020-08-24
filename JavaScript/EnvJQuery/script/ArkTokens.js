
if ( !('ARKBase' in window) ){
	console.error('ArkBase init error. You should load ArkBase.js first!');
} else {
	ARKBase.addModule('ArkIdea', 
		{ type: 'ArkIdeaType', id: 'Type'},
		{ type: 'ArkIdeaType', id: 'Member'},
		{ type: 'ArkIdeaType', id: 'Tag'},
		{ type: 'ArkIdeaType', id: 'Constant'},
		
		{ type: 'ArkIdeaTag', id: 'Val'},
		{ type: 'ArkIdeaTag', id: 'Integer', info : {owner: 'Val'}},
		{ type: 'ArkIdeaTag', id: 'Real', info : {owner: 'Val'}},
		{ type: 'ArkIdeaTag', id: 'Ref', info : {owner: 'Val'}},

		{ type: 'ArkIdeaTag', id: 'Coll'},
		{ type: 'ArkIdeaTag', id: 'One', info : {owner: 'Coll'}},
		{ type: 'ArkIdeaTag', id: 'Arr', info : {owner: 'Coll'}},
		{ type: 'ArkIdeaTag', id: 'Set', info : {owner: 'Coll'}},
		{ type: 'ArkIdeaTag', id: 'Map', info : {owner: 'Coll'}},
	);

	ARKBase.addModule('ArkModel', 
		{ type: 'ArkIdeaType', id: 'Entity'},
		{ type: 'ArkIdeaMember', id: 'Id', info : {owner: 'ArkIdeaType__Entity'}},
		{ type: 'ArkIdeaMember', id: 'PrimaryType', info : {owner: 'ArkIdeaType__Entity'}},
		{ type: 'ArkIdeaMember', id: 'Owner', info : {owner: 'ArkIdeaType__Entity'}},
		{ type: 'ArkIdeaMember', id: 'Tags', info : {owner: 'ArkIdeaType__Entity'}},
	);

	ARKBase.addModule('ArkNative', 
		{ type: 'ArkIdeaType', id: 'Cmd'},
		{ type: 'ArkIdeaTag', id: 'String', info : {owner: 'ArkIdea_Val'}},
		{ type: 'ArkIdeaTag', id: 'Text', info : {owner: 'ArkIdea_Val'}},
		{ type: 'ArkIdeaTag', id: 'Date', info : {owner: 'ArkIdea_Val'}},
		{ type: 'ArkIdeaTag', id: 'Object', info : {owner: 'ArkIdea_Val'}},
	);


	ARKBase.addModule('ArkDialog', 
		{ type: 'ArkNativeCmd', id: 'Chk'},
		{ type: 'ArkNativeCmd', id: 'Get'},
		{ type: 'ArkNativeCmd', id: 'Set'},
		{ type: 'ArkNativeCmd', id: 'Add'},
		{ type: 'ArkNativeCmd', id: 'Del'},
	);


	ARKBase.addModule('ArkDecorate', 		
		{ type: 'ArkIdeaTag', id: 'State'},
		{ type: 'ArkIdeaTag', id: 'StateHidden', info : {owner: 'State'}},
		{ type: 'ArkIdeaTag', id: 'StateDisabled', info : {owner: 'State'}},
		{ type: 'ArkIdeaTag', id: 'StateActive', info : {owner: 'State'}},
		{ type: 'ArkIdeaTag', id: 'StateHighlighted', info : {owner: 'State'}},
	);
	
	ARKBase.addModule('ArkEvent', 
		{ type: 'ArkIdeaTag', id: 'Level'},
		{ type: 'ArkIdeaTag', id: 'LevelCritical', info : {owner: 'Level'}},
		{ type: 'ArkIdeaTag', id: 'LevelError', info : {owner: 'Level'}},
		{ type: 'ArkIdeaTag', id: 'LevelWarning', info : {owner: 'Level'}},
		{ type: 'ArkIdeaTag', id: 'LevelInfo', info : {owner: 'Level'}},
		{ type: 'ArkIdeaTag', id: 'LevelOK', info : {owner: 'Level'}},
		{ type: 'ArkIdeaTag', id: 'LevelTrace', info : {owner: 'Level'}},
		{ type: 'ArkIdeaTag', id: 'LevelDebug', info : {owner: 'Level'}},
	);
	
	ARKBase.addModule('ArkLogic', 
		{ type: 'ArkNativeCmd', id: 'Init'},
		{ type: 'ArkNativeCmd', id: 'Begin'},
		{ type: 'ArkNativeCmd', id: 'Process'},
		{ type: 'ArkNativeCmd', id: 'End'},
		{ type: 'ArkNativeCmd', id: 'Release'},
	);
	
	ARKBase.addModule('ArkStore', 
		{ type: 'ArkNativeCmd', id: 'IsChanged'},
		{ type: 'ArkNativeCmd', id: 'Undo'},
		{ type: 'ArkNativeCmd', id: 'Redo'},
		{ type: 'ArkNativeCmd', id: 'Commit'},
		{ type: 'ArkNativeCmd', id: 'Rollback'},
	);

	console.log('ArkTokens 01 initialized.');
}

