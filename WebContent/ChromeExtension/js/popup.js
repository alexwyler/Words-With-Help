var options;
var board;
var rack;
var loading = false;

function find(test) {
  if (test) {
    rack = ['w', 'o', 'r', 'd', 'z', 'y', 'u'];
    board = [
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, 'p', 'e', 'n', 's', null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
      [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]
    ];

    options = [];
    loadingGif = "<img src='loading.gif'/>";
    status = $("#status").html("Finding Moves...<br/>" + loadingGif);
    for ( var i = 0; i < 7; i++) {
      var tile = rack[i];
      tile = tile == null ? "&nbsp;" : tile.toUpperCase();
      $("#rack_" + i).html(tile);
    }
    clearBoard();
    loadMoves();
  } else {
    chrome.extension.onRequest
      .addListener(
        function(request, sender, sendResponse) {
          rack = request.rack;
          board = request.board;
          options = [];
          loadingGif = "<img src='loading.gif'/>";
          status = $("#status").html("Finding Moves...<br/>" + loadingGif);
          for ( var i = 0; i < 7; i++) {
            var tile = rack[i];
            tile = tile == null ? "&nbsp;" : tile.toUpperCase();
            $("#rack_" + i).html(tile);
          }
          clearBoard();
          loadMoves();
        });
    chrome.tabs.executeScript(
      null, {
        allFrames : true,
        file : "/js/iframe.js"
      });
  }
}
find();

function sortByScore(a, b) {
	if (a.score == b.score && a.words && b.words) {
		return b.words.toString() < a.words.toString(); 
	} else {
		return b.score - a.score;
	}
}

function buildLinks(words) {
  var links = "";
  if (words) {
	  for (var j = 0; j < words.length; j++) {
	    links += "<a target=\"_blank\" " +
	      "href=\"https://www.google.com/search?btnG=1&pws=0&q=define:" + words[j] + "\">" +
	      words[j].toUpperCase() + "</a>&nbsp;";
	  }
  }
  return links;
}

function loadMoves() {
	if (config.clientDawgz) {
		var game = new Game(board, DawgUtil.dawg);
		var chooser = new Chooser(game, rack);
		updateResults(chooser.getOptions());
		finalizeResults();
	} else {
		loadFromServer();
	}
}

function loadFromServer() {
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
	  request.api = config.api;

	  $.ajax({
	    url : config.url,
	    type : "POST",
	    data : JSON.stringify(request),
	    dataType : "json",
	    beforeSend : function(x) {
	      if (x && x.overrideMimeType) {
	        x.overrideMimeType("application/j-son;charset=UTF-8");
	      }
	    },
	    success : function(result) {
	      if (result.error) {
	      	updateStatus(result.error);
	      } else {
	      	processResults(result.options);
	        if (result.status == 'more') {
	          setTimeout(loadMoves, 100);
	        } else {
	          finalizeResults();
	          loading = false;
	        }
	      }
	    },
	    error : function(xhr, ajaxOptions, thrownError) {
	    	updateStatus("Unable to connect to server");
	    }
	  });
}


function updateStatus(html) {
	$("#status").html(html);
}

function processResults(results) {
	options = options.concat(results);
  options.sort(sortByScore);
  options = options.splice(0, 12);
  $(".option").remove();
  for ( var i = 0; i < options.length; i++) {
    var words = "" + options[i].words;
    $("#moveOptions").append(
      "<tr class=\"option\" onclick=\"selectOption(" + i + ")\" id=\"option" + i + "\">" + 
        "<td>" + 
          "<a href='#'>" + options[i].score + " - " +
          words.toUpperCase() + "</a>" + 
        "</td>" + 
      "</tr>"
    );
  }
  if (options.length > 0) {
    $("#status").html("Moves Found!  Getting more...");
  }
}

function finalizeResults() {
	if (options.length < 1) {
    $("#status").html("No moves found");
  } else {
    $("#status").html("All moves found!");
  }
}

function selectOption(idx) {
  clearBoard();
  $("#currentSelection").html(buildLinks(options[idx].words));
  $(".option").removeClass("selected");
  $("#option" + idx).addClass("selected");
  loadOption(options[idx]);
}

function loadOption(move) {
  $('#scoreContainer').show();
  $("#points").html(move.score);
  for ( var i in move.plays) {
    var letter = move.plays[i].letter.toUpperCase();
    if (move.plays[i].wildcard) {
      $("tr[row=\"" + move.plays[i].y + "\"]").children(
        "td[col=\"" + move.plays[i].x + "\"]").html(
          "<div class='blank move'>" + letter + "<div/>").addClass("active");
    } else {
      $("tr[row=\"" + move.plays[i].y + "\"]").children(
        "td[col=\"" + move.plays[i].x + "\"]").html(
          "<div class='move'>" + letter + "<div/>").addClass("active");
    }
  }
}

function clearBoard() {
  $('#scoreContainer').hide();
  for ( var i = 0; i < 15; i++) {
    for ( var j = 0; j < 15; j++) {
      var letter = board[i][j];
      letter = letter == null ? "&nbsp;" : letter.toUpperCase();
      var tile = $("tr[row=\"" + j + "\"]").children("td[col=\"" + i + "\"]");
      tile.removeClass("active");
      tile.html(letter);
    }
  }
}