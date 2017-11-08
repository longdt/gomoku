package com.solt.game.gomoku;

import com.solt.game.save.ReplayInputStream;
import com.solt.game.save.ReplayOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Replay {
	private Symbol wonPlayer;
	private List<Move> history;

	public Replay() {
		history = new ArrayList<>();
	}

	public void addMove(Point p, Symbol player) {
        addMove(new Move(p, player));
	}

	public void addMove(Move move) {
        history.add(move);
	}

	public Symbol getWonPlayer() {
		return wonPlayer;
	}
	
	

	public void setWonPlayer(Symbol wonPlayer) {
		this.wonPlayer = wonPlayer;
	}

	public List<Move> getHistory() {
		return history;
	}

	public void saveMatch(String filesave) throws IOException {
		saveMatch(new FileOutputStream(filesave, true));
	}

	public void saveMatch(OutputStream out) throws IOException {
		try (ReplayOutputStream ros = new ReplayOutputStream(new BufferedOutputStream(out))) {
			ros.write(this);
		}
	}

	
	public static Replay loadFirstMatch(String filesave) throws IOException {
		return loadFirstMatch(new FileInputStream(filesave));
	}

	public static Replay loadLastMatch(String filesave) throws IOException {
		Replay last = null;
		Replay replay;
		try (ReplayInputStream ris = new ReplayInputStream(new BufferedInputStream(new FileInputStream(filesave)))) {
			while ((replay = ris.read()) != null) {
				last = replay;
			}
			return last;
		}
	}

	public static Replay loadFirstMatch(InputStream in) throws IOException {
		try (ReplayInputStream ris = new ReplayInputStream(new BufferedInputStream(in))) {
			return ris.read();
		}
	}

	public void reset() {
		wonPlayer = null;
		history.clear();
	}

	public void undo() {
		if (!history.isEmpty()) {
			history.remove(history.size() - 1);
		}
	}
}
