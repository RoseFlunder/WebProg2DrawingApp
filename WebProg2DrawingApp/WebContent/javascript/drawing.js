var webSocket, tools, tool, canvas, ctx, previewCanvas, previewCtx, animator, drawHistory = new Map();
var selectedHistoryElement;

function init() {
	animator = new animator();
	var person = prompt("Please enter your name", "User");
		
	var uri = "ws://" + window.location.host + window.location.pathname + "websocket/drawing/" + person;
	console.log(uri);
	
	webSocket = new WebSocket(uri);
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
	
	canvasArea.onkeydown = function(ev){
		console.log("key down event");
		console.log(tools['POLYGON'].keypressed);
		if(ev.ctrlKey && !tools['POLYGON'].keypressed){
			// console.log("keydown ctrl");
			tools['POLYGON'].keypressed = true;
		}
	}

	canvasArea.onkeyup = function(ev){
		console.log("key up event");
		console.log(tools['POLYGON'].keypressed);
		if(ev.keyCode == 17 && tools['POLYGON'].keypressed){
			// console.log("keyup ctrl");
			tools['POLYGON'].keypressed = false;
			tools['POLYGON'].endPolygon();
		}
	}
	
	previewCanvas.tabIndex = 1000;
	
	document.addEventListener('onkeydown', ev_canvas, false);
	document.addEventListener('onkeyup', ev_canvas, false);

	tools = {
		LINE : new lineTool(),
		CIRCLE : new circleTool(),
		RECTANGLE : new rectangleTool(),
		POLYGON : new polygonTool()
	};

	tool = tools["LINE"];
}

function onOpen(event) {
	document.getElementById('messages').innerHTML = 'Connection established';
};

function onError(event) {
	alert(event.data);
	console.log(event.data);
};

function onClose(event) {
	document.getElementById('messages').innerHTML += '<br />' + 'Connection closed';
};

function onMessage(event) {
	var msg = JSON.parse(event.data);

	switch (msg.type) {
	case "CHATMESSAGE":
		console.log("received chat message");
		var chatDiv = document.getElementById('messages');
		
		var textNode = document.createTextNode(msg.user + ' (' + convertMillisToFormattedTime(msg.timestamp) + ')'
				+ ": " + msg.content.message);
		var divChatMsg = document.createElement("div");
		divChatMsg.appendChild(textNode);
		chatDiv.appendChild(divChatMsg);
		chatDiv.scrollTop = chatDiv.scrollHeight;
		break;
	case "DRAWMESSAGE":
//		console.log("received draw message");
//		console.log(msg);
		if (!drawHistory.has(msg.id)){
			drawHistory.set(msg.id, msg);
			
			var msgType = msg.content;
			var msgContent = msgType.content;
			
			var spanText = msg.user + " | " + convertMillisToFormattedTimestamp(msg.timestamp) + " | " + msgType.type;
			var divnode = document.createElement("div");
			divnode.setAttribute("onclick", "clickOnDiv(this)");
			divnode.setAttribute("class", "deActiveDiv");
			divnode.setAttribute("id", msg.id);
			switch(msgType.type){
				case "CIRCLE":
					spanText += " | (" + msgContent.x + "/" + msgContent.y + ") radius:" + msgContent.radius;
				break;
				case "LINE":
					spanText += " | (" + msgContent.x1 + "/" + msgContent.y1 + ")(" + msgContent.x2 + "/" + msgContent.y2 + ")";
				break;
				case "RECTANGLE":
					spanText += " | (" + msgContent.x + "/" + msgContent.y + ") widht:" + msgContent.width + " height:" + msgContent.height;
				break;
				case "POLYGON":
					spanText += " | points: " + msgContent.xPoints.length;
				break;
			}

			var textnode = document.createTextNode(spanText);
			divnode.appendChild(textnode);
			document.getElementById('history').appendChild(divnode);
			tools[msg.content.type].draw(msg.content);
		} else {
			// TODO: incoming animation start or stop message
			console.log("Animation message");
			console.log(msg);
			drawHistory.set(msg.id, msg);
			redrawHistoryOnCanvas();
		}
		
		break;
		
	case "DELETE_RESPONSE_MESSAGE":
		console.log("received delete message");
		console.log(msg);
		// remove ids to delete from history object		
		for (var id of msg.content.deletedIds) {
			console.log(id);
			drawHistory.delete(id);
			document.getElementById("history").removeChild(document.getElementById(id));
		}
		
		// refresh canvas
		redrawHistoryOnCanvas();
		
		break;
		
	case "REGISTER_USERNAME_MESSAGE":
		var nameBox = document.getElementById("username");
		nameBox.value = msg.user;
		break;

	default:
		break;
	}
};

function getRandom(min, max) {
	  return Math.random() * (max - min) + min;
}

function redrawHistoryOnCanvas(){
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	for (var [key, value] of drawHistory) {
		tools[value.content.type].draw(value.content);
	}
}

function animator(){
	this.isRunning = false;
	this.elementsToAnimate = new Map();
	
	this.addElement = function (msg){
		this.elementsToAnimate.set(msg.id, msg);
		if (!this.isRunning){
			this.isRunning = true;
			this.animate();
		}
	};
	
	this.removeElement = function(msg){
		this.elementsToAnimate.delete(msg.id);
		if (this.elementsToAnimate.size == 0){
			this.isRunning = false;
		}
	}
	
	this.animate = function(){
		if (this.elementsToAnimate.size > 0){
			console.log("redraw");
			
			for (var [key, value] of this.elementsToAnimate) {
				tools[value.content.type].onAnimate(value.content);
				webSocket.send(JSON.stringify(value));
			}
			
			window.requestAnimationFrame(function() {
				this.animate();
	        }.bind(this));
		}
	};
}

function convertMillisToFormattedTimestamp(millis){
	var d = new Date(millis);
	return d.toLocaleDateString() + " " + d.toLocaleTimeString();
}

function convertMillisToFormattedTime(millis){
	var d = new Date(millis);
	return d.toLocaleTimeString();
}

function animateSelected(){
	// TODO: check if selected is own element
	var msg = drawHistory.get(selectedHistoryElement.getAttribute("id"));
	var drawMsg = msg.content;
	drawMsg.animate = true;
	tools[drawMsg.type].setAnimationParams(drawMsg);
	
	animator.addElement(msg);
}

function stopAnimateSelected(){
	// TODO: check if selected is own element
	var msg = drawHistory.get(selectedHistoryElement.getAttribute("id"));
	msg.content.animate = false;	
	animator.removeElement(msg);
}

function clickOnDiv(element){
	if (selectedHistoryElement)
		selectedHistoryElement.setAttribute("class", "deActiveDiv");
	
	element.setAttribute("class", "activeDiv");
	selectedHistoryElement = element;
}

function deleteSelected(mode){
	var selectedId = selectedHistoryElement.getAttribute("id");	
	
	var msg = {
		user : document.getElementById("username").value,
		type : "DELETE_REQUEST_MESSAGE",
		content : {
			drawMessageId : selectedId,
			mode : mode
		}
	}
		
	console.log("Sending delete message:");
	
	webSocket.send(JSON.stringify(msg));
}


function selectTool(name) {
	tool = tools[name];
	previewCanvas.focus();
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

function getColorFromRGBA(color){
	var alpha = (color >> 24) & 0xFF;
	var red = (color >> 16) & 0xFF;
	var green = (color >> 8) & 0xFF;
	var blue = (color >> 0) & 0xFF;
	
	return 'rgba(' + red + ',' + green + ',' + blue + ',' + alpha + ')';
}

function sendDrawMessage(content){
	var hex = 'FF' + document.getElementById('lineColorPicker').value.substring(1);
	content.lineColor = parseInt(hex, 16);
	
	if (document.getElementById('useFillColor').checked){
		hex = 'FF' + document.getElementById('fillColorPicker').value.substring(1);
		content.fillColor = parseInt(hex, 16);
	}

	var msg = {
		user : document.getElementById("username").value,
		type : "DRAWMESSAGE",

		content : content
	};
	console.log(msg);
	webSocket.send(JSON.stringify(msg));
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
			
			var content = {
				type : "LINE",
				content : {
					x1 : x1,
					y1 : y1,
					x2 : x2,
					y2 : y2
				}
			};
			
			sendDrawMessage(content);
		}
	};

	this.draw = function(drawMessage) {
		content = drawMessage.content;
		ctx.strokeStyle = getColorFromRGBA(drawMessage.lineColor);
		ctx.beginPath();
		ctx.moveTo(content.x1, content.y1);
		ctx.lineTo(content.x2, content.y2);
		ctx.closePath();
		ctx.stroke();
	}
	
	this.setAnimationParams = function (drawMessage){
		content = drawMessage.content;
		
		content.vx1 = Math.random() * 5;
		content.vy1 = Math.random() * 5;
		content.vx2 = Math.random() * 5;
		content.vy2 = Math.random() * 5;
	}
	
	this.onAnimate = function(drawMessage){
		if (drawMessage.animate){
			content = drawMessage.content;
			content.x1 += content.vx1;
			content.x2 += content.vx2;
			content.y1 += content.vy1;
			content.y2 += content.vy2;
			
			if (content.x1 > canvas.width || content.x1 < 0){
				content.vx1 = -content.vx1;
			}
			
			if (content.y1 > canvas.height || content.y1 < 0){
				content.vy1 = -content.vy1;
			}
			
			if (content.x2 > canvas.width || content.x2 < 0){
				content.vx2 = -content.vx2;
			}
			
			if (content.y2 > canvas.height || content.y2 < 0){
				content.vy2 = -content.vy2;
			}
		}
	}
}

function rectangleTool() {
	var tool = this;
	var started = false;

	var x1, x2, y1, y2, width, height;

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
			
			width = x2 - x1;
			height = y2 - y1;
			
			previewCtx.beginPath();
			previewCtx.rect(x1, y1, width, height);
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
			
			width = x2 - x1;
			height = y2 - y1;
			
			var content = {
				type : "RECTANGLE",
				content : {
					x : Math.min(x1, x2),
					y : Math.min(y1, y2),
					width : Math.abs(x2 - x1),
					height : Math.abs(y2 - y1)
				}
			}
			
			sendDrawMessage(content);
		}
	};

	this.draw = function(drawMessage) {
		content = drawMessage.content;
		var oldFillStyle = ctx.fillStyle;
		ctx.strokeStyle = getColorFromRGBA(drawMessage.lineColor);
		ctx.beginPath();
		ctx.rect(content.x, content.y, content.width, content.height);
		if (drawMessage.fillColor){
			ctx.fillStyle = getColorFromRGBA(drawMessage.fillColor);
			ctx.fillRect(content.x+1, content.y+1, content.width-1, content.height-1);
		}
		ctx.closePath();
		ctx.stroke();
		
		ctx.fillStyle = oldFillStyle;
	}
	
	this.setAnimationParams = function (drawMessage){
		content = drawMessage.content;
		
		content.vx = Math.random() * 5;
		content.vy = Math.random() * 5;
		content.vWidth = Math.random() * 5;
		content.vHeight = Math.random() * 5;
	}
	
	this.onAnimate = function(drawMessage){
		if (drawMessage.animate){
			content = drawMessage.content;
			content.x += content.vx;
			content.y += content.vy;
			
			if (content.x + content.width > canvas.width || content.x < 0){
				content.vx = -content.vx;
			}
			
			if (content.y + content.height > canvas.height || content.y < 0){
				content.vy = -content.vy;
			}
			
			if (content.x + content.width + content.vWidth <= canvas.width && content.width + content.vWidth >= 0)
				content.width += content.vWidth;
			else
				content.vWidth = -content.vWidth;
			
			if (content.y + content.height + content.vHeight <= canvas.height && content.height + content.vHeight >= 0)
				content.height += content.vHeight;
			else
				content.vHeight = -content.vHeight;
		}
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
			
			if (radius > 0){
				var content = {
					type : "CIRCLE",
					content : {
						x : x1,
						y : y1,
						radius : radius
					}
				}
				
				sendDrawMessage(content);
			}
		}
	};

	this.draw = function(drawMessage) {
		content = drawMessage.content;
		ctx.strokeStyle = getColorFromRGBA(drawMessage.lineColor);
		ctx.beginPath();
		ctx.arc(content.x, content.y, content.radius, 0, Math.PI * 2);
		
		var oldFillStyle = ctx.fillStyle;
		if (drawMessage.fillColor){
			ctx.fillStyle = getColorFromRGBA(drawMessage.fillColor);
			ctx.fill();
		}
		ctx.fillStyle = oldFillStyle;
		
		ctx.closePath();
		ctx.stroke();
	}
	
	this.setAnimationParams = function (drawMessage){
		content = drawMessage.content;
		
		content.vx = Math.random() * 5;
		content.vy = Math.random() * 5;
		content.vRadius = Math.random() * 5;
	}
	
	this.onAnimate = function(drawMessage){
		if (drawMessage.animate){
			content = drawMessage.content;
			content.x += content.vx;
			content.y += content.vy;
			
			if (content.x + content.radius > canvas.width || content.x < 0){
				content.vx = -content.vx;
			}
			
			if (content.y + content.radius > canvas.height || content.y < 0){
				content.vy = -content.vy;
			}
			
			if (content.x + content.radius + content.vRadius <= canvas.width &&
					content.y + content.radius + content.vRadius <= canvas.height &&
					content.radius + content.vRadius >= 0)
				content.radius += content.vRadius;
			else
				content.vRadius = -content.vRadius;
		}
	}
}

function polygonTool() {
	var tool = this;
	var started = false;

	var x = [], y = [];
	var x2, y2;
	
	this.keypressed = false;

	this.mousedown = function(ev) {
		if(tool.keypressed){
			console.log("mousedown polygon with key pressed");
			ctx.beginPath();
			ctx.moveTo(ev._x, ev._y);
			x.push(ev._x);
			y.push(ev._y);
			tool.started = true;
		}
	};
	
	this.endPolygon = function() {
		if (tool.started) {
			console.log("tool end");
			tool.started = false;
			previewCtx.clearRect(0, 0, previewCanvas.width,
					previewCanvas.height);
			
			var content = {
				type : "POLYGON",
				content : {
					xPoints : x,
					yPoints : y
				}
			}
			sendDrawMessage(content);
			
			x = [];
			y = [];
		}
	}
	
	this.mousemove = function(ev) {
		if (tool.started) {
			console.log("mousemove polygon with key pressed");
			previewCtx.clearRect(0, 0, previewCanvas.width,
					previewCanvas.height);
			
			x2 = ev._x;
			y2 = ev._y;
			
			previewCtx.beginPath();
			previewCtx.moveTo(x[0], y[0]);
			
			if(x.length > 1){
				for(var i = 1; i < x.length; ++i){
					previewCtx.lineTo(x[i], y[i]);
				}
			}
			
			previewCtx.lineTo(x2, y2);
			previewCtx.lineTo(x[0], y[0]);
			previewCtx.closePath();
			previewCtx.stroke();
		}
	};

	this.mouseup = function(ev) {
	};

	this.draw = function(drawMessage) {
		content = drawMessage.content;
		
		ctx.strokeStyle = getColorFromRGBA(drawMessage.lineColor);
		ctx.beginPath();
		ctx.moveTo(content.xPoints[0], content.yPoints[0]);
		if(content.xPoints.length > 1){
			for(var i = 1; i < content.xPoints.length; ++i){
				ctx.lineTo(content.xPoints[i], content.yPoints[i]);
			}
		}
		ctx.lineTo(content.xPoints[0], content.yPoints[0]);
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


