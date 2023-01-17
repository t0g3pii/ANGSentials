package de.nilsang.ANGSentials.Scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;
import java.util.Set;

public class ScoreboardSetter {
    public int moneyCount = 0;
    public int teamOnlineCount = 0;
    public int teamAvailableCount = 1;
    public long playtimeCount = 0;

    public void setScoreboard(Player p) {
        Scoreboard board = Objects.requireNonNull(Bukkit.getServer().getScoreboardManager()).getNewScoreboard();
        Scoreboard mainBoard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        Objective obj = board.registerNewObjective("Server", Criteria.DUMMY, "Server");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "NilsANG");

        Team money = board.registerNewTeam("money");
        Team team = board.registerNewTeam("teaminfo");
        Team playtime = board.registerNewTeam("playtime");

        obj.getScore(" ").setScore(0);
        obj.getScore(ChatColor.GOLD + "Kontostand: ").setScore(-1);
        obj.getScore(ChatColor.RED + "").setScore(-2);
        obj.getScore("  ").setScore(-3);
        obj.getScore(ChatColor.GOLD + "Team: ").setScore(-4);
        obj.getScore(ChatColor.GREEN + "").setScore(-5);
        obj.getScore("   ").setScore(-6);
        obj.getScore(ChatColor.GOLD + "Spielzeit: ").setScore(-6);
        obj.getScore(ChatColor.AQUA + "").setScore(-7);
        obj.getScore("    ").setScore(-8);

        money.addEntry(ChatColor.RED + "");
        money.setPrefix(ChatColor.RED + Integer.toString(moneyCount) + "$");

        team.addEntry(ChatColor.GREEN + "");
        team.setPrefix(ChatColor.GREEN  + "[" + teamOnlineCount + "/" + teamAvailableCount + "]");

        playtime.addEntry(ChatColor.AQUA + "");
        playtime.setPrefix(ChatColor.AQUA + calcPlaytime(playtimeCount));

        //region Prefix Tab beim Joinen setzen
        mainBoard.getTeams().forEach(mainTeam -> {
            Team tempTeam;
            if(board.getTeam(mainTeam.getName()) != null) {
                tempTeam = board.getTeam(mainTeam.getName());
            } else {
                tempTeam = board.registerNewTeam(mainTeam.getName());
            }

            assert tempTeam != null;
            tempTeam.setPrefix(mainTeam.getPrefix());
            tempTeam.setColor(mainTeam.getColor());
            tempTeam.setSuffix(mainTeam.getSuffix());
            mainTeam.getEntries().forEach(tempTeam::addEntry);
            tempTeam.setDisplayName(team.getDisplayName());
        });
        //endregion

        p.setScoreboard(board);
    }

    public void updateScoreboard(Player p) {
        Scoreboard board = p.getScoreboard();
        Team money = board.getTeam("money");
        Team team = board.getTeam("teaminfo");
        Team playtime = board.getTeam("playtime");
        assert money != null;
        assert team != null;
        assert playtime != null;


        money.setPrefix(ChatColor.RED + Integer.toString(moneyCount) + "$");
        if(teamAvailableCount == 0) {
            team.setPrefix(ChatColor.RED  + "[KEIN TEAM]");
        } else {
            team.setPrefix(ChatColor.GREEN  + "[" + teamOnlineCount + "/" + teamAvailableCount + "]");
        }
        playtime.setPrefix(ChatColor.AQUA + calcPlaytime(playtimeCount));
    }
    public void updateTeams(Player p) {
        //region Prefix Fix alle 1 Sekunde
        // Team Änderungen im Tab sind nur alle 1 Sekunden zu sehen...
        // Fix dafür eventuell ein eigener Timer? (TPS Probleme evtl?)
        Scoreboard board = p.getScoreboard();
        Scoreboard mainBoard = Objects.requireNonNull(Bukkit.getServer().getScoreboardManager()).getMainScoreboard();
        mainBoard.getTeams().forEach(mainTeam -> {
            Team tempTeam;
            if(board.getTeam(mainTeam.getName()) != null) {
                tempTeam = board.getTeam(mainTeam.getName());
            } else {
                tempTeam = board.registerNewTeam(mainTeam.getName());
            }

            assert tempTeam != null;
            if(!mainTeam.getPrefix().equals(tempTeam.getPrefix())) { tempTeam.setPrefix(mainTeam.getPrefix()); }
            if(!mainTeam.getSuffix().equals(tempTeam.getSuffix())) { tempTeam.setSuffix(mainTeam.getSuffix()); }
            if(!mainTeam.getDisplayName().equals(tempTeam.getDisplayName())) { tempTeam.setDisplayName(mainTeam.getDisplayName()); }
            if(mainTeam.getColor() != tempTeam.getColor()) { tempTeam.setColor(mainTeam.getColor()); }

            Set<String> tempTeamPlayers = tempTeam.getEntries();
            if(mainTeam.getEntries() != tempTeamPlayers) {
                tempTeam.getEntries().forEach(tempTeam::removeEntry);
                mainTeam.getEntries().forEach(tempTeam::addEntry);
            }
        });
        //endregion
    }
    public String calcPlaytime(long playtime) {
        int days = (int) Math.floor(playtime / (20 * 60 * 60 * 24));
        playtime -= days * (20 * 60 * 60 * 24);
        int hours = (int) Math.floor(playtime / (20 * 60 * 60));
        playtime -= hours * (20 * 60 * 60);
        int minutes = (int) Math.floor(playtime / (20 * 60));

        return ChatColor.AQUA + Integer.toString(days) + "D " + hours + "H " + minutes + "M";
    }
}
