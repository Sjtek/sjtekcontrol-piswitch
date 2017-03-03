package nl.sjtek.control.piswitch;

import java.io.IOException;

/**
 * Created by wouter on 3-3-17.
 */
public class PiSwitch {

    public static void main(String args[]) throws IOException {
        Config.init(args[0]);
        AMPQ ampq = new AMPQ(
                Config.get().getHost(),
                Config.get().getUsername(),
                Config.get().getPassword()
        );
        EventHandler eventHandler = new EventHandler();
        System.in.read();
    }
}
