function Chooser(game, rack) {

	this.game = game;
	this.rack = rack;
	this.options = [];

	this.getOptions = function() {
		this.getAcrossOptions();
		this.game.flip();
		this.getAcrossOptions();
		this.game.flip();
		return options;
	};

	this.getAcrossOptions = function() {
		for ( var y = 0; y < 15; y++) {
			var anchors = [];
			if (!this.game.empty) {
				for ( var x = 0; x < 15; x++) {
					p = new Point(x, y);
					right = new Point(x + 1, y);
					left = new Point(x - 1, y);
					down = new Point(x, y + 1);
					up = new Point(x, y - 1);
					if (this.game.inBounds(right) && this.game.letterAt(right)
							|| this.game.inBounds(down) && this.game.letterAt(down)
							|| this.game.inBounds(left) && this.game.letterAt(left)
							|| this.game.inBounds(up) && this.game.letterAt(up)) {
						anchors.push(p);
					}
				}
			} else {
				if (y == 6) {
					for ( var x = 1; x <= 7; x++) {
						anchors.push(new Point(x, y));
					}
				}
			}

			console.log(anchors);
			for ( var i = 0; i < anchors.length; i++) {
				var p = anchors[i];
				var limit = 0;
				var left = new Point(p.x - 1, p.y);
				while (this.game.inBounds(left) && !this.game.letterAt(left)) {
					limit++;
					left = new Point(left.x - 1, left.y);
				}
				if (limit == 0) {
					cur = new Point(p.x - 1, p.y);
					while (this.game.inBounds(cur) && this.game.letterAt(cur)) {
						cur = new Point(cur.x - 1, cur.y);
					}
					/* array<Tile> */var partial = [];
					/* DawgNode */var node = this.game.dawg;
					cur = new Point(cur.x + 1, cur.y);
					while (this.game.inBounds(cur) && this.game.letterAt(cur)) {
						partial.push(this.game.letterAt(cur));
						node = node[this.game.letterAt(cur)];
						cur = new Point(cur.x + 1, cur.y);
					}
					this.extendRight(partial, node, p);
				} else {
					this.leftPart([], this.game.dawg, limit, p);
				}
			}
		}
	};

	this.leftPart = function(/* array<Tile> */partial, /* DawgNode */node,
			limit, anchor) {
		console.log("LEFT PART ");
		console.log(partial);
		console.log(anchor);
		this.extendRight(partial, node, anchor);
		if (limit > 0) {
			for ( var i = 0; i < this.rack.length; i++) {
				/* Tile */var removed = this.rack.splice(i, 1);
				/* array<Tile> */var toChecks = [];
				if (removed.wildcard) {
					for ( var idx in this.game.alphabet) {
						var letter = this.game.alphabet[idx];
						toChecks.push(new Tile(letter, true));
					}
				} else {
					toChecks.push(removed);
				}
				for ( var j = 0; j < toChecks.length; j++) {
					var toCheck = toChecks[j];
					var next = node[toCheck.letter];
					if (next) {
						partial.push(toCheck);
						leftPart(partial, next, limit - 1, anchor);
						partial.pop();
					}
				}
				this.rack.splice(i, 0, removed);
			}
		}
	};

	this.extendRight = function(/* array<Tile> */partial, /* DawgNode */node,
			point) {
		console.log("EXTEND RIGHT");
		console.log(partial);
		console.log(point);
		if (node["_"]) {
			this.recordMove(partial, point);
		}
		if (this.game.inBounds(point) && this.game.letterAt(point)) {
			for ( var i = 0; i < this.rack.length; i++) {
				/* Tile */var removed = this.rack.splice(i, 1);
				/* array<Tile> */var toChecks = [];
				if (removed.wildcard) {
					for ( var idx in this.game.alphabet) {
						var letter = this.game.alphabet[idx];
						toChecks.push(new Tile(letter, true));
					}
				} else {
					toChecks.add(removed);
				}
				for ( var j = 0; j < toChecks.length; j++) {
					var toCheck = toChecks[j];
					var next = node[toCheck.letter];
					if (next) {
						partial.push(toCheck);
						right = new Point(square.x + 1, square.y);
						extendRight(partial, next, right);
						partial.pop();
					}
				}
				this.rack.splice(i, 0, removed);
			}
		} else {
			var tile = this.game.letterAt(point);
			var next = node[tile.letter];
			if (next) {
				partial.push(tile);
				right = new Point(point.x + 1, point.y);
				this.extendRight(partial, next, right);
				partial.pop();
			}
		}
	};

	this.recordMove = function(/* array<Tile> */partial, terminal) {
		var plays = [];
		var curPoint = new Point(terminator.x - 1, terminator.y);
		var i = partial.length - 1;

		while (i >= 0 || this.game.inBounds(curPoint)
				&& this.game.letterAt(curPoint)) {
			if (this.inBounds(curPoint) && this.game.letterAt(curPoint)) {
				plays.push({
					"point" : curPoint,
					"tile" : partial.get(i)
				});
			}
			i--;
			curPoint = new Point(cur.x - 1, cur.y);
		}

		var score = -1;
		this.game.placeLetters(plays);
		var errors = game.getErrors();
		if (!errors) {
			score = game.scorePending();
		}
		game.discardPending();

		if (this.game.flipped) {
			var flippedPlays = [];
			for ( var i = 0; i < plays.length; i++) {
				var point = plays[i].point;
				var tile = plays[i].tile;
				flippedPlays.push({
					"point" : new Point(point.y, point.x),
					"tile" : tile
				});
			}
			plays = flippedPlays;
		}

		if (score < 0 || $.inArray(seenMoves, plays)) {
			return;
		} else {
			seenMoves.push(plays);
		}
		options.push({
			"plays" : plays,
			"score" : score
		});
	};
}