package com.peacecraftec.bukkit.internal.selection;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Selector {
    private static final Selector INSTANCE = new Selector();

    public static Selector get() {
        return INSTANCE;
    }

    private Map<String, Selection> selections = new HashMap<String, Selection>();

    private Selector() {
    }

    public Selection getSelection(Player player) {
        return this.selections.get(player.getName());
    }

    public void setSelection(Player player, Selection selection) {
        this.selections.put(player.getName(), selection);
    }

    public void clearPlayer(Player player) {
        this.selections.remove(player.getName());
    }
}
