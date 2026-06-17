package com.klechino.antibot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AntibotCommand implements CommandExecutor {
    private final KlechinosAntiBot plugin;

    public AntibotCommand(KlechinosAntiBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!s.hasPermission("klechino.antibot.admin")) {
            s.sendMessage("You don't have permission.");
            return true;
        }
        if (args.length == 0) {
            s.sendMessage("Usage: /antibot reload");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            s.sendMessage("Klechinos Anti Bot config reloaded.");
            return true;
        }
        s.sendMessage("Unknown subcommand.");
        return true;
    }
}
