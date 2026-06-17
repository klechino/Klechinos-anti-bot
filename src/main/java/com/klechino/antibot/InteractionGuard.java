package com.klechino.antibot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class InteractionGuard implements Listener {
    private final KlechinosAntiBot plugin;
    private final VerificationManager vm;

    public InteractionGuard(KlechinosAntiBot plugin, VerificationManager vm) {
        this.plugin = plugin;
        this.vm = vm;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent ev) {
        if (vm.isPending(ev.getPlayer())) ev.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent ev) {
        if (vm.isPending(ev.getPlayer())) ev.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent ev) {
        if (ev.getEntity() instanceof org.bukkit.entity.Player p && vm.isPending(p)) ev.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent ev) {
        if (vm.isPending(ev.getPlayer())) {
            String cmd = ev.getMessage().split(" ")[0].toLowerCase();
            // allow /verify and /antibot always
            if (cmd.equals("/verify") || cmd.equals("/antibot")) return;
            ev.setCancelled(true);
            ev.getPlayer().sendMessage(plugin.getConfig().getString("messages.blocked_command", "You must verify before using commands."));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        if (vm.isPending(ev.getPlayer())) {
            String msg = ev.getMessage().trim();
            // if they typed the code directly, accept it
            if (vm.checkAndVerify(ev.getPlayer(), msg)) {
                ev.setCancelled(true); // do not broadcast their code
            } else {
                ev.setCancelled(true);
                ev.getPlayer().sendMessage(plugin.getConfig().getString("messages.chat_blocked", "You must verify before chatting."));
            }
        }
    }
}
