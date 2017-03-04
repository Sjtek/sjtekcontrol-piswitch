package nl.sjtek.control.piswitch;

import nl.sjtek.control.data.ampq.events.TemperatureEvent;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wouter on 3-3-17.
 */
public class Temperature {

    private static final String[] COMMAND = {"/usr/bin/temperature"};
    private static final int ID = 1;
    private final ScheduledThreadPoolExecutor executor;

    public Temperature() {
        executor = new ScheduledThreadPoolExecutor(5);
        executor.setRemoveOnCancelPolicy(true);
        executor.schedule(this::sendTemperature, 1, TimeUnit.MINUTES);
    }

    private void sendTemperature() {
        try {
            float temperature = Executor.executeWithResult(COMMAND);
            Bus.post(new TemperatureEvent(ID, temperature));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
