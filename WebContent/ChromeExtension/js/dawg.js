DawgUtil = {};

DawgUtil.inDict = function(dawg, wordStr) {
  var nextDawg = dawg;
	for ( var i = 0; i < wordStr.length; i++) {
		var letter = wordStr.charAt(i);
    nextDawg = nextDawg[letter];
		if (!nextDawg) {
			break;
		}
	}

	if (nextDawg && nextDawg["te"]) {
		return true;
	}
};

DawgUtil.edges = function(dawg) {
	var edges = [];
	for (var edge in dawg) {
		edges.push(edge);
	}
	return edges;
};

DawgUtil.test = function(game, dawg) {
	var alpha = game.alphabet;
	console.log(DawgUtil.edges(dawg));
	console.log(alpha);
	for (var idx in alpha) {
		console.log(DawgUtil.edges(dawg[alpha[idx]]));
	}
};