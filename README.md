# Nebula Failsafe Webhook V2
###
## ⚠️ Important Note (Rewrite Version)

 **This version is completely different from the first version.**  
It was fully rewritten.

- **Old version:** relied on log tailing (reading Minecraft log output for chat detection)
- **This rewrite:** uses a direct in client chat capture approach by polling Minecraft’s chat GUI history every client tick.

  **Hook point:** `ChatPoller#onClientTick(TickEvent.ClientTickEvent)`  
  **Chat source:** `GuiNewChat.chatLines` (via reflection)
###
## ✅ Features

-  Sends detected chat triggers to a Discord Webhook
-  Config file support (`config/nebwebhook.cfg`)
-  Supports blacklist filtering (ignore unwanted words)
-  Optional `@everyone` ping when sending webhook
###
## 📦 Installation
1. Build the mod or download the compiled `.jar`
2. Put the `.jar` inside your Minecraft mods folder
###
🔧 Config
After running Minecraft once, the config file is created here:
.minecraft/config/nebwebhook.cfg

> **Note:** This README was rephrased by AI to make it easier to understand because my English is not very good.
