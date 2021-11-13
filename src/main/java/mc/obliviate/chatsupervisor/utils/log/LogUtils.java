package mc.obliviate.chatsupervisor.utils.log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {

	public static File logFile;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

	public static String getDate() {
		return (sdf.format(new Date()));
	}

	public static void log(String text) {
		try {
			final Writer writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8)));

			writer.write("[" + getDate() + "] " + text + "\n");
			writer.flush();
			writer.close();
		} catch (IOException eb) {

			eb.printStackTrace();
		}
	}
}
