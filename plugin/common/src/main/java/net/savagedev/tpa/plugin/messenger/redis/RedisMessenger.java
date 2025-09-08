package net.savagedev.tpa.plugin.messenger.redis;

import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.messenger.BungeeTpMessenger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/*
 * Yes, I know it's bizarre and isn't D.R.Y. compliant to have each platform to have its own implementation of Redis/RabbitMQ messenger.
 * It's only structured like this since each platform only has "decoder functions" that are necessary for its respective platform.
 */
public class RedisMessenger extends BungeeTpMessenger<Void> implements BiConsumer<String, String> {
    private static final Executor JEDIS_PUBSUB_EXECUTOR = Executors.newSingleThreadExecutor();

    private final JedisPubSub pubSubListener = new LocalPubSubListener(this);

    private final String host;
    private final int port;

    private final String user;
    private final String password;

    private JedisPool pool;

    public RedisMessenger(BungeeTpPlugin plugin, String host, int port, String user, String password) throws IllegalArgumentException {
        super(plugin);

        if (host == null) {
            throw new IllegalArgumentException("host cannot be null");
        }
        this.host = host;

        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 0 and 65535");
        }
        this.port = port;

        // These can be null - that's fine.
        this.user = user;
        this.password = password;
    }

    @Override
    public void init() {
        if (this.pool != null) {
            throw new IllegalStateException("pool has already been initialized");
        }

        this.pool = new JedisPool(this.host, this.port, this.user, this.password);

        try (final Jedis jedis = this.pool.getResource()) {
            JEDIS_PUBSUB_EXECUTOR.execute(() -> jedis.subscribe(this.pubSubListener, ChannelConstants.CHANNEL_NAME));
        }
    }

    @Override
    public void shutdown() {
        if (this.pool == null) {
            throw new IllegalStateException("pool has not been initialized");
        }
        this.pool.close();
        this.pool = null;
    }

    @Override
    public void sendData(Void unused, Message message) {
        this.sendData(message);
    }

    @Override
    public void sendData(Message message) {
        try (final Jedis jedis = this.pool.getResource()) {
            jedis.publish(ChannelConstants.CHANNEL_NAME, message.serialize());
        }
    }

    @Override
    public void accept(String channel, String message) {
        // TODO: I somehow NEED to get the serverId here... But the server itself doesn't know its ID... :thonk:
        // ^ Or do I? ...
        super.handleIncomingMessage(null, channel, message.getBytes(StandardCharsets.UTF_8));
    }

    // TODO: Move this to the common module. This can just be reused across both platforms.
    //       Do the same with the RabbitMQ implementation.
    private static final class LocalPubSubListener extends JedisPubSub {
        private final BiConsumer<String, String> messageConsumer;

        private LocalPubSubListener(BiConsumer<String, String> messageConsumer) {
            this.messageConsumer = messageConsumer;
        }

        @Override
        public void onMessage(String channel, String message) {
            this.messageConsumer.accept(channel, message);
        }
    }
}
