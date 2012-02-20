function Game(board) {

	this.board = board;
	this.flipped = false;
	this.empty = true;
	this.flipped = false;
	this.pendingPoints = [];
	
	this.inBounds = function(point) {
		return point.x >= 0 && point.x < 15 && point.y >= 0 && point.y < 15;
	};
	
	this.playLetter = function(point, letter) {
		board[point.x][point.y] = letter;
		pendingPoints.push(point);
	};
	
	this.letterAt = function(point) {
		return board[point.x][point.y];
	};

	this.getErrors = function() {
		if (this.pendingPoints.length == 0) {
			return "Must play tiles";
		}
		
		for (var i = 0; i < this.pendingPoints.length; i++) {
			if (this.letterAt(this.pendingPoints[i]).isBlank()) {
				return "Must instantiate blank tiles";
			}
		}
		
		var createdWords = this.getCreatedWords();
		
		if (this.empty) {
			center = new Point(Math.floor(this.board.length / 2), Math.floor(this.board[0].length / 2));
			
		}
		
			if (empty) {

				Point center = new Point(board.getWidth() / 2,
						board.getHeight() / 2);
				if (!createdWords.get(0).contains(center)) {
					return "Must play across center tile on first turn";
				}
			} else {
				boolean wordIncludesExistingLetter = false;
				for (List<Point> word : createdWords) {
					orderLetters(word);
					String wordStr = wordToString(word);
					if (!dict.isInDictionary(wordStr)) {
						return "'" + wordStr + "' is not a valid word";
					}

					for (Point point : word) {
						if (!pendingPoints.contains(point)) {
							wordIncludesExistingLetter = true;
							break;
						}
					}
				}

				if (!wordIncludesExistingLetter) {
					return "Word is formed without using any pre-existing letters";
				}
			}

			int x = pendingPoints.get(0).x;
			int y = pendingPoints.get(0).y;
			boolean horizontalRow = true;
			boolean verticalRow = true;
			for (Point point : pendingPoints) {
				if (point.x != x) {
					horizontalRow = false;
				}
				if (point.y != y) {
					verticalRow = false;
				}
			}

			if (!(verticalRow || horizontalRow)) {
				return "Must play letters in a straight line, horizontally or vertically";
			}
			return null;
		}
	}
	
}

function Point(x, y) {
	this.x = x;
	this.y = y;
}

