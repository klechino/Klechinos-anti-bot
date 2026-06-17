package com.klechino.antibot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VerifyCommand implements CommandExecutor {
    private final KlechinosAntiBot plugin;

    public VerifyCommand(KlechinosAntiBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage("Only players can verify.");
            return true;
        }
        Player p = (Player) s;
        if (args.length == 0) {
            p.sendMessage(plugin.getConfig().getString("messages.use_verify", "Use /verify <code> or type the code in chat."));
            return true;
        }
        String code = args[0].trim();
        if (plugin.getVerificationManager().checkAndVerify(p, code)) {
            return true;
        } else {
            p.sendMessage(plugin.getConfig().getString("messages.invalid_code", "Invalid verification code."));
            return true;
        }
    }
}
