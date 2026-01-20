package com.example.nebula;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MbeldozCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "mbeldoz";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mbeldoz <reload|status>";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("mbz");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
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
        sender.addChatMessage(new ChatComponentText(msg));
        System.out.println(msg);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true; // client-only mod
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args, net.minecraft.util.BlockPos pos) {
        List<String> out = new ArrayList<String>();
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase())) out.add("reload");
            if ("status".startsWith(args[0].toLowerCase())) out.add("status");
        }
        return out;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
