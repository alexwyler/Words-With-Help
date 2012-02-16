var options;
var curOptionIdx;
var board;

function find() {
  chrome.extension.onRequest.addListener(
    function(request, sender, sendResponse) {
      
      loadingGif = "<img src='loading.gif'/>";
      status = $("#status").html("Finding Moves...<br/>" + loadingGif);
      
      for (var i = 0; i < 7; i++) {
        $("#rack_" + i).html(request.rack[i]);
      }
      
      board = request.board;
      clearBoard();
      
      $.ajax(
        {
	  url: "http://ec2-107-22-41-246.compute-1.amazonaws.com/WWH/",
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
	    } else if (result.options.length < 1) {
	      $("#status").html("No moves found");
	    } else {
	      $("#status").html("Moves Found!");
	      options = result.options;
	      for (var i = 0; i < options.length; i++) {
		$("#options").append(
		  "<span onclick=\"selectOption(" + i + ")\" id=\"option" + i + "\">" + (i+1) +
		    " </span>"
		);
	      }
	      curOptionIdx = 0;
	      $("#option" + curOptionIdx).addClass("red");
	      loadOption(options[curOptionIdx]);
	    }
	  },
          error:function (xhr, ajaxOptions, thrownError) {
    	    $("#status").html("Unable to connect to server");
          }
        });
    });
  chrome.tabs.executeScript(null,
			    {allFrames: true, file:"iframe.js"});
}
find();


function loadOption(move) {
  $('#scoreContainer').show();
  $("#points").html(move.score);
  for (var i in move.plays) {
    if (move.plays[i].blankLetter) {
      $("tr[row=\"" + move.plays[i].y + "\"]").children("td[col=\"" + move.plays[i].x + "\"]")
        .html("<div class='blankMove'>" + move.plays[i].letter + "<div/>").addClass("active");
    }
    $("tr[row=\"" + move.plays[i].y + "\"]").children("td[col=\"" + move.plays[i].x + "\"]")
      .html("<div class='move'>" + move.plays[i].letter + "<div/>").addClass("active");
  }
}

function clearBoard() {
  $('#scoreContainer').hide();
  for (var i = 0; i < 15; i++) {
    for (var j = 0; j < 15; j++) {
      var letter = board[i][j];
      letter = letter == null ? " " : letter;
      tile = $("tr[row=\"" + j + "\"]").children("td[col=\"" + i + "\"]");
      tile.removeClass("active");
      tile.html(letter);
    }
  }
}

function selectOption(idx) {
  clearBoard();
  $("#option" + curOptionIdx).removeClass("red");
  curOptionIdx = idx;
  $("#option" + curOptionIdx).addClass("red");
  loadOption(options[curOptionIdx]);
}

function prevOption() {
  if (curOptionIdx > 0) {
    clearBoard();
    $("#option" + curOptionIdx).removeClass("red");
    curOptionIdx--;
    $("#option" + curOptionIdx).addClass("red");
    loadOption(options[curOptionIdx]);
  }
}

function nextOption() {
  if (curOptionIdx < options.length - 1) {
    clearBoard();
    $("#option" + curOptionIdx).removeClass("red");
    curOptionIdx++;
    $("#option" + curOptionIdx).addClass("red");
    loadOption(options[curOptionIdx]);
  }
}

