var Test = {};

Test.getTestData = function() {
	  rack = [ {letter:'e'}, {letter:'t'}];
	board = [
		[ null, null, null, null, null, null, c('w'), c('a'), c('x'), null, c('d'), null, null, null, null ],
		[ null, null, null, null, null, null, null,   null,   c('e'), null, null,   null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
		[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ] ];

	return {
		rack : rack,
		board : board
	};
};

function c(letter, wildcard) {
    var ret = {letter: letter};
    if (wildcard) {
        ret.wildcard = wildcard;
    }
    return ret;
}