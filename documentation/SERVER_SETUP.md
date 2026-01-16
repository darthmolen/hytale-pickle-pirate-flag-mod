# Hytale Server Setup Guide

A complete guide to setting up a Hytale dedicated server for mod development and testing.

## System Requirements

### Minimum
- **OS**: Windows 10/11, Linux (Ubuntu 22.04+), or macOS
- **RAM**: 4 GB (8 GB recommended)
- **CPU**: 4 cores
- **Storage**: 10 GB free space
- **Network**: Stable internet connection

### Recommended for Development
- **RAM**: 16 GB
- **CPU**: 8 cores
- **SSD**: For faster world loading
- **Network**: Low latency connection

## Step 1: Install Java 25

Hytale requires Java 25 or later.

### Windows

1. Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
2. Run the installer
3. Verify:
   ```cmd
   java -version
   ```
   Should show `java version "25.x.x"`

### Linux (Ubuntu/Debian)

```bash
# Add repository
sudo apt update
sudo apt install -y wget apt-transport-https

# Install OpenJDK 25 (or latest available)
sudo apt install -y openjdk-25-jdk

# Verify
java -version
```

### macOS

```bash
# Using Homebrew
brew install openjdk@25

# Add to path
echo 'export PATH="/opt/homebrew/opt/openjdk@25/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify
java -version
```

## Step 2: Download Hytale Server

Use the official **hytale-downloader** tool to download the server files.

### Get the Downloader

Download the binary for your platform:
- **Windows**: `hytale-downloader-windows-amd64.exe`
- **Linux**: `hytale-downloader-linux-amd64`

### First-Time Setup

```bash
# Linux - make executable
chmod +x hytale-downloader-linux-amd64

# Run the downloader
./hytale-downloader-linux-amd64
```

```cmd
# Windows
hytale-downloader-windows-amd64.exe
```

**On first run**, you'll see a URL and authorization code. Open the URL in a browser and log in with your Hytale account. The tool will automatically detect when you've authenticated and start the download.

### Download Commands

| Command | Description |
|---------|-------------|
| `./hytale-downloader` | Download latest release |
| `./hytale-downloader -print-version` | Check available version without downloading |
| `./hytale-downloader -download-path game.zip` | Download to specific file |
| `./hytale-downloader -patchline pre-release` | Download from pre-release channel |

### Extract the Server

```bash
mkdir ~/hytale-server
cd ~/hytale-server
unzip /path/to/downloaded-game.zip
```

### Expected Files

After extraction:
```
hytale-server/
├── server.jar           # Main server executable
├── config.json          # Server configuration
├── start-server.sh      # Linux/Mac start script
├── start-server.bat     # Windows start script
├── mods/                # Put mods here
├── plugins/             # Alternative plugin location
└── universe/            # World saves (created on first run)
```

### Downloader Troubleshooting

| Problem | Solution |
|---------|----------|
| Authentication error | Delete `.hytale-downloader-credentials.json` and re-run |
| Device code expired | Restart the tool to get a new authorization code |
| Checksum mismatch | Retry the download |
| 401 Unauthorized | Re-authenticate (delete credentials file) |
| 404 Not Found | Check patchline name & your access permissions |

> **Note**: Credentials are saved after first login. Add `.hytale-downloader-credentials.json` to `.gitignore` to keep them secure.

## Step 3: Initial Server Configuration

### config.json

Edit the main configuration file:

```json
{
    "server-name": "My Dev Server",
    "server-port": 25565,
    "max-players": 10,
    "game-mode": "creative",
    "difficulty": "normal",
    "view-distance": 10,
    "enable-mods": true,
    "whitelist": false,
    "online-mode": true
}
```

### Key Settings for Development

| Setting | Development Value | Notes |
|---------|------------------|-------|
| `game-mode` | `"creative"` | Easy access to all blocks |
| `max-players` | `10` | Keep low for testing |
| `view-distance` | `8` | Lower = less RAM usage |
| `enable-mods` | `true` | Required for mods |
| `whitelist` | `false` | Easy access during dev |

## Step 4: First Server Start

### Linux/macOS

```bash
cd ~/hytale-server
chmod +x start-server.sh
./start-server.sh
```

### Windows

```cmd
cd C:\hytale-server
start-server.bat
```

### First Run Output

You should see:
```
[INFO] Starting Hytale Server v1.0.0...
[INFO] Loading world...
[INFO] Server started on port 25565
[INFO] Done! Type 'help' for commands.
```

### Accept EULA (if prompted)

If you see a message about accepting the EULA:
1. Open `eula.txt`
2. Change `eula=false` to `eula=true`
3. Restart the server

## Step 5: Connect and Test

### From Hytale Game

1. Launch Hytale
2. Select "Multiplayer"
3. Add server: `localhost` (or your IP)
4. Connect

### Verify Connection

Server logs should show:
```
[INFO] Player 'YourName' connected
```

## Step 6: Install Mods

### Create Mods Folder (if not exists)

```bash
mkdir -p ~/hytale-server/mods
```

### Copy Mod JARs

```bash
cp PicklePirateFlag-1.0.0.jar ~/hytale-server/mods/
```

### Restart Server

```bash
# Stop server (in server console)
stop

# Start again
./start-server.sh
```

### Verify Mod Loading

Check logs for:
```
[INFO] Loading plugins...
[INFO] Pickle Pirate Flag v1.0.0 loading...
[INFO] Pickle Pirate Flag plugin setup complete!
```

## Server Management

### Start Script with Memory Settings

Create `start-dev.sh`:

```bash
#!/bin/bash
java -Xms2G -Xmx4G -jar server.jar nogui
```

- `-Xms2G`: Initial heap size (2 GB)
- `-Xmx4G`: Maximum heap size (4 GB)
- `nogui`: Run without GUI (optional)

### Stop Server Gracefully

In the server console:
```
stop
```

Or send SIGTERM:
```bash
pkill -f "hytale.*server.jar"
```

### Backup World

```bash
# Stop server first!
cp -r universe/ backup/universe-$(date +%Y%m%d)/
```

## Development Workflow

### Quick Iteration Cycle

1. **Edit code** in your IDE
2. **Build**: `./scripts/build.sh`
3. **Deploy**: `./scripts/deploy.sh`
4. **Reload** (if server supports hot-reload) or restart
5. **Test** in game
6. Repeat

### Live Reload (if supported)

Some servers support plugin reload:
```
reload plugins
```
Or specific plugin:
```
reload plugin PicklePirateFlag
```

### Viewing Logs

```bash
# Follow logs in real-time
tail -f ~/hytale-server/logs/latest.log

# Search for errors
grep -i "error\|exception" logs/latest.log
```

## Troubleshooting

### Server Won't Start

1. **Check Java version**: `java -version`
2. **Check port availability**: `netstat -an | grep 25565`
3. **Check file permissions**: `ls -la server.jar`
4. **Read error logs**: `cat logs/latest.log`

### Can't Connect

1. **Firewall**: Allow port 25565
   ```bash
   sudo ufw allow 25565
   ```
2. **Correct IP**: Use `localhost` for local, or actual IP for remote
3. **Server running**: Check process is alive
4. **Online mode**: Match with game client settings

### Mod Not Loading

1. **JAR in correct folder**: `ls mods/`
2. **Java compatibility**: Built with Java 25?
3. **Dependencies**: Are all required mods present?
4. **Check logs**: Look for errors during startup

### Out of Memory

1. **Increase heap**: Change `-Xmx4G` to `-Xmx8G`
2. **Reduce view distance**: Lower `view-distance` in config
3. **Fewer mods**: Remove unnecessary mods
4. **Check for leaks**: Monitor with `jstat` or VisualVM

## Running as a Service (Linux)

### Systemd Service

Create `/etc/systemd/system/hytale.service`:

```ini
[Unit]
Description=Hytale Server
After=network.target

[Service]
User=hytale
WorkingDirectory=/home/hytale/server
ExecStart=/usr/bin/java -Xms2G -Xmx4G -jar server.jar nogui
ExecStop=/bin/kill -SIGTERM $MAINPID
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable hytale
sudo systemctl start hytale
sudo systemctl status hytale
```

## Security Considerations

### For Development
- Use whitelist if exposing to internet
- Keep `online-mode: true`
- Don't run as root

### For Production
- Use a firewall
- Set up DDoS protection
- Regular backups
- Monitor server health

## Additional Resources

- **[Hytale Server Hosting Guide](https://hytale.com/docs/hosting)** - Official docs
- **[HytaleModding.dev](https://hytalemodding.dev)** - Community guides
- **[Server Admin Discord](https://discord.gg/hytale)** - Community support

---

*This guide covers basic server setup. For advanced configurations, consult the official Hytale documentation.*
