# SwiftConnect

A lightweight Velocity proxy plugin that automatically creates command aliases for all your registered servers — with a fully configurable YAML config.

Instead of typing `/server skyblock`, just type `/skyblock`. That's it.

## Tested Versions

| Component | Version |
|-----------|---------|
| Velocity  | 3.5.0 (Build #580) |
| Paper     | 1.21.1 (Build #127) |
| Java      | 21+ |

## Installation

1. Download `SwiftConnect` from [Releases](../../releases)
2. Drop it into your Velocity proxy's `plugins/` folder
3. Start Velocity and `plugins/swiftconnect/config.yml` will be automatically generated
4. Edit the config to customize messages, aliases, and permissions
5. Restart Velocity to apply changes

## Auto-Generated Config

On first launch, if your `velocity.toml` has servers `hub`, `survival`, and `skyblock`, the plugin generates:

```yaml
lang: en_US
macros:
  hub:
    description: Transfer player to hub.
    permission: ''
    aliases: []
    actions:
    - type: transfer
      options:
        target: hub
        message: §7⏳ Initializing connection to §fHub§7...
  survival:
    description: Transfer player to survival.
    permission: ''
    aliases: []
    actions:
    - type: transfer
      options:
        target: survival
        message: §7⏳ Initializing connection to §fSurvival§7...
  skyblock:
    description: Transfer player to skyblock.
    permission: ''
    aliases: []
    actions:
    - type: transfer
      options:
        target: skyblock
        message: §7⏳ Initializing connection to §fSkyblock§7...
```

Then you can customize it, e.g. add aliases and colored messages:

```yaml
macros:
  hub:
    description: Transfer player to hub.
    permission: ''
    aliases: [lobby, h]
    actions:
    - type: transfer
      options:
        target: hub
        message: §7⏳ Initializing connection to §fHub§7...
  skyblock:
    description: Transfer player to skyblock.
    permission: ''
    aliases: [sb]
    actions:
    - type: transfer
      options:
        target: skyblock
        message: §7⏳ Initializing connection to §6Skyblock§7...
```

## Building from Source

```bash
gradle clean build
```

The JAR will be at `build/libs/SwiftConnect-1.0.0.jar`.

## License

MIT
