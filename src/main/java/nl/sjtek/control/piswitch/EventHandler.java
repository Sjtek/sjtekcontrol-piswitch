package nl.sjtek.control.piswitch;

import com.google.common.eventbus.Subscribe;
import nl.sjtek.control.data.ampq.events.LightEvent;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by wouter on 3-3-17.
 */
public class EventHandler {

    public EventHandler() {
        Bus.regsiter(this);
    }

    @Subscribe
    public void onEvent(LightEvent event) {
        Config config = Config.get();
        if (!config.hasTarget(event.getId())) return;


        String command[] = {
                config.getRcSwitchPath(),
                config.getSystemCode(),
                String.valueOf(event.getId()),
                (event.isEnabled() ? "1" : "0")
        };

        try {
            if (event.useRgb()) {
                System.out.println("Received command with RGB, not supported.");
            }
            System.out.print("ID: " + event.getId() + " STATE: " + (event.isEnabled() ? "1" : "0"));
            System.out.print(" CMD: " + Arrays.toString(command));
            int result = Executor.execute(command);
            System.out.println(" RESULT: " + result);
        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
        } finally {
            System.out.println();
        }
    }
}
