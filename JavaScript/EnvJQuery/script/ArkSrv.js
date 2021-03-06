
if ( !('ARKBase' in window) ){
	console.error('ArkBase init error. You should load ArkBase.js first!');
} else {
	function ArkSrv () {
		var requestId = 0;
		
		function doSend(request) {
			request.beforeSend = function(jqXHR, settings ) {
				jqXHR.arkRequestId = ++requestId;
				jqXHR.arkMethod = request.method;
				jqXHR.arkURL = request.url;
				
				console.log('Request ' + jqXHR.arkRequestId + ' sending...');
			};
			
			$.ajax(request)  
			.done(function(data, textStatus, jqXHR) {
				console.log('Request ' + jqXHR.arkRequestId + ' done.');
			})
			.fail(function(jqXHR, textStatus, errorThrown ) {
				console.log('Request ' + jqXHR.arkRequestId + ' failed: ' + errorThrown);
			});
		}
		
		this.sendData = function(cmd, content, request) {			
			if ( !request ) {
				request = {};
			}
			
			request.method = 'POST';
			request.url = '/cmd/send';
			
			var data = {
				cmd: cmd,
				data: content
			}

			request.data = JSON.stringify(data);
		
			doSend(request);
		};
		
		this.loadResource = function(request) {
			request.method = 'GET';
			doSend(request);
		}		
	}
	
	ARKBase.Srv = new ArkSrv();

	console.log('ArkSrv 01 initialized.');
}