package foodisgood.mods.joushou_tweaks.command;

import foodisgood.mods.joushou_tweaks.StarGenerator.Direction;
import net.minecraft.block.Block;
import foodisgood.mods.joushou_tweaks.*;
import net.minecraft.command.*;

public class JTTunnelCommand extends CommandBase {
	@Override
	public String getCommandName() {
		return "tunnel";
	}
	
	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/" + this.getCommandName() + " [x] [y] [z] [direction] [length] [blockID] [high/low (H/L)]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		try {
			if (args.length>5) {
				int x, y, z, length, blockID;
				Direction d;
				boolean positive = false;
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				if (y<0 || y>255-3)
		            throw new WrongUsageException("Y value out of bounds");
				z = Integer.parseInt(args[2]);
				if (args[3].length()==1) {
					switch (args[3].charAt(0)) {
					case 's':case 'S':
						positive = true;//TODO: Check if this is right (that positive Z is south)
					case 'n':case 'N':
						d = Direction.Z;
						break;
					default:
			            throw new WrongUsageException("Direction not formatted correctly");
					case 'e':case 'E':
						positive = true;
					case 'w':case 'W':
						d = Direction.X;
					}
				} else if (args[3].length()==2) {
					if (args[3].charAt(0)=='+')
						positive = true;
					else if (args[3].charAt(0)!='-')
			            throw new WrongUsageException("Direction not formatted correctly");
					switch (args[3].charAt(1)) {
					case 'x':case 'X':
						d = Direction.X;
						break;
					default:
			            throw new WrongUsageException("Direction not formatted correctly");
					case 'y':case 'Y':
			            throw new WrongUsageException("You want to make a vertical rail tunnel?");
					case 'z':case 'Z':
						d = Direction.Z;
					}
				} else
		            throw new WrongUsageException("Incorrect formatting for direction");
				length = Integer.parseInt(args[4]);
				blockID = Integer.parseInt(args[5]);
				if (blockID<0)
		            throw new WrongUsageException("Negative block ID?");
				else if (!Block.blockRegistry.containsId(blockID) || Block.getBlockById(blockID)==null)
		            throw new WrongUsageException("That block doesn't exist!");
				if (args[6].length()!=1)
		            throw new WrongUsageException("Incorrect input formatting, must be H or L");
				switch (args[6].charAt(0)) {
				case 'h':case 'H':
					StarGenerator.tunnelHigh(x, y, z, length, d, sender.getEntityWorld(), positive, Block.getBlockById(blockID));
					break;
				default:
		            throw new WrongUsageException("Incorrect input formatting, must be H or L");
				case 'l':case 'L':
					StarGenerator.tunnel(x, y, z, length, d, sender.getEntityWorld(), positive, Block.getBlockById(blockID));
				}
			} else
	            throw new WrongUsageException("Not enough arguments!");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

    /*@Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        GCCorePlayerMP playerBase = null;
        if (astring.length > 0) {
            try {
                playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName());
                if (playerBase != null) {
                    HashMap<String, Integer> map = WorldUtil.getArrayOfPossibleDimensions(WorldUtil.getPossibleDimensionsForSpaceshipTier(Integer.MAX_VALUE), playerBase);
                    String temp = "";
                    int count = 0;
                    for (Entry<String, Integer> entry : map.entrySet()) {
                        temp = temp.concat(entry.getKey() + (count < map.entrySet().size() - 1 ? "." : ""));
                        count++;
                    }
                    playerBase.playerNetServerHandler.sendPacketToPlayer(PacketUtil.createPacket(GalacticraftCore.CHANNEL, EnumPacketClient.UPDATE_DIMENSION_LIST, new Object[] { playerBase.username, temp }));
                    playerBase.setSpaceshipTier(Integer.MAX_VALUE);
                    playerBase.setUsingPlanetGui();
                    playerBase.mountEntity(null);
                    CommandBase.notifyAdmins(icommandsender, "commands.dimensionteleport", new Object[] { String.valueOf(EnumColor.GREY + "[" + playerBase.getEntityName()), "]" });
                }
            } catch (final Exception var6) {
                throw new CommandException(var6.getMessage(), new Object[0]);
            }
        } else
            throw new WrongUsageException("Not enough command arguments! Usage: " + this.getCommandUsage(icommandsender), new Object[0]);
    }*/
}
