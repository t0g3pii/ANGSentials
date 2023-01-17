package de.nilsang.ANGSentials;

import de.nilsang.ANGSentials.Quests.QuestsGUI;
import de.nilsang.ANGSentials.Scoreboard.listeners.ScoreboardListener;
import de.nilsang.ANGSentials.Voicechat.listeners.VoicechatListener;
import de.nilsang.ANGSentials.Scoreboard.ScoreboardSetter;
import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main instance;
    public ScoreboardSetter statsBoard = new ScoreboardSetter();
    public String prefix = ChatColor.GRAY + "[" + ChatColor.GREEN + "ANG" + ChatColor.GRAY + "] ";

    @Override
    public void onEnable() {
        instance = this;

        //region Scoreboard
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        //endregion

        //region Voicechat + Luckperms
        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (service != null && provider != null) {
            LuckPerms api = provider.getProvider();
            service.registerPlugin(new VoicechatListener(this, api));
        }
        //endregion

        //region Quests
        Objects.requireNonNull(this.getCommand("quest")).setExecutor(new QuestsGUI());
        getServer().getPluginManager().registerEvents(new QuestsGUI(), this);
        //endregion

        System.out.println(prefix + "has been enabled.");
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println(prefix + "has been disabled.");
    }
}
