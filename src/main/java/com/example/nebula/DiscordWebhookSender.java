package com.example.nebula;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class DiscordWebhookSender {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static void sendAsync(final String webhookUrl, final String content) {
        if (webhookUrl == null || webhookUrl.trim().isEmpty()) return;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String c = (content == null) ? "" : content;
                    
                    if (c.length() > 1900) {
                        c = c.substring(0, 1900) + "...";
                    }

                    String json = "{\"content\":\"" + escapeJson(c) + "\"}";
                    postDiscordWebhook(webhookUrl.trim(), json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "NebulaWebhook-Sender");

        t.setDaemon(true);
        t.start();
    }

    private static void postDiscordWebhook(String url, String json) throws Exception {
        byte[] body = json.getBytes(UTF8);

        HttpURLConnection conn = openFollowingRedirects(url);
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(12000);
        conn.setDoOutput(true);

        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        conn.setRequestProperty("Connection", "close");
        conn.setFixedLengthStreamingMode(body.length);

        OutputStream os = null;
        try {
            os = conn.getOutputStream();
            os.write(body);
            os.flush();
        } finally {
            if (os != null) try { os.close(); } catch (Exception ignored) {}
        }

        int code = conn.getResponseCode();
        String resp = readAll(code >= 400 ? conn.getErrorStream() : conn.getInputStream());
        conn.disconnect();

        if (code != 204 && code != 200) {
            throw new RuntimeException("Webhook failed HTTP " + code + " resp=" + resp);
        }

        System.out.println("[NebulaWebhook] Webhook response: " + code);
    }

    private static HttpURLConnection openFollowingRedirects(String url) throws Exception {
        String current = url;
        for (int i = 0; i < 3; i++) {
            HttpURLConnection c = (HttpURLConnection) new URL(current).openConnection();
            c.setInstanceFollowRedirects(false);

            c.setRequestMethod("GET");
            c.setConnectTimeout(8000);
            c.setReadTimeout(12000);
            c.setRequestProperty("User-Agent", "Mozilla/5.0");

            int code = c.getResponseCode();
            if (code == 301 || code == 302 || code == 307 || code == 308) {
                String loc = c.getHeaderField("Location");
                c.disconnect();
                if (loc == null || loc.trim().isEmpty()) break;
                current = loc;
                continue;
            }

            c.disconnect();
            HttpURLConnection post = (HttpURLConnection) new URL(current).openConnection();
            post.setInstanceFollowRedirects(true);
            return post;
        }

        HttpURLConnection post = (HttpURLConnection) new URL(current).openConnection();
        post.setInstanceFollowRedirects(true);
        return post;
    }

    private static String readAll(InputStream in) {
        if (in == null) return "";
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            int r;
            while ((r = in.read(buf)) != -1) bout.write(buf, 0, r);
        } catch (Exception ignored) {
        } finally {
            try { in.close(); } catch (Exception ignored) {}
        }
        try {
            return new String(bout.toByteArray(), UTF8);
        } catch (Exception e) {
            return "";
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
