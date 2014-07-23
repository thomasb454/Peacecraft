package com.peacecraftec.bukkit.protect.core.interact;

import com.peacecraftec.bukkit.protect.core.Access;

public class ProtectAction implements InteractAction {
	private Access access;

	public ProtectAction(Access access) {
		this.access = access;
	}

	public Access getAccess() {
		return this.access;
	}
}
