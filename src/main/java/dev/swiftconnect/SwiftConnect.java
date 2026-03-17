package dev.swiftconnect;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import com.google.inject.Inject;

@Plugin(
        id = "swiftconnect",
        name = "SwiftConnect",
        version = "1.0.0",
        description = "Automatically creates command aliases for all registered Velocity servers",
        authors = {"SwiftConnect"}
)
public class SwiftConnect {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public SwiftConnect(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        int count = 0;

        for (RegisteredServer registeredServer : server.getAllServers()) {
            String serverName = registeredServer.getServerInfo().getName();
            registerServerAlias(serverName, registeredServer);
            count++;
        }

        logger.info("SwiftConnect loaded! Registered {} server alias(es).", count);
    }

    private void registerServerAlias(String serverName, RegisteredServer targetServer) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal(serverName.toLowerCase())
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    Player player = (Player) context.getSource();
                    player.sendMessage(Component.text("Connecting to " + serverName + "...", NamedTextColor.GREEN));
                    player.createConnectionRequest(targetServer).fireAndForget();
                    return 1;
                })
                .build();

        BrigadierCommand command = new BrigadierCommand(node);
        server.getCommandManager().register(command);
        logger.info("Registered alias /{} -> /server {}", serverName.toLowerCase(), serverName);
    }
}
