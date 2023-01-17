package de.nilsang.ANGSentials.Voicechat.listeners;

import de.nilsang.ANGSentials.Main;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.player.PlayerLoginProcessEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.UUID;

public class VoicechatListener implements Listener, VoicechatPlugin {
    private static LuckPerms luckPerms;
    public VoicechatListener(Main main, LuckPerms api) {
        luckPerms = api;
        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(main, PlayerLoginProcessEvent.class, this::onPlayerJoin);
    }

    public void onPlayerJoin(PlayerLoginProcessEvent event) {
        User user = event.getUser();
        assert user != null;
        if(user.getCachedData().getMetaData().getSuffix() != null) {
            user.data().clear(node -> node.getType() == NodeType.SUFFIX);
        }
        user.data().add(Node.builder("suffix.0. &c[\uE238]").build());
        luckPerms.getUserManager().saveUser(user);
    }

    @Override
    public String getPluginId() {
        return "ANGSentials";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(PlayerConnectedEvent.class, this::onVoicechatConnection);
    }

    public void onVoicechatConnection(PlayerConnectedEvent event) {
        VoicechatConnection con = event.getConnection();
        UUID playerUUID = con.getPlayer().getUuid();
        User user = luckPerms.getUserManager().getUser(playerUUID);
        assert user != null;
        if(user.getCachedData().getMetaData().getSuffix() != null) {
            user.data().clear(node -> node.getType() == NodeType.SUFFIX);
        }
        user.data().add(Node.builder("suffix.0. &a[\uE239]").build());
        luckPerms.getUserManager().saveUser(user);
        Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).sendMessage(Main.getInstance().prefix + "Du bist erfolgreich mit der Voicemod Verbunden!");
    }
}
