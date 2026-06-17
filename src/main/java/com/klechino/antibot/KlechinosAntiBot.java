package com.klechino.antibot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class KlechinosAntiBot extends JavaPlugin {
    private VerificationManager verificationManager;
    private AttackDetector attackDetector;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            this.verificationManager = new VerificationManager(this);
            this.attackDetector = new AttackDetector(this);

            getCommand("antibot").setExecutor(new AntibotCommand(this));
            getCommand("verify").setExecutor(new VerifyCommand(this));

            Bukkit.getPluginManager().registerEvents(new JoinListener(this, verificationManager, attackDetector), this);
            Bukkit.getPluginManager().registerEvents(new InteractionGuard(this, verificationManager), this);

            // periodic cleanup
            getServer().getScheduler().runTaskTimerAsynchronously(this, verificationManager::cleanupExpired, 20*10L, 20*10L);

            getLogger().info("Klechinos Anti Bot enabled");
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Failed to enable Klechinos Anti Bot", ex);
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        if (verificationManager != null) verificationManager.clearAll();
        getLogger().info("Klechinos Anti Bot disabled");
    }

    public VerificationManager getVerificationManager() {
        return verificationManager;
    }

    public AttackDetector getAttackDetector() {
        return attackDetector;
    }
}
