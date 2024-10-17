package net.savagedev.tpa.bridge.messenger;

import com.google.gson.JsonObject;
import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.bridge.model.Location;
import net.savagedev.tpa.common.hook.economy.EconomyResponse;
import net.savagedev.tpa.common.messaging.AbstractMessenger;
import net.savagedev.tpa.common.messaging.messages.AbstractEconomyRequest;
import net.savagedev.tpa.common.messaging.messages.AbstractMessageRequestTeleport.Type;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoRequest;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoResponse;
import net.savagedev.tpa.common.messaging.messages.MessageCurrencyFormatRequest;
import net.savagedev.tpa.common.messaging.messages.MessageCurrencyFormatResponse;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyDepositRequest;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyResponse;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyWithdrawRequest;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleportCoords;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleportPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public abstract class BungeeTpBridgeMessenger<T> extends AbstractMessenger<T> {
    private static final Map<String, Function<JsonObject, Message>> DECODER_FUNCTIONS = new HashMap<>();

    static {
        DECODER_FUNCTIONS.put(MessageRequestTeleportPlayer.class.getSimpleName(), MessageRequestTeleportPlayer::deserialize);
        DECODER_FUNCTIONS.put(MessageRequestTeleportCoords.class.getSimpleName(), MessageRequestTeleportCoords::deserialize);
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
        if (message instanceof MessageRequestTeleportPlayer) {
            this.handleTeleportRequest((MessageRequestTeleportPlayer) message);
            return;
        }

        if (message instanceof MessageRequestTeleportCoords) {
            this.handleTeleportRequest((MessageRequestTeleportCoords) message);
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

    private void handleTeleportRequest(MessageRequestTeleportPlayer request) {
        final UUID requesterId = request.getRequester();
        final BungeeTpPlayer receiver = this.platform.getBungeeTpPlayer(request.getReceiver());

        if (request.getType() == Type.INSTANT) {
            final BungeeTpPlayer requester = this.platform.getBungeeTpPlayer(requesterId);
            requester.teleportTo(receiver);
        } else {
            this.platform.getTpCache().put(requesterId, receiver);
        }
    }

    private void handleTeleportRequest(MessageRequestTeleportCoords request) {
        final UUID requesterId = request.getRequester();
        final Location location = Location.fromMessage(request);

        if (request.getType() == Type.INSTANT) {
            final BungeeTpPlayer requester = this.platform.getBungeeTpPlayer(requesterId);
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
        this.sendData(new MessageBasicServerInfoResponse(this.platform.getSoftwareName(), this.platform.getVersion(),
                this.platform.getEconomyProvider().isPresent(), this.platform.getAllWorlds()));
    }

    private void handleCurrencyFormatRequest(MessageCurrencyFormatRequest request) {
        if (this.platform.getEconomyProvider().isPresent()) {
            this.sendData(new MessageCurrencyFormatResponse(request.getAmount(), this.platform.getEconomyProvider().get().format(request.getAmount())));
        } else {
            this.sendData(new MessageCurrencyFormatResponse(request.getAmount(), String.valueOf(request.getAmount())));
        }
    }
}
