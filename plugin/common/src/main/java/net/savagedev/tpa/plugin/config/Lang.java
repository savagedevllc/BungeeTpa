package net.savagedev.tpa.plugin.config;

import net.savagedev.tpa.plugin.config.loader.ConfigurationLoader;
import net.savagedev.tpa.plugin.config.loader.ConfigurationNode;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public enum Lang {
    INVALID_ARGUMENTS("invalid_arguments"),
    UNKNOWN_PLAYER("unknown_player"),
    PLAYER_OFFLINE("player_offline"),
    RESTRICTED_SERVER("restricted_server"),
    TELEPORT_SELF("teleport_self"),
    NO_REQUESTS("no_requests"),
    NO_REQUEST_FROM("no_request_from"),
    REQUEST_ALREADY_SENT("request_already_sent"),
    REQUEST_ALREADY_RECEIVED("request_already_received"),
    REQUEST_CANCELLED_SENDER("request_cancelled_sender"),
    REQUEST_CANCELLED_RECEIVER("request_cancelled_receiver"),
    PAYMENT_FAILED("payment_failed"),
    TELEPORTING("teleporting"),
    TELEPORT_HERE("teleport_here"),
    TPA_REQUEST_SENT("tpa_request_sent"),
    TPA_REQUEST_SENT_PAID("tpa_request_sent_paid"),
    TPA_REQUEST_RECEIVED("tpa_request_received"),
    TPA_HERE_REQUEST_SENT("tpahere_request_sent"),
    TPA_HERE_REQUEST_SENT_PAID("tpahere_request_sent_paid"),
    TPA_HERE_REQUEST_RECEIVED("tpahere_request_received"),
    TPA_REQUEST_ACCEPT("tpa_request_accept"),
    TPA_REQUEST_ACCEPTED("tpa_request_accepted"),
    TPA_REQUEST_ACCEPTED_DELAYED("tpa_request_accepted_delayed"),
    TPA_HERE_REQUEST_ACCEPT("tpahere_request_accept"),
    TPA_HERE_REQUEST_ACCEPT_DELAYED("tpahere_request_accept_delayed"),
    TPA_HERE_REQUEST_ACCEPTED("tpahere_request_accepted"),
    TPA_REQUEST_DENY("tpa_request_deny"),
    TPA_REQUEST_DENIED("tpa_request_denied"),
    TPA_HERE_REQUEST_DENY("tpahere_request_deny"),
    TPA_HERE_REQUEST_DENIED("tpahere_request_denied"),
    REQUEST_EXPIRED_FROM("request_expired_from"),
    REQUEST_EXPIRED_TO("request_expired_to");

    private static ConfigurationLoader s_Loader;
    private static ConfigurationNode s_RootNode;

    public static void load(ConfigurationLoader loader) {
        s_Loader = loader;
        reload();
    }

    public static void reload() {
        if (s_Loader == null) {
            throw new IllegalStateException("loader");
        }
        s_RootNode = s_Loader.load();
    }

    public static void saveAll() {
        s_Loader.save(s_RootNode);
    }

    private final String key;

    Lang(String key) {
        this.key = key;
    }

    public void save() {
        s_Loader.save(s_RootNode);
    }

    public boolean notExists() {
        return s_RootNode.noChild(this.key);
    }

    public Lang set(Object value) {
        s_RootNode.node(this.key).set(value);
        return this;
    }

    public <M> void send(ProxyPlayer<?, M> player, Placeholder... placeholders) {
        String message = s_RootNode.node(this.key).getString();
        if (message == null || message.isEmpty()) {
            return; // Nothing to send. The message is null, or empty.
        }

        for (Placeholder placeholder : placeholders) { // Replace all placeholders before parsing the message.
            message = message.replace(placeholder.placeholder, placeholder.value);
        }

        player.sendMessage(message);
    }

    public static class Placeholder {
        private final String placeholder;
        private final String value;

        public Placeholder(String placeholder, String value) {
            this.placeholder = placeholder;
            this.value = value;
        }
    }
}
