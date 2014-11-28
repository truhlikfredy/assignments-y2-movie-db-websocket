// ==ClosureCompiler==
// @compilation_level ADVANCED_OPTIMIZATIONS
// @externs_url ./externs/jquery-1.9.js
// ==/ClosureCompiler==

var ws = new WebSocket("ws://localhost:9001/");

$(document).ready(function() {
	$("#silueta").animate({
		opacity : 1.0
	}, 1000);
	$("#login").animate({
		opacity : 1.0
	}, 1000);

	$("#login_button").click(function() {
	    ws.send(JSON.stringify({'t':1,'name':$("#login_username").val(),'pass':$("#login_password").val()}));		
	})
});

function change_name() {
	var r = {'r':1,'v':
	document.getElementById('user_name').value
};
ws.send(JSON.stringify(r));
}

function do_damage() {
	var r={'r':1
}

//	alert(r);
ws.send(JSON.stringify(r));
}

function send_chat() {
	ws.send(JSON.stringify({'r':1,'v':
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
	if (e['t']==2) {
		if (e['v']) {
			$("#welcome").hide();
		} else {
			alert('Failed to login.');
		}
		//		alert(evt.data);
		//		alert(e['v']);
	}
	if (e['r']==1) {
		var tmp = '';
		for (var i = 0; i < e['v'].length; i++) {
			tmp += e['v'][i] + '<br>';
		}
		//		alert(tmp);
		document.getElementById('users').innerHTML = tmp;
	}
	if (e['r']==1) {
		document.getElementById('chat').innerHTML = document.getElementById('chat').innerHTML + e['v'] + '\n';
	}
}

