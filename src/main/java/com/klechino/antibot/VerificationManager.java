package com.klechino.antibot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VerificationManager {
    private final Plugin plugin;
    private final Map<UUID, PendingVerification> pending = new ConcurrentHashMap<>();

    public VerificationManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void createFor(Player p, String code, int timeoutSeconds) {
        pending.put(p.getUniqueId(), new PendingVerification(code, Instant.now().plusSeconds(timeoutSeconds)));
    }

    public boolean isPending(Player p) {
        return pending.containsKey(p.getUniqueId());
    }

    public boolean checkAndVerify(Player p, String code) {
        PendingVerification pv = pending.get(p.getUniqueId());
        if (pv == null) return false;
        if (pv.isExpired()) {
            pending.remove(p.getUniqueId());
            return false;
        }
        if (pv.code.equals(code)) {
            pending.remove(p.getUniqueId());
            p.sendMessage(translate(plugin.getConfig().getString("messages.verified", "You are verified.")));
            return true;
        }
        return false;
    }

    public void remove(Player p) {
        pending.remove(p.getUniqueId());
    }

    public void cleanupExpired() {
        Instant now = Instant.now();
        pending.entrySet().removeIf(e -> e.getValue().expiry.isBefore(now));
    }

    public void clearAll() {
        pending.clear();
    }

    private String translate(String s) {
        return s.replace("&", "§");
    }

    private static class PendingVerification {
        final String code;
        final Instant expiry;
        PendingVerification(String code, Instant expiry) {
            this.code = code;
            this.expiry = expiry;
        }
        boolean isExpired() { return Instant.now().isAfter(expiry); }
    }
}
