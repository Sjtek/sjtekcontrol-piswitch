package nl.sjtek.control.piswitch;

import java.io.IOException;

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
}
