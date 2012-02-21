DawgUtil = {};

DawgUtil.inDict = function(dawg, wordStr) {

	var nextDawg = null;
	for ( var i = 0; i < wordStr.length; i++) {
		var letter = wordStr.charAt(i);
		nextDawg = dawg[letter];
		if (!nextDawg) {
			break;
		}
	}

	if (nextDawg && nextDawg["_"]) {
		return true;
	}
};