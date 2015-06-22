package foodisgood.mods.joushou_tweaks;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.registry.*;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.terraingen.*;

@SuppressWarnings("unused")//TODO: Remove this later when/if I work more on this
public class JTWall extends StructureVillagePieces.Village {
	private StructureVillagePieces.Start start;
	@SuppressWarnings("rawtypes")
	private List pieces;
	private boolean hasMadeWallBlock;

	public JTWall() {}
	
	public JTWall(StructureVillagePieces.Start start, int componentType, Random rand, StructureBoundingBox bounds, int baseMode) {
		super(start, componentType);
		this.coordBaseMode = baseMode;
		this.boundingBox = bounds;
		this.start = start;
	}

	@Override
	public boolean addComponentParts(World arg0, Random arg1,
			StructureBoundingBox arg2) {
		// TODO Auto-generated method stub
		return false;
	}
	
/*	public static JTWall func_74921_a(StructureVillagePieces.Start startPiece, List pieces, Random rand, int p1, int p2, int p3, int p4, int p5) {
		StructureBoundingBox bounds = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 2, 7, 2, p4);
		boolean create = (func_74895_a(bounds)) && (StructureComponent.findIntersecting(pieces, bounds) == null) && (!containsWalls(pieces));
		return create ? new JTWall(startPiece, p5, rand, bounds, p4) : null;
	}
	
	public static boolean containsWalls(List pieces) {
		//TODO
	}
	
	public void func_74861_a(StructureComponent component, List pieces, Random rand) {
		super.func_74861_a(component, pieces, rand);
		this.pieces = pieces;
	}*/
}
