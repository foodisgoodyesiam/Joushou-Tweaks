package foodisgood.mods.joushou_tweaks.command;

import net.minecraft.command.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class JTLCommand extends CommandBase {
	@Override
	public String getCommandName() {
		return "l";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return " [player]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		try {
			World w = sender.getEntityWorld();
			EntityPlayer p;
			if (args.length>0)
				p = w.getPlayerEntityByName(args[0]);
			else
				p = w.getPlayerEntityByName(sender.getCommandSenderName());
			
			w.spawnEntityInWorld(new EntityLightningBolt(w, p.posX+2, p.posY, p.posZ));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
