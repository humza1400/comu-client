# Comu Client

**Comu Client** is a lightweight, modular utility client for Minecraft built on the [Fabric](https://fabricmc.net/) modding framework. It provides powerful tools and enhancements to improve your gameplay experience — from movement and combat modules to rendering overlays and utility features.

---

## 🛠️ Installation

1. Download and install [Fabric Loader](https://fabricmc.net/use/).
2. Download the latest `comu-client-x.x.x.jar` from the [Releases](#) tab (TODO: add release link).
3. Place it in your `.minecraft/mods` directory.
4. Launch Minecraft with the Fabric profile.

---

## 🧪 Development

### Requirements
- JDK 17+
- Gradle (or use the Gradle wrapper)
- IntelliJ IDEA (recommended) or VSCode

### Setup

```bash
git clone https://github.com/your-username/comu-client.git
cd comu-client
./gradlew genSources
./gradlew idea
```
Then open the project in IntelliJ and run the Minecraft Client configuration.

### 📁 Project Structure
- `me/comu/module/` – All modules (Toggleable, Visual, Utility, Combat, etc.)
- `me/comu/mixin/` - All mixins and client-specific injection code
- `me/comu/events/` – Custom event classes
- `me/comu/config/` – Config loading/saving logic
- `me/comu/command/` – Command manager and custom commands
- `me/comu/property/` – Module properties (booleans, numbers, enums)
- `me/comu/core/Comu.java` – Client initialization

### 📌 Goals
- Clean and performant design
- Fully customizable modules with UI and keybinds
- Expandable via an event-driven architecture
- Well-documented code for learning and contribution

### 🤝 Contributing
Pull requests are welcome! If you want to add a new module, fix a bug, or improve performance, feel free to fork and PR. For larger changes, open an issue or discussion first.

### ❤️ Credits
- [FabricMC](https://fabricmc.net/) – Modding platform
- [Minecraft Forge MCP mappings](https://github.com/MinecraftForge/MCPConfig)
- You – for checking out Comu Client!
