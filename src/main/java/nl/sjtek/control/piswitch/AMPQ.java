package nl.sjtek.control.piswitch;

import com.rabbitmq.client.*;
import nl.sjtek.control.data.ampq.events.LightEvent;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by wouter on 3-3-17.
 */
public class AMPQ {

    private static final String EXCHANGE_LIGHTS = "lights";
    private Channel channelAction;
    private Connection connection;
    private final ConnectionFactory factory;

    public AMPQ(String host, String username, String password) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setAutomaticRecoveryEnabled(true);
        new PingThread("https://sjtek.nl/rabbitmq", this::connect).start();
    }

    private void connect() {
        try {
            connection = factory.newConnection();
            channelAction = connection.createChannel();
            channelAction.exchangeDeclare(EXCHANGE_LIGHTS, "fanout");
            String updateQueueName = channelAction.queueDeclare().getQueue();
            channelAction.queueBind(updateQueueName, EXCHANGE_LIGHTS, "");
            channelAction.basicConsume(updateQueueName, true, new MessageConsumer(channelAction));
            System.out.println("Connected to broker.");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
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
