package com.joshuacc.iceman.main;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class IcebootsCommand extends Command {

	private Config config;

	public IcebootsCommand(FWMain main) {
		super("iceboots", "Enchants your boots with ice walker ability!");
		this.setPermission("frost.iceboots");
		this.commandParameters.put("default", new CommandParameter[] {
				new CommandParameter("radius", CommandParamType.INT, false)
		});
		this.config = main.getConfig();
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {

		if(!(sender instanceof Player))
			return false;

		Player player = (Player) sender;
		if(player.hasPermission("frost.iceboots"))
		{
			switch(args.length)
			{
			case 0: player.sendMessage(format("Little-Arguements Message")); break;
			case 1:
				try {
					int value = Integer.parseInt(args[0]);
					Item item = player.getInventory().getItemInHand();
					if(item.isBoots())
					{
						if(value <= config.getInt("Maximum Radius"))
						{
							if(value > 0)
							{
								player.getInventory().remove(item);
								item.setLore("§r"+convertRadius("Iceboots Enchantment", value));
								player.getInventory().addItem(item);
								addRadiusTag(item, value);
								player.sendMessage(convertRadius("Successful Message", value));
							} else
								player.sendMessage(format("Lower-Radius Message"));
						} else 
							player.sendMessage(format("Higher-Radius Message"));
					} else 
						player.sendMessage(format("Incorrect-Item Message"));
				} catch (Exception e) {
					player.sendMessage(format("Incorrect-Number Message"));
				}
				break;
			}
		} else 
			player.sendMessage(format("No-Permission Message"));
		return true;
	}

	private String convertRadius(String sel, int value)
	{
		return TextFormat.colorize('&', config.getString(sel).replace("%RADIUS%", Integer.toString(value)));
	}

	private String format(String message)
	{
		return TextFormat.colorize('&', config.getString(message));
	}
	
	private void addRadiusTag(Item item, int radius)
	{
		CompoundTag tag;
		if(!item.hasCompoundTag())
			tag = new CompoundTag();
		else
			tag = item.getNamedTag();
		tag.putInt("radius", radius);

		item.setNamedTag(tag);
	}
}
