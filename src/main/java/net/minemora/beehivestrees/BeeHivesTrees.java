package net.minemora.beehivestrees;

import java.util.List;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Bee;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BeeHivesTrees extends JavaPlugin implements Listener {
    private final Random rng = new Random();

    private BlockData     nest;
    private boolean       allBiomes, allTypes, boneMeal;
    private int           chance, maxBees, minBees;
    private List<String>  biomes, types;
    private Set<Material> leaves;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration c = getConfig();
        c.options().copyDefaults(true);

        allBiomes = c.getBoolean("allow-all-biomes");
        allTypes  = c.getBoolean("allow-all-tree-types");
        boneMeal  = c.getBoolean("allow-from-bonemeal");
        minBees   = c.getInt("min-bees-per-nest");
        maxBees   = c.getInt("max-bees-per-nest") - minBees;
        chance    = c.getInt("chance-to-have-a-bee-nest");
        biomes    = c.getStringList("allowed-biomes");
        types     = c.getStringList("allowed-tree-types");
        leaves    = Tag.LEAVES.getValues();
        nest      = Bukkit.createBlockData(Material.BEE_NEST);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true) // @formatter:off
    public void onStructureGrow(StructureGrowEvent e) {
        if ((boneMeal  || !e.isFromBonemeal())
         && (allTypes  || types.contains(e.getSpecies().name()))
         && (allBiomes || biomes.contains(e.getLocation().getBlock().getBiome().name()))
         && chance > rng.nextInt(100)) spawn(e.getBlocks());
    } // @formatter:on

    private void spawn(List<BlockState> blocks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (BlockState bs : blocks) {
                    if (!leaves.contains(bs.getType())) continue;
                    Block b = bs.getBlock().getRelative(BlockFace.DOWN);
                    if (b.getType() != Material.AIR) continue;
                    b.setBlockData(nest);
                    Location h = b.getLocation(), l = h.clone().add(0, -1, 0);
                    World    w = h.getWorld();
                    for (int i = 0; i < rng.nextInt(maxBees) + minBees; i++) w.spawn(l, Bee.class).setHive(h);
                    return;
                }
            }
        }.runTask(this);
    }
}
