package net.savagedev.tpa.bridge.messenger;

import com.google.gson.JsonObject;
import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.bridge.model.Location;
import net.savagedev.tpa.common.hook.economy.EconomyResponse;
import net.savagedev.tpa.common.messaging.AbstractMessenger;
import net.savagedev.tpa.common.messaging.messages.AbstractEconomyRequest;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoRequest;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoResponse;
import net.savagedev.tpa.common.messaging.messages.MessageCurrencyFormatRequest;
import net.savagedev.tpa.common.messaging.messages.MessageCurrencyFormatResponse;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyDepositRequest;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyResponse;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyWithdrawRequest;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport.TeleportTime;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport.TeleportType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public abstract class BungeeTpBridgeMessenger<T> extends AbstractMessenger<T> {
    private static final Map<String, Function<JsonObject, Message>> DECODER_FUNCTIONS = new HashMap<>();

    static {
        DECODER_FUNCTIONS.put(MessageRequestTeleport.class.getSimpleName(), MessageRequestTeleport::deserialize);
        DECODER_FUNCTIONS.put(MessageEconomyWithdrawRequest.class.getSimpleName(), MessageEconomyWithdrawRequest::deserialize);
        DECODER_FUNCTIONS.put(MessageEconomyDepositRequest.class.getSimpleName(), MessageEconomyDepositRequest::deserialize);
        DECODER_FUNCTIONS.put(MessageBasicServerInfoRequest.class.getSimpleName(), MessageBasicServerInfoRequest::deserialize);
        DECODER_FUNCTIONS.put(MessageCurrencyFormatRequest.class.getSimpleName(), MessageCurrencyFormatRequest::deserialize);
    }

    private final BungeeTpBridgePlatform platform;

    public BungeeTpBridgeMessenger(BungeeTpBridgePlatform platform) {
        super(DECODER_FUNCTIONS);
        this.platform = platform;
    }

    @Override
    public void handleIncomingMessage(Message message) {
        if (message instanceof MessageRequestTeleport) {
            this.handleTeleportRequest((MessageRequestTeleport) message);
            return;
        }

        if (message instanceof MessageEconomyWithdrawRequest) {
            this.handleEconomyRequest((MessageEconomyWithdrawRequest) message);
        }

        if (message instanceof MessageEconomyDepositRequest) {
            this.handleEconomyRequest((MessageEconomyDepositRequest) message);
        }

        if (message instanceof MessageBasicServerInfoRequest) {
            this.handleBasicInfoRequest((MessageBasicServerInfoRequest) message);
        }

        if (message instanceof MessageCurrencyFormatRequest) {
            this.handleCurrencyFormatRequest((MessageCurrencyFormatRequest) message);
        }
    }

    private void handleTeleportRequest(MessageRequestTeleport request) {
        final UUID requesterId = request.getRequester();

        final Location location;
        if (request.getTeleportType() == TeleportType.PLAYER_LOCATION) {
            final BungeeTpPlayer receiver = this.platform.getBungeeTpPlayer(request.getReceiver()
                    .orElseThrow(() -> new IllegalStateException("Malformed teleport request message.")));
            if (receiver == null) {
                // TODO: Send the sender a message somehow that the receiver is offline now.
                return;
            }
            location = receiver.getLocation();
        } else { // Only one other type... For now anyway...
            location = new Location(request.getWorldName().orElse(this.platform.getDefaultWorld()),
                    request.getX(), request.getY(), request.getZ());
        }

        if (request.getTeleportTime() == TeleportTime.INSTANT) {
            final BungeeTpPlayer requester = this.platform.getBungeeTpPlayer(requesterId);
            if (requester == null) {
                throw new IllegalStateException("TeleportTime cannot be INSTANT if the target is offline!");
            }
            requester.teleportTo(location);
        } else {
            this.platform.getTpCache().put(requesterId, location);
        }
    }

    private void handleEconomyRequest(AbstractEconomyRequest request) {
        final BungeeTpPlayer player = this.platform.getBungeeTpPlayer(request.getUniqueId());

        if (player == null) {
            return;
        }

        final Optional<AbstractEconomyHook> optionalEconomy = this.platform.getEconomyProvider();

        if (!optionalEconomy.isPresent()) {
            return;
        }

        final AbstractEconomyHook economy = optionalEconomy.get();

        EconomyResponse response;
        if (request instanceof MessageEconomyDepositRequest) {
            response = economy.deposit(player, request.getAmount());
        } else {
            response = economy.withdraw(player, request.getAmount());
        }

        this.sendData(new MessageEconomyResponse(player.getUniqueId(),
                response.getAmount(),
                response.getBalance(),
                economy.format(response.getAmount()),
                economy.format(response.getBalance()),
                response.isSuccess()));
    }

    private void handleBasicInfoRequest(MessageBasicServerInfoRequest ignored) {
        this.sendData(new MessageBasicServerInfoResponse(this.platform.getSoftwareName(), this.platform.getVersion(), this.platform.getEconomyProvider().isPresent()));
    }

    private void handleCurrencyFormatRequest(MessageCurrencyFormatRequest request) {
        if (this.platform.getEconomyProvider().isPresent()) {
            this.sendData(new MessageCurrencyFormatResponse(request.getAmount(), this.platform.getEconomyProvider().get().format(request.getAmount())));
        } else {
            this.sendData(new MessageCurrencyFormatResponse(request.getAmount(), String.valueOf(request.getAmount())));
        }
    }
}
