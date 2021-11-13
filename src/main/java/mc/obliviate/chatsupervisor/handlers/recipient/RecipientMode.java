package mc.obliviate.chatsupervisor.handlers.recipient;

public enum RecipientMode {

	/**
	 * All messages sends all players.
	 */
	DEFAULT,

	/**
	 * Default messages sends to ranged players.
	 * Global chat messages (prefixed) sends to all players.
	 */
	LOCAL,

	/**
	 * Default messages sends to ranged players.
	 * Per-World-Global chat messages (prefixed) sends to players in the same world.
	 * Global messages (prefixed with another string) sends to all players.
	 */
	PER_WORLD_GLOBAL,

	/**
	 * All messages sends to players in the same world.
	 */
	PER_WORLD

}
