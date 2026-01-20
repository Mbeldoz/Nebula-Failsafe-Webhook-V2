package com.example.nebula;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.List;

public class ChatPoller {

    private Field chatLinesField;

    private int lastSeenCounter = -1;
    private String lastText = "";
    private long lastMs = 0L;

    public ChatPoller() {
        tryInitReflection();
    }

    private void tryInitReflection() {
        if (chatLinesField != null) return;

        try {
            chatLinesField = GuiNewChat.class.getDeclaredField("chatLines");
            chatLinesField.setAccessible(true);
            System.out.println("[Mbeldoz] ChatPoller hooked GuiNewChat.chatLines");
            return;
        } catch (Exception ignored) {}

        try {
            chatLinesField = GuiNewChat.class.getDeclaredField("field_146253_i"); //drawnChatLines things
            chatLinesField.setAccessible(true);
            System.out.println("[Mbeldoz] ChatPoller hooked GuiNewChat.field_146253_i");
        } catch (Exception e) {
            System.out.println("[Mbeldoz] ChatPoller failed to find chatLines field: " + e.getMessage());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.func_71410_x();
        if (mc == null || mc.field_71456_v == null) return;

        GuiNewChat chat = mc.field_71456_v.func_146158_b();
        if (chat == null) return;

        if (chatLinesField == null) tryInitReflection();
        if (chatLinesField == null) return;

        try {
            Object v = chatLinesField.get(chat);
            if (!(v instanceof List)) return;

            List lines = (List) v;
            if (lines.size() == 0) return;

            int maxCounterThisTick = lastSeenCounter;

            for (int i = 0; i < lines.size(); i++) {
                Object o = lines.get(i);
                if (!(o instanceof ChatLine)) continue;

                ChatLine cl = (ChatLine) o;
                int counter = cl.func_74540_b();

                if (counter <= lastSeenCounter) {
                    break;
                }

                if (counter > maxCounterThisTick) maxCounterThisTick = counter;

                if (cl.func_151461_a() == null) continue;
                String text = cl.func_151461_a().func_150260_c();
                if (text == null) continue;
                text = text.trim();
                if (text.length() == 0) continue;

                long now = System.currentTimeMillis();
                if (text.equals(lastText) && (now - lastMs) < 200L) continue;
                lastText = text;
                lastMs = now;

                // System.out.println("[Mbeldoz][CHAT] " + text);

                NebulaTrigger.handleChatLine(text);
            }

            lastSeenCounter = maxCounterThisTick;

        } catch (Throwable t) {
            System.out.println("[Mbeldoz] Error: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            chatLinesField = null;
        }
    }
}
