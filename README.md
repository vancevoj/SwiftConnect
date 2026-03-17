# SwiftConnect

A lightweight Velocity proxy plugin that automatically creates command aliases for all your registered servers.

Instead of typing `/server skyblock`, just type `/skyblock`. That's it.

## Features

- **Zero configuration** — drop the JAR into your Velocity `plugins/` folder and go
- **Auto-detection** — reads all servers from your `velocity.toml` and registers aliases
- **Lightweight** — no config files, no dependencies, no bloat

## Compatibility

| Component | Version |
|-----------|---------|
| Velocity  | 3.5.0 (Build #580) |
| Paper     | 1.21.1 (Build #127) |
| Java      | 21+ |

## Installation

1. Download `SwiftConnect-1.0.0.jar` from [Releases](../../releases)
2. Drop it into your Velocity proxy's `plugins/` folder
3. Restart (or start) Velocity

## How It Works

If your `velocity.toml` has these servers:

```toml
[servers]
lobby = "127.0.0.1:25566"
skyblock = "127.0.0.1:25567"
survival = "127.0.0.1:25568"
```

SwiftConnect automatically registers:
- `/lobby` → connects you to the lobby server
- `/skyblock` → connects you to the skyblock server
- `/survival` → connects you to the survival server

No configuration needed.

## Building from Source

```bash
mvn clean package
```

The JAR will be at `target/SwiftConnect-1.0.0.jar`.

## License

MIT
