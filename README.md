# BungeeTP  
This plugin brings a new dimension to teleportation, providing seamless and efficient teleport commands for players navigating across your Waterfall/Velocity network.


**Key Features:**

**Cross-Server Teleportation:** BungeeTP enables players to effortlessly teleport to each other between different servers within your Waterfall/Velocity network. Whether it's a hub, minigame server, or survival world, your players can seamlessly traverse the vast landscapes of your Minecraft universe.

**Player-Friendly Commands:** With intuitive and easy-to-use teleport commands, players can quickly teleport to each other between servers.

**Vanish Support:** Supports most vanish plugins.

**Permission System:** Admins have full control over who can access specific teleportation commands. Set up a robust permission system to manage teleportation privileges and maintain server security.

**Configuration Options:** Fine-tune the plugin to suit your server's unique needs. Customize teleport request expiration times, teleportation delays (to bypass delays: bungeetp.cooldown.bypass), and other parameters (coming soon) to create a teleportation system tailored to your server's gameplay style.

| Command               | Alias        | Permission           | Description                                         |
|-----------------------|--------------|----------------------|-----------------------------------------------------|
| `/bungeetp reload`    | -            | `bungeetp.admin`     | Reload the plugin configs.                          |
| `/tpa <player>`       | -            | `bungeetp.tpa`       | Send a teleport request to another player           |
| `/tp <player>`        | -            | `bungeetp.tp`        | Teleport to another player                          |
| `/tpahere <player>`   | -            | `bungeetp.tpahere`   | Request another player to teleport to your location |
| `/tphere <player>`    | `/s <player>`| `bungeetp.tphere`    | Teleport another player to your location (alias: /s)|
| `/tpaccept`           | -            | `bungeetp.accept`    | Accept a teleport request                           |
| `/tpcancel`           | -            | `bungeetp.cancel`    | Cancel a teleport request                           |
| `/tpdenyall`          | -            | `bungeetp.deny.all`  | Deny all pending teleport requests                  |
| `/tpdeny`             | -            | `bungeetp.deny`      | Deny a teleport request                             |


**Installation Instructions:**
Installing BungeeTP is a straightforward process to seamlessly integrate advanced teleportation capabilities into your BungeeCord and Spigot servers. Follow these simple steps:

**Waterfall/Velocity Server:**
1. Place the BungeeTP-Bungee.jar or BungeeTP-Velocity.jar file into the plugins folder of your Waterfall/Velocity server.
2 Restart your Waterfall/Velocity server.

**Paper Server:**
1. Place the BungeeTP-Bridge-Spigot.jar file into the plugins folder of your Paper server.
2. Restart your Paper server to activate the bridge between Waterfall/Velocity and Paper, ensuring seamless communication for teleportation commands.

If you encounter any issues or have questions, refer to the plugin documentation or ask for help in the discussion section.
