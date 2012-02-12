function find() {
  chrome.extension.onRequest.addListener(
    function(request, sender, sendResponse) {

      for (var i = 0; i < 7; i++) {
        $("#rack_" + i).html(request.rack[i]);
      }

      for (var i = 0; i < 15; i++) {
        for (var j = 0; j < 15; j++) {
          var letter = request.board[j][i];
          letter = letter == null ? "_" : letter;
          $("#" + i + "_" + j).html(letter);
        }
      }

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
	      console.log(result);
              var move = result.options[0];
	      $("#points").html(move.score);
	      for (var i in move.plays) {
               $("#" + move.plays[i].y + "_" + move.plays[i].x).html(move.plays[i].letter).addClass("red");
              }
          }
			    });
  });

  chrome.tabs.executeScript(null,
      {allFrames: true, file:"iframe.js"});
}
