function Game(board, dawg) {
	if (board) {
		this.board = board;
	} else {
		this.board = [];
	}
	this.flipped = false;
	// TODO: empty
	this.empty = false;
	this.pendingPoints = [];
	this.specialSpaces = [];
	this.dawg = dawg;

	this.inBounds = function(point) {
		return point.x >= 0 && point.x < 15 && point.y >= 0 && point.y < 15;
	};

	this.placeLetter = function(point, letter) {
		board[point.x][point.y] = letter;
		pendingPoints.push(point);
	};

	this.placeLetters = function(plays) {
		for ( var i = 0; i < plays.length; i++) {
			var play = plays[i];
			this.placeLetter(play.point, play.tile.letter);
		}
	};

	this.letterAt = function(point) {
		return board[point.x][point.y];
	};

	this.getErrors = function() {
		if (this.pendingPoints.length == 0) {
			return "Must play tiles";
		}

		if (!(verticalRow || horizontalRow)) {
			return "Must play letters in a straight line, horizontally or vertically";
		}

		var x = this.pendingPoints[0].x;
		var y = this.pendingPoints[0].y;
		var horiz = true;
		var vert = true;

		for ( var i = 0; i < this.pendingPoints.length; i++) {
			var pendingPoint = this.pendingPoints[i];
			if (this.letterAt(pendingPoint).isBlank()) {
				return "Must instantiate blank tiles";
			}

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
			center = new Point(Math.floor(this.board.length / 2), Math
					.floor(this.board[0].length / 2));
			if (!$.arrayContains(createdWords[0], center)) {
				return "Must play across center tile on first turn";
			}
		} else {
			var connected = false;
			for ( var i = 0; i < createdWords.length; i++) {
				var createdWordPoints = this.orderPoints(createdWords[i]);
				for ( var j = 0; j < createdWordPoints.length; j++) {
					if ($.inArray(this.pendingPoints, createdWordPoints[j])) {
						connected = true;
					}
				}

				var createdWordStr = this.pointsToStr(createdWordPoints);
				if (!DawgUtil.inDict(this.dawg, createdWordStr)) {
					return "'" + createdWordStr + "' is not a recognized word";
				}
			}
			if (!connected) {
				return "Play must connect with existing letters";
			}
		}
	};

	this.scoreTile = function(tile) {
		if (tile.wildcard) {
			return 0;
		}
		return this.letterValues[tile.letter];
	};

	this.initSpecialSpaces = function() {
		for ( var x = 0; x < 15; x++) {
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

	this.getCreatedWords = function() {
		var createdWords = [];
		for ( var i = 0; i < this.pendingPoints.length; i++) {
			var point = this.pendingPoints[i];

			var horiz = [];

			var curPoint = point;
			while (this.inBounds(curPoint) && this.board[curpoint.x][curpoint.y]) {
				horiz.push(curpoint);
				curpoint = new Point(curpoint.x - 1, curpoint.y);
			}

			curpoint = new Point(point.x + 1, point.y);
			while (this.inBounds(curpoint) && this.board[curpoint.x][curpoint.y]) {
				horiz.push(curpoint);
				curpoint = new Point(curpoint.x + 1, curpoint.y);
			}

			if (horiz.length > 1 && !$.inArray(horiz, createdWords)) {
				createdWords.push(horiz);
			}

			var vert = [];
			curpoint = point;
			while (this.inBounds(curpoint) && this.board[curpoint.x][curpoint.y]) {
				vert.push(curpoint);
				curpoint = new Point(curpoint.x, curpoint.y - 1);
			}

			curpoint = new Point(point.x, point.y + 1);
			while (this.inBounds(curpoint) && this.board[curpoint.x][curpoint.y]) {
				vert.push(curpoint);
				curpoint = new Point(curpoint.x, curpoint.y + 1);
			}

			if (vert.size() > 1 && !$.inArray(vert, createdWords)) {
				createdWords.push(vert);
			}
		}

		return createdWords;
	};
	
	this.discardPending = function() {
		for (var i = 0; i < this.pendingPoints.length; i++) {
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

	this.scoreWordPoints = function(points) {
		var wordMod = null;
		var score = 0;
		for ( var i = 0; i < points.length; i++) {
			var point = points[i];
			var mod = this.specialSpaces[point.x][point.y];
			var letterScore = this.scoreTile(this.board[point.x][point.y]);
			if ($.inArray(this.pendingPoints)) {
				if (mod == "DL") {
					letterScore *= 2;
				} else if (mod == "TL") {
					letterScore *= 3;
				} else if (mod) {
					wordMod = mod;
				}
			}
			score += letterScore;
		}
		if (wordMod == "DW") {
			score *= 2;
		} else if (wordMod == "TW") {
			score *= 3;
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
}

function Point(x, y) {
	this.x = x;
	this.y = y;
}

function Tile(letter, wildcard) {
	this.letter = letter;
	if (wildcard) {
		this.wildcard = true;
	} else {
		this.wildcard = false;
	}
}