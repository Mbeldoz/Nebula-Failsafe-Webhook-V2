package com.example.nebula;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.Properties;

public class MbeldozConfig {

    public static String webhookUrl = "";
    public static String blacklist = "";
    public static boolean pingEveryone = false;

    private static File getConfigFile() {
        File mcDir = Minecraft.getMinecraft().mcDataDir;
        File cfgDir = new File(mcDir, "config");
        if (!cfgDir.exists()) cfgDir.mkdirs();
        return new File(cfgDir, "nebwebhook.cfg");
    }

    public static boolean load() {
        File f = getConfigFile();
        Properties p = new Properties();

        InputStream in = null;
        try {
            if (f.exists()) {
                in = new FileInputStream(f);
                p.load(in);
            } else {
                // defaults
                p.setProperty("webhookUrl", "");
                p.setProperty("blacklist", "");
                p.setProperty("pingEveryone", "false");

                OutputStream out = null;
                try {
                    out = new FileOutputStream(f);
                    p.store(out, "Nebula webhook config");
                } finally {
                    if (out != null) try { out.close(); } catch (Exception ignored) {}
                }
            }

            webhookUrl = trim(p.getProperty("webhookUrl"));
            blacklist = trim(p.getProperty("blacklist"));
            pingEveryone = "true".equalsIgnoreCase(trim(p.getProperty("pingEveryone")));

            System.out.println("[Mbeldoz] Config loaded. webhookSet=" + (webhookUrl.length() > 0));
            return true;

        } catch (Exception e) {
            System.out.println("[Mbeldoz] Config load failed: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            if (in != null) try { in.close(); } catch (Exception ignored) {}
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
