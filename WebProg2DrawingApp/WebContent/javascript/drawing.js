var webSocket, tools, tool, canvas, ctx;

function init() {
	webSocket = new WebSocket(
			"ws://localhost:8080/WebProg2DrawingApp/websocket/drawing");

	webSocket.onerror = onError;
	webSocket.onopen = onOpen;
	webSocket.onmessage = onMessage;
	webSocket.onclose = onClose;

	canvas = document.getElementById("canvas");
	ctx = canvas.getContext('2d');

	canvas.addEventListener('mousedown', ev_canvas, false);
	canvas.addEventListener('mousemove', ev_canvas, false);
	canvas.addEventListener('mouseup', ev_canvas, false);
	
	tools = {
		LINE : new toolLine()
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
		break;
	case "DRAWMESSAGE":
		console.log("received draw message");
		tools[msg.content.type].draw(msg.content);
		break;

	default:
		break;
	}
};

// The general-purpose event handler. This function just determines
// the mouse position relative to the <canvas> element
function ev_canvas(ev) {
	console.log(ev);
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

function toolLine() {
	var tool = this;
	var started = false;

	var x1, x2, y1, y2;

	// This is called when you start holding down the mouse button.
	// This starts the pencil drawing.
	this.mousedown = function(ev) {
		ctx.beginPath();
		ctx.moveTo(ev._x, ev._y);
		x1 = ev._x;
		y1 = ev._y;
		tool.started = true;
	};

	// This function is called every time you move the mouse. Obviously, it only
	// draws if the tool.started state is set to true (when you are holding down
	// the mouse button).
	// this.mousemove = function (ev) {
	// if (tool.started) {
	// ctx.lineTo(ev._x, ev._y);
	// ctx.stroke();
	// }
	// };

	// This is called when you release the mouse button.
	this.mouseup = function(ev) {
		if (tool.started) {
			// tool.mousemove(ev);
			tool.started = false;
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

function send() {
	var msg = {
		user : "demo",
		type : "CHATMESSAGE",

		content : {
			message : "beste"
		}
	};
	webSocket.send(JSON.stringify(msg));
};