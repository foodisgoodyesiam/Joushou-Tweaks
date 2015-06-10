package foodisgood.mods.joushou_tweaks;

//import java.util.Iterator;
//import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.world.World;
//import net.minecraft.world.gen.structure.*;

public class StarStructureStart /*extends StructureStart*/ {
  /*public StarStructureStart(World world, Random par2Random, int par3, int par4)
  {
    super(par3, par4);
    
    StructureStar componentStar = new StructureStar(0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2, world);
    this.components.add(componentStar);
    componentStar.buildComponent(componentStar, this.components, par2Random);
    this.updateBoundingBox();
    markAvailableHeight(world, par2Random, 68);
  }
  
  /*public void generateStructureImmidiate(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox, Block[] blocksData, byte[] blockMetas) {
    Iterator iterator = this.components.iterator();
    while (iterator.hasNext())
    {
      ComponentStar structurecomponent = (ComponentStar)iterator.next();
      if ((structurecomponent.func_74874_b().func_78884_a(par3StructureBoundingBox)) && (!structurecomponent.addComponentParts(par1World, par2Random, par3StructureBoundingBox, blocksData, blockMetas))) {
        iterator.remove();
      }
    }
  }*/
  
  public void func_143022_a(NBTTagCompound par1NBTTagCompound) {}
  
  public void func_143017_b(NBTTagCompound par1NBTTagCompound) {}
}
