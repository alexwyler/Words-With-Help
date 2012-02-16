var options;
var board;
var rack;
var loading = false;

//var url = "http://ec2-107-22-41-246.compute-1.amazonaws.com/WWH/";
var url = "http://127.0.0.1:8080/WordsWithCheats/";
//var url = "http://172.25.100.80:8080/WordsWithCheats/";


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
//          clearBoard();
          loadMoves();
        });
    chrome.tabs.executeScript(
      null, {
        allFrames : true,
        file : "iframe.js"
      });
  }
}
find(true);

function sortByScore(a, b) {
  return b.score - a.score;
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

  $.ajax({
    url : url,
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
        options = options.splice(0, 5);
        $(".option").remove();
        for ( var i = 0; i < options.length; i++) {
          $("#moveOptions").append(
            "<tr class=\"option\" onclick=\"selectOption(" + i + ")\" id=\"option" + i + "\">" + 
              "<td>" + options[i].score + "</td>" + 
              "<td>" + "word, words" + "</td>" + 
            "</tr>"
          );
        }
        if (options.length > 0) {
          $("#status").html("Moves Found!  Getting more...");
        }
        if (result.status == 'more') {
          setTimeout(loadMoves, 500);
        } else {
          if (result.options.length < 1) {
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
    "<td>" + options[idx].score + "</td>" +
    "<td>" + "wordz" + "</td>" 
  );
  loadOption(options[idx]);
}

function loadOption(move) {
  $('#scoreContainer').show();
  $("#points").html(move.score);
  for ( var i in move.plays) {
    var letter = move.plays[i].letter.toUpperCase();
    if (move.plays[i].blankLetter) {
      $("tr[row=\"" + move.plays[i].y + "\"]").children(
          "td[col=\"" + move.plays[i].x + "\"]").html(
              "<div class='blankMove'>" + letter + "<div/>").addClass("active");
    }
    $("tr[row=\"" + move.plays[i].y + "\"]").children(
        "td[col=\"" + move.plays[i].x + "\"]").html(
            "<div class='move'>" + letter + "<div/>").addClass("active");
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
