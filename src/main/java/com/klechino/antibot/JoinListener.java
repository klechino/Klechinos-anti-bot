package com.klechino.antibot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.Deque;

public class JoinListener implements Listener {
    private final KlechinosAntiBot plugin;
    private final VerificationManager verificationManager;
    private final AttackDetector attackDetector;
    private final SecureRandom random = new SecureRandom();
    private final Deque<Long> recentJoins = new ArrayDeque<>();

    public JoinListener(KlechinosAntiBot plugin, VerificationManager vm, AttackDetector ad) {
        this.plugin = plugin;
        this.verificationManager = vm;
        this.attackDetector = ad;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        Player p = ev.getPlayer();
        if (p.hasPermission("klechino.antibot.bypass") || p.isOp()) return;

        // rate tracking
        long now = System.currentTimeMillis();
        recentJoins.addLast(now);
        int windowMs = plugin.getConfig().getInt("rate.window_ms", 5000);
        while (!recentJoins.isEmpty() && recentJoins.peekFirst() < now - windowMs) recentJoins.removeFirst();
        int threshold = plugin.getConfig().getInt("rate.threshold", 6);
        if (recentJoins.size() >= threshold) {
            attackDetector.signalAttackWindow();
        }

        // create verification
        int length = plugin.getConfig().getInt("captcha.length", 5);
        String code = generateNumeric(length);
        int timeout = plugin.getConfig().getInt("captcha.timeout_seconds", 90);
        verificationManager.createFor(p, code, timeout);

        p.sendMessage(format(plugin.getConfig().getString("messages.join")));
        p.sendMessage(format(plugin.getConfig().getString("messages.captcha").replace("{code}", code)));
        p.sendMessage(format(plugin.getConfig().getString("messages.how_to_verify")));

        // optionally, if configured to kick on failure, schedule a task to check later
        int kickAfter = plugin.getConfig().getInt("captcha.kick_after_seconds", 0);
        if (kickAfter > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (verificationManager.isPending(p)) {
                    p.kickPlayer(format(plugin.getConfig().getString("messages.kick_timeout")));
                }
            }, 20L * kickAfter);
        }
    }

    private String generateNumeric(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i=0;i<len;i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    private String format(String s) {
        if (s == null) return "";
        return s.replace("&", "§");
    }
}
