package com.peacecraftec.web.stats.disabled;

import java.util.HashMap;
import java.util.Map;

import com.peacecraftec.web.stats.StatSystem;

public class DisabledStatSystem implements StatSystem {

	@Override
	public Map<String, Map<String, Double>> getAll() {
		return new HashMap<String, Map<String, Double>>();
	}

	@Override
	public Map<String, Double> getAll(String player) {
		return new HashMap<String, Double>();
	}

	@Override
	public void addPlayer(String player) {
	}

	@Override
	public boolean contains(String category, String player) {
		return false;
	}

	@Override
	public byte getByte(String stat, String player) {
		return 0;
	}

	@Override
	public boolean getBoolean(String stat, String player) {
		return false;
	}

	@Override
	public char getChar(String stat, String player) {
		return 0;
	}

	@Override
	public short getShort(String stat, String player) {
		return 0;
	}

	@Override
	public int getInt(String stat, String player) {
		return 0;
	}

	@Override
	public long getLong(String stat, String player) {
		return 0;
	}

	@Override
	public float getFloat(String stat, String player) {
		return 0;
	}

	@Override
	public double getDouble(String stat, String player) {
		return 0;
	}

	@Override
	public void setStat(String stat, String player, byte value) {
	}

	@Override
	public void setStat(String stat, String player, boolean value) {
	}

	@Override
	public void setStat(String stat, String player, char value) {
	}

	@Override
	public void setStat(String stat, String player, short value) {
	}

	@Override
	public void setStat(String stat, String player, int value) {
	}

	@Override
	public void setStat(String stat, String player, long value) {
	}

	@Override
	public void setStat(String stat, String player, float value) {
	}

	@Override
	public void setStat(String stat, String player, double value) {
	}

	@Override
	public void increment(String stat, String player) {
	}

	@Override
	public void increment(String stat, String player, double amount) {
	}

	@Override
	public void cleanup() {
	}

}
