package foodisgood.mods.joushou_tweaks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

@SuppressWarnings("rawtypes")
public class JTPath2 extends JTPath {
	JTPath parent;
	int tier;
	
    public static JTPath2 construct(JTPath parentPath, int tier, StructureVillagePieces.Start start, List pieces, Random rand, int p1, int p2, int p3, int p4, int p5) {
    	StructureBoundingBox bounds = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 16, 26, 16, p4);
    	return (StructureComponent.findIntersecting(pieces, bounds) == null) ? new JTPath2(parentPath, tier, start, p5, rand, bounds, p4) : null;
    }
	
	public JTPath2(JTPath parentPath, int tier, StructureVillagePieces.Start start, int int2, Random rand, StructureBoundingBox box, int terrainType) {
		super(start, int2, rand, box, terrainType);
		parent = parentPath;
		this.tier = tier;
	}
	
    public static StructureBoundingBox func_74933_a(StructureVillagePieces.Start p_74933_0_, List p_74933_1_, Random p_74933_2_, int p_74933_3_, int p_74933_4_, int p_74933_5_, int p_74933_6_) {
        for (int i1 = 7 * MathHelper.getRandomIntegerInRange(p_74933_2_, 3, 7); i1 >= 7; i1 -= 7) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_74933_3_, p_74933_4_, p_74933_5_, 0, 0, 0, 3, 3, i1, p_74933_6_);
            if (StructureComponent.findIntersecting(p_74933_1_, structureboundingbox) == null)
                return structureboundingbox;
         }
        return null;
    }
    
	@Override
    protected void tryEndRoads(StructureComponent p_74861_1_, List p_74861_2_, Random rand) {
		if (tier>5)
			return;
        if (rand.nextInt(6) != 0 && parent.totalMade<maxBuildings) {//TODO: Replace this with a configurable number
            switch (this.coordBaseMode) {
                case 0:
                    getNextComponentVillagePath2((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().minX - 1, this.getBoundingBox().minY, this.getBoundingBox().maxZ - 2, 1, this.getComponentType());
                    break;
                case 1:
                    getNextComponentVillagePath2((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().minX, this.getBoundingBox().minY, this.getBoundingBox().minZ - 1, 2, this.getComponentType());
                    break;
                case 2:
                    getNextComponentVillagePath2((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().minX - 1, this.getBoundingBox().minY, this.getBoundingBox().minZ, 1, this.getComponentType());
                    break;
                case 3:
                    getNextComponentVillagePath2((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().maxX - 2, this.getBoundingBox().minY, this.getBoundingBox().minZ - 1, 2, this.getComponentType());
            }
        }
        if (rand.nextInt(6) != 0 && parent.totalMade<maxBuildings) {//TODO: Don't forget this one too!
            switch (this.coordBaseMode) {
                case 0:
                    getNextComponentVillagePath2((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().maxX + 1, this.getBoundingBox().minY, this.getBoundingBox().maxZ - 2, 3, this.getComponentType());
                    break;
                case 1:
                    getNextComponentVillagePath2((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().minX, this.getBoundingBox().minY, this.getBoundingBox().maxZ + 1, 0, this.getComponentType());
                    break;
                case 2:
                    getNextComponentVillagePath2((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().maxX + 1, this.getBoundingBox().minY, this.getBoundingBox().minZ, 3, this.getComponentType());
                    break;
                case 3:
                    getNextComponentVillagePath2((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().maxX - 2, this.getBoundingBox().minY, this.getBoundingBox().maxZ + 1, 0, this.getComponentType());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
	protected StructureComponent getNextComponentVillagePath2(StructureVillagePieces.Start p_75080_0_, List p_75080_1_, Random p_75080_2_, int p_75080_3_, int p_75080_4_, int p_75080_5_, int p_75080_6_, int p_75080_7_) {
    	if (p_75080_7_ > 3 + p_75080_0_.terrainType)
            return null;
        else if (Math.abs(p_75080_3_ - p_75080_0_.getBoundingBox().minX) <= villageSize && Math.abs(p_75080_5_ - p_75080_0_.getBoundingBox().minZ) <= villageSize) {
            StructureBoundingBox structureboundingbox = JTPath2.func_74933_a(p_75080_0_, p_75080_1_, p_75080_2_, p_75080_3_, p_75080_4_, p_75080_5_, p_75080_6_);
            if (structureboundingbox != null && structureboundingbox.minY > 10) {
                JTPath2 path = new JTPath2(parent, tier+1, p_75080_0_, p_75080_7_, p_75080_2_, structureboundingbox, p_75080_6_);
                int j1 = (path.getBoundingBox().minX + path.getBoundingBox().maxX) / 2;
                int k1 = (path.getBoundingBox().minZ + path.getBoundingBox().maxZ) / 2;
                int l1 = path.getBoundingBox().maxX - path.getBoundingBox().minX;
                int i2 = path.getBoundingBox().maxZ - path.getBoundingBox().minZ;
                int j2 = l1 > i2 ? l1 : i2;
                if (!recheckBiomes || p_75080_0_.getWorldChunkManager().areBiomesViable(j1, k1, j2 / 2 + 4, MapGenVillage.villageSpawnBiomes)) {
                    p_75080_1_.add(path);
                    p_75080_0_.field_74930_j.add(path);
                    return path;
                }
            }
            return null;
        } else
            return null;
    }

    /**
     * Gets the next village component, with the bounding box shifted -1 in the X and Z direction.
     */
	@Override
    protected StructureComponent getNextComponentNN(StructureVillagePieces.Start p_74891_1_, List p_74891_2_, Random p_74891_3_, int p_74891_4_, int p_74891_5_) {
        parent.totalMade++;
		switch (this.coordBaseMode) {
            case 0:
                return getNextVillageStructureComponent(p_74891_1_, p_74891_2_, p_74891_3_, this.getBoundingBox().minX - 1, this.getBoundingBox().minY + p_74891_4_, this.getBoundingBox().minZ + p_74891_5_, 1, this.getComponentType());
            case 1:
                return getNextVillageStructureComponent(p_74891_1_, p_74891_2_, p_74891_3_, this.getBoundingBox().minX + p_74891_5_, this.getBoundingBox().minY + p_74891_4_, this.getBoundingBox().minZ - 1, 2, this.getComponentType());
            case 2:
                return getNextVillageStructureComponent(p_74891_1_, p_74891_2_, p_74891_3_, this.getBoundingBox().minX - 1, this.getBoundingBox().minY + p_74891_4_, this.getBoundingBox().minZ + p_74891_5_, 1, this.getComponentType());
            case 3:
                return getNextVillageStructureComponent(p_74891_1_, p_74891_2_, p_74891_3_, this.getBoundingBox().minX + p_74891_5_, this.getBoundingBox().minY + p_74891_4_, this.getBoundingBox().minZ - 1, 2, this.getComponentType());
            default:
                return null;
        }
    }

    /**
     * Gets the next village component, with the bounding box shifted +1 in the X and Z direction.
     */
    @Override
    protected StructureComponent getNextComponentPP(StructureVillagePieces.Start p_74894_1_, List p_74894_2_, Random p_74894_3_, int p_74894_4_, int p_74894_5_) {
    	parent.totalMade++;
    	switch (coordBaseMode) {
            case 0:
                return getNextVillageStructureComponent(p_74894_1_, p_74894_2_, p_74894_3_, this.getBoundingBox().maxX + 1, this.getBoundingBox().minY + p_74894_4_, this.getBoundingBox().minZ + p_74894_5_, 3, this.getComponentType());
            case 1:
                return getNextVillageStructureComponent(p_74894_1_, p_74894_2_, p_74894_3_, this.getBoundingBox().minX + p_74894_5_, this.getBoundingBox().minY + p_74894_4_, this.getBoundingBox().maxZ + 1, 0, this.getComponentType());
            case 2:
                return getNextVillageStructureComponent(p_74894_1_, p_74894_2_, p_74894_3_, this.getBoundingBox().maxX + 1, this.getBoundingBox().minY + p_74894_4_, this.getBoundingBox().minZ + p_74894_5_, 3, this.getComponentType());
            case 3:
                return getNextVillageStructureComponent(p_74894_1_, p_74894_2_, p_74894_3_, this.getBoundingBox().minX + p_74894_5_, this.getBoundingBox().minY + p_74894_4_, this.getBoundingBox().maxZ + 1, 0, this.getComponentType());
            default:
                return null;
        }
    }
    
    public static class JTPath2Handler implements IVillageCreationHandler {//TODO: Do I need this?\
    	public JTPath2Handler() {}
    	
		@Override
		public Object buildComponent(PieceWeight arg0, Start arg1, List arg2,
				Random arg3, int arg4, int arg5, int arg6, int arg7, int arg8) {
			return JTPath2.construct(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);//TODO: Can't add parent here, I don't think I should have villages generating this on their own...
		}

		@Override
		public Class<?> getComponentClass() {
			return JTPath2.class;
		}

		@Override
		public PieceWeight getVillagePieceWeight(Random arg0, int arg1) {
			return new StructureVillagePieces.PieceWeight(JTPath2.class, 10, 0);
		}
    }
}
