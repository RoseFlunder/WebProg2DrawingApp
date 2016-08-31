var webSocket, tools, tool, canvas, ctx, previewCanvas, previewCtx, drawHistory = new Map();

function init() {
	webSocket = new WebSocket(
			"ws://localhost:8080/WebProg2DrawingApp/websocket/drawing");

	webSocket.onerror = onError;
	webSocket.onopen = onOpen;
	webSocket.onmessage = onMessage;
	webSocket.onclose = onClose;

	canvas = document.getElementById("canvas");
	ctx = canvas.getContext('2d');

	// Add the temporary canvas.
	var container = canvas.parentNode;
	previewCanvas = document.createElement('canvas');
	if (!previewCanvas) {
		alert('Error: I cannot create a new canvas element!');
		return;
	}

	previewCanvas.id = 'previewCanvas';
	previewCanvas.width = canvas.width;
	previewCanvas.height = canvas.height;
	container.appendChild(previewCanvas, canvas);

	previewCtx = previewCanvas.getContext('2d');

	previewCanvas.addEventListener('mousedown', ev_canvas, false);
	previewCanvas.addEventListener('mousemove', ev_canvas, false);
	previewCanvas.addEventListener('mouseup', ev_canvas, false);

	tools = {
		LINE : new lineTool(),
		CIRCLE : new circleTool()
	};

	tool = tools["LINE"];
}

function onOpen(event) {
	document.getElementById('messages').innerHTML = 'Connection established';
};

function onError(event) {
	alert(event.data);
};

function onClose(event) {
	document.getElementById('messages').innerHTML = 'Connection closed';
};

function onMessage(event) {
	var msg = JSON.parse(event.data);

	switch (msg.type) {
	case "CHATMESSAGE":
		console.log("received chat message");
		document.getElementById('messages').innerHTML += '<br />' + msg.user
				+ ": " + msg.content.message;
		var chatDiv = document.getElementById('messages');
		chatDiv.scrollTop = chatDiv.scrollHeight;
		break;
	case "DRAWMESSAGE":
		console.log("received draw message");
		drawHistory.set(msg.id, msg);
		
		var textnode = document.createTextNode(event.data);
		document.getElementById('history').appendChild(textnode);
		var linebreak = document.createElement("BR");
		document.getElementById('history').appendChild(linebreak);
		tools[msg.content.type].draw(msg.content);
		break;
		
	case "DELETEMESSAGE":
		console.log("received delete message");
		console.log(msg);
		// remove ids to delete from history object
		for (var id of msg.content.messageIdsToDelete) {
			console.log(id);
			drawHistory.delete(id);
		}
		
		// remove ids from history table in DOM
		
		// refresh canvas
		ctx.clearRect(0, 0, canvas.width, canvas.height);
		for (var [key, value] of drawHistory) {
			tools[value.content.type].draw(value.content);
		}
		
		break;

	default:
		break;
	}
};

// just random delete to test
function sendDeleteMessage(){
	var idsToDelete = [];
	console.log(drawHistory);
	for (var [key, value] of drawHistory) {
		console.log("key: " + key);
		if (Math.random() > 0.6)
			idsToDelete.push(key);
	}
	
	var msg = {
		user : "demo",
		type : "DELETEMESSAGE",
		content : {
			messageIdsToDelete : idsToDelete
		}
	}
	
	console.log("Sending delete message:");
	console.log(idsToDelete);
	
	webSocket.send(JSON.stringify(msg));
}

function selectTool(name) {
	tool = tools[name];
}

function ev_canvas(ev) {
	// Firefox
	if (ev.layerX || ev.layerX == 0) {
		ev._x = ev.layerX;
		ev._y = ev.layerY;
	}

	// Call the event handler of the tool
	var func = tool[ev.type];
	if (func) {
		func(ev);
	}
}

function lineTool() {
	var tool = this;
	var started = false;

	var x1, x2, y1, y2;

	// This is called when you start holding down the mouse button.
	// This starts the pencil drawing.
	this.mousedown = function(ev) {
		x1 = ev._x;
		y1 = ev._y;
		tool.started = true;
	};

	this.mousemove = function(ev) {
		if (tool.started) {
			previewCtx.clearRect(0, 0, previewCanvas.width,
					previewCanvas.height);

			previewCtx.beginPath();
			previewCtx.moveTo(x1, y1);
			previewCtx.lineTo(ev._x, ev._y);
			previewCtx.stroke();
			previewCtx.closePath();
		}
	};

	// This is called when you release the mouse button.
	this.mouseup = function(ev) {
		if (tool.started) {
			tool.started = false;
			previewCtx.clearRect(0, 0, previewCanvas.width,
					previewCanvas.height);

			x2 = ev._x;
			y2 = ev._y;

			var msg = {
				user : "demo",
				type : "DRAWMESSAGE",

				content : {
					type : "LINE",
					content : {
						x1 : x1,
						y1 : y1,
						x2 : x2,
						y2 : y2
					}
				}
			};
			console.log(msg);
			webSocket.send(JSON.stringify(msg));
		}
	};

	this.draw = function(drawMessage) {
		content = drawMessage.content;
		ctx.beginPath();
		ctx.moveTo(content.x1, content.y1);
		ctx.lineTo(content.x2, content.y2);
		ctx.closePath();
		ctx.stroke();
	}
}

function circleTool() {
	var tool = this;
	var started = false;

	var x1, x2, y1, y2;

	this.mousedown = function(ev) {
		ctx.beginPath();
		ctx.moveTo(ev._x, ev._y);
		x1 = ev._x;
		y1 = ev._y;
		tool.started = true;
	};
	
	this.mousemove = function(ev) {
		if (tool.started) {
			previewCtx.clearRect(0, 0, previewCanvas.width,
					previewCanvas.height);
			
			x2 = ev._x;
			y2 = ev._y;

			var radius = parseInt(Math.sqrt(Math.pow(Math.abs(x2 - x1), 2)
					+ Math.pow(Math.abs(y2 - y1), 2)));

			previewCtx.beginPath();
			previewCtx.arc(x1, y1, radius, 0, Math.PI * 2);
			previewCtx.closePath();
			previewCtx.stroke();
		}
	};

	this.mouseup = function(ev) {
		if (tool.started) {
			tool.started = false;
			previewCtx.clearRect(0, 0, previewCanvas.width,
					previewCanvas.height);
			x2 = ev._x;
			y2 = ev._y;

			var radius = parseInt(Math.sqrt(Math.pow(Math.abs(x2 - x1), 2)
					+ Math.pow(Math.abs(y2 - y1), 2)));

			var msg = {
				user : "demo",
				type : "DRAWMESSAGE",

				content : {
					type : "CIRCLE",
					content : {
						x : x1,
						y : y1,
						radius : radius
					}
				}
			};
			console.log(msg);
			webSocket.send(JSON.stringify(msg));
		}
	};

	this.draw = function(drawMessage) {
		content = drawMessage.content;
		ctx.beginPath();
		ctx.arc(content.x, content.y, content.radius, 0, Math.PI * 2);
		ctx.closePath();
		ctx.stroke();
	}
}

function send() {
	var textBox = document.getElementById("usermsg");
	var nameBox = document.getElementById("username");
	
	var msg = {
		user : nameBox.value,
		type : "CHATMESSAGE",

		content : {
			message : textBox.value
		}
	};
	textBox.value = "";
	
	webSocket.send(JSON.stringify(msg));
};

function onKeyPressed(ev) {
	var e = ev || event;
	if(e.keyCode == 13) {
		send();
	}
	document.getElementById("messages").scrollTop = document.getElementById("messages").scrollHeight;
};
