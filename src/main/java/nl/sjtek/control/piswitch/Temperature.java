package nl.sjtek.control.piswitch;

import io.habets.javautils.Bus;
import io.habets.javautils.Executor;
import nl.sjtek.control.data.ampq.events.SensorEvent;
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
            Executor.Result result = Executor.execute(new String[]{Config.get().getTemperatureCommand()});
            float temp = Float.parseFloat(result.getData());
            Bus.post(new TemperatureEvent(ID, temp));
            Bus.post(new SensorEvent(SensorEvent.Type.TEMPERATURE, ID, temp));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
