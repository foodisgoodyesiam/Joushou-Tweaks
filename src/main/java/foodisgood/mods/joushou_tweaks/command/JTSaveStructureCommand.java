package foodisgood.mods.joushou_tweaks.command;

import java.io.*;
import java.util.ArrayList;
//import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.command.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class JTSaveStructureCommand extends CommandBase {
	public static boolean op;
	
	@Override
	public String getCommandName() {
		return "savestruct";
	}
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !op || super.canCommandSenderUseCommand(sender);
    }

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " x1 y1 z1 x2 y2 z2 [text/binary, T/B] [mark? y/n)] [file name]";
	}
	
	public static final void saveShort(byte[] out, short num) {
		if (out.length!=2)
			out = new byte[2];
		out[0] = (byte)(num>>8);
		out[1] = (byte)(num&255);
	}
	
	public static final byte[] fromString(String s) {
		byte[] ret = new byte[s.length()];
		for (int i=0; i<s.length(); i++)
			ret[i] = (byte)s.charAt(i);
		return ret;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length<9)
			throw new WrongUsageException("Not enough arguments!");
		String path = args[8];
		boolean binary = true, mark = true;
		int x1, y1, z1, x2, y2, z2;
		for (int i=9; i<args.length; i++)
			path += " " + args[i];
		if (path.startsWith("\""))
			path = path.substring(1);
		if (path.endsWith("\""))
			path = path.substring(0, path.length()-1);
		switch (args[6].charAt(0)) {//Binary or text file?
		default:
			throw new WrongUsageException("Argument 7 must be T or B. (count starting from 1)");
		case 't':case 'T':
			binary = false;
		case 'b':case 'B':
		}
		switch (args[7].charAt(0)) {//Mark bounds of structure in world by replacing corner blocks?
		default:
			throw new WrongUsageException("Argument 8 must be Y or N. (count starting from 1)");
		case 'n':case 'N':
			mark = false;
		case 'y':case 'Y':
		}
		try {
			x1 = Integer.parseInt(args[0]);
			y1 = Integer.parseInt(args[1]);
			z1 = Integer.parseInt(args[2]);
			x2 = Integer.parseInt(args[3]);
			y2 = Integer.parseInt(args[4]);
			z2 = Integer.parseInt(args[5]);
		} catch (NumberFormatException e) {
			throw new WrongUsageException("That's not a number!");
		}
		if (x1>x2) {
			int temp = x2;
			x2 = x1;
			x1 = temp;
		}
		if (y1>y2) {
			int temp = y2;
			y2 = y1;
			y1 = temp;
		}
		if (z1>z2) {
			int temp = z2;
			z2 = z1;
			z1 = temp;
		}
		World w;
		try {
			w = sender.getEntityWorld();
		} catch (Throwable e) {
			sender.addChatMessage(new ChatComponentText("Error getting world object, are you an entity?"));
			return;
		}
		try {
			File f = new File(path);
			if (f.exists())
				throw new WrongUsageException("That file already exists!");
			f.createNewFile();
			if (f.canWrite()) {
				int xS = x2-x1+1, yS = y2-y1+1, zS = z2-z1+1;//Size of array in blocks in each direction
				short[][][] blocks = new short[xS][yS][zS];
				byte meta[][][] = new byte[xS][yS][zS];
				//Map<short, Block> m;
				ArrayList<Block> key = new ArrayList<Block>(10);
				for (int x=x1; x<x2+1; x++)
					for (int y=y1; y<y2+1; y++)
						for (int z=z1; z<z2+1; z++) {
							Block b = w.getBlock(x, y, z);
							if (!key.contains(b))
								key.add(b);
							blocks[x-x1][y-y1][z-z1] = (short) key.indexOf(b);
							meta[x-x1][y-y1][z-z1] = (byte) w.getBlockMetadata(x, y, z);
						}
				if (mark) {
					sender.addChatMessage(new ChatComponentText("Note: markers are outside saved region, not inside"));
					w.setBlock(x1-1, y1-1, z1-1, Blocks.redstone_block);
					w.setBlock(x2+1, y1-1, z1-1, Blocks.redstone_block);
					w.setBlock(x1-1, y1-1, z2+1, Blocks.redstone_block);
					w.setBlock(x2+1, y1-1, z2+1, Blocks.redstone_block);
					w.setBlock(x1-1, y2+1, z1-1, Blocks.redstone_block);
					w.setBlock(x2+1, y2+1, z1-1, Blocks.redstone_block);
					w.setBlock(x1-1, y2+1, z2+1, Blocks.redstone_block);
					w.setBlock(x2+1, y2+1, z2+1, Blocks.redstone_block);
				}
				/* {{1, 2, 3, 4}
				 * {5, 6, 7, 8}
				 * {9, 10, 11, 12}}
				 */
				if (binary) {
					FileOutputStream out = new FileOutputStream(f);
					byte[] temp = {0, 0};
					saveShort(temp, (short)xS);
					out.write(temp);
					saveShort(temp, (short)yS);
					out.write(temp);
					saveShort(temp, (short)zS);
					out.write(temp);
					saveShort(temp, (short)key.size());
					out.write(temp);
					for (int x=0; x<xS; x++)
						for (int y=0; y<yS; y++)
							for (int z=0; z<zS; z++) {
								saveShort(temp, blocks[z][y][z]);
								out.write(temp);
							}
					byte[] metaOut = new byte[xS*yS*zS];
					for (int x=0; x<xS; x++)
						for (int y=0; y<yS; y++)
							for (int z=0; z<zS; z++)
								metaOut[z+y*zS+x*yS*zS] = meta[x][y][z];
					out.write(metaOut);
					for (int i=0; i<key.size(); i++) {
						saveShort(temp, (short)key.get(i).getUnlocalizedName().length());
						out.write(fromString(key.get(i).getUnlocalizedName()));
					}
					out.close();
				} else {
					FileWriter out = new FileWriter(f);
					out.write("X size: " + xS + "\nY size: " + yS + "\nZ size:" + zS);
					out.write("\nNote: Below, order is y, x, z, not x, y, z!\n\nBlocks:\n{");
					for (int y=y1; y<y2+1; y++) {
						if (y>y1)
							out.write(",\n{");
						else
							out.write('{');
						for (int x=x1; x<x2+1; x++) {
							if (x>x1)
								out.write(",\n{");
							else
								out.write('{');
							for (int z=z1; z<z2+1; z++) {
								if (z>z1)
									out.write(", ");
								out.write(Short.toString(blocks[x][y][z]));
							}
							out.write('}');
						}
						out.write('}');
					}
					out.write("}\n\nMeta:\n{");
					for (int y=0; y<yS; y++) {
						if (y>0)
							out.write(",\n{");
						else
							out.write('{');
						for (int x=0; x<xS; x++) {
							if (x>0)
								out.write(",\n{");
							else
								out.write('{');
							for (int z=0; z<zS; z++) {
								if (z>0)
									out.write(", ");
								out.write(Byte.toString(meta[x][y][z]));
							}
							out.write('}');
						}
						out.write('}');
					}
					out.write("}\n\nBlock key:\nFormat: [above list index],\t[id],\t[Unlocalized name],\n[obfuscated class name],\n[localized name]\n");
					for (int i=0; i<key.size(); i++) {
						out.write("\n\n" + Integer.toString(i) + ",\t" + Integer.toString(Block.getIdFromBlock(key.get(i))) + "\t" + key.get(i).getUnlocalizedName());
						out.write("\n" + key.get(i).getClass().getName() + '\n' + key.get(i).getLocalizedName());
					}
					out.close();
				}
			} else {
				sender.addChatMessage(new ChatComponentText("Error opening file."));
			}
		} catch (IOException e) {
			sender.addChatMessage(new ChatComponentText("Error in file I//O"));
			sender.addChatMessage(new ChatComponentText(e.getMessage()));
			e.printStackTrace();
		} catch (Throwable e) {
			sender.addChatMessage(new ChatComponentText("Some kind of non I//O error?"));
			sender.addChatMessage(new ChatComponentText(e.getMessage()));
			e.printStackTrace();
		}
	}
}
