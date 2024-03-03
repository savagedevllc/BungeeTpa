package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageEconomyWithdrawRequest extends AbstractEconomyRequest {
    public static MessageEconomyWithdrawRequest deserialize(JsonObject object) {
        final long mostSigBits = object.get("msb").getAsLong();
        final long leastSigBits = object.get("lsb").getAsLong();
        return new MessageEconomyWithdrawRequest(new UUID(mostSigBits, leastSigBits), object.get("amt").getAsDouble());
    }

    public MessageEconomyWithdrawRequest(UUID uniqueId, double amount) {
        super(uniqueId, amount);
    }
}
