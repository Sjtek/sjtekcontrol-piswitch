package nl.sjtek.control.piswitch;

import com.google.common.eventbus.Subscribe;
import com.rabbitmq.client.*;
import io.habets.javautils.Bus;
import io.habets.javautils.PingThread;
import nl.sjtek.control.data.ampq.events.LightEvent;
import nl.sjtek.control.data.ampq.events.LightStateEvent;
import nl.sjtek.control.data.ampq.events.SensorEvent;
import nl.sjtek.control.data.ampq.events.TemperatureEvent;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by wouter on 3-3-17.
 */
public class AMPQ {

    private static final String EXCHANGE_LIGHTS = "lights";
    private static final String EXCHANGE_TEMPERATURE = "temperature";
    private static final String EXCHANGE_LIGHT_STATE = "lights_state";
    private static final String EXCHANGE_SENSORS = "sensors";
    private final ConnectionFactory factory;
    private Channel channelAction;
    private Channel channelTemperature;
    private Channel channelLightState;
    private Channel channelSensors;
    private Connection connection;

    public AMPQ(String host, String username, String password) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setAutomaticRecoveryEnabled(true);
        new PingThread("https://sjtek.nl/rabbitmq", 1000, this::connect).start();
        Bus.regsiter(this);
    }

    private void connect() {
        try {
            connection = factory.newConnection();

            channelAction = connection.createChannel();
            channelAction.exchangeDeclare(EXCHANGE_LIGHTS, "fanout");
            String updateQueueName = channelAction.queueDeclare().getQueue();
            channelAction.queueBind(updateQueueName, EXCHANGE_LIGHTS, "");
            channelAction.basicConsume(updateQueueName, true, new MessageConsumer(channelAction));

            channelTemperature = connection.createChannel();
            channelTemperature.exchangeDeclare(EXCHANGE_TEMPERATURE, "fanout");

            channelLightState = connection.createChannel();
            channelLightState.exchangeDeclare(EXCHANGE_LIGHT_STATE, BuiltinExchangeType.FANOUT);

            channelSensors = connection.createChannel();
            channelSensors.exchangeDeclare(EXCHANGE_SENSORS, BuiltinExchangeType.FANOUT);

            System.out.println("Connected to broker.");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onTemperatureUpdate(TemperatureEvent event) {
        if (channelTemperature != null && channelTemperature.isOpen()) {
            try {
                channelTemperature.basicPublish(EXCHANGE_TEMPERATURE, "", null, event.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void onSensorUpdate(SensorEvent event) {
        if (channelSensors != null && channelSensors.isOpen()) {
            try {
                channelSensors.basicPublish(EXCHANGE_SENSORS, "", null, event.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Exchange " + EXCHANGE_SENSORS + " not open.");
        }
    }

    public void onStateChange(LightStateEvent event) {
        if (channelLightState != null && channelLightState.isOpen()) {
            try {
                channelLightState.basicPublish(EXCHANGE_LIGHT_STATE, "", null, event.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Exchange " + EXCHANGE_LIGHT_STATE + " not open.");
        }
    }

    private static class MessageConsumer extends DefaultConsumer {

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public MessageConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            LightEvent event = new LightEvent(new String(body));
            Bus.post(event);
        }
    }
}
