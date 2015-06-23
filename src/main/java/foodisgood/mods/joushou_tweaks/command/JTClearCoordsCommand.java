package foodisgood.mods.joushou_tweaks.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class JTClearCoordsCommand extends CommandBase {
	public static boolean op;
	
	@Override
	public final String getCommandName() {
		return "clearcoords";
	}
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return (!op || super.canCommandSenderUseCommand(sender));
    }

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return " [range]/ [range] [x] [z]/ [range] [x] [z] [width] (width is in chunks, other in blocks)";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		int cx, cz, range, width = 1;
		World w = sender.getEntityWorld();
		try {
			switch (args.length) {
			case 1:
				try {
					EntityPlayer p = w.getPlayerEntityByName(sender.getCommandSenderName());
					cx = (int)p.posX;
					cz = (int)p.posZ;
				} catch (Throwable e) {
					throw new WrongUsageException("Are you a player?");
				}
				break;
			default:
	            throw new WrongUsageException("Wrong number of arguments!");
			case 4:
				width = Integer.parseInt(args[3]);
				if (width<1)
					throw new WrongUsageException("Invalid width");
			case 3:
				cx = Integer.parseInt(args[1]);
				cz = Integer.parseInt(args[2]);
			}
			range = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			throw new WrongUsageException("That's not a number!");
		}
		for (int x=cx; x>cx-range; x-=16)
			for (int z=cz-16*(width/2); z<cz+16*(width/2+width%2); z+=16)
				w.getBlock(x, 20, z);
		sender.addChatMessage(new ChatComponentText("West done"));
		for (int x=cx; x<cx+range; x+=16)
			for (int z=cz-16*(width/2); z<cz+16*(width/2+width%2); z+=16)
				w.getBlock(x, 20, z);
		sender.addChatMessage(new ChatComponentText("East done"));
		for (int z=cz; z>cz-range; z-=16)
			for (int x=cx-16*(width/2); x<cx+16*(width/2+width%2); x+=16)
				w.getBlock(x, 20, z);
		sender.addChatMessage(new ChatComponentText("North done"));
		for (int z=cz; z<cz+range; z+=16)
			for (int x=cx-16*(width/2); x<cx+16*(width/2+width%2); x+=16)
				w.getBlock(x, 20, z);
		sender.addChatMessage(new ChatComponentText("South done"));
	}
}
