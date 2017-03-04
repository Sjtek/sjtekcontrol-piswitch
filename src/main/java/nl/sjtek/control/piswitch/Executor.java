package nl.sjtek.control.piswitch;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

import java.io.IOException;
import java.io.InputStreamReader;

public class Executor {

    /**
     * Execute a command.
     *
     * @param command
     * @throws IOException
     * @throws InterruptedException
     */
    public synchronized static int execute(String[] command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        return process.exitValue();
    }

    public synchronized static float executeWithResult(String[] command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        process.getOutputStream();
        String text;
        InputStreamReader reader = new InputStreamReader(process.getInputStream(), Charsets.UTF_8);
        boolean threw = true;
        try {
            text = CharStreams.toString(reader);
            threw = false;
        } finally {
            Closeables.close(reader, threw);
        }

        return Float.valueOf(text);
    }
}
