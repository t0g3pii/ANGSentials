package de.nilsang.ANGSentials.Scoreboard.listeners;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;
import de.nilsang.ANGSentials.Main;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ScoreboardListener implements Listener {

    BukkitTask scoreboardUpdater;
    BukkitTask teamUpdater;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Main.getInstance().statsBoard.setScoreboard(p);

        scoreboardUpdater = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            try {
                //region Money
                int money = Integer.parseInt(Economy.getMoneyExact(p.getUniqueId()).toString());
                //endregion

                //region Team
                Scoreboard mainScoreboard = Objects.requireNonNull(Bukkit.getServer().getScoreboardManager()).getMainScoreboard();
                int teamAvailable;
                int teamOnline;

                if(mainScoreboard.getEntryTeam(p.getName()) != null) {
                    Team team = mainScoreboard.getEntryTeam(p.getName());
                    assert team != null;
                    teamAvailable = team.getSize();
                    AtomicInteger onlinePlayers = new AtomicInteger();
                    team.getEntries().forEach(teamPlayer -> {
                        if (Bukkit.getServer().getOfflinePlayer(teamPlayer).isOnline()) {
                            onlinePlayers.getAndIncrement();
                        }
                    });
                    teamOnline = Integer.parseInt(onlinePlayers.toString());
                } else {
                    teamAvailable = 0;
                    teamOnline = 0;
                }
                //endregion

                //region Playtime
                long statsTime = 0;
                Plugin essentialsplugin = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                if (essentialsplugin instanceof Essentials){
                    Essentials essentials = (Essentials) essentialsplugin;
                    User user = essentials.getUser(p);
                    statsTime = user.getBase().getStatistic(Statistic.PLAY_ONE_MINUTE);
                }
                //endregion

                //region Setter
                Main.getInstance().statsBoard.moneyCount = money;
                Main.getInstance().statsBoard.teamOnlineCount = teamOnline;
                Main.getInstance().statsBoard.teamAvailableCount = teamAvailable;
                Main.getInstance().statsBoard.playtimeCount = statsTime;
                Main.getInstance().statsBoard.updateScoreboard(p);
                //endregion
            } catch (UserDoesNotExistException e) {
                throw new RuntimeException(e);
            }
        }, 0 , 200);

        teamUpdater = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> Main.getInstance().statsBoard.updateTeams(p), 0, 20);

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        scoreboardUpdater.cancel();
        teamUpdater.cancel();
    }
}
