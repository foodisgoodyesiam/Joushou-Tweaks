package foodisgood.mods.joushou_tweaks.command;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class JTMyNameCommand extends CommandBase {
	@Override
	public String getCommandName() {
		return "myname";
	}
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/myname";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		sender.addChatMessage(new ChatComponentText("Your name is: " + sender.getCommandSenderName()));
		try {
			ChunkCoordinates coords = sender.getPlayerCoordinates();
			String message;
			if (coords==null)
				message = "Your coordinates are null! What the heck are you?";
			else
				message = "x: " + coords.posX + ", y: " + coords.posY + ", z: " + coords.posZ;
			sender.addChatMessage(new ChatComponentText(message));
		} catch (Throwable e) {
			sender.addChatMessage(new ChatComponentText(e.getMessage()));
			e.printStackTrace();
			FMLRelaunchLog.log(Level.ERROR, e.getMessage());
		}
	}
}
