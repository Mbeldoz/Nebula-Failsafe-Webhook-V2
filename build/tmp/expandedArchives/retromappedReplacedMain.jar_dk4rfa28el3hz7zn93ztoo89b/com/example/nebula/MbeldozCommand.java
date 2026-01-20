package com.example.nebula;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MbeldozCommand extends CommandBase {

    @Override
    public String func_71517_b() {
        return "mbeldoz";
    }

    @Override
    public String func_71518_a(ICommandSender sender) {
        return "/mbeldoz <reload|status>";
    }

    @Override
    public List func_71514_a() {
        return Arrays.asList("mbz");
    }

    @Override
    public void func_71515_b(ICommandSender sender, String[] args) {
        if (sender == null) return;

        if (args == null || args.length == 0) {
            send(sender, "[Mbeldoz] Commands:");
            send(sender, " - /mbeldoz reload");
            send(sender, " - /mbeldoz status");
            printConfigStatus(sender);
            return;
        }

        String sub = args[0].toLowerCase();

        if ("reload".equals(sub)) {
            boolean ok = MbeldozConfig.load();
            send(sender, "[Mbeldoz] Config reload: " + (ok ? "OK" : "FAILED"));
            printConfigStatus(sender);
            return;
        }

        if ("status".equals(sub) || "config".equals(sub)) {
            printConfigStatus(sender);
            return;
        }

        send(sender, "[Mbeldoz] Unknown subcommand: " + args[0]);
        send(sender, "[Mbeldoz] Try: /mbeldoz reload OR /mbeldoz status");
    }

    private void printConfigStatus(ICommandSender sender) {
        String url = safe(MbeldozConfig.webhookUrl);
        String bl = safe(MbeldozConfig.blacklist);

        send(sender, "[Mbeldoz] Config status:");
        send(sender, " webhookUrl: " + (url.length() == 0 ? "NOT SET" : "SET"));
        send(sender, " pingEveryone: " + (MbeldozConfig.pingEveryone ? "true" : "false"));
        send(sender, " blacklist: " + (bl.length() == 0 ? "(empty)" : bl));
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private void send(ICommandSender sender, String msg) {
        sender.func_145747_a(new ChatComponentText(msg));
        System.out.println(msg);
    }

    @Override
    public boolean func_71519_b(ICommandSender sender) {
        return true; // client-only mod
    }

    @Override
    public List func_180525_a(ICommandSender sender, String[] args, net.minecraft.util.BlockPos pos) {
        List<String> out = new ArrayList<String>();
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase())) out.add("reload");
            if ("status".startsWith(args[0].toLowerCase())) out.add("status");
        }
        return out;
    }

    @Override
    public boolean func_82358_a(String[] args, int index) {
        return false;
    }
}
