package net.savagedev.tpa.plugin.messenger;

import com.google.gson.JsonObject;
import net.savagedev.tpa.common.messaging.AbstractMessenger;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyWithdrawResponse;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoResponse;
import net.savagedev.tpa.common.messaging.messages.MessagePlayerInfo;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BungeeTpMessenger<T extends Server<?>> extends AbstractMessenger<T> {
    private static final Map<String, Function<JsonObject, Message>> DECODER_FUNCTIONS = new HashMap<>();

    static {
        DECODER_FUNCTIONS.put(MessagePlayerInfo.class.getSimpleName(), MessagePlayerInfo::deserialize);
        DECODER_FUNCTIONS.put(MessageEconomyWithdrawResponse.class.getSimpleName(), MessageEconomyWithdrawResponse::deserialize);
        DECODER_FUNCTIONS.put(MessageBasicServerInfoResponse.class.getSimpleName(), MessageBasicServerInfoResponse::deserialize);
    }

    private final Map<String, Consumer<? extends Message>> consumers = new HashMap<>();

    {
        consumers.put(MessagePlayerInfo.class.getSimpleName(), new PlayerInfoConsumer());
        consumers.put(MessageBasicServerInfoResponse.class.getSimpleName(), new ServerInfoConsumer());
    }

    private final BungeeTpPlugin plugin;

    public BungeeTpMessenger(BungeeTpPlugin plugin) {
        super(DECODER_FUNCTIONS);
        this.plugin = plugin;
    }

    @Override
    public void handleIncomingMessage(Message message) {
        this.getConsumer(message.getClass().getSimpleName()).accept(message);
    }

    private <M> Consumer<M> getConsumer(String name) {
        return (Consumer<M>) this.consumers.get(name);
    }

    private final class PlayerInfoConsumer implements Consumer<MessagePlayerInfo> {
        @Override
        public void accept(MessagePlayerInfo playerInfo) {
            if (playerInfo == null) {
                return;
            }

            plugin.getPlayer(playerInfo.getUniqueId())
                    .ifPresent(player -> player.setHidden(playerInfo.isHidden()));
        }
    }

    private final class ServerInfoConsumer implements Consumer<MessageBasicServerInfoResponse> {
        @Override
        public void accept(MessageBasicServerInfoResponse serverInfo) {
            if (serverInfo == null) {
                return;
            }

            plugin.getServerManager().getOrLoad(serverInfo.getServerId())
                    .ifPresent(server -> {
                        server.setSentBasicInfo(true);
                        server.setEconomySupport(serverInfo.hasEconomySupport());
                    });
        }
    }
}
