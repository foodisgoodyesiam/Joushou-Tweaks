package foodisgood.mods.joushou_tweaks;

import java.util.Random;

import biomesoplenty.api.content.BOPCBlocks;
import jp.mc.ancientred.starminer.basics.SMModContainer;
import jp.mc.ancientred.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.*;

public class StarGeneratorNether implements IWorldGenerator {
	public static int probabilityOfStar, probabilityOfPillar;
	
	//public static int INNER_CORE_ID = 1736, OUTER_CORE_ID = 1737, GRAVITY_CORE_ID = 1735;
	
	public static Block[] blocksNormal, blocksExotic, blocksRare;
	
	public static void initBlocks() {
		Block[] normal = {Blocks.netherrack, Blocks.soul_sand, Blocks.glass, Blocks.hardened_clay, Blocks.mossy_cobblestone, BOPCBlocks.hive};
		Block[] exotic = {Blocks.brick_block, Blocks.end_stone, Blocks.clay, Blocks.glowstone, Blocks.ice, Blocks.nether_brick, Blocks.obsidian, Blocks.quartz_block};
		Block[] rare = {Blocks.anvil, Blocks.hay_block, Blocks.melon_block, Blocks.packed_ice, Blocks.monster_egg, Blocks.pumpkin, Blocks.tnt, Blocks.air};
		blocksNormal = normal;
		blocksExotic = exotic;
		blocksRare = rare;
	}
	
	@Override
	public void generate(Random gen, int chunkX, int chunkZ, World w, IChunkProvider provider1, IChunkProvider pprovider2) {
		if (w.provider.dimensionId!=-1)
			return;
		int rand = Math.abs(gen.nextInt()), starX, starY, starZ, radius, temp, height, gravRad, innerRad;
		if (probabilityOfStar!=0)
			switch (Math.abs(gen.nextInt())%probabilityOfStar) {
			default:				//Generate nothing
				return;
			case 1:case 2:case 3:	//Generate star with no sphere
				starX = chunkX*16+(rand%16);
				starZ = chunkZ*16+((rand>>4)%16);
				starY = (rand>>8)%256;
				if (!w.setBlock(starX, starY, starZ, SMModContainer.GravityCoreBlock))
					do {
						rand = gen.nextInt();
						starX = chunkX*16+(rand%16);
						starZ = chunkZ*16+((rand>>4)%16);
						starY = (rand>>8)%255+1;
					} while (!w.setBlock(starX, starY, starZ, SMModContainer.GravityCoreBlock));
			      w.setBlock(starX, starY, starZ, SMModContainer.GravityCoreBlock);
			      TileEntityGravityGenerator tileEntityGravity = (TileEntityGravityGenerator)w.getTileEntity(starX, starY, starZ);
			      tileEntityGravity.starRad = 3;
			      if(rand%23==2)
			    	  tileEntityGravity.gravityRange = rand%80+2;
			      else
			    	  tileEntityGravity.gravityRange = rand%20+5;
			      switch (rand%11) {
			      case 0:case 1:case 2: case 3:case 4:case 5:case 6:
			    	  tileEntityGravity.type = TileEntityGravityGenerator.GTYPE_SPHERE;
			    	  break;
			      case 8:
			    	  tileEntityGravity.type = TileEntityGravityGenerator.GTYPE_SQUARE;
			    	  break;
			      case 9:
			    	  tileEntityGravity.type = TileEntityGravityGenerator.GTYPE_XCYLINDER;
			    	  break;
			      case 10:
			    	  tileEntityGravity.type = TileEntityGravityGenerator.GTYPE_YCYLINDER;
			    	  break;
			      case 7:
			    	  tileEntityGravity.type = TileEntityGravityGenerator.GTYPE_ZCYLINDER;
			      }
			      tileEntityGravity.resetWorkState();
				//w.getBlock()
				break;
			case 0: {					//Generate star with sphere
				Block outer, inner;
				starX = chunkX*16+(rand%16);
				starZ = chunkZ*16+((rand>>4)%16);
				boolean round = rand%29<23;
				if (rand%41<33)
					outer = blocksNormal[(rand>>10)%blocksNormal.length];
				else
					outer = blocksExotic[(rand>>10)%blocksExotic.length];
				if (rand%7<5) {	//Solid star
					if (rand%53<30) //Filling same as shell
						inner = outer;
					else { //Filling different from shell
						temp = rand%43;
						if (temp<21)
							inner = blocksNormal[(rand>>13)%blocksNormal.length];
						else if (temp<35)
							inner = blocksExotic[(rand>>13)%blocksExotic.length];
						else
							inner = blocksRare[(rand>>13)%blocksRare.length];
					}
				} else			//Filled with liquid
					inner = (rand%3==1 ? Blocks.lava : BOPCBlocks.blood);
				if (rand%7>4 && rand%3==1) {
					if ((rand>>19)%2==0)
						outer = BOPCBlocks.flesh;
					else
						outer = blocksNormal[(rand>>16)%blocksNormal.length];
				}
				
				temp = rand%59;//Height
				if (temp<19)
					height = 180+gen.nextInt()%(230-180);
				else if (temp<29)
					height = 60+gen.nextInt()%30;
				else if (temp==35)
					height = 127;
				else
					height = 25+gen.nextInt()%(180-25);
				height = Math.abs(height);
				
				temp = rand%61;//Radius
				if (temp<56)
					radius = 10+gen.nextInt()%20;
				else
					radius = 20+gen.nextInt()%60;
				radius = Math.abs(radius);
				while (radius+height>253 || height-radius<2)
					radius--;
	
				temp = Math.abs(gen.nextInt());//Inner radius
				if (rand%7>4 && temp%2==0) {//If liquid-filled, then 50% chance will be completely filed
					innerRad = 6;
				} else {
					temp>>=1;
					if (temp%23<13)
						innerRad = radius-3;
					else if (temp%23<17)
						innerRad = Math.max(7, radius-3-((temp>>5)%4));
					else if (temp%23==17)
						innerRad = radius-1;
					else if (temp%23<20)
						innerRad = Math.max(8, radius-3-((temp>>5)%(radius-6)));
					else
						innerRad = Math.max(7, radius-3-((temp>>5)%30));
				}
				
				temp = rand%67;//Gravity radius
				if (temp<16 && radius<30)
					gravRad = (int)(radius*4F);
				else 
					gravRad = (int)(radius*1.9F);
					
				starY = height;
				if (round) {//Sphere
					for (int x=starX-radius; x<starX+radius+1; x++)
						for (int y=starY-radius; y<starY+radius+1; y++)
							for (int z=starZ-radius; z<starZ+radius+1; z++) {
								int d = StarGenerator.distSp(x, y, z, starX, starY, starZ);
								if (d>radius)
									continue;
								else if (d==radius)
									w.setBlock(x, y, z, outer);
								else if (d<innerRad) {
									if (d<4)
										w.setBlock(x, y, z, SMModContainer.InnerCoreBlock);
									else if (d==4)
										w.setBlock(x, y, z, SMModContainer.OuterCoreBlock);
								} else
									w.setBlock(x, y, z, inner);
							}
				} else {
					if ((rand>>18)%4==0) { //Diamond
						for (int x=starX-radius; x<starX+radius+1; x++)
							for (int y=starY-radius; y<starY+radius+1; y++)
								for (int z=starZ-radius; z<starZ+radius+1; z++) {
									int d = StarGenerator.distD(x, y, z, starX, starY, starZ);
									if (d>radius)
										continue;
									else if (d==radius)
										w.setBlock(x, y, z, outer);
									else if (d<innerRad) {
										if (d<4)
											w.setBlock(x, y, z, SMModContainer.InnerCoreBlock);
										else if (d==5)
											w.setBlock(x, y, z, SMModContainer.OuterCoreBlock);
									} else
										w.setBlock(x, y, z, inner);
								}
					} else {               //Cube
						/*for (int x=starX-radius; x<starX+radius+1; x++)
							for (int z=starZ-radius; z<starZ+radius+1; z++) {
								w.setBlock(x, starY-radius, z, outer);
								w.setBlock(x, starY+radius+1, z, outer);
							}
						for (int x=starX-radius; x<starX+radius+1; x++)
							for (int z=0; z<starZ+radius+1; z++)*/
						for (int x=starX-radius; x<starX+radius+1; x++)
							for (int y=starY-radius; y<starY+radius+1; y++)
								for (int z=starZ-radius; z<starZ+radius+1; z++) {
									int d = StarGenerator.distSq(x, y, z, starX, starY, starZ);
									if (d>radius)
										continue;
									else if (d==radius)
										w.setBlock(x, y, z, outer);
									else if (d<innerRad) {
										if (d<4)
											w.setBlock(x, y, z, SMModContainer.InnerCoreBlock);
										else if (d==5)
											w.setBlock(x, y, z, SMModContainer.OuterCoreBlock);
									} else
										w.setBlock(x, y, z, inner);
								}
					}
				}
				
				if (rand%7==2) {//Rings
					int radiusInner = radius+Math.abs(gen.nextInt())%20, radiusOuter = radiusInner+Math.abs(gen.nextInt())%50;
					switch (Math.abs(gen.nextInt())%5) {
					case 0://xy plane
						switch(Math.abs(gen.nextInt())%3) {
						case 0:
							for (int x=starX-radius; x<starX+radius+1; x++)
								for (int y=Math.max(starY-radius, 0); y<Math.min(starY+radius+1, 255); y++) {
									int d = StarGenerator.distSp(x, y, starZ, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(x, y, starZ, outer);
								}
							break;
						case 1:
							for (int x=starX-radius; x<starX+radius+1; x++)
								for (int y=Math.max(starY-radius, 0); y<Math.min(starY+radius+1, 255); y++) {
									int d = StarGenerator.distD(x, y, starZ, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(x, y, starZ, outer);
								}
							break;
						case 2:
							for (int x=starX-radius; x<starX+radius+1; x++)
								for (int y=Math.max(starY-radius, 0); y<Math.min(starY+radius+1, 255); y++) {
									int d = StarGenerator.distSq(x, y, starZ, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(x, y, starZ, outer);
								}
						}
						break;
					case 1:case 3://xz plane
						switch(Math.abs(gen.nextInt())%3) {
						case 0:
							for (int x=starX-radius; x<starX+radius+1; x++)
								for (int z=Math.max(starZ-radius, 0); z<Math.min(starZ+radius+1, 255); z++) {
									int d = StarGenerator.distSp(x, starY, z, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(x, starY, z, outer);
								}
							break;
						case 1:
							for (int x=starX-radius; x<starX+radius+1; x++)
								for (int z=Math.max(starZ-radius, 0); z<Math.min(starZ+radius+1, 255); z++) {
									int d = StarGenerator.distSq(x, starY, z, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(x, starY, z, outer);
								}
							break;
						case 2:
							for (int x=starX-radius; x<starX+radius+1; x++)
								for (int z=Math.max(starZ-radius, 0); z<Math.min(starZ+radius+1, 255); z++) {
									int d = StarGenerator.distD(x, starY, z, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(x, starY, z, outer);
								}
						}
						break;
					case 2://yz plane
						switch(Math.abs(gen.nextInt())%3) {
						case 0:
							for (int y=starY-radius; y<starY+radius+1; y++)
								for (int z=Math.max(starZ-radius, 0); z<Math.min(starZ+radius+1, 255); z++) {
									int d = StarGenerator.distSp(starX, y, z, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(starX, y, z, outer);
								}
							break;
						case 1:
							for (int y=starY-radius; y<starY+radius+1; y++)
								for (int z=Math.max(starZ-radius, 0); z<Math.min(starZ+radius+1, 255); z++) {
									int d = StarGenerator.distD(starX, y, z, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(starX, y, z, outer);
								}
							break;
						case 2:
							for (int y=starY-radius; y<starY+radius+1; y++)
								for (int z=Math.max(starZ-radius, 0); z<Math.min(starZ+radius+1, 255); z++) {
									int d = StarGenerator.distSq(starX, y, z, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(starX, y, z, outer);
								}
						}
						break;
					case 4:
						switch(Math.abs(gen.nextInt())%3) {
						case 0:
							for (int x=starX-radius; x<starX+radius+1; x++)
								for (int y=Math.max(starY-radius, 0); y<Math.min(starY+radius+1, 255); y++) {
									int d = StarGenerator.distD(x, y, starZ+y-starY, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(x, y, starZ+y-starY, outer);
								}
							break;
						case 1:
							for (int x=starX-radius; x<starX+radius+1; x++)
								for (int y=Math.max(starY-radius, 0); y<Math.min(starY+radius+1, 255); y++) {
									int d = StarGenerator.distSq(x, y, starZ+y-starY, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(x, y, starZ+y-starY, outer);
								}
							break;
						case 2:
							for (int x=starX-radius; x<starX+radius+1; x++)
								for (int y=Math.max(starY-radius, 0); y<Math.min(starY+radius+1, 255); y++) {
									int d = StarGenerator.distSp(x, y, starZ+y-starY, starX, starY, starZ);
									if (d<radiusOuter && d>radiusInner)
										w.setBlock(x, y, starZ+y-starY, outer);
								}
						}
					}
				}
			    w.setBlock(starX, starY, starZ, SMModContainer.GravityCoreBlock);
			    TileEntityGravityGenerator tileEntityGravity2 = (TileEntityGravityGenerator)w.getTileEntity(starX, starY, starZ);
			    tileEntityGravity2.starRad = radius;
			    tileEntityGravity2.gravityRange = gravRad;
			    if (!round)
			    	tileEntityGravity2.type = TileEntityGravityGenerator.GTYPE_SQUARE;
			    tileEntityGravity2.resetWorkState();
				} break;
			}
		
				//Pillars
		if (probabilityOfPillar>0 && Math.abs(gen.nextInt())%probabilityOfPillar==0) {
			Block outer, inner;
			temp = rand%23;
			if (temp<16)
				outer = blocksNormal[Math.abs(gen.nextInt())%blocksNormal.length];
			else
				outer = blocksExotic[Math.abs(gen.nextInt())%blocksExotic.length];
			temp = rand%47;
			if (temp<28)
				inner = outer;
			else if (temp<33)
				inner = Blocks.air;
			else if (temp<38)
				inner = blocksNormal[Math.abs(gen.nextInt())%blocksNormal.length];
			else if (temp<42)
				inner = blocksExotic[Math.abs(gen.nextInt())%blocksExotic.length];
			else
				inner = blocksRare[Math.abs(gen.nextInt())%blocksRare.length];
			for (int y=0; y<10; y++)
				for (int x=chunkX*16; x<chunkX*16+15; x++)
					for (int z=chunkZ*16; z<chunkZ*16+15; z++)
						w.setBlock(x, y, z, Blocks.bedrock);
			for (int y=10; y<245; y++) {
				for (int x=chunkX*16; x<chunkX*16+15; x++) {
					w.setBlock(x, y, chunkZ*16, outer);
					w.setBlock(x, y, chunkZ*16+14, outer);
				}
				for (int z=chunkZ*16+1; z<chunkZ*16+14; z++) {
					w.setBlock(chunkX*16, y, z, outer);
					w.setBlock(chunkX*16+14, y, z, outer);
					for (int x=chunkX*16+1; x<chunkX*16+14; x++)
						w.setBlock(x, y, z, inner);
				}
			}
			for (int x=chunkX*16; x<chunkX*16+15; x++)
				for (int z=chunkZ*16; z<chunkZ*16+15; z++)
					w.setBlock(x, 245, z, Blocks.bedrock);
			temp = (rand>>10)%4;
			radius = 0;
			switch (temp) {
			case 3:
				radius+=(rand>>7)%32;
			case 2:
				radius+=(rand>>3)%16;
			case 0:case 1:
				radius+=16;
			}
			final int TYPES[] = {TileEntityGravityGenerator.GTYPE_SQUARE,
				TileEntityGravityGenerator.GTYPE_YCYLINDER,
				TileEntityGravityGenerator.GTYPE_SQUARE, 
				TileEntityGravityGenerator.GTYPE_SPHERE,
				TileEntityGravityGenerator.GTYPE_SQUARE,
				TileEntityGravityGenerator.GTYPE_YCYLINDER,
				TileEntityGravityGenerator.GTYPE_SQUARE,
				TileEntityGravityGenerator.GTYPE_YCYLINDER,
				TileEntityGravityGenerator.GTYPE_SQUARE,
				TileEntityGravityGenerator.GTYPE_XCYLINDER,
				TileEntityGravityGenerator.GTYPE_SQUARE,
				TileEntityGravityGenerator.GTYPE_YCYLINDER,
				TileEntityGravityGenerator.GTYPE_SQUARE,
				TileEntityGravityGenerator.GTYPE_ZCYLINDER,
				TileEntityGravityGenerator.GTYPE_SQUARE,
				TileEntityGravityGenerator.GTYPE_YCYLINDER,
				TileEntityGravityGenerator.GTYPE_SQUARE};
			temp = TYPES[Math.abs(gen.nextInt())%TYPES.length]; //Type of gravity
			for (int y=5; y<244; y+=13) {
				w.setBlock(chunkX*16+7, y, chunkZ*16+7, SMModContainer.GravityCoreBlock);
				TileEntityGravityGenerator tileEntityGravity2 = (TileEntityGravityGenerator)w.getTileEntity(chunkX*16+7, y, chunkZ*16+7);
				tileEntityGravity2.starRad = 7;
				tileEntityGravity2.gravityRange = radius;
				tileEntityGravity2.type = temp;
				tileEntityGravity2.resetWorkState();
			}
		}
		if (rand%6000==0) {
			starX = chunkX*16+(rand%16);
			starZ = chunkZ*16+((rand>>4)%16);
			for (int y=1; y<250; y++)
				w.setBlockToAir(starX, y, starZ);
			w.setBlock(starX, 0, starZ, Blocks.bedrock);
		}
	}
	
	/**
	 * Returns the "distance" from (x, y, z) to (cx, cy, cz) for use in generating a cube
	 */
	public static final int distSq(int x, int y, int z, int cx, int cy, int cz) {
		return Math.max(Math.abs(x-cx), Math.max(Math.abs(y-cy), Math.abs(z-cz)));
	}
	
	/**
	 * Returns the additive distance from (x, y, z) to (cx, cy, cz), for use in generating an octahedron
	 */
	public static final int distD(int x, int y, int z, int cx, int cy, int cz) {
		return Math.abs(x-cx)+Math.abs(y-cy)+Math.abs(z-cz);
	}
	
	/**
	 * Returns the spherical distance from (x, y, z) to (cx, cy, cz)
	 */
	public static int distSp(int x, int y, int z, int cx, int cy, int cz) {
		return (int) MathHelper.sqrt_float((x-cx)*(x-cx) + (y-cy)*(y-cy) + (z-cz)*(z-cz));
	}
}
