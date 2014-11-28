// ==ClosureCompiler==
// @compilation_level ADVANCED_OPTIMIZATIONS
// @externs_url ./externs/jquery-1.9.js
// ==/ClosureCompiler==

var ws = new WebSocket("ws://localhost:9001/");

$(document).ready(function() {
	$("#admin").hide();
	$("#user").hide();
	
	
	$("#login_button").click(function() {
	    ws.send(JSON.stringify({'t':%R_LOGIN%,'name':$("#login_username").val(),'pass':$("#login_password").val()}));		
	});
});

function send_chat() {
	ws.send(JSON.stringify({'r':%R_LOGIN%,'v':
	document.getElementById('chat_message').value
}));

}

ws.onopen = function() {

}

ws.onclose = function(e) {
	alert('Can\'t connect to server');
}
ws.onmessage = function(evt) {
	//	var ping=(new Date()).getTime()-parseInt(evt.data);
	//	document.getElementById('result').innerHTML=document.getElementById('result').innerHTML+ping+'ms ';
	//	ws.send((new Date()).getTime());
	e = JSON.parse(evt.data);
	
	if (e['t']==%A_PASS_FAIL%) {
		if (e['v']) {
			$("#welcome").hide();
		} else {
			alert('Failed to login.');
		}
		if (e['admin']) {
			$("#admin").show();
		} else {
			$("#user").show();			
		}
	}
	
	if (e['r']==%R_LOGIN%) {
		var tmp = '';
		for (var i = 0; i < e['v'].length; i++) {
			tmp += e['v'][i] + '<br>';
		}
		//		alert(tmp);
		document.getElementById('users').innerHTML = tmp;
	}
	if (e['r']==%R_LOGIN%) {
		document.getElementById('chat').innerHTML = document.getElementById('chat').innerHTML + e['v'] + '\n';
	}
}

