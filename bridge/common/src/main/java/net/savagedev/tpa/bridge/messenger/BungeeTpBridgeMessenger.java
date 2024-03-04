package net.savagedev.tpa.bridge.messenger;

import com.google.gson.JsonObject;
import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.hook.economy.EconomyResponse;
import net.savagedev.tpa.common.messaging.AbstractMessenger;
import net.savagedev.tpa.common.messaging.messages.AbstractEconomyRequest;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoRequest;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoResponse;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyDepositRequest;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyResponse;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyWithdrawRequest;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport.Type;

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
    }

    private void handleTeleportRequest(MessageRequestTeleport request) {
        final UUID requesterId = request.getRequester();
        final BungeeTpPlayer receiver = this.platform.getBungeeTpPlayer(request.getReceiver());

        if (request.getType() == Type.INSTANT) {
            final BungeeTpPlayer requester = this.platform.getBungeeTpPlayer(requesterId);
            requester.teleportTo(receiver);
        } else {
            this.platform.getTpCache().put(requesterId, receiver.getUniqueId());
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
}
