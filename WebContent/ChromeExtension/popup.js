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
        file : "iframe.js"
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
	      words[j] + "</a>&nbsp;";
	  }
  }
  return links;
}

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
        $("#status").html(result.error);
      } else {
        options = options.concat(result.options);
        options.sort(sortByScore);
        options = options.splice(0, 12);
        $(".option").remove();
        for ( var i = 0; i < options.length; i++) {
          $("#moveOptions").append(
            "<tr class=\"option\" onclick=\"selectOption(" + i + ")\" id=\"option" + i + "\">" + 
              "<td width='20%'>" + options[i].score + "</td>" + 
              "<td width='80%'>" + options[i].words + "</td>" + 
            "</tr>"
          );
        }
        if (options.length > 0) {
          $("#status").html("Moves Found!  Getting more...");
        }
        if (result.status == 'more') {
          setTimeout(loadMoves, 100);
        } else {
          if (options.length < 1) {
            $("#status").html("No moves found");
          } else {
            $("#status").html("All moves found!");
          }
          loading = false;
        }
      }
    },
    error : function(xhr, ajaxOptions, thrownError) {
      $("#status").html("Unable to connect to server");
    }
  });
}

function selectOption(idx) {
  clearBoard();
  $("#currentSelection").html(
    "<td width='20%'>" + options[idx].score + "</td>" +
    "<td width='80%'>" + buildLinks(options[idx].words) + "</td>" 
  );
  loadOption(options[idx]);
}

function loadOption(move) {
  $('#scoreContainer').show();
  $("#points").html(move.score);
  for ( var i in move.plays) {
    console.log(move.plays[i].wildcard);
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
