package dev.swiftconnect;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.swiftconnect.config.*;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import com.google.inject.Inject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "swiftconnect",
        name = "SwiftConnect",
        version = "1.3.0",
        description = "Automatically creates command aliases for all registered Velocity servers",
        authors = {"SwiftConnect"}
)
public class SwiftConnect {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private ConfigManager configManager;
    private final Set<String> registeredCommands = new HashSet<>();

    @Inject
    public SwiftConnect(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        configManager = new ConfigManager(dataDirectory);

        loadAndRegisterAll();
        registerSwiftConnectCommand();
    }

    private void loadAndRegisterAll() {
        SwiftConfig config;

        try {
            config = configManager.load();
        } catch (IOException e) {
            logger.error("Failed to load config.yml", e);
            return;
        }

        if (config == null) {
            config = generateDefaultConfig();
            try {
                configManager.save(config);
                logger.info("Generated default config.yml with {} detected server(s).", config.getMacros().size());
            } catch (IOException e) {
                logger.error("Failed to save config.yml", e);
                return;
            }
        }

        int count = 0;
        for (Map.Entry<String, MacroEntry> entry : config.getMacros().entrySet()) {
            String name = entry.getKey();
            MacroEntry macro = entry.getValue();
            registerMacro(name, macro);
            count++;
        }

        logger.info("SwiftConnect loaded! Registered {} macro command(s).", count);
    }

    private void reload(CommandSource source) {
        // Unregister all macro commands
        unregisterAllMacroCommands();

        // Re-load config
        SwiftConfig config;
        try {
            config = configManager.load();
        } catch (IOException e) {
            logger.error("Failed to reload config.yml", e);
            source.sendMessage(LEGACY.deserialize("§c[SwiftConnect] Failed to reload config: " + e.getMessage()));
            return;
        }

        if (config == null) {
            config = new SwiftConfig();
            config.setLang("en_US");
            config.setMacros(new LinkedHashMap<>());
        }

        // Detect new servers not yet in the config and add them
        int newServers = 0;
        for (RegisteredServer registeredServer : server.getAllServers()) {
            String name = registeredServer.getServerInfo().getName();
            String lower = name.toLowerCase();

            if (!config.getMacros().containsKey(lower)) {
                MacroEntry macro = new MacroEntry();
                macro.setDescription("Transfer player to " + lower + ".");
                macro.setPermission("");
                macro.setAliases(new ArrayList<>());

                MacroAction action = new MacroAction();
                action.setType("transfer");
                Map<String, String> options = new LinkedHashMap<>();
                options.put("target", lower);
                options.put("message", "§7⏳ Initializing connection to §f" + capitalize(name) + "§7...");
                action.setOptions(options);
                macro.setActions(List.of(action));

                config.getMacros().put(lower, macro);
                newServers++;
                logger.info("Detected new server '{}', added to config.", lower);
            }
        }

        // Save updated config if new servers were added
        if (newServers > 0) {
            try {
                configManager.save(config);
                logger.info("Saved config.yml with {} new server(s).", newServers);
            } catch (IOException e) {
                logger.error("Failed to save config.yml after adding new servers", e);
            }
        }

        // Re-register all commands
        int count = 0;
        for (Map.Entry<String, MacroEntry> entry : config.getMacros().entrySet()) {
            String name = entry.getKey();
            MacroEntry macro = entry.getValue();
            registerMacro(name, macro);
            count++;
        }

        String msg = "§a[SwiftConnect] Reloaded! §7Registered §f" + count + "§7 macro(s)";
        if (newServers > 0) {
            msg += ", §f" + newServers + "§7 new server(s) added to config";
        }
        msg += ".";
        source.sendMessage(LEGACY.deserialize(msg));
        logger.info("SwiftConnect reloaded. {} macro(s), {} new server(s).", count, newServers);
    }

    private void unregisterAllMacroCommands() {
        CommandManager cmdManager = server.getCommandManager();
        for (String cmd : registeredCommands) {
            cmdManager.unregister(cmd);
        }
        registeredCommands.clear();
    }

    private void registerSwiftConnectCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("swiftconnect")
                .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                        .requires(source -> source.hasPermission("swiftconnect.reload"))
                        .executes(context -> {
                            reload(context.getSource());
                            return 1;
                        })
                )
                .executes(context -> {
                    context.getSource().sendMessage(LEGACY.deserialize(
                            "§7[SwiftConnect] §fv1.3.0 §7— Use §f/swiftconnect reload §7to reload config."));
                    return 1;
                })
                .build();

        BrigadierCommand command = new BrigadierCommand(node);
        server.getCommandManager().register(
                server.getCommandManager().metaBuilder(command)
                        .aliases("sc")
                        .build(),
                command
        );
    }

    private SwiftConfig generateDefaultConfig() {
        SwiftConfig config = new SwiftConfig();
        config.setLang("en_US");

        Map<String, MacroEntry> macros = new LinkedHashMap<>();
        for (RegisteredServer registeredServer : server.getAllServers()) {
            String name = registeredServer.getServerInfo().getName();
            String lower = name.toLowerCase();

            MacroEntry macro = new MacroEntry();
            macro.setDescription("Transfer player to " + lower + ".");
            macro.setPermission("");
            macro.setAliases(new ArrayList<>());

            MacroAction action = new MacroAction();
            action.setType("transfer");
            Map<String, String> options = new LinkedHashMap<>();
            options.put("target", lower);
            options.put("message", "§7⏳ Initializing connection to §f" + capitalize(name) + "§7...");
            action.setOptions(options);
            macro.setActions(List.of(action));

            macros.put(lower, macro);
        }

        config.setMacros(macros);
        return config;
    }

    private void registerMacro(String name, MacroEntry macro) {
        // Register the main command
        registerCommand(name, macro);

        // Register aliases
        if (macro.getAliases() != null) {
            for (String alias : macro.getAliases()) {
                if (alias != null && !alias.trim().isEmpty()) {
                    registerCommand(alias.trim().toLowerCase(), macro);
                    logger.info("Registered alias /{} -> /{}", alias.trim().toLowerCase(), name);
                }
            }
        }
    }

    private void registerCommand(String commandName, MacroEntry macro) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal(commandName)
                .requires(source -> {
                    if (!(source instanceof Player)) return false;
                    String perm = macro.getPermission();
                    return perm == null || perm.isEmpty() || source.hasPermission(perm);
                })
                .executes(context -> {
                    Player player = (Player) context.getSource();
                    executeMacro(player, macro);
                    return 1;
                })
                .build();

        BrigadierCommand command = new BrigadierCommand(node);
        server.getCommandManager().register(command);
        registeredCommands.add(commandName);
        logger.info("Registered command /{}", commandName);
    }

    private void executeMacro(Player player, MacroEntry macro) {
        for (MacroAction action : macro.getActions()) {
            if ("transfer".equalsIgnoreCase(action.getType())) {
                String target = action.getOptions().get("target");
                String message = action.getOptions().get("message");

                if (target == null) continue;

                Optional<RegisteredServer> targetServer = server.getServer(target);
                if (targetServer.isEmpty()) {
                    player.sendMessage(LEGACY.deserialize("§cServer §f" + target + " §cis not available."));
                    return;
                }

                if (message != null && !message.isEmpty()) {
                    player.sendMessage(LEGACY.deserialize(message));
                }

                player.createConnectionRequest(targetServer.get()).fireAndForget();
            }
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
