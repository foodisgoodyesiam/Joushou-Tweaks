package foodisgood.mods.joushou_tweaks;

import java.util.*;

import net.minecraft.world.gen.structure.*;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraft.block.Block;
import net.minecraft.init.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SuppressWarnings("rawtypes")
public class JTPath extends StructureVillagePieces.Path {
	static {
		try {
			StructureVillagePieces.Village.class.getField("field_74887_e").setAccessible(true);//getBoundingBox()
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	protected int length;
	public int totalMade;
	
	public static int maxLength, villageSize, maxBuildings;
	public static boolean recheckBiomes = false;

    public JTPath() {}

    public JTPath(StructureVillagePieces.Start start, int int2, Random rand, StructureBoundingBox box, int terrainType) {
        super(start, int2, rand, box, terrainType);
        length = Math.max(box.getXSize(), box.getZSize());
        totalMade = 0;
    }
    
    public static JTPath construct(StructureVillagePieces.Start start, List pieces, Random rand, int p1, int p2, int p3, int p4, int p5) {
    	StructureBoundingBox bounds = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 16, 26, 16, p4);
    	return (StructureComponent.findIntersecting(pieces, bounds) == null) ? new JTPath(start, p5, rand, bounds, p4) : null;
    }

    @Override
    protected void func_143012_a(NBTTagCompound p_143012_1_) {//TODO: Do I need this? I don't think so...
        super.func_143012_a(p_143012_1_);
        p_143012_1_.setInteger("Length", length);
    }

    @Override
    protected void func_143011_b(NBTTagCompound p_143011_1_) {
        super.func_143011_b(p_143011_1_);
        length = p_143011_1_.getInteger("Length");
    }

    @Override
    public void buildComponent(StructureComponent p_74861_1_, List p_74861_2_, Random rand) {
        boolean flag = false;
        int i;
        StructureComponent structurecomponent1;
        for (i = rand.nextInt(5); i < length - 8; i += 1 + rand.nextInt(3)) {
            structurecomponent1 = this.getNextComponentNN((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, 0, i);
            if (structurecomponent1 != null) {
                i += Math.max(structurecomponent1.getBoundingBox().getXSize(), structurecomponent1.getBoundingBox().getZSize());
                flag = true;
                totalMade++;
            }
        }
        for (i = rand.nextInt(5); i < length - 8; i += 1 + rand.nextInt(3)) {
            structurecomponent1 = this.getNextComponentPP((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, 0, i);
            if (structurecomponent1 != null) {
                i += Math.max(structurecomponent1.getBoundingBox().getXSize(), structurecomponent1.getBoundingBox().getZSize());
                flag = true;
                totalMade++;
            }
        }
        if (flag)
        	tryEndRoads(p_74861_1_, p_74861_2_, rand);
    }
    
    protected void tryEndRoads(StructureComponent p_74861_1_, List p_74861_2_, Random rand) {
        if (rand.nextInt(3) != 0 && totalMade<maxBuildings) {//TODO: Replace this with a configurable number
            switch (this.coordBaseMode) {
                case 0:
                    getNextComponentVillagePath((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().minX - 1, this.getBoundingBox().minY, this.getBoundingBox().maxZ - 2, 1, this.getComponentType());
                    break;
                case 1:
                    getNextComponentVillagePath((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().minX, this.getBoundingBox().minY, this.getBoundingBox().minZ - 1, 2, this.getComponentType());
                    break;
                case 2:
                    getNextComponentVillagePath((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().minX - 1, this.getBoundingBox().minY, this.getBoundingBox().minZ, 1, this.getComponentType());
                    break;
                case 3:
                    getNextComponentVillagePath((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().maxX - 2, this.getBoundingBox().minY, this.getBoundingBox().minZ - 1, 2, this.getComponentType());
            }
        }
        if (rand.nextInt(3) != 0 && totalMade<maxBuildings) {//TODO: Don't forget this one too!
            switch (this.coordBaseMode) {
                case 0:
                    getNextComponentVillagePath((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().maxX + 1, this.getBoundingBox().minY, this.getBoundingBox().maxZ - 2, 3, this.getComponentType());
                    break;
                case 1:
                    getNextComponentVillagePath((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().minX, this.getBoundingBox().minY, this.getBoundingBox().maxZ + 1, 0, this.getComponentType());
                    break;
                case 2:
                    getNextComponentVillagePath((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().maxX + 1, this.getBoundingBox().minY, this.getBoundingBox().minZ, 3, this.getComponentType());
                    break;
                case 3:
                    getNextComponentVillagePath((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, rand, this.getBoundingBox().maxX - 2, this.getBoundingBox().minY, this.getBoundingBox().maxZ + 1, 0, this.getComponentType());
            }
        }
    }

    /*    public static StructureBoundingBox getComponentToAddBoundingBox(int i, int j, int k, int l, int i1, int j1, int k1, int l1,
            int i2, int j2)
    { -> (i, j, k, l, i1, j1, k1, l1, i2, j2)
    = (xStart, yStart, zStart, l, i1, j1, k1, l1, i2, direction)
    -> dir 2: -Z. 0: +Z. 1: -X. 3: +X.
        switch (j2)
        {
            default:
                return new StructureBoundingBox(i + l, j + i1, k + j1, ((i + k1) - 1) + l, ((j + l1) - 1) + i1, ((k + i2) - 1) + j1);

            case 2:
                return new StructureBoundingBox(i + l, j + i1, (k - i2) + 1 + j1, ((i + k1) - 1) + l, ((j + l1) - 1) + i1, k + j1);

            case 0:
                return new StructureBoundingBox(i + l, j + i1, k + j1, ((i + k1) - 1) + l, ((j + l1) - 1) + i1, ((k + i2) - 1) + j1);

            case 1:
                return new StructureBoundingBox((i - i2) + 1 + j1, j + i1, k + l, i + j1, ((j + l1) - 1) + i1, ((k + k1) - 1) + l);

            case 3:
                return new StructureBoundingBox(i + j1, j + i1, k + l, ((i + i2) - 1) + j1, ((j + l1) - 1) + i1, ((k + k1) - 1) + l);
        }
    }*/
    
    public static StructureBoundingBox func_74933_a(StructureVillagePieces.Start startPiece, List p_74933_1_, Random p_74933_2_, int xCoord, int p_74933_4_, int zCoord, int terrainType) {
        for (int i1 = 7 * MathHelper.getRandomIntegerInRange(p_74933_2_, 3, maxLength); i1 >= 7; i1 -= 7) {
        	switch (terrainType) {//Actually direction, not terrain type...
        	case 2://-Z
        		if (Math.abs(startPiece.getBoundingBox().minZ-zCoord+i1)>=villageSize)
        			continue;
        		break;
        	case 0://+Z
        		if (Math.abs(startPiece.getBoundingBox().minZ-zCoord-i1)>=villageSize)
        			continue;
        		break;
        	case 1://-X
        		if (Math.abs(startPiece.getBoundingBox().minX-xCoord+i1)>=villageSize)
        			continue;
        		break;
        	default:
        	case 3://+X
        		if (Math.abs(startPiece.getBoundingBox().minX-xCoord-i1)>=villageSize)
        			continue;
        	}
        	//if (Math.abs(i1 - startPiece.getBoundingBox().minX - (terrainType==)) <= villageSize)
            StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(xCoord, p_74933_4_, zCoord, 0, 0, 0, 3, 3, i1, terrainType);
            if (StructureComponent.findIntersecting(p_74933_1_, structureboundingbox) == null)
                return structureboundingbox;
         }
        return null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
     * Mineshafts at the end, it adds Fences...
     */
    public boolean addComponentParts(World p_74875_1_, Random p_74875_2_, StructureBoundingBox p_74875_3_) {
        Block block = this.func_151558_b(Blocks.gravel, 0);
        for (int i = this.getBoundingBox().minX; i <= this.getBoundingBox().maxX; ++i)
            for (int j = this.getBoundingBox().minZ; j <= this.getBoundingBox().maxZ; ++j)
                if (p_74875_3_.isVecInside(i, 64, j)) {
                    int k = p_74875_1_.getTopSolidOrLiquidBlock(i, j) - 1;
                    p_74875_1_.setBlock(i, k, j, block, 0, 2);
                }
        return true;
    }
    /*public static void registerVillagePieces()
    {
        MapGenStructureIO.func_143031_a(StructureVillagePieces.House1.class, "ViBH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Field1.class, "ViDF");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Field2.class, "ViF");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Torch.class, "ViL");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Hall.class, "ViPH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.House4Garden.class, "ViSH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.WoodHut.class, "ViSmH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Church.class, "ViST");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.House2.class, "ViS");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Start.class, "ViStart");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Path.class, "ViSR");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.House3.class, "ViTRH");
        MapGenStructureIO.func_143031_a(StructureVillagePieces.Well.class, "ViW");
    }*/

    /**
     * Calculates total remaining "weight" of components.
     * @param p_75079_0_ List of components
     * @return
     */
    private static int func_75079_a(List p_75079_0_) {//remaining weight
        boolean flag = false;
        int i = 0;//TODO: Experiment with effects of changing this? so that it doesn't count pieces that have hit their limit?
        StructureVillagePieces.PieceWeight pieceweight;
        for (Iterator iterator = p_75079_0_.iterator(); iterator.hasNext(); i += pieceweight.villagePieceWeight) {
            pieceweight = (StructureVillagePieces.PieceWeight)iterator.next();
            if (pieceweight.villagePiecesLimit > 0 && pieceweight.villagePiecesSpawned < pieceweight.villagePiecesLimit)
                flag = true;
        }
        return flag ? i : -1;
    }

    private static StructureVillagePieces.Village func_75083_a(StructureVillagePieces.Start p_75083_0_, StructureVillagePieces.PieceWeight p_75083_1_, List p_75083_2_, Random p_75083_3_, int p_75083_4_, int p_75083_5_, int p_75083_6_, int p_75083_7_, int p_75083_8_) {
        Class oclass = p_75083_1_.villagePieceClass;
        Object object = null;
        if (oclass == StructureVillagePieces.House4Garden.class)
            object = StructureVillagePieces.House4Garden.func_74912_a(p_75083_0_, p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        else if (oclass == StructureVillagePieces.Church.class)
            object = StructureVillagePieces.Church.func_74919_a(p_75083_0_, p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        else if (oclass == StructureVillagePieces.House1.class)
            object = StructureVillagePieces.House1.func_74898_a(p_75083_0_, p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        else if (oclass == StructureVillagePieces.WoodHut.class)
            object = StructureVillagePieces.WoodHut.func_74908_a(p_75083_0_, p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        else if (oclass == StructureVillagePieces.Hall.class)
            object = StructureVillagePieces.Hall.func_74906_a(p_75083_0_, p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        else if (oclass == StructureVillagePieces.Field1.class)
            object = StructureVillagePieces.Field1.func_74900_a(p_75083_0_, p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        else if (oclass == StructureVillagePieces.Field2.class)
            object = StructureVillagePieces.Field2.func_74902_a(p_75083_0_, p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        else if (oclass == StructureVillagePieces.House2.class)
            object = StructureVillagePieces.House2.func_74915_a(p_75083_0_, p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        else if (oclass == StructureVillagePieces.House3.class)
            object = StructureVillagePieces.House3.func_74921_a(p_75083_0_, p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        else
            object = VillagerRegistry.getVillageComponent(p_75083_1_, p_75083_0_ , p_75083_2_, p_75083_3_, p_75083_4_, p_75083_5_, p_75083_6_, p_75083_7_, p_75083_8_);
        return (StructureVillagePieces.Village)object;
    }

	protected StructureVillagePieces.Village getNextVillageComponent(StructureVillagePieces.Start startPiece, List p_75081_1_, Random rand, int p_75081_3_, int p_75081_4_, int p_75081_5_, int p_75081_6_, int p_75081_7_) {
        int totalWeight = func_75079_a(startPiece.structureVillageWeightedPieceList);
        if (totalWeight <= 0)
            return null;
        else {
            int k1 = 0;
            while (k1 < 5) {//Here?
                ++k1;//TODO: What the heck does this do?
                int l1 = rand.nextInt(totalWeight);
                Iterator iterator = startPiece.structureVillageWeightedPieceList.iterator();
                while (iterator.hasNext()) {
                    StructureVillagePieces.PieceWeight pieceweight = (StructureVillagePieces.PieceWeight)iterator.next();
                    l1 -= pieceweight.villagePieceWeight;
                    if (l1 < 0) {
                        if (!pieceweight.canSpawnMoreVillagePiecesOfType(p_75081_7_) || pieceweight == startPiece.structVillagePieceWeight && startPiece.structureVillageWeightedPieceList.size() > 1)
                            break;
                        StructureVillagePieces.Village village = func_75083_a(startPiece, pieceweight, p_75081_1_, rand, p_75081_3_, p_75081_4_, p_75081_5_, p_75081_6_, p_75081_7_);
                        if (village != null) {
                            ++pieceweight.villagePiecesSpawned;
                            startPiece.structVillagePieceWeight = pieceweight;
                            if (!pieceweight.canSpawnMoreVillagePieces()/* || pieceweight.villagePieceClass==Class.forName("com.")*/)
                                startPiece.structureVillageWeightedPieceList.remove(pieceweight);
                            return village;
                        }
                    }
                }
            }
            StructureBoundingBox structureboundingbox = StructureVillagePieces.Torch.func_74904_a(startPiece, p_75081_1_, rand, p_75081_3_, p_75081_4_, p_75081_5_, p_75081_6_);
            if (structureboundingbox != null)
                return new StructureVillagePieces.Torch(startPiece, p_75081_7_, rand, structureboundingbox, p_75081_6_);
            else
                return null;
        }
    }

    @SuppressWarnings("unchecked")
	protected StructureComponent getNextVillageStructureComponent(StructureVillagePieces.Start p_75077_0_, List p_75077_1_, Random p_75077_2_, int p_75077_3_, int p_75077_4_, int p_75077_5_, int p_75077_6_, int p_75077_7_) {
    	totalMade++;
        if (p_75077_7_ > 50)//Apparently this is getStructureType()
            return null;//What's this do?
        else if (Math.abs(p_75077_3_ - p_75077_0_.getBoundingBox().minX) <= villageSize && Math.abs(p_75077_5_ - p_75077_0_.getBoundingBox().minZ) <= villageSize) {
            StructureVillagePieces.Village village = getNextVillageComponent(p_75077_0_, p_75077_1_, p_75077_2_, p_75077_3_, p_75077_4_, p_75077_5_, p_75077_6_, p_75077_7_ + 1);
            if (village != null) {
                int j1 = (village.getBoundingBox().minX + village.getBoundingBox().maxX) / 2;
                int k1 = (village.getBoundingBox().minZ + village.getBoundingBox().maxZ) / 2;
                int l1 = village.getBoundingBox().maxX - village.getBoundingBox().minX;
                int i2 = village.getBoundingBox().maxZ - village.getBoundingBox().minZ;
                int j2 = l1 > i2 ? l1 : i2;
                if (!recheckBiomes || p_75077_0_.getWorldChunkManager().areBiomesViable(j1, k1, j2 / 2 + 4, MapGenVillage.villageSpawnBiomes)) {
                    p_75077_1_.add(village);
                    p_75077_0_.field_74932_i.add(village);
                    return village;
                }
            }
            return null;
        } else
            return null;
    }

    @SuppressWarnings("unchecked")
	protected static StructureComponent getNextComponentVillagePath(StructureVillagePieces.Start p_75080_0_, List p_75080_1_, Random p_75080_2_, int xCoord, int p_75080_4_, int zCoord, int p_75080_6_, int p_75080_7_) {
        if (p_75080_7_ > 3 + p_75080_0_.terrainType)
            return null;
        else if (Math.abs(xCoord - p_75080_0_.getBoundingBox().minX) <= villageSize && Math.abs(zCoord - p_75080_0_.getBoundingBox().minZ) <= villageSize) {
            StructureBoundingBox structureboundingbox = JTPath.func_74933_a(p_75080_0_, p_75080_1_, p_75080_2_, xCoord, p_75080_4_, zCoord, p_75080_6_);
            if (structureboundingbox != null && structureboundingbox.minY > 10) {
                JTPath path = new JTPath(p_75080_0_, p_75080_7_, p_75080_2_, structureboundingbox, p_75080_6_);
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

    @SuppressWarnings("unchecked")
	protected StructureComponent getNextComponentVillagePath2(StructureVillagePieces.Start p_75080_0_, List p_75080_1_, Random p_75080_2_, int xCoord, int p_75080_4_, int zCoord, int p_75080_6_, int p_75080_7_) {
        if (p_75080_7_ > 3 + p_75080_0_.terrainType)
            return null;
        else if (Math.abs(xCoord - p_75080_0_.getBoundingBox().minX) <= villageSize && Math.abs(zCoord - p_75080_0_.getBoundingBox().minZ) <= villageSize) {
            StructureBoundingBox structureboundingbox = JTPath2.func_74933_a(p_75080_0_, p_75080_1_, p_75080_2_, xCoord, p_75080_4_, zCoord, p_75080_6_);
            if (structureboundingbox != null && structureboundingbox.minY > 10) {
                JTPath2 path = new JTPath2(this, 0, p_75080_0_, p_75080_7_, p_75080_2_, structureboundingbox, p_75080_6_);
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

  /*  public static class Start extends StructureVillagePieces.Well
        {
            public WorldChunkManager worldChunkMngr;
            /** Boolean that determines if the village is in a desert or not. * /
            public boolean inDesert;
            /** World terrain type, 0 for normal, 1 for flap map * /
            public int terrainType;
            public StructureVillagePieces.PieceWeight structVillagePieceWeight;
            /**
             * Contains List of all spawnable Structure Piece Weights. If no more Pieces of a type can be spawned, they
             * are removed from this list
             * /
            public List structureVillageWeightedPieceList;
            public List field_74932_i = new ArrayList();
            public List field_74930_j = new ArrayList();
            private static final String __OBFID = "CL_00000527";
            public BiomeGenBase biome;

            public Start() {}

            public Start(WorldChunkManager p_i2104_1_, int p_i2104_2_, Random p_i2104_3_, int p_i2104_4_, int p_i2104_5_, List p_i2104_6_, int p_i2104_7_)
            {
                super((StructureVillagePieces.Start)null, 0, p_i2104_3_, p_i2104_4_, p_i2104_5_);
                this.worldChunkMngr = p_i2104_1_;
                this.structureVillageWeightedPieceList = p_i2104_6_;
                this.terrainType = p_i2104_7_;
                BiomeGenBase biomegenbase = p_i2104_1_.getBiomeGenAt(p_i2104_4_, p_i2104_5_);
                this.inDesert = biomegenbase == BiomeGenBase.desert || biomegenbase == BiomeGenBase.desertHills;
                this.biome = biomegenbase;
            }

            public WorldChunkManager getWorldChunkManager()
            {
                return this.worldChunkMngr;
            }
        }*/


    /**
     * Gets the next village component, with the bounding box shifted -1 in the X and Z direction.
     */
    protected StructureComponent getNextComponentNN(StructureVillagePieces.Start p_74891_1_, List p_74891_2_, Random p_74891_3_, int p_74891_4_, int p_74891_5_) {
    	if (p_74891_3_.nextInt()%5==2)
    		switch (this.coordBaseMode) {
            case 0:
                return getNextComponentVillagePath(p_74891_1_, p_74891_2_, p_74891_3_, this.getBoundingBox().minX - 1, this.getBoundingBox().minY + p_74891_4_, this.getBoundingBox().minZ + p_74891_5_, 1, this.getComponentType());
            case 1:
                return getNextComponentVillagePath(p_74891_1_, p_74891_2_, p_74891_3_, this.getBoundingBox().minX + p_74891_5_, this.getBoundingBox().minY + p_74891_4_, this.getBoundingBox().minZ - 1, 2, this.getComponentType());
            case 2:
                return getNextComponentVillagePath(p_74891_1_, p_74891_2_, p_74891_3_, this.getBoundingBox().minX - 1, this.getBoundingBox().minY + p_74891_4_, this.getBoundingBox().minZ + p_74891_5_, 1, this.getComponentType());
            case 3:
                return getNextComponentVillagePath(p_74891_1_, p_74891_2_, p_74891_3_, this.getBoundingBox().minX + p_74891_5_, this.getBoundingBox().minY + p_74891_4_, this.getBoundingBox().minZ - 1, 2, this.getComponentType());
            default:
                return null;
    		}
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
    protected StructureComponent getNextComponentPP(StructureVillagePieces.Start p_74894_1_, List p_74894_2_, Random p_74894_3_, int p_74894_4_, int p_74894_5_) {
        if (p_74894_3_.nextInt()%5==3)
        	switch (coordBaseMode) {
            case 0:
                return getNextComponentVillagePath(p_74894_1_, p_74894_2_, p_74894_3_, this.getBoundingBox().maxX + 1, this.getBoundingBox().minY + p_74894_4_, this.getBoundingBox().minZ + p_74894_5_, 3, this.getComponentType());
            case 1:
                return getNextComponentVillagePath(p_74894_1_, p_74894_2_, p_74894_3_, this.getBoundingBox().minX + p_74894_5_, this.getBoundingBox().minY + p_74894_4_, this.getBoundingBox().maxZ + 1, 0, this.getComponentType());
            case 2:
                return getNextComponentVillagePath(p_74894_1_, p_74894_2_, p_74894_3_, this.getBoundingBox().maxX + 1, this.getBoundingBox().minY + p_74894_4_, this.getBoundingBox().minZ + p_74894_5_, 3, this.getComponentType());
            case 3:
                return getNextComponentVillagePath(p_74894_1_, p_74894_2_, p_74894_3_, this.getBoundingBox().minX + p_74894_5_, this.getBoundingBox().minY + p_74894_4_, this.getBoundingBox().maxZ + 1, 0, this.getComponentType());
            default:
                return null;
        	}
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

    /**
     * Discover the y coordinate that will serve as the ground level of the supplied BoundingBox. (A median of
     * all the levels in the BB's horizontal rectangle).
     */
    protected int getAverageGroundLevel(World p_74889_1_, StructureBoundingBox p_74889_2_) {
        int i = 0, j = 0;
        for (int k = this.getBoundingBox().minZ; k <= this.getBoundingBox().maxZ; ++k)
            for (int l = this.getBoundingBox().minX; l <= this.getBoundingBox().maxX; ++l)
                if (p_74889_2_.isVecInside(l, 64, k)) {
                    i += Math.max(p_74889_1_.getTopSolidOrLiquidBlock(l, k), p_74889_1_.provider.getAverageGroundLevel());
                    ++j;
                }
        return j==0 ? -1 : i/j;
    }
    
    public static class JTPathHandler implements IVillageCreationHandler {
    	public int min, max, weight;
    	
    	public JTPathHandler(int weight, int minimum, int maximum) {
    		this.weight = weight;
    		min = minimum;
    		max = maximum;
    	}
    	
		@Override
		public Object buildComponent(PieceWeight arg0, Start arg1, List arg2,
				Random arg3, int arg4, int arg5, int arg6, int arg7, int arg8) {
			return JTPath.construct(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		}

		@Override
		public Class<?> getComponentClass() {
			return JTPath.class;
		}

		@Override
		public PieceWeight getVillagePieceWeight(Random arg0, int arg1) {
			return new StructureVillagePieces.PieceWeight(JTPath.class, weight, max);
		}
    }
}
