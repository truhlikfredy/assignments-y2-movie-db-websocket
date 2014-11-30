// ==ClosureCompiler==
// @compilation_level ADVANCED_OPTIMIZATIONS
// @externs_url ./externs/jquery-1.9.js
// ==/ClosureCompiler==

// Author and copyright Anton Krug (C) 2014

var ws = new WebSocket("ws://localhost:9001/");

//attach all event listeners
$(document).ready(function() {

	$('.dropdown').dropdown();
	$('.sidebar').sidebar('show');
	$('.rating').rating();
	$('.rating.readonly').rating('disable');

	$('.ui .item').on('click', function() {
		$('.ui .item').removeClass('active');
		$(this).addClass('active');
	});

	$('#search').click(function() {
		var id = $('#search_text').val();
		ws.send(JSON.stringify({'t':%R_SEARCH%,'v':id}));
	});

	$('#ubtn_home').click(function() {
		$('.defhid').hide();
		$('#user_home_tab').show();
	});

	$('#ubtn_movies').click(function() {
		$('.defhid').hide();
		$('#user_movies_tab').show();
		ws.send(JSON.stringify({'t':%R_LIST_MOVIES%,'only_rated':false}));
	});

	$('#ubtn_rated').click(function() {
		$('.defhid').hide();
		$('#user_movies_tab').show();
		ws.send(JSON.stringify({'t':%R_LIST_MOVIES%,'only_rated':true}));
	});

	$('#ubtn_genres').click(function() {
		$('.defhid').hide();
		$('#list_genres').show();
		ws.send(JSON.stringify({'t':%R_LIST_GENRES%,'v':0}));
	});

	$('#ubtn_logout').click(function() {
		$('.defhid').hide();
		$("#welcome").show();
		ws.send(JSON.stringify({'t':%R_LOGOUT%}));
	});

	$("#login_button").click(function() {
		var pass = $("#login_password").val();
		ws.send(JSON.stringify({'t':%R_LOGIN%,'name':$("#login_username").val(),'pass':pass}))
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

	$('.defhidmain').hide();
	$('.defhid').hide();
	$("#welcome").show();

});

//few common fuctions
function populateMovies(input) {
	var tmp = "";

	var key, val;
	for (key in input) {
		val = input[key];
		// console.log(val);
		// console.log(val['name']);
		// console.log(val['year']);
		// console.log(val['genre']['name']);
		// console.log(val['plot']);
		// console.log(val['coverImageURL']);
		// console.log(val['actors']);

		var rating = 5 + Math.round(val['averageRating'] + 0.5);

		tmp += '<div><img src="' + val['coverImageURL'] + '" width=100 height=150 style="float:left;clear:both;">';
		tmp += '<b>' + val['name'] + '</b><br>';
		tmp += val['genre']['name'] + ' - ' + val['year'] + '<br><br>';
		// tmp+='<div style="width:200px">'+val['plot']+'</div';

		tmp += '<i class="top aligned right triangle icon"></i>&nbsp;<b>Rating</b>&nbsp;';
		tmp += '<div class="ui mini star rating readonly" data-rating="' + rating + '" data-max-rating="10"></div><br>';
		// tmp+=val['averageRating'];

		tmp += '<i class="top aligned right triangle icon"></i>&nbsp;<b>Actors</b>';
		tmp += '' + val['actors'] + '<br>';

		tmp += '<i class="top aligned right triangle icon"></i>&nbsp;<b>Your rating</b>&nbsp;';

		tmp += '<div class="ui large heart rating" data-rating="' + val['rated'] + '" data-max-rating="5"></div>';
		tmp += '<br>';
		tmp += val['plot'];

		tmp += '</div><br><br>';
	}
	return tmp;
}

//webscoket listeners
ws.onopen = function() {

}

ws.onclose = function(e) {
	alert('Can\'t connect to server');
}

ws.onmessage = function(evt) {
	//console.log(evt.data);
	e = JSON.parse(evt.data);

	// will proccess to JSON answer packets

	if (e['t']==%A_PASS_FAIL%) {
		if (e['v']) {
			$('.defhidmain').hide();
			if (e['admin']) {
				$("#admin").show();
			} else {
				$("#user").show();
				$('#ubtn_logout').html('<i class="sign out icon"></i> Logout ' + e['name']);
			}
		} else {
			alert('Failed to login.');
		}
	}

	if (e['t']==%A_LIST_GENRES%) {
		console.log(e['v']);

		var tmp = '';

		for (var i = 0; i < e['v'].length; i++) {
			tmp += '<div class="ui green basic button genres" data-name="' + e['v'][i]['name'] + '">' + e['v'][i]['name'] + ' (' + e['v'][i]['timesUsed'] + ')</div>';
		}
		$('#list_genres').html(tmp);

		$('.genres').click(function() {
			var btn = $(this);
			var query = btn.data('name');
			$('.genres').removeClass('active');
			btn.addClass('active');
			ws.send(JSON.stringify({'t':%R_SEARCH%,'v':query}));
		});

	}

	if (e['t']==%A_LIST_MOVIES%) {
		// console.log(e['v']);

		$('#list_movies').html(populateMovies(e['v']));

		$('.ui.rating').rating();
		$('.ui.rating.readonly').rating('disable');

	}

}

