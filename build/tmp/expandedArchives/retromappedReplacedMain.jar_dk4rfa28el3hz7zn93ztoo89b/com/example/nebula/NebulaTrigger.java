package com.example.nebula;

import java.util.Locale;

public class NebulaTrigger {

    private static final String TRIGGER = "Failsafe triggered";
    private static String lastClean = "";
    private static long lastMs = 0L;

    public static void handleChatLine(String msg) {
        if (msg == null) return;

        String clean = msg.replace("Â", "").trim();
        if (clean.isEmpty()) return;

        String lower = clean.toLowerCase(Locale.ROOT);
        if (!lower.contains(TRIGGER.toLowerCase(Locale.ROOT))) return;

        long now = System.currentTimeMillis();
        if (clean.equals(lastClean) && (now - lastMs) < 2000L) return;
        lastClean = clean;
        lastMs = now;

        String bl = MbeldozConfig.blacklist == null ? "" : MbeldozConfig.blacklist.trim();
        if (!bl.isEmpty()) {
            String[] list = bl.split("\\|");
            for (String bad : list) {
                if (bad == null) continue;
                bad = bad.trim();
                if (bad.isEmpty()) continue;
                if (lower.contains(bad.toLowerCase(Locale.ROOT))) return;
            }
        }

        System.out.println("[Mbeldoz][DEBUG] = " + clean);

        //check url
        String url = MbeldozConfig.webhookUrl == null ? "" : MbeldozConfig.webhookUrl.trim();
        if (url.length() == 0) return;

        String mention = MbeldozConfig.pingEveryone ? "@everyone " : "";
        String payload = mention + "Trigger Detected\n```" + clean + "```";

        DiscordWebhookSender.sendAsync(url, payload);

        
    }
}
