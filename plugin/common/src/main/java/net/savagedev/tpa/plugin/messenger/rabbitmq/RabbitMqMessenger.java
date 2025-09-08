package net.savagedev.tpa.plugin.messenger.rabbitmq;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.messenger.BungeeTpMessenger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

// Hold off on this for now - it isn't necessary. There are bigger fish to fry.
public class RabbitMqMessenger extends BungeeTpMessenger<Void> implements BiConsumer<String, byte[]> {
    private final Consumer rabbitMqConsumer = new LocalRabbitMqListener(this);

    private final String host;
    private final int port;

    private final String user;
    private final String password;

    private Connection connection;
    private Channel channel;

    public RabbitMqMessenger(BungeeTpPlugin plugin, String host, int port, String user, String password) {
        super(plugin);

        if (host == null) {
            throw new IllegalArgumentException("host cannot be null");
        }
        this.host = host;

        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 0 and 65535");
        }
        this.port = port;

        this.user = user;
        this.password = password;
    }

    @Override
    public void init() {
        if (this.connection != null) {
            throw new IllegalStateException("connection has already been initialized");
        }

        final ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(this.host);
        factory.setPort(this.port);
        factory.setUsername(this.user);
        factory.setPassword(this.password);

        try {
            this.connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        try {
            this.channel = this.connection.createChannel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.channel.exchangeDeclare(ChannelConstants.CHANNEL_NAME, "fanout");
            final String queueName = this.channel.queueDeclare().getQueue();
            this.channel.queueBind(queueName, ChannelConstants.CHANNEL_NAME, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            //                        queueName,                      autoAck, consumer
            this.channel.basicConsume(ChannelConstants.CHANNEL_NAME, false, this.rabbitMqConsumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        if (this.connection == null) {
            throw new IllegalStateException("connection has not been initialized");
        }
        try {
            this.connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendData(Void unused, Message message) {
    }

    @Override
    public void accept(String channel, byte[] message) {
        // TODO: Get the serverId somehow, or figure out a way to not need the serverId.
        super.handleIncomingMessage(null, channel, message);
    }

    private static final class LocalRabbitMqListener implements Consumer {
        private final BiConsumer<String, byte[]> messageConsumer;

        private LocalRabbitMqListener(BiConsumer<String, byte[]> messageConsumer) {
            this.messageConsumer = messageConsumer;
        }

        @Override
        public void handleConsumeOk(String s) {
        }

        @Override
        public void handleCancelOk(String s) {
        }

        @Override
        public void handleCancel(String s) throws IOException {
        }

        @Override
        public void handleShutdownSignal(String s, ShutdownSignalException e) {
        }

        @Override
        public void handleRecoverOk(String s) {
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties basicProperties, byte[] body) {
            this.messageConsumer.accept(consumerTag, body);
        }
    }
}
