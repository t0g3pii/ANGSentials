package de.nilsang.ANGSentials.Papierkorb;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;

import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.util.concurrent.atomic.AtomicInteger;

import static org.bukkit.Bukkit.getServer;

public class ScoreboardEvent implements Listener {

    //region Strings
    static String kontostand;
    static String teamOnline;
    static String teamVorhanden;
    static String teamString;
    static String spielzeit;
    //endregion


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ScoreboardSetter();
        
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandSendEvent event) {
        ScoreboardSetter();
        
    }

    @EventHandler
    public void onPlayerCommandPre(PlayerCommandPreprocessEvent event) {
        ScoreboardSetter();
        
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        ScoreboardSetter();
        
    }

    @EventHandler
    public void onPlayerStatsChange(PlayerStatisticIncrementEvent event) {
        ScoreboardSetter();
        
    }

    @EventHandler
    public void onBalanceChange(UserBalanceUpdateEvent event) {
        ScoreboardSetter();
        
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        ScoreboardSetter();
        
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ScoreboardSetter();
        
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ScoreboardSetter();
        Player player = event.getPlayer();
        player.setScoreboard(getServer().getScoreboardManager().getNewScoreboard());
        
    }

    public void ScoreboardSetter() {
        Bukkit.getOnlinePlayers().forEach(player -> {

            //region Scoreboard Initialize
            Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
            Scoreboard scoreboard1 = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

            Objective objective = scoreboard.registerNewObjective("Server", Criteria.DUMMY, "Server");
            objective.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "NilsAng.de");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            //endregion

            //region Static Scores
            objective.getScore(" ").setScore(0);
            objective.getScore(ChatColor.GOLD + "Kontostand:").setScore(-1);
            objective.getScore("  ").setScore(-3);
            objective.getScore(ChatColor.GOLD + "Team:").setScore(-4);
            objective.getScore("   ").setScore(-6);
            objective.getScore(ChatColor.GOLD + "Spielzeit:").setScore(-7);
            objective.getScore("    ").setScore(-9);
            //endregion

            //region Scoreboard Timer
            try {
                //region delete old scores
                if(kontostand != null) {
                    objective.getScoreboard().resetScores(kontostand);
                }
                if(spielzeit != null) {
                    objective.getScoreboard().resetScores(spielzeit);
                }
                if(teamString != null) {
                    objective.getScoreboard().resetScores(teamString);
                }
                //endregion

                //region Money
                objective.getScore(ChatColor.GOLD + Economy.getMoneyExact(player.getUniqueId()).toString() + "$").setScore(-2);
                kontostand = ChatColor.GOLD + Economy.getMoneyExact(player.getUniqueId()).toString() + "$";
                //endregion

                //region Team
                if(scoreboard1.getEntryTeam(player.getName()) != null) {
                    Team team = scoreboard1.getEntryTeam(player.getName());
                    teamVorhanden = Integer.toString(team.getSize());
                    AtomicInteger onlinePlayers = new AtomicInteger();
                    team.getEntries().forEach(teamPlayer -> {
                        if (Bukkit.getServer().getOfflinePlayer(teamPlayer).isOnline()) {
                            onlinePlayers.getAndIncrement();
                        }
                    });
                    teamOnline = onlinePlayers.toString();
                    objective.getScore(ChatColor.GOLD + "[" + teamOnline + "/" + teamVorhanden + "]").setScore(-5);
                    teamString = ChatColor.GOLD + "[" + teamOnline + "/" + teamVorhanden + "]";
                } else {
                    objective.getScore(ChatColor.GOLD + "KEIN TEAM").setScore(-5);
                    teamString = ChatColor.GOLD + "KEIN TEAM";
                }
                //endregion

                //region Spielzeit Berechnung
                Plugin essentialsplugin = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                if (essentialsplugin instanceof Essentials){
                    Essentials essentials = (Essentials) essentialsplugin;
                    User user = essentials.getUser(player);
                    long statsTime = user.getBase().getStatistic(Statistic.PLAY_ONE_MINUTE);
                    int days = (int) Math.floor(statsTime / (20 * 60 * 60 * 24));
                    statsTime -= days * (20 * 60 * 60 * 24);
                    int hours = (int) Math.floor(statsTime / (20 * 60 * 60));
                    statsTime -= hours * (20 * 60 * 60);
                    int minutes = (int) Math.floor(statsTime / (20 * 60));

                    objective.getScore(ChatColor.GOLD + Integer.toString(days) + "D " + hours + "H " + minutes + "M").setScore(-8);
                    spielzeit = ChatColor.GOLD + Integer.toString(days) + "D " + hours + "H " + minutes + "M";
                }
                //endregion

            } catch (UserDoesNotExistException e) {
                throw new RuntimeException(e);
            }
            //endregion

            //region Prefix Fix
            try {
                scoreboard1.getTeams().forEach(team -> {
                    Team tempTeam;
                    if(scoreboard.getTeam(team.getName()) != null) {
                        tempTeam = scoreboard.getTeam(team.getName());
                    } else {
                        tempTeam = scoreboard.registerNewTeam(team.getName());
                    };

                    tempTeam.setPrefix(team.getPrefix());
                    tempTeam.setColor(team.getColor());
                    tempTeam.setSuffix(team.getSuffix());
                    team.getEntries().forEach(teamPlayer -> {
                        tempTeam.addEntry(teamPlayer);
                    });
                    tempTeam.setDisplayName(team.getDisplayName());
                });
                player.setScoreboard(scoreboard);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //endregion
        });
    }
}

