package net.savagedev.tpa.bungee.tasks;

public class HousekeeperTask implements Runnable {
    @Override
    public void run() {
    }
//    private final BungeeTpBungeePlugin bungeeTp;
//    private final long expire;
//
//    public HousekeeperTask(BungeeTpBungeePlugin bungeeTp, long expire) {
//        this.bungeeTp = bungeeTp;
//        this.expire = expire;
//    }
//
//    @Override
//    public void run() {
//        final Map<ProxiedPlayer, TeleportRequest> requestMap = this.bungeeTp.getTeleportManager().getAll();
//        final long time = System.currentTimeMillis();
//        for (ProxiedPlayer player : requestMap.keySet()) {
//            final TeleportRequest request = requestMap.get(player);
//            if ((time - request.getTimeSent()) / 1000L >= this.expire) {
//                this.announceExpired(player, request.getRequester());
//                requestMap.remove(player);
//            }
//        }
//    }
//
//    private void announceExpired(ProxiedPlayer player, ProxiedPlayer other) {
//        if (player.isConnected()) {
//            Lang.REQUEST_EXPIRED_TO.send(player, new Lang.Placeholder("%player%", other.getName()));
//        }
//        if (other.isConnected()) {
//            Lang.REQUEST_EXPIRED_FROM.send(other, new Lang.Placeholder("%player%", player.getName()));
//        }
//    }
}
