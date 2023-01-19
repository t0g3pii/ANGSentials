package de.nilsang.ANGSentials.Quests;

import de.nilsang.ANGSentials.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class QuestsGUI implements Listener, CommandExecutor {

    private final Inventory inv;

    public QuestsGUI() {
        inv = Bukkit.createInventory(null, 27, ChatColor.BLUE + "" + ChatColor.BOLD + "Tägliche Quests");
    }

    public void initializeItems(Player player) {
        Object[][] stats = checkStats(player);
        for(int i = 0; i < 26; i++) {
            inv.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, false, "", ""));
            if(i == 11) {
                inv.setItem(i, createGuiItem((Material) stats[0][1], (Boolean) stats[0][2], (String) stats[0][0], (String) stats[0][3]));
            } else if(i == 13) {
                inv.setItem(i, createGuiItem((Material) stats[1][1], (Boolean) stats[1][2], (String) stats[1][0], (String) stats[1][3]));
            } else if(i == 15) {
                inv.setItem(i, createGuiItem((Material) stats[2][1], (Boolean) stats[2][2], (String) stats[2][0], (String) stats[2][3]));
            }
        }
    }

    public Object[][] checkStats(Player player) {
        Object[][] list = new Object[3][4];
        list[0][0] = player.getStatistic(Statistic.KILL_ENTITY, EntityType.SHEEP) >= 10 ? ChatColor.GREEN + "" + ChatColor.BOLD + "Töte 10 Schafe" : ChatColor.RED + "" + ChatColor.BOLD + "Töte 10 Schafe [" + player.getStatistic(Statistic.KILL_ENTITY, EntityType.SHEEP) + "/10]";
        list[0][1] = player.getStatistic(Statistic.KILL_ENTITY, EntityType.SHEEP) >= 10 ? Material.BARRIER : Material.NETHERITE_SWORD;
        list[0][2] = player.getStatistic(Statistic.KILL_ENTITY, EntityType.SHEEP) >= 10;
        list[0][3] = player.getStatistic(Statistic.KILL_ENTITY, EntityType.SHEEP) >= 10 ? ChatColor.GOLD + "" + ChatColor.BOLD + "Belohnung: " + ChatColor.RESET + ChatColor.GREEN + "Bereits erhalten." + ChatColor.RESET + ChatColor.GRAY + " (10$)" : ChatColor.GOLD + "" + ChatColor.BOLD + "Belohnung: " + ChatColor.RESET + ChatColor.GRAY + "10$";

        list[1][0] = (player.getStatistic(Statistic.WALK_ONE_CM) / 100) >= 10000 ? ChatColor.GREEN + "" + ChatColor.BOLD + "Laufe 10.000 Blöcke" : ChatColor.RED + "" + ChatColor.BOLD + "Laufe 10.000 Blöcke [" + (player.getStatistic(Statistic.WALK_ONE_CM) / 100) + "/10000]";
        list[1][1] = (player.getStatistic(Statistic.WALK_ONE_CM) / 100) >= 10000 ? Material.BARRIER : Material.NETHERITE_BOOTS;
        list[1][2] = (player.getStatistic(Statistic.WALK_ONE_CM) / 100) >= 10000;
        list[1][3] = (player.getStatistic(Statistic.WALK_ONE_CM) / 100) >= 10000 ? ChatColor.GOLD + "" + ChatColor.BOLD + "Belohnung: " + ChatColor.RESET + ChatColor.GREEN + "Bereits erhalten." + ChatColor.RESET + ChatColor.GRAY + " (10$)" : ChatColor.GOLD + "" + ChatColor.BOLD + "Belohnung: " + ChatColor.RESET + ChatColor.GRAY + "10$";

        list[2][0] = player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) >= 1000 ? ChatColor.GREEN + "" + ChatColor.BOLD + "Baue 1.000 Steine ab" : ChatColor.RED + "" + ChatColor.BOLD + "Baue 1.000 Steine ab [" + player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) + "/1000]";
        list[2][1] = player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) >= 1000 ? Material.BARRIER : Material.NETHERITE_PICKAXE;
        list[2][2] = player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) >= 1000;
        list[2][3] = player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) >= 1000 ? ChatColor.GOLD + "" + ChatColor.BOLD + "Belohnung: " + ChatColor.RESET + ChatColor.GREEN + "Bereits erhalten." + ChatColor.RESET + ChatColor.GRAY + " (10$)" : ChatColor.GOLD + "" + ChatColor.BOLD + "Belohnung: " + ChatColor.RESET + ChatColor.GRAY + "10$";
        return list;
    }

    protected ItemStack createGuiItem(final Material material, final boolean ench, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if(ench) {
            meta.addEnchant((item.getType() == Material.BOW) ? Enchantment.PROTECTION_ENVIRONMENTAL : Enchantment.ARROW_INFINITE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setLore(Arrays.asList(lore));
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if(event.getView().getTitle().equals(ChatColor.BLUE + "" + ChatColor.BOLD + "Tägliche Quests")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent event) {
        if(event.getView().getTitle().equals(ChatColor.BLUE + "" + ChatColor.BOLD + "Tägliche Quests")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length == 0) {
                initializeItems(player);
                openInventory(player);
            }
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("show")) {
                    player.sendMessage(Main.getInstance().prefix + "Hier sind deine aktuellen Statistiken: ");
                    player.sendMessage("Stein abgebaut: " + player.getStatistic(Statistic.MINE_BLOCK, Material.STONE));
                    player.sendMessage("Schafe getötet: " + player.getStatistic(Statistic.KILL_ENTITY, EntityType.SHEEP));
                    player.sendMessage("Gelaufene Blöcke: " + (player.getStatistic(Statistic.WALK_ONE_CM) / 100));
                } else if(args[0].equalsIgnoreCase("reset")) {
                    player.sendMessage(Main.getInstance().prefix + "Deine Statistiken wurden zurückgesetzt.");
                    player.setStatistic(Statistic.MINE_BLOCK, Material.STONE, 0);
                    player.setStatistic(Statistic.KILL_ENTITY, EntityType.SHEEP, 0);
                    player.setStatistic(Statistic.WALK_ONE_CM, 0);
                } else if(args[0].equalsIgnoreCase("todo")) {
                    String[] todo = new String[10];
                    todo[0] = "YAML anlegen und lernen wie man sie benutzt.";
                    todo[1] = "Wenn Spieler joint, prüfen ob bereits Eintrag in der playerData.\nWenn Nein, anlegen und Random 3 Quests zuweisen mit Geld und Stats zurücksetzen!";
                    todo[2] = "Quest täglich um 00:00 für alle löschen.";
                    todo[3] = "[Done] ";
                    todo[4] = "Automatische Erkennung bei Questabschluss. + Chatnachricht";
                    todo[5] = "Bei Questabschluss dem Spieler Geld geben.";
                    todo[6] = "[Done] ";
                    todo[7] = "Inventar an Spieler binden, sonst wird Inventar 'getauscht'";
                    todo[8] = "TabCompleter einbasteln";
                    todo[9] = "Listen ausarbeiten. (Max Werte und Money zuweisen)";
                    player.sendMessage(Main.getInstance().prefix + ChatColor.RED + "Hier ist die Aktuelle TODO Liste von t0g3pii von " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + ":");
                    for(int i = 0; i < todo.length; i++) {
                        if(!todo[i].equals("")) {
                            player.sendMessage(Main.getInstance().prefix + i+1 + "." + todo[i]);
                        }
                    }
                }
            }

        }
        return true;
    }

    private static final EntityType[] ENTITIES = {
            //region MONSTER
            EntityType.BLAZE, // max 10
            EntityType.CAVE_SPIDER, // max 10
            EntityType.CREEPER, // max 10
            EntityType.ENDERMAN, // max 10
            EntityType.GHAST, // max 5
            EntityType.MAGMA_CUBE, // max 10
            EntityType.PIGLIN, // max 25
            EntityType.SKELETON, // max 10
            EntityType.SLIME, // max 10
            EntityType.SPIDER, // max 10
            EntityType.WITCH, // max 3
            EntityType.ZOMBIE, // max 10
            EntityType.HUSK, // max 10
            //endregion

            //region ANIMALS
            EntityType.COW, // max 25
            EntityType.PIG, // max 25
            EntityType.SHEEP, // max 25
            EntityType.HORSE, // max 10
            EntityType.AXOLOTL, // max 5
            EntityType.BEE, // max 5
            EntityType.CHICKEN, // max 25
            EntityType.COD, // max 25
            EntityType.FOX, // max 10
            EntityType.FROG, // max 10
            EntityType.GLOW_SQUID, // max 10
            EntityType.SQUID, // max 10
            EntityType.GOAT, // max 10
            EntityType.IRON_GOLEM, // max 5
            EntityType.LLAMA, // max 10
            EntityType.MUSHROOM_COW, // max 5
            EntityType.OCELOT, // max 10
            EntityType.POLAR_BEAR, // max 5
            EntityType.RABBIT, // max 10
            EntityType.SNOWMAN, // max 10
            EntityType.TURTLE, // max 5
            EntityType.WOLF, // max 10
            //endregion

            //region OTHERS || Super Seltene Chance || Max 1!
            EntityType.PLAYER,
            EntityType.ENDER_DRAGON,
            EntityType.WARDEN,
            EntityType.WITHER,
            //endregion
    };

    private static final Material[] BLOCKS = {
            //region BLOCKS
            Material.STONE,
            Material.DIRT,
            Material.GRASS,
            Material.SAND,
            Material.SANDSTONE,
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.JUNGLE_LOG,
            Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG,
            Material.CRIMSON_STEM,
            Material.WARPED_STEM,
            Material.ANDESITE,
            Material.DIORITE,
            Material.GRANITE,
            Material.DEEPSLATE,
            Material.CALCITE,
            Material.TUFF,
            Material.DRIPSTONE_BLOCK,
            Material.CRIMSON_NYLIUM,
            Material.WARPED_NYLIUM,
            Material.RED_SAND,
            Material.GRAVEL,
            Material.GLOWSTONE,
            Material.OBSIDIAN,
            //endregion

            //region ORES
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.LAPIS_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.GLOWSTONE,
            //endregion

            //region FOOD
            Material.PUMPKIN,
            Material.MELON,
            Material.WHEAT,
            Material.CARROT,
            Material.POTATO,
            Material.BEETROOT,
            Material.HAY_BLOCK
            //endregion
    };

    private static final int[] RUN = {
            1000,
            2500,
            5000,
            7500,
            10000
    };
}
