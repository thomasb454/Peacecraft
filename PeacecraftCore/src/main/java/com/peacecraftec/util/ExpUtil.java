package com.peacecraftec.util;

import java.util.Arrays;

public class ExpUtil {
	
	private static int hardMaxLevel = 100000;
	private static int xpRequiredForNextLevel[];
	private static int xpTotalToReachLevel[];

	static {
		initLookupTables(50);
	}

	private static void initLookupTables(int maxLevel) {
		xpRequiredForNextLevel = new int[maxLevel];
		xpTotalToReachLevel = new int[maxLevel];
		xpTotalToReachLevel[0] = 0;
		int incr = 17;
		for(int i = 1; i < xpTotalToReachLevel.length; i++) {
			xpRequiredForNextLevel[i - 1] = incr;
			xpTotalToReachLevel[i] = xpTotalToReachLevel[i - 1] + incr;
			if(i >= 30) {
				incr += 7;
			} else if(i >= 16) {
				incr += 3;
			}
		}
		
		xpRequiredForNextLevel[xpRequiredForNextLevel.length - 1] = incr;
	}

	private static int calculateLevelForExp(int exp) {
		int level = 0;
		int curExp = 7;
		int incr = 10;
		while(curExp <= exp) {
			curExp += incr;
			level++;
			incr += (level % 2 == 0) ? 3 : 4;
		}
		
		return level;
	}
	
	public static int getCurrentExp(int level, float exp) {
		return getXpForLevel(level) + (int) Math.round(xpRequiredForNextLevel[level] * exp);
	}

	public static int getLevelForExp(int exp) {
		if(exp <= 0) return 0;
		if(exp > xpTotalToReachLevel[xpTotalToReachLevel.length - 1]) {
			int newMax = calculateLevelForExp(exp) * 2;
			if(newMax > hardMaxLevel) {
				throw new IllegalArgumentException("Level for exp " + exp + " > hard max level " + hardMaxLevel);
			}
			
			initLookupTables(newMax);
		}
		
		int pos = Arrays.binarySearch(xpTotalToReachLevel, exp);
		return pos < 0 ? -pos - 2 : pos;
	}

	public static int getXpForLevel(int level) {
		if(level > hardMaxLevel) {
			throw new IllegalArgumentException("Level " + level + " > hard max level " + hardMaxLevel);
		}

		if(level >= xpTotalToReachLevel.length) {
			initLookupTables(level * 2);
		}
		
		return xpTotalToReachLevel[level];
	}
	
}
