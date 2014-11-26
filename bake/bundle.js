//asi nefunguje

// ==ClosureCompiler==
// @compilation_level ADVANCED_OPTIMIZATIONS
// @externs_url ./externs/jquery-1.9.js
// ==/ClosureCompiler==

//var ws = new WebSocket("ws://kramm2.info:8002/");


$(document).ready(function() {
  $("#silueta").animate({opacity:1.0}, 1000);
  $("#login").animate({opacity:1.0}, 1000);


});

$("#slider").slider({
    orientation: "vertical",
    value  : 30,
    step   : 1,
    range  : 'min',
    min    : 0,
    max    : 100,
    slide : function(event,ui){
//        var value = $("#slider").slider("value");
        var value = ui.value;
//        console.log(ui);
        document.getElementById("audio1").volume = (value / 100);
//        console.log(value);
    },
    create : function(event,ui){
        document.getElementById("audio1").volume = ($("#slider").slider("value")/ 100);
    }
});

function input_block_on(t) {
  $('#'+t.attr("id")+'_block').attr("data-orig-bg",$('#'+t.attr("id")+'_block').css('backgroundColor'));
  $('#'+t.attr("id")+'_block').animate({backgroundColor: '#171824'});
}
window['input_block_on'] = input_block_on;

function input_block_off(t) {
  $('#'+t.attr("id")+'_block').animate({backgroundColor: $('#'+t.attr("id")+'_block').attr("data-orig-bg")});
}
window['input_block_off'] = input_block_off;


$('.oznac_block').focus(function() { input_block_on($(this)); });
$('.oznac_block').blur(function() { input_block_off($(this)); });

$('.hover_orange').mouseover(function() {
  $(this).attr("data-orig-bg",$(this).css('backgroundColor'));
  $(this).animate({backgroundColor: '#EB4100'});
});
$('.hover_orange').mouseout(function() {
  $(this).animate({backgroundColor: $(this).attr("data-orig-bg")});
});

// povodne
/*
function change_name() {
	var r = {'r':%r_change_name%,'v':document.getElementById('user_name').value};
	ws.send(JSON.stringify(r));
}

function do_damage() {
	var r={'r':%r_do_damage%}
//	alert(r);
	ws.send(JSON.stringify(r));
}

function send_chat() {
	ws.send(JSON.stringify({'r':%r_chat_send%,'v':document.getElementById('chat_message').value}));
}
ws.onopen = function() {
	change_name();
}

window['change_name'] = change_name;
window['do_damage'] = do_damage;
window['send_chat'] = send_chat;
//window['chat_update'] = chat_update;

*/

/*
ws.onclose = function(e) {
	alert('Can\'t connect to server');
}
ws.onmessage = function (evt) {
//	var ping=(new Date()).getTime()-parseInt(evt.data);
//	document.getElementById('result').innerHTML=document.getElementById('result').innerHTML+ping+'ms ';
//	ws.send((new Date()).getTime());
	e=JSON.parse(evt.data);
	if (e['r']==%r_update_hp%) {
//		alert(evt.data);
//		alert(e['v']);
		document.getElementById('live').innerHTML=e['v']+' HP';
	}
	if (e['r']==%r_update_users%) {
		var tmp='';
		for (var i=0;i<e['v'].length;i++) {
			tmp+=e['v'][i]+'<br>';
		}
//		alert(tmp);
		document.getElementById('users').innerHTML=tmp;
	}
	if (e['r']==%r_chat_update%) {
		document.getElementById('chat').innerHTML=document.getElementById('chat').innerHTML+e['v']+'\n';
	}
}

window['ws.onopen'] =  ws.onopen;
window['ws.onclose'] = ws.onclose;
window['ws.onmessage'] = ws.onmessage;

*/



