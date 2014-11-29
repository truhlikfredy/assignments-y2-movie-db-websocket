// ==ClosureCompiler==
// @compilation_level ADVANCED_OPTIMIZATIONS
// @externs_url ./externs/jquery-1.9.js
// ==/ClosureCompiler==

var ws = new WebSocket("ws://localhost:9001/");

$(document).ready(function() {

	$('.dropdown').dropdown();
	$('.sidebar').sidebar('show');

	$('.ui .item').on('click', function() {
		$('.ui .item').removeClass('active');
		$(this).addClass('active');
	});

	$('#ubtn_home').click(function() {
		$('#user_home_tab').show();
		$('#user_movies_tab').hide();
	});

	$('#ubtn_movies').click(function() {
		$('#user_home_tab').hide();
		$('#user_movies_tab').show();
		ws.send(JSON.stringify({'t':%R_LIST_MOVIES%}));
	});

	$('#ubtn_logout').click(function() {
		$("#admin").hide();
		$("#user").hide();
		$("#welcome").show();
		ws.send(JSON.stringify({'t':%R_LOGOUT%}));
	});

	$("#admin").hide();
	$("#user").hide();

	// var obj = jQuery.parseJSON( '[{"name":"Action","timesUsed":57},{"name":"Adventure","timesUsed":3},{"name":"Biography","timesUsed":6},{"name":"Comedy","timesUsed":30},{"name":"Crime","timesUsed":36},{"name":"Drama","timesUsed":24},{"name":"Not categorized","timesUsed":9}]');
	//	console.log(obj);

	$("#login_button").click(function() {
	ws.send(JSON.stringify({'t':%R_LOGIN%,'name':$("#login_username").val(),'pass':$("#login_password").val()}))
});

$("#w_user").click(function() {
	ws.send(JSON.stringify({'t':%R_LOGIN%,'name':'user','pass':'user'}));
});

$("#w_admin").click(function() {
	ws.send(JSON.stringify({'t':%R_LOGIN%,'name':'admin','pass':'admin'}));
});

$("#u_list_genres").click(function() {
	ws.send(JSON.stringify({'t':%R_LIST_GENRES%}));
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
	//console.log(evt.data);
	e = JSON.parse(evt.data);

	if (e['t']==%A_PASS_FAIL%) {
		if (e['v']) {
			$("#welcome").hide();
			if (e['admin']) {
				$("#admin").show();
			} else {
				$("#user").show();
			}
		} else {
			alert('Failed to login.');
		}
	}

	if (e['t']==%A_LIST_GENRES%) {
		console.log(e['v']);

		var tmp = '';

		for (var i = 0; i < e['v'].length; i++) {
			tmp += '<div class="ui green basic button">' + e['v'][i]['name'] + '</div>';
		}
		//		document.getElementById('list_genres').innerHTML = tmp;
		$('#list_genres').html(tmp);

		/*
		 var tmp = '';
		 for (var i = 0; i < e['v'].length; i++) {
		 tmp += e['v'][i] + '<br>';
		 }
		 //		alert(tmp);
		 document.getElementById('users').innerHTML = tmp;
		 */
	}

	if (e['t']==%A_LIST_MOVIES%) {
		// console.log(e['v']);

		var tmp = '<div class="ui segment"><div class="ui list">';

        var key,val;
		for (key in e['v']) {
			val=e['v'][key];
			// console.log(val);
			// console.log(val['name']);
			// console.log(val['year']);
			// console.log(val['genre']['name']);
			// console.log(val['plot']);
			// console.log(val['coverImageURL']);
			// console.log(val['actors']);
			tmp+='<div class="item"><img class="ui top aligned avatar image" src="'+val['coverImageURL']+'">';
			tmp+='<div class="content"><div class="header">'+val['name']+'</div>';
			tmp+=val['plot'];
			tmp+=val['genre']['name']+' - '+val['year'];
			tmp+='<div class="list">';
			tmp+='<div class="item"><i class="top aligned right triangle icon"></i><div class="content"><a class="header">Plot</a>';
			tmp+='<div class="description">'+val['plot']+'</div></div></div>';
			tmp+='<div class="item"><i class="top aligned right triangle icon"></i><div class="content"><a class="header">Rating</a>';
			tmp+='<div class="description">'+val['averageRating']+'</div></div></div>';
			tmp+='<div class="item"><i class="top aligned right triangle icon"></i><div class="content"><a class="header">Actors</a>';
			tmp+='<div class="description">'+val['actors']+'</div></div></div>';
			tmp+='</div></div></div>';
		}

		tmp += '</div></div>';

		$('#list_movies').html(tmp);

	}

	if (e['r']==%R_LOGIN%) {
		document.getElementById('chat').innerHTML = document.getElementById('chat').innerHTML + e['v'] + '\n';
	}
}

