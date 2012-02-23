function Game(brd, dawg) {
	this.board = brd || [];
	this.flipped = false;
	// TODO: empty
	this.empty = false;
	this.pendingPoints = [];
	this.specialSpaces = [];
	this.dawg = dawg;

	this.inBounds = function(point) {
		return point.x >= 0 && point.x < 15 && point.y >= 0 && point.y < 15;
	};

	this.placeTile = function(point, tile) {
		this.board[point.x][point.y] = tile;
		this.pendingPoints.push(point);
	};

	this.placeTiles = function(plays) {
		for ( var i = 0; i < plays.length; i++) {
			var play = plays[i];
			this.placeTile(play.point, play.tile);
		}
	};

	this.tileAt = function(point) {
		return this.board[point.x][point.y];
	};

	this.getErrors = function() {
		if (this.pendingPoints.length == 0) {
			return "Must play tiles";
		}

		var x = this.pendingPoints[0].x;
		var y = this.pendingPoints[0].y;
		var horiz = true;
		var vert = true;

		for ( var i = 0; i < this.pendingPoints.length; i++) {
			var pendingPoint = this.pendingPoints[i];

			if (pendingPoint.x != x) {
				horiz = false;
			}

			if (pendingPoint.y != y) {
				vert = false;
			}
		}
		if (!horiz && !vert) {
			return "Must play all tiles in a row";
		}

		var createdWords = this.getCreatedWords();

		if (this.empty) {
			center = {
				x : Math.floor(this.board.length / 2),
				y : Math.floor(this.board[0].length / 2)
			};
			if (!$.arrayContains(createdWords[0], center)) {
				return "Must play across center tile on first turn";
			}
		} else {
			var connected = false;
      this.createdWordStr = [];
			for ( var i = 0; i < createdWords.length; i++) {
				var createdWordPoints = this.orderPoints(createdWords[i]);
				for ( var j = 0; j < createdWordPoints.length; j++) {
					if ($.inArray(this.pendingPoints, createdWordPoints[j]) < 0) {
						connected = true;
					}
				}
				this.createdWordStr[i] = this.pointsToStr(createdWordPoints);

				if (this.createdWordStr == "") {
					return "Empty created word!";
				} else if (!DawgUtil.inDict(this.dawg, this.createdWordStr[i])) {
					return "'" + this.createdWordStr + "' is not a recognized word";
				}
			}
			if (!connected) {
				return "Play must connect with existing letters";
			}
		}
	};

  this.pointsToStr = function(points) {
    var str = "";
    for (var i in points) {
      var tile = this.tileAt(points[i]);
      if (tile) {
        str += tile.letter;
      } else {
        console.warn("tile missing on board!");
      }
    }
    return str;
  };

  function sortByX(a, b) {
		return a.x - b.x;
  }

  function sortByY(a, b) {
		return a.y - b.y;
  }

  this.orderPoints = function(points) {
    var sortedPoints = points;
    sortedPoints.sort(sortByX);
    sortedPoints.sort(sortByY);
    return sortedPoints;
  };

	this.scoreTile = function(tile) {
		if (!tile) {
      return 0;
    } else if (tile.wildcard) {
			return 0;
		}
		return this.letterValues[tile.letter];
	};

	this.initSpecialSpaces = function() {
    this.specialSpaces = new Array(15);
		for ( var x = 0; x < 15; x++) {
      this.specialSpaces[x] = new Array(15);
			for ( var y = 0; y < 15; y++) {
				this.specialSpaces[x][y] = null;
			}
		}

		this.specialSpaces[3][0] = "TW";
		this.specialSpaces[6][0] = "TL";
		this.specialSpaces[2][1] = "DL";
		this.specialSpaces[5][1] = "DW";
		this.specialSpaces[1][2] = "DL";
		this.specialSpaces[4][2] = "DL";
		this.specialSpaces[0][3] = "TW";
		this.specialSpaces[3][3] = "TL";
		this.specialSpaces[6][0] = "TL";
		this.specialSpaces[2][4] = "DL";
		this.specialSpaces[6][4] = "DL";
		this.specialSpaces[1][5] = "DW";
		this.specialSpaces[5][5] = "TL";
		this.specialSpaces[0][6] = "TL";
		this.specialSpaces[4][6] = "DL";
		this.specialSpaces[7][3] = "DW";
		this.specialSpaces[3][7] = "DW";
		this.specialSpaces[11][7] = "DW";
		this.specialSpaces[7][11] = "DW";

		for ( var x = 0; x < Math.floor(this.specialSpaces.length / 2); x++) {
			var ylength = this.specialSpaces[x].length;
			for ( var y = 0; y < Math.floor(this.specialSpaces[x].length / 2); y++) {
				this.specialSpaces[this.specialSpaces.length - x - 1][y] = this.specialSpaces[x][y];
				this.specialSpaces[this.specialSpaces.length - x - 1][ylength - y - 1] = this.specialSpaces[x][y];
				this.specialSpaces[x][ylength - y - 1] = this.specialSpaces[x][y];
			}
		}
	};
  this.initSpecialSpaces();

	this.getCreatedWords = function() {
		var createdWords = [];
    var createdStrings =  [];
		for (var i = 0; i < this.pendingPoints.length; i++) {
			var point = this.pendingPoints[i];
			var horiz = [];

			var curPoint = point;
			while (this.inBounds(curPoint) && this.board[curPoint.x][curPoint.y]) {
				horiz.push(curPoint);
				curPoint = {
					x : curPoint.x - 1,
					y : curPoint.y
				};
			}
			curPoint = {
				x : point.x + 1,
				y : point.y
			};
			while (this.inBounds(curPoint) && this.board[curPoint.x][curPoint.y]) {
				horiz.push(curPoint);
				curPoint = {
					x : curPoint.x + 1,
					y : curPoint.y
				};
			}
      horiz = this.orderPoints(horiz);
      var moveStr = JSON.stringify(horiz) + this.pointsToStr(horiz);
			if (horiz.length > 1 && $.inArray(moveStr, createdStrings) < 0) {
				createdWords.push(horiz);
        createdStrings.push(moveStr);
			}

			var vert = [];
			curPoint = point;
			while (this.inBounds(curPoint) && this.board[curPoint.x][curPoint.y]) {
        vert.push(curPoint);
        curPoint = {
					x : curPoint.x,
					y : curPoint.y - 1
				};
			}

			curPoint = {
				x : point.x,
				y : point.y + 1
			};
			while (this.inBounds(curPoint) && this.board[curPoint.x][curPoint.y]) {
				vert.push(curPoint);
				curPoint = {
					x : curPoint.x,
					y : curPoint.y + 1
				};
			}

      vert = this.orderPoints(vert);
      moveStr = JSON.stringify(vert) + this.pointsToStr(vert);
			if (vert.length > 1 && $.inArray(moveStr, createdStrings) < 0) {
				createdWords.push(vert);
        createdStrings.push(moveStr);
			}
		}
		return createdWords;
	};

	this.discardPending = function() {
		for ( var i = 0; i < this.pendingPoints.length; i++) {
			var point = this.pendingPoints[i];
			this.board[point.x][point.y] = null;
		}
		this.pendingPoints = [];
	};

	this.scorePending = function() {
		var createdWords = this.getCreatedWords();
		var score = 0;
		for ( var i = 0; i < createdWords.length; i++) {
			score += this.scoreWordPoints(createdWords[i]);
		}
		if (this.pendingPoints.length >= 7) {
			score += 35;
		}
		return score;
	};

	this.flip = function() {
		var flippedBoard = new Array(15);
		;
		for ( var x = 0; x < board.length; x++) {
			flippedBoard[x] = new Array(15);
			for ( var y = 0; y < board[x].length; y++) {
				var tile = this.board[y][x];
				flippedBoard[x][y] = tile;
			}
		}

		this.board = flippedBoard;
		var flippedPending = [];
		for ( var i = 0; i < this.pendingPoints.length; i++) {
			var point = this.pendingPoints[i];
			flippedPending.push({
				                    x : point.y,
				                    y : point.x
			                    });
		}

		this.pendingPoints = flippedPending;
		this.flipped = !this.flipped;
	};

	this.scoreWordPoints = function(points) {
		var wordMods = [];
		var score = 0;
		for ( var i = 0; i < points.length; i++) {
			var point = points[i];
			var mod = this.specialSpaces[point.x][point.y];
			var letterScore = this.scoreTile(this.board[point.x][point.y]);
			if ($.inArray(this.pendingPoints) >= 0) {
				if (mod == "DL") {
					letterScore *= 2;
				} else if (mod == "TL") {
					letterScore *= 3;
				} else if (mod) {
					  wordMods.push(mod);
				}
			}
			score += letterScore;
		}

    for (var idx in wordMods) {
      wordMod = wordMods[idx];
		  if (wordMod == "DW") {
			  score *= 2;
		  } else if (wordMod == "TW") {
			  score *= 3;
		  }
    }
		return score;
	},

	this.letterValues = {
		'a' : 1,
		'b' : 4,
		'c' : 4,
		'd' : 2,
		'e' : 1,
		'f' : 4,
		'g' : 3,
		'h' : 3,
		'i' : 1,
		'j' : 10,
		'k' : 5,
		'l' : 2,
		'm' : 4,
		'n' : 2,
		'o' : 1,
		'p' : 4,
		'q' : 10,
		'r' : 1,
		's' : 1,
		't' : 1,
		'u' : 2,
		'v' : 5,
		'w' : 4,
		'x' : 8,
		'y' : 3,
		'z' : 10
	};

	this.alphabet = [];
	for ( var letter in this.letterValues) {
		this.alphabet.push(letter);
	}

	// DawgUtil.test(this, dawg);
}

function Point(x, y) {
	this.x = x;
	this.y = y;
}