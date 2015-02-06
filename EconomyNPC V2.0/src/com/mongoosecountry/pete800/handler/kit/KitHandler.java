package com.mongoosecountry.pete800.handler.kit;

import java.util.UUID;

public class KitHandler {
	private String npcName;
	private UUID player;
	private int numTokens;
	public String getNpcName() {
		return npcName;
	}
	public void setNpcName(String npcName) {
		this.npcName = npcName;
	}
	public int getNumTokens() {
		return numTokens;
	}
	public void setNumTokens(int numTokens) {
		this.numTokens = numTokens;
	}
	public UUID getPlayer() {
		return player;
	}
	public void setPlayer(UUID player) {
		this.player = player;
	}
}
