package net.savagedev.tpa.plugin.config.updates;

import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Setting;

public class ConfigUpdater_v2 extends AbstractConfigUpdater {
    @Override
    protected void applyLangUpdates() {
        boolean anyUpdated = false;
        if (Lang.NO_REQUEST_FROM.notExists()) {
            Lang.NO_REQUEST_FROM.set("&cYou don't have any requests from %player%");
            anyUpdated = true;
        }
        if (Lang.REQUEST_ALREADY_SENT.notExists()) {
            Lang.REQUEST_ALREADY_SENT.set("[\"\",{\"text\":\"You already sent a request to %player%. \",\"color\":\"red\"},{\"text\":\"Click here\",\"bold\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpcancel\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click to cancel.\"}},{\"text\":\" to cancel this request.\",\"color\":\"red\"}]");
            anyUpdated = true;
        }
        if (Lang.REQUEST_ALREADY_RECEIVED.notExists()) {
            Lang.REQUEST_ALREADY_RECEIVED.set("[\"\",{\"text\":\"You already have a pending request from %player%. \",\"color\":\"green\"},{\"text\":\"Click here\",\"bold\":true,\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpaccept\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Click to accept.\"}},{\"text\":\" to accept this request.\",\"color\":\"green\"}]");
            anyUpdated = true;
        }
        if (Lang.REQUEST_CANCELLED_SENDER.notExists()) {
            Lang.REQUEST_CANCELLED_SENDER.set("&aYou have cancelled your request to %player%.");
            anyUpdated = true;
        }
        if (Lang.REQUEST_CANCELLED_RECEIVER.notExists()) {
            Lang.REQUEST_CANCELLED_RECEIVER.set("&cTeleport request from %player% was cancelled.");
            anyUpdated = true;
        }
        if (Lang.PAYMENT_FAILED.notExists()) {
            Lang.PAYMENT_FAILED.set("&cInsufficient funds! Required amount: %amount% &a(Current balance: %balance%)");
            anyUpdated = true;
        }
        if (Lang.TPA_REQUEST_SENT_PAID.notExists()) {
            Lang.TPA_REQUEST_SENT_PAID.set("&aTeleport request sent to %player%. &c-%amount% (You can cancel this request for a refund using /tpcancel)");
            anyUpdated = true;
        }
        if (Lang.TPA_HERE_REQUEST_SENT_PAID.notExists()) {
            Lang.TPA_HERE_REQUEST_SENT_PAID.set("&aTeleport request sent to %player%. &c-%amount% (You can cancel this request for a refund using /tpcancel)");
            anyUpdated = true;
        }
        if (anyUpdated) {
            Lang.saveAll();
        }
    }

    @Override
    protected void applySettingUpdates() {
        if (Setting.TELEPORT_COST.notExists()) {
            Setting.TELEPORT_COST.set(25.0f)
                    .save();
        }
    }
}
