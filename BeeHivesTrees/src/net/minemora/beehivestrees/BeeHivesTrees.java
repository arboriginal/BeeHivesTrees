package net.minemora.beehivestrees;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.beehivestrees.config.ConfigMain;

public class BeeHivesTrees extends JavaPlugin implements Listener {

	private Random random = new Random();

	@Override
	public void onEnable() {
		ConfigMain.getInstance().setup(this);
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {
		if(!ConfigMain.isAllowAllTrees() && !ConfigMain.getTreeTypes().contains(event.getSpecies().name())) {
			return;
		}
		if(!ConfigMain.isAllowAllBiomes() && !ConfigMain.getBiomes().contains(event.getLocation().getBlock().getBiome().name())) {
			return;
		}
		if(random.nextFloat() <= ConfigMain.getChanceToHaveNest()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(BlockState bs : event.getBlocks()) {
						Block block = bs.getBlock();
						if(!block.getType().name().endsWith("LEAVES")) {
							continue;
						}
						if(block.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
							continue;
						}
						Block hive = block.getRelative(BlockFace.DOWN);
						hive.setType(Material.BEE_NEST);
						for(int i = 0; i < ConfigMain.getBeesPerNest(); i++) {
							Bee bee = (Bee) block.getWorld().spawnEntity(hive.getLocation().add(0, -1, 0), EntityType.BEE);
							bee.setHive(hive.getLocation());
						}
						return;
					}
				}
			}.runTaskLater(this, 1);
		}
	}	
}
