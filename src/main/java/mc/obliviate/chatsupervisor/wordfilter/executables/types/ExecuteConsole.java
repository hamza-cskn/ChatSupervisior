package mc.obliviate.chatsupervisor.wordfilter.executables.types;

import mc.obliviate.chatsupervisor.ChatSupervisor;
import mc.obliviate.chatsupervisor.wordfilter.executables.IExecutable;
import mc.obliviate.chatsupervisor.wordfilter.regex.MatchedStringMeta;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ExecuteConsole implements IExecutable {

	public static final String prefix = "[EXECUTE_CONSOLE]";
	private final String rawString;
	private final String command;

	public ExecuteConsole(String rawString) {
		this.rawString = rawString;
		this.command = rawString.substring(prefix.length()+1);
	}

	@Override
	public void execute(MatchedStringMeta matchedStringMeta) {
		if (matchedStringMeta.getPlayer() == null) return;
		if (!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(ChatSupervisor.class), () -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			});
		} else {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		}
	}
}
