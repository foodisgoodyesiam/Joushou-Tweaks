package foodisgood.mods.joushou_tweaks.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.*;

public class JTFindStructureCommand extends CommandBase {
	@Override
	public String getCommandName() {
		return "findstruct";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return " [x] [z] [range] [name]?";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length<4)
            throw new WrongUsageException("Wrong number of arguments!");
		int x, z, range;
		try {
			x = Integer.parseInt(args[0]);
			z = Integer.parseInt(args[1]);
			range = Integer.parseInt(args[2]); 
		} catch (Throwable e) {
			throw new WrongUsageException("That's not a number!");
		}
		String name = args[3];
		for (int i=4; i<args.length; i++)
			name+=" " + args[i];
		World w = sender.getEntityWorld();
		ChunkPosition result = w.findClosestStructure(name, x, z, range);
		sender.addChatMessage(new ChatComponentText("x: " + result.chunkPosX));
		sender.addChatMessage(new ChatComponentText("y: " + result.chunkPosY));
		sender.addChatMessage(new ChatComponentText("z: " + result.chunkPosZ));
	}
}
