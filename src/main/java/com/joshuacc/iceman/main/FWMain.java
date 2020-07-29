package com.joshuacc.iceman.main;

import java.util.ArrayList;
import java.util.Iterator;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;

public class FWMain extends PluginBase implements Listener {

	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getCommandMap().register("iceboots", new IcebootsCommand(this));
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		Item boots = player.getInventory().getBoots();
		if(boots.hasCompoundTag())
		{
			int rad = getRadius(boots);
			if(rad != 0)
			{
				ArrayList<Location> blocks = new ArrayList<>();
				for(int z = -rad; z <= rad; z++)
				{
					for(int x = -rad; x <= rad; x++)
					{
						Location loc = player.getLocation().add(x, -1, z);
						Block block = loc.getLevelBlock();

						if(block.up().getId() != Block.ICE_FROSTED && block.getDamage() == 0 && 
								block.up().getId() != Block.WATER && block.up().getId() != Block.STILL_WATER && 
								(block.getId() == Block.WATER || block.getId() == Block.STILL_WATER))
						{	
							if(loc.distance(player) >= rad+1)
								continue;

							blocks.add(loc);
							player.getLevel().setBlock(loc, Block.get(Block.ICE_FROSTED));
						}
					}
				}

				if(!blocks.isEmpty())
					decayIce(player, blocks);
			}
		}
	}

	private void decayIce(Player player, ArrayList<Location> blocks)
	{
		Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() {

			@Override
			public void onRun(int arg0) 
			{	
				Iterator<Location> b = blocks.iterator();
				if(player.isOnline())
				{
					Item item = player.getInventory().getBoots();
					int radius = getRadius(item);

					while(b.hasNext())
					{
						Location block = b.next();
						if(block.distance(player) >= radius+1)
							clearIce(block);

						if(block.getLevelBlock().getId() != Block.ICE_FROSTED)
							b.remove();
					}
				} else {
					while(b.hasNext())
					{
						Location block = b.next();
						clearIce(block);
						b.remove();
					}
				}

				if(blocks.isEmpty())
					this.cancel();
			}

		}, getConfig().getInt("Decay Seconds") * 20);
	}

	private void clearIce(Location block)
	{
		if(block.getLevelBlock().getId() == Block.ICE_FROSTED)
		{
			block.getLevel().setBlock(block, Block.get(Block.WATER));
			block.getLevel().addParticle(new DestroyBlockParticle(block, Block.get(Block.ICE_FROSTED)));
			block.getLevel().addSound(block, Sound.BLOCK_LANTERN_BREAK);
		}
	}

	public static int getRadius(Item item)
	{
		if(item.hasCompoundTag() && item.getNamedTag().contains("radius"))
			return item.getNamedTag().getInt("radius");
		else
			return 0;
	}
}
