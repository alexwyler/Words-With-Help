function Chooser(game, rack) {

	this.game = game;
	this.rack = rack;

	this.getOptions = function() {
    this.options = [];
    this.seenMoves = [];
		this.getAcrossOptions();
		this.game.flip();
		this.getAcrossOptions();
		this.game.flip();
		return this.options;
	};

	this.getAcrossOptions = function() {
		for ( var y = 0; y < 15; y++) {
			var anchors = [];
			if (!this.game.empty) {
				for ( var x = 0; x < 15; x++) {
					p = {
						x : x,
						y : y
					};
					right = {
						x : x + 1,
						y : y
					};
					left = {
						x : x - 1,
						y : y
					};
					down = {
						x : x,
						y : y + 1
					};
					up = {
						x : x,
						y : y - 1
					};
					if (this.game.inBounds(right) && this.game.tileAt(right)
							|| this.game.inBounds(down) && this.game.tileAt(down)
							|| this.game.inBounds(left) && this.game.tileAt(left)
							|| this.game.inBounds(up) && this.game.tileAt(up)) {
						anchors.push(p);
					}
				}
			} else {
				if (y == 6) {
					for ( var x = 1; x <= 7; x++) {
						anchors.push({
							             x : x,
							             y : y
						             });
					}
				}
			}

			for ( var i = 0; i < anchors.length; i++) {
				var p = anchors[i];
				var limit = 0;
				var left = {
					x : p.x - 1,
					y : p.y
				};
				while (this.game.inBounds(left) && !this.game.tileAt(left)) {
					limit++;
					left = {
						x : left.x - 1,
						y : left.y
					};
				}
				if (limit == 0) {
					cur = {
						x : p.x - 1,
						y : p.y
					};
					while (this.game.inBounds(cur) && this.game.tileAt(cur)) {
						cur = {
							x : cur.x - 1,
							y : cur.y
						};
					}
					/* array<Tile> */var partial = [];
					/* DawgNode */var node = this.game.dawg;
					cur = {
						x : cur.x + 1,
						y : cur.y
					};
					while (this.game.inBounds(cur) && this.game.tileAt(cur)) {
						partial.push(this.game.tileAt(cur));
						node = node[this.game.tileAt(cur).letter];
						cur = {
							x : cur.x + 1,
							y : cur.y
						};
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
		this.extendRight(partial, node, anchor);
		if (limit > 0) {
			for ( var i = 0; i < this.rack.length; i++) {
				// todo: wtf
				/* Tile */var removed = this.rack.splice(i, 1)[0];
				/* array<Tile> */var toChecks = [];
				if (removed.wildcard) {
					for ( var idx in this.game.alphabet) {
						var letter = this.game.alphabet[idx];
						toChecks.push({
							              letter : letter,
							              wildcard : true
						              });
					}
				} else {
					toChecks.push(removed);
				}

				for ( var j = 0; j < toChecks.length; j++) {
					var toCheck = toChecks[j];
					var next = node[toCheck.letter];
					if (next) {
						partial.push(toCheck);
						this.leftPart(partial, next, limit - 1, anchor);
						partial.pop();
					}
				}
				this.rack.splice(i, 0, removed);
			}
		}
	};

	this.extendRight = function(/* array<Tile> */partial, /* DawgNode */node,
		point) {
		if (node["te"]) {
			this.recordMove(partial, point);
		}
		if (this.game.inBounds(point) && !this.game.tileAt(point)) {
			for ( var i = 0; i < this.rack.length; i++) {
				/* Tile */var removed = this.rack.splice(i, 1)[0];
				/* array<Tile> */var toChecks = [];
				if (removed.wildcard) {
					for ( var idx in this.game.alphabet) {
						var letter = this.game.alphabet[idx];
						toChecks.push({
							              letter : letter,
							              wildcard : true
						              });
					}
				} else {
					toChecks.push(removed);
				}
				for ( var j = 0; j < toChecks.length; j++) {
					var toCheck = toChecks[j];
					var next = node[toCheck.letter];
					if (next) {
						partial.push(toCheck);
						right = {
							x : point.x + 1,
							y : point.y
						};
						this.extendRight(partial, next, right);
						partial.pop();
					}
				}
				this.rack.splice(i, 0, removed);
			}
		} else {
			var tile = this.game.tileAt(point);
			var next = node[tile.letter];
			if (next) {
				partial.push(tile);
				right = {
					x : point.x + 1,
					y : point.y
				};
				this.extendRight(partial, next, right);
				partial.pop();
			}
		}
	};

	this.recordMove = function(/* array<Tile> */partial, terminal) {
		var plays = [];
		var curPoint = {
			x : terminal.x - 1,
			y : terminal.y
		};
		var i = partial.length - 1;

		while (i >= 0 || (this.game.inBounds(curPoint) && this.game.tileAt(curPoint))) {
			if (this.game.inBounds(curPoint) && !this.game.tileAt(curPoint)) {
				plays.push({
					           "point" : curPoint,
					           "tile" : partial[i]
				           });
			}
			i--;
			curPoint = {
				x : curPoint.x - 1,
				y : curPoint.y
			};
		}

		var score = -1;
    var words = "";
		this.game.placeTiles(plays);
		var errors = game.getErrors();
		if (errors) {
		  game.discardPending();
      return;
    }

		score = game.scorePending();
    words = game.createdWordStr;
		game.discardPending();

		if (this.game.flipped) {
			var flippedPlays = [];
			for ( var i = 0; i < plays.length; i++) {
				var point = plays[i].point;
				var tile = plays[i].tile;
				flippedPlays.push({
					                  "point" : {
						                  x : point.y,
						                  y : point.x
					                  },
					                  "tile" : tile
				                  });
			}
			plays = flippedPlays;
		}

		var option = {
			"plays" : plays,
			"score" : score,
      "words" : words
		};

    var str = JSON.stringify(game.orderPoints(plays));
    if (this.seenMoves.indexOf(str) >= 0) {
      return;
    } else {
		  options.push(option);
      this.seenMoves.push(str);
    }
	};
}