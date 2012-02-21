var Test = {};

Test.getTestData = function() {
	rack = [ {letter: 'w'}, {letter:'o'}, {letter:'r'}];
	board = [
		    [ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
				[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
				[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
				[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
				[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
				[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
				[ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ],
				[ null, null, null, null, null, {letter: 'd'}, null, null, null, null, null, null, null, null, null ],
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