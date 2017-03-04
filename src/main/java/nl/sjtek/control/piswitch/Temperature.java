package nl.sjtek.control.piswitch;

import nl.sjtek.control.data.ampq.events.TemperatureEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wouter on 3-3-17.
 */
public class Temperature {

    private static final int ID = 1;
    private final ScheduledThreadPoolExecutor executor;

    public Temperature() {
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
        executor.scheduleAtFixedRate(this::sendTemperature, 30, 30, TimeUnit.SECONDS);
    }

    private void sendTemperature() {
        try {
            float temperature = Executor.executeWithResult(new String[]{Config.get().getTemperatureCommand()});
            Bus.post(new TemperatureEvent(ID, temperature));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
