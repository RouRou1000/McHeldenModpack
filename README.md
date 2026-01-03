# HeldenMod (Minecraft 1.21.4) — Lives / PvP-Elimination Mod (Deutsch)

Kurz: Harte PvP-Mod, jeder Spieler hat Heldenherzen (Lives). Stirbt man verliert man ein Herz. Bei 0 Herzen: Eliminierung (Spectator oder Ban, konfigurierbar). Combat-Tag verhindert Logout/Chest/Teleport etc.

Wichtig
- Server-seitige Speicherung aller Herzen
- Synchronisation per Forge-Netzwerkpaketen
- Kein Combat-Logging möglich (Logout → sofortiger Herzverlust)
- Keine Client-Manipulation möglich

Installation
1. Forge 1.21.4 MDK in deinem Workspace installieren.
2. Kopiere dieses Projekt in das MDK oder nutze das MDK build system.
3. Java 17 ist erforderlich.

Build (kurz):
- ./gradlew build
- Das Ergebnis findest du in build/libs

Commands
- /helden takeheart <spieler> <anzahl>  — Admin-Befehl: Entfernt Herzen vom Spieler; die entfernten Herzen werden als Items an den Executor gegeben (oder gedroppt).
- /helden giveheart <spieler> <anzahl>  — (optional) Gibt Herzen.

Config
config/heldenmod-common.toml wird beim ersten Start erzeugt. Standard-Optionen:
- combatDurationSeconds = 30
- maxHearts = 3
- logoutPunishment = "DEATH"
- hudEnabled = true
- onZeroHearts = "SPECTATOR"
- reviveCost = 3
