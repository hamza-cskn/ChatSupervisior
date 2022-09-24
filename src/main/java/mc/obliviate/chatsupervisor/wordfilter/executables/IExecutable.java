package mc.obliviate.chatsupervisor.wordfilter.executables;

import mc.obliviate.chatsupervisor.wordfilter.regex.MatchedStringMeta;

/**
 * Purpose of child classes of IExecutable is,
 * executing commands, sending message or make another
 * executable stuff.
 */
public interface IExecutable {

	void execute(MatchedStringMeta matchedStringMeta);

}
