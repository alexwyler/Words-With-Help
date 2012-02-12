package com.alexwyler.wwc.chooser;

import java.util.List;

import com.alexwyler.wwc.GameStateException;

public abstract class PlayChooser {

	public abstract List<PlayOption> getOptions() throws GameStateException;
}
