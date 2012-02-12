var options;
var curOptionIdx;
var board;

function find() {
  chrome.extension.onRequest.addListener(
    function(request, sender, sendResponse) {
      $("#status").html("Game Board Found, Finding Moves...");
      for (var i = 0; i < 7; i++) {
        $("#rack_" + i).html(request.rack[i]);
      }
      
      board = request.board;
      clearBoard();

      $.ajax({
	  url: "http://10.0.0.6:8080/WordsWithCheats/",
	  type: "POST",
	  data: JSON.stringify(request),
	  dataType: "json",
	  beforeSend: function(x) {
	      if (x && x.overrideMimeType) {
		  x.overrideMimeType("application/j-son;charset=UTF-8");
	      }
	  },
	  success: function(result) {
	      $("#status").html("Moves Found!");
	      options = result.options;
	      curOptionIdx = 0;
	      loadOption(options[curOptionIdx]);
	  },
	  failure: function(result) {
	      $("#status").html("ERROR SADFACE");
	  },
	  error: function(xhr, status) {
	      $("#status").html("ERROR " + xhr.status);
	  }
      });
    });
    debugger;
    chrome.tabs.executeScript(null,
			      {allFrames: true, file:"iframe.js"});
}
find();


function loadOption(move) {
    $("#points").html(move.score);
    for (var i in move.plays) {
        $("#" + move.plays[i].y + "_" + move.plays[i].x).html(move.plays[i].letter).addClass("red");
    }
}

function clearBoard() {
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

function prevOption() {
    if (curOptionIdx > 0) {
	clearBoard();
	curOptionIdx--;
	loadOption(options[curOptionIdx]);
    }
}

function nextOption() {
    if (curOptionIdx < options.length - 1) {
	clearBoard();
	curOptionIdx++;
	loadOption(options[curOptionIdx]);
    }
}

