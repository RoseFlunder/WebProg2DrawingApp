var name = "Client" + parseInt(Math.random() * 1000);
        var webSocket = new WebSocket("ws://localhost:8080/WebProg2DrawingApp/websocket/drawing");
 
        webSocket.onerror = function(event) {
            onError(event)
        };
 
        webSocket.onopen = function(event) {
            onOpen(event)
        };
 
        webSocket.onmessage = function(event) {
            onMessage(event)
        };
       
        webSocket.onclose = function(event) {
            onClose(event)
        };
       
        function onClose(event){
            document.getElementById('messages').innerHTML
                = 'Connection closed';
        };
       
        function onMessage(event) {
            var msg = event.data;
            document.getElementById('messages').innerHTML
                += '<br />' + msg;
        };
 
        function onOpen(event) {
            document.getElementById('messages').innerHTML
                = 'Connection established';
        };
 
        function onError(event) {
            alert(event.data);
        };
       
        function send() {
        	var msg = {
        		user: "demo",
        		type : "CHATMESSAGE",
        	
        		content : {
        			message : "beste"
        		}
        	};
            webSocket.send(JSON.stringify(msg));
        };