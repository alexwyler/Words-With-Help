var options;
var curOptionIdx;
var board;
var rack;
var loading = false;

var url = "http://ec2-107-22-41-246.compute-1.amazonaws.com/WWH/";
//var url = "http://127.0.0.1:8080/WordsWithCheats/";

function find() {
  chrome.extension.onRequest.addListener(
    function(request, sender, sendResponse) {
    	console.log(request);
    	rack = request.rack;
    	board = request.board;
    	options = [];
    	loadingGif = "<img src='loading.gif'/>";
    	status = $("#status").html("Finding Moves...<br/>" + loadingGif);
        for (var i = 0; i < 7; i++) {
            $("#rack_" + i).html(request.rack[i]);
        }
        clearBoard();
        loadMoves();
    });
  chrome.tabs.executeScript(null, {
	  	allFrames: true, 
	  	file:"iframe.js"
	  	});
}
find();
   
function loadMoves() {
	request = {
		board : board,
		rack : rack
	};
	
	if (loading) {
		request.command = "async-update";
	} else {
		request.command = "async-start";
		loading = true;
	}
	
	$.ajax({
		url: url,
		type: "POST",
		data: JSON.stringify(request),
		dataType: "json",
		beforeSend: function(x) {
			if (x && x.overrideMimeType) {
				x.overrideMimeType("application/j-son;charset=UTF-8");
			}
		},
	  success: function(result) {
		  if (result.error) {
			  $("#status").html(result.error);
		  } else {
		      options.concat(result.options);
		      for (var i = 0; i < options.length; i++) {
		        $("#options").append(
		            "<span onclick=\"selectOption(" + i + ")\" id=\"option" + i + "\">" + (i+1) +
		            " </span>"
		        );
		      }
          if (result.options.length > 0) {
            $("#status").html("Moves Found!");
            curOptionIdx = 0;
            $("#option" + curOptionIdx).addClass("red");
            loadOption(options[curOptionIdx]);
          }
		      if (result.status == 'more') {
		        setTimeout(loadMoves, 500);
		      } else {
		        if (result.options.length < 1) {
		          $("#status").html("No moves found");
		        }
		        loading = false;
		      }
		  }
	  },
	  error:function (xhr, ajaxOptions, thrownError) {
	    $("#status").html("Unable to connect to server");
	  }
	});
}

function loadOption(move) {
	$('#scoreContainer').show();
    $("#points").html(move.score);
    for (var i in move.plays) {
        $("#" + move.plays[i].y + "_" + move.plays[i].x).html(move.plays[i].letter).addClass("red");
    }
}

function clearBoard() {
	$('#scoreContainer').hide();
    for (var i = 0; i < 15; i++) {
        for (var j = 0; j < 15; j++) {
            var letter = board[j][i];
            letter = letter == null ? "_" : letter;
            tile = $("#" + i + "_" + j);
            tile.removeClass("red");
            tile.html(letter);
        }
    }
}


