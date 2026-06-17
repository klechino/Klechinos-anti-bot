package com.klechino.antibot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Very small helper that captures repeated join spikes.
 * Behaviour is configurable via config.yml.
 */
public class AttackDetector {
    private final Plugin plugin;
    private final AtomicLong attackWindowUntil = new AtomicLong(0);

    public AttackDetector(Plugin plugin) {
        this.plugin = plugin;
    }

    public void signalAttackWindow() {
        int seconds = plugin.getConfig().getInt("rate.attack_window_seconds", 30);
        attackWindowUntil.set(System.currentTimeMillis() + seconds * 1000L);
        Bukkit.getLogger().info("Klechinos Anti Bot: entering attack window for " + seconds + "s");
    }

    public boolean inAttackWindow() {
        return System.currentTimeMillis() < attackWindowUntil.get();
    }
}
