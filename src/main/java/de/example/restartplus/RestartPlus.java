package de.example.restartplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RestartPlus extends JavaPlugin {

    private BukkitRunnable restartTask = null;
    private BukkitRunnable stopTask = null;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String cmd = command.getName().toLowerCase();

        // ---------------- RESTART ----------------
        if (cmd.equals("restartplus")) {
            if (!sender.hasPermission("restartplus.restart")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }

            if (restartTask != null) {
                sender.sendMessage(ChatColor.RED + "A restart is already running.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /restartplus <seconds> <reason>");
                return true;
            }

            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Time must be a number.");
                return true;
            }

            String reason = String.join(" ", args).substring(args[0].length() + 1);

            Bukkit.broadcastMessage(ChatColor.RED + "⚠ Server Restart Scheduled");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + reason);
            Bukkit.broadcastMessage(ChatColor.GOLD + "Restart in " + time + " seconds");

            restartTask = new BukkitRunnable() {
                int countdown = time;

                @Override
                public void run() {
                    if (countdown <= 0) {
                        Bukkit.broadcastMessage(ChatColor.RED + "Restarting server now!");
                        Bukkit.spigot().restart();
                        cancel();
                        restartTask = null;
                        return;
                    }

                    if (countdown <= 10 || countdown % 30 == 0) {
                        Bukkit.broadcastMessage(ChatColor.GOLD + "Restart in " + countdown + " seconds");
                    }

                    countdown--;
                }
            };

            restartTask.runTaskTimer(this, 20L, 20L);
            return true;
        }

        // ---------------- STOP ----------------
        if (cmd.equals("stopplus")) {
            if (!sender.hasPermission("restartplus.stop")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }

            if (stopTask != null) {
                sender.sendMessage(ChatColor.RED + "A stop is already running.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /stopplus <seconds> <reason>");
                return true;
            }

            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Time must be a number.");
                return true;
            }

            String reason = String.join(" ", args).substring(args[0].length() + 1);

            Bukkit.broadcastMessage(ChatColor.RED + "⚠ Server Stop Scheduled");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + reason);
            Bukkit.broadcastMessage(ChatColor.GOLD + "Server stops in " + time + " seconds");

            stopTask = new BukkitRunnable() {
                int countdown = time;

                @Override
                public void run() {
                    if (countdown <= 0) {
                        Bukkit.broadcastMessage(ChatColor.RED + "Stopping server now!");
                        Bukkit.shutdown();
                        cancel();
                        stopTask = null;
                        return;
                    }

                    if (countdown <= 10 || countdown % 30 == 0) {
                        Bukkit.broadcastMessage(ChatColor.GOLD + "Server stops in " + countdown + " seconds");
                    }

                    countdown--;
                }
            };

            stopTask.runTaskTimer(this, 20L, 20L);
            return true;
        }

        // ---------------- CANCEL RESTART ----------------
        if (cmd.equals("cancelrestart")) {
            if (!sender.hasPermission("restartplus.cancelrestart")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }

            if (restartTask == null) {
                sender.sendMessage(ChatColor.RED + "No restart is currently running.");
                return true;
            }

            restartTask.cancel();
            restartTask = null;
            Bukkit.broadcastMessage(ChatColor.GREEN + "The server restart has been cancelled.");
            return true;
        }

        // ---------------- CANCEL STOP ----------------
        if (cmd.equals("cancelstop")) {
            if (!sender.hasPermission("restartplus.cancelstop")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }

            if (stopTask == null) {
                sender.sendMessage(ChatColor.RED + "No stop is currently running.");
                return true;
            }

            stopTask.cancel();
            stopTask = null;
            Bukkit.broadcastMessage(ChatColor.GREEN + "The server stop has been cancelled.");
            return true;
        }

        return false;
    }
}
