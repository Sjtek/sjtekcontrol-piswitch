package nl.sjtek.control.piswitch;

import com.google.common.eventbus.EventBus;

/**
 * Created by wouter on 3-3-17.
 */
public class Bus {
    private static Bus instance = new Bus();
    private EventBus eventBus;

    private Bus() {
        eventBus = new EventBus();
    }

    public static Bus getInstance() {
        return instance;
    }

    public static void regsiter(Object object) {
        getInstance().getEventBus().register(object);
    }

    public static void unregister(Object object) {
        getInstance().getEventBus().unregister(object);
    }

    public static void post(Object object) {
        getInstance().getEventBus().post(object);
    }

    private EventBus getEventBus() {
        return eventBus;
    }
}
