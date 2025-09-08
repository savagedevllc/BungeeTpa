package net.savagedev.tpa.plugin.messenger;

import com.google.gson.JsonObject;
import net.savagedev.tpa.common.messaging.AbstractMessenger;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoResponse;
import net.savagedev.tpa.common.messaging.messages.MessageCurrencyFormatResponse;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyResponse;
import net.savagedev.tpa.common.messaging.messages.MessagePlayerInfo;
import net.savagedev.tpa.common.messaging.messages.MessageWhitelistInfo;
import net.savagedev.tpa.common.messaging.messages.MessageWorldInfo;
import net.savagedev.tpa.common.messaging.messages.MessageWorldInfo.Action;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.economy.RemoteEconomyResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BungeeTpMessenger<T> extends AbstractMessenger<T> {
    private static final Map<String, Function<JsonObject, Message>> DECODER_FUNCTIONS = new HashMap<>();

    static {
        DECODER_FUNCTIONS.put(MessagePlayerInfo.class.getSimpleName(), MessagePlayerInfo::deserialize);
        DECODER_FUNCTIONS.put(MessageEconomyResponse.class.getSimpleName(), MessageEconomyResponse::deserialize);
        DECODER_FUNCTIONS.put(MessageBasicServerInfoResponse.class.getSimpleName(), MessageBasicServerInfoResponse::deserialize);
        DECODER_FUNCTIONS.put(MessageCurrencyFormatResponse.class.getSimpleName(), MessageCurrencyFormatResponse::deserialize);
        DECODER_FUNCTIONS.put(MessageWorldInfo.class.getSimpleName(), MessageWorldInfo::deserialize);
        DECODER_FUNCTIONS.put(MessageWhitelistInfo.class.getSimpleName(), MessageWhitelistInfo::deserialize);
    }

    private final Map<String, Consumer<? extends Message>> consumers = new HashMap<>();

    {
        consumers.put(MessagePlayerInfo.class.getSimpleName(), new PlayerInfoConsumer());
        consumers.put(MessageEconomyResponse.class.getSimpleName(), new EconomyWithdrawResponseConsumer());
        consumers.put(MessageBasicServerInfoResponse.class.getSimpleName(), new ServerInfoConsumer());
        consumers.put(MessageCurrencyFormatResponse.class.getSimpleName(), new CurrencyFormatConsumer());
        consumers.put(MessageWorldInfo.class.getSimpleName(), new WorldInfoConsumer());
        consumers.put(MessageWhitelistInfo.class.getSimpleName(), new WhitelistInfoConsumer());
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
            plugin.getPlayer(playerInfo.getUniqueId())
                    .ifPresent(player -> player.setHidden(playerInfo.isHidden()));
        }
    }

    private final class EconomyWithdrawResponseConsumer implements Consumer<MessageEconomyResponse> {
        @Override
        public void accept(MessageEconomyResponse economyResponse) {
            final CompletableFuture<RemoteEconomyResponse> response = plugin.getPlayerManager().removePendingTransaction(economyResponse.getUniqueId());

            if (response == null) {
                return; // IDK what the hell happened if this happens...
            }

            response.complete(new RemoteEconomyResponse(economyResponse.getAmount(),
                    economyResponse.getBalance(),
                    economyResponse.getFormattedAmount(),
                    economyResponse.getFormattedBalance(),
                    economyResponse.wasSuccessful()));
        }
    }

    private final class ServerInfoConsumer implements Consumer<MessageBasicServerInfoResponse> {
        @Override
        public void accept(MessageBasicServerInfoResponse serverInfo) {
            plugin.getServerManager().getOrLoad(serverInfo.getServerId())
                    .ifPresent(server -> {
                        server.setSentBasicInfo(true);
                        server.setServerSoftware(serverInfo.getSoftwareName());
                        server.setBridgeVersion(serverInfo.getBridgeVersion());
                        server.setEconomySupport(serverInfo.hasEconomySupport());
                        server.setWhitelistEnabled(serverInfo.isWhitelisted());
                        server.getAllWorlds().addAll(serverInfo.getWorlds());
                        server.getWhitelist().addAll(serverInfo.getWhitelist());
                    });
        }
    }

    private final class CurrencyFormatConsumer implements Consumer<MessageCurrencyFormatResponse> {
        @Override
        public void accept(MessageCurrencyFormatResponse currencyFormatResponse) {
            final CompletableFuture<String> future = plugin.getServerManager().removeAwaitingCurrencyFormat(currencyFormatResponse.getServerId());

            if (future == null) {
                return;
            }

            future.complete(currencyFormatResponse.getFormattedAmount());
        }
    }

    private final class WorldInfoConsumer implements Consumer<MessageWorldInfo> {
        @Override
        public void accept(MessageWorldInfo worldInfo) {
            plugin.getServerManager().getOrLoad(worldInfo.getServerId())
                    .ifPresent(server -> {
                        final Action action = worldInfo.getAction();
                        if (action == Action.ADD) {
                            server.getAllWorlds().add(worldInfo.getWorldId());
                        } else {
                            server.getAllWorlds().remove(worldInfo.getWorldId());
                        }
                    });
        }
    }

    private final class WhitelistInfoConsumer implements Consumer<MessageWhitelistInfo> {
        @Override
        public void accept(MessageWhitelistInfo whitelistInfo) {
            plugin.getServerManager().getOrLoad(whitelistInfo.getServerId())
                    .ifPresent(server -> {
                        final MessageWhitelistInfo.Action action = whitelistInfo.getAction();
                        if (action == MessageWhitelistInfo.Action.STATUS_CHANGE) {
                            server.setWhitelistEnabled(whitelistInfo.isActive());
                            return;
                        }
                        if (action == MessageWhitelistInfo.Action.ADD) {
                            server.getWhitelist().add(whitelistInfo.getUniqueId());
                        } else {
                            server.getWhitelist().remove(whitelistInfo.getUniqueId());
                        }
                    });
        }
    }
}
