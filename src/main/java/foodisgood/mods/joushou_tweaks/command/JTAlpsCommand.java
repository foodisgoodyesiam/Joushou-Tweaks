package foodisgood.mods.joushou_tweaks.command;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class JTAlpsCommand extends CommandBase {
	@Override
	public String getCommandName() {
		return "findalps";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return " [username]/ [username] [range]/ [x] [z] [range]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		int x, z, range = 2000;
		EntityPlayer p;
		World w;
		w = sender.getEntityWorld();
		switch (args.length) {
		case 0:
            throw new WrongUsageException("Error: Not enough arguments");
        default:
            throw new WrongUsageException("Error: Too many arguments");
		case 2:
			try {
				range = Integer.parseInt(args[1]);
			} catch (Throwable e) {
	            throw new WrongUsageException("That's not a number!");
			}
		case 1:
			try {
				p = w.getPlayerEntityByName(sender.getCommandSenderName());
			} catch (Throwable e) {
				e.printStackTrace();
				sender.addChatMessage(new ChatComponentText(e.getMessage()));
				return;
			}
			if (p==null)
	            throw new WrongUsageException("Error: No player name given and command not send by player?");
			x = (int)p.posX;
			z = (int)p.posZ;
			break;
		case 3:
			try {
				x = Integer.parseInt(args[0]);
				z = Integer.parseInt(args[1]);
				range = Integer.parseInt(args[2]);
			} catch (Throwable e) {
	            throw new WrongUsageException("That's not a number!");
			}
			if (range<1)
	            throw new WrongUsageException("Your radius is negative!");
		}
		ArrayList<BiomeGenBase> alpsList = new ArrayList<BiomeGenBase>(1);
		alpsList.add(biomesoplenty.api.content.BOPCBiomes.alps);
		ChunkPosition alpsPos = w.getWorldChunkManager().findBiomePosition(x, z, range, alpsList, w.rand);
		sender.addChatMessage(new ChatComponentText("Alps x: " + alpsPos.chunkPosX));
		sender.addChatMessage(new ChatComponentText("Alps y: " + alpsPos.chunkPosY));
		sender.addChatMessage(new ChatComponentText("Alps z: " + alpsPos.chunkPosZ));
	}
}
