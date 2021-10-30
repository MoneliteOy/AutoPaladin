<br/>
<a href="https://pld.ac" target="blank_">
    <center><img height="200" alt="AutoPaladin" src="https://cdn.paladin.ac/paladin.svg"/></center>
</a>
<br/> 
<br>
<br>

# <b>AutoPaladin</b>: Scan players with one command
An enterprise-only solution to allow networks to automatically scan suspected players with <b>Paladin</b> <br>
Automated scanning is essential to saving time for everyone, this plugin helps accomplish scans in <i>under thirty seconds!</i>

## <b>Features</b>
- A single command to scan a player & obtain results
- Compatible with 1.7-LATEST Spigot & 1.7-LATEST clients
- Reloadable configuration with /paladin reload
- Automatically thaws (unfreezes) players five minutes after completion
- Proper freezing, preventing players from unwanted actions
- Easy to use per-player-language string configuration system
- Configurable ban command for frozen players who disconnect
- Abstract code design allowing other plugins to easily communicate
- Real-time in-game scan progress bar displayed via experience level

## <b>Installation <i>(Enterprise owners only!)</i></b>
1. Download `AutoPaladin.jar` from [Releases](https://github.com/MoneliteOy/AutoPaladin/releases/latest)<br>
2. Upload AutoPaladin to your /plugins folder & start your server<br>
3. Get your API key from [Create Key](https://dash.paladin.ac/api)<br>
4. Insert your API key to `/plugins/AutoPaladin/config.json`<br>
5. Go in-game to execute <u>/paladin reload</u>, then begin using AutoPaladin<br>
- Need assistance?<br>
    [Contact 24/7 support](https://pld.ac/support) or <br>
        [![Chat](https://img.shields.io/badge/chat-Discord%20Tickets-brightred?style=flat&label=Contact%20us%20via&color=%321a)](https://pld.ac/discord) <br>

## <b>Staff Guide</b>
1. /paladin (username)
2. Wait for completion
3. Click link to view results
4. Determine the outcome
5. /thaw or /ban the player

## <b>Permissions & Commands</b>
- paladin.auto.freeze
    - /freeze (username) or /paladin (username)
        - creates & sends a download link to user
        - real-time chat & XP level progress updates
        - provides scan results link after completion
        - autothaws user 5 minutes after scan completion
    - /thaw (username) or /unfreeze (username)
        - restores the user to original state
- paladin.auto.reload
    - /paladin reload
        - reloads all configuration files

## <b>Restriction Functionality</b>
### <b>While frozen, players are:</b>
- Prevented from moving outside of one block
- Prevented from breaking & placing
- Prevented from dropping & picking up
- Prevented from dealing & receiving attacks
- Temporarily set to adventure gamemode
- Temporarily given full hunger
- Prevented from sending commands
    - Configurable via `/plugins/AutoPaladin/config.json`

## <b>Building</b>
### Prerequisites
 - JDK 8 - 17

Execute `./gradlew shadowJar` <br>
Locate build at `builds/libs/`

## <b>Contributing</b>
For those requesting a feature or reporting a bug, please create a [GitHub issue](https://github.com/MoneliteOy/AutoPaladin/issues/new)

## <b>Community</b>
Join the Paladin discord community over at [discord.gg/paladin](https://pld.ac/discord)

## <b>Licensing</b>
AutoPaladin is licensed under [GPL v3](https://github.com/MoneliteOy/AutoPaladin/blob/master/LICENSE)