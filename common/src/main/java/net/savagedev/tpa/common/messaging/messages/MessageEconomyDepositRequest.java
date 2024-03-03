package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageEconomyDepositRequest extends AbstractEconomyRequest {
    public static MessageEconomyDepositRequest deserialize(JsonObject object) {
        final long mostSigBits = object.get("msb").getAsLong();
        final long leastSigBits = object.get("lsb").getAsLong();
        return new MessageEconomyDepositRequest(new UUID(mostSigBits, leastSigBits), object.get("amt").getAsDouble());
    }

    public MessageEconomyDepositRequest(UUID uniqueId, double amount) {
        super(uniqueId, amount);
    }
}
