var Test = {};

Test.getTestData = function() {
	  rack = [ {letter: 'i'}, {letter:'g'}, {letter:'e'}, {letter:'r'}];
	board = [
		  [ null, null, null, null, null, null, null, {letter: 'l'}, null, null, null, null, null, null, null ],
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
				[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
				[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ] ];

	return {
		rack : rack,
		board : board
	};
};