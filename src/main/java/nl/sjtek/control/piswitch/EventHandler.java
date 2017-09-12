package nl.sjtek.control.piswitch;

import com.google.common.eventbus.Subscribe;
import io.habets.javautils.Bus;
import io.habets.javautils.Executor;
import nl.sjtek.control.data.amqp.SwitchEvent;
import nl.sjtek.control.data.amqp.SwitchStateEvent;

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
    public void onEvent(SwitchEvent event) {
        Config config = Config.get();
        Integer id = config.getTarget(event.getId());
        if (id == null) return;


        String command[] = {
                config.getRcSwitchPath(),
                config.getSystemCode(),
                String.valueOf(id),
                (event.getState() ? "1" : "0")
        };

        try {
            if (event.getUseRgb()) {
                System.out.println("Received command with RGB, not supported.");
            }
            System.out.print("ID: " + id + " STATE: " + (event.getState() ? "1" : "0"));
            System.out.print(" CMD: " + Arrays.toString(command));
            int result = Executor.execute(command).getReturnCode();
            System.out.println(" RESULT: " + result);
            if (result == 0) {
                Bus.post(new SwitchStateEvent(event.getId(), event.getState()));
            }
        } catch (IOException | InterruptedException ignored) {
        } finally {
            System.out.println();
        }
    }
}
