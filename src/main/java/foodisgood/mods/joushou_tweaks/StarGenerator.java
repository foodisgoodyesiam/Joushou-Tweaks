package foodisgood.mods.joushou_tweaks;

import java.util.ArrayList;
import java.util.Random;

import org.apache.logging.log4j.Level;

import biomesoplenty.api.content.BOPCBlocks;
import jp.mc.ancientred.starminer.basics.SMModContainer;
import jp.mc.ancientred.starminer.basics.tileentity.TileEntityGravityGenerator;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.*;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.entity.player.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;

public class StarGenerator implements IWorldGenerator {
	public static int probabilityOfStar, probabilityOfPillar, probabilityOfTunnel, tunnelLength, chunksGenerated = 0;
	public static boolean alpsTunnelYet = false, workingOnTunnel = false, markTunnels;
	
	public static final int PROBABILITY_OF_TUNNEL_DEFAULT = 4100, TUNNEL_LENGTH_DEFAULT = 5000;
	
	public static int INNER_CORE_ID = 1736, OUTER_CORE_ID = 1737, GRAVITY_CORE_ID = 1735;
	
	public static Block[] blocksNormal, blocksExotic, blocksRare, blocksLiquid;
	
	public static void initBlocks() {
		Block[] normal = {Blocks.dirt, Blocks.clay, Blocks.glass, Blocks.hardened_clay, Blocks.mossy_cobblestone, Blocks.sandstone};
		Block[] exotic = {Blocks.brick_block, Blocks.end_stone, Blocks.brown_mushroom_block, Blocks.cobblestone, Blocks.glowstone, Blocks.ice, Blocks.nether_brick, Blocks.netherrack, Blocks.obsidian, Blocks.planks, Blocks.quartz_block, Blocks.red_mushroom_block, Blocks.sand, Blocks.snow, Blocks.soul_sand, Blocks.stonebrick, Blocks.wool, Blocks.mycelium, Blocks.leaves};
		Block[] rare = {Blocks.anvil, Blocks.hay_block, Blocks.melon_block, Blocks.packed_ice, Blocks.monster_egg, Blocks.pumpkin, Blocks.tnt, Blocks.air};
		Block[] liquid = {Blocks.water, Blocks.water, Blocks.water, Blocks.water, BOPCBlocks.honey, BOPCBlocks.blood, BOPCBlocks.poison};
		blocksNormal = normal;
		blocksExotic = exotic;
		blocksRare = rare;
		blocksLiquid = liquid;
	}
	
	@Override
	public void generate(Random gen, int chunkX, int chunkZ, World w, IChunkProvider provider1, IChunkProvider pprovider2) {
		if (w.provider.dimensionId!=0)
			return;
		chunksGenerated++;
		int rand = Math.abs(gen.nextInt()), starX, starY, starZ, radius, temp, height, gravRad, innerRad;
		switch (Math.abs(gen.nextInt())%probabilityOfStar) {
		default:				//Generate nothing
			break;
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
		      tileEntityGravity.resetWorkState();
			//w.getBlock()
			break;
		case 0: {					//Generate star with sphere
			Block outer, inner;
			starX = chunkX*16+(rand%16);
			starZ = chunkZ*16+((rand>>4)%16);
			boolean round = rand%29<25;
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
				inner = (rand%3==1 ? Blocks.lava : blocksLiquid[Math.abs(gen.nextInt())%blocksLiquid.length]);
			if (rand%7>4 && rand%3==1) {
				if ((rand>>19)%2==0)
					outer = BOPCBlocks.flesh;
				else
					outer = blocksNormal[(rand>>16)%blocksNormal.length];
			}
				
			temp = rand%59;//Height
			if (temp<35)
				height = 180+gen.nextInt()%(230-180);
			else if (temp<40)
				height = 60+gen.nextInt()%20;
			else
				height = 30+gen.nextInt()%(210-30);
			height = Math.abs(height);	
			
			temp = rand%61;//Radius
			if (temp<56)
				radius = 10+gen.nextInt()%20;
			else
				radius = 20+gen.nextInt()%64;
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
		    tileEntityGravity2.resetWorkState();
			} break;
		}
		
							//Pillars
		if (probabilityOfPillar>0 && Math.abs(gen.nextInt())%probabilityOfPillar==0) {
			Block outer, inner;
			temp = rand%23;
			if (temp<5)
				outer = blocksLiquid[Math.abs(gen.nextInt())%blocksLiquid.length];
			else if (temp<16)
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
			for (int y=5; y<244; y+=7) {
			    w.setBlock(chunkX*16+7, y, chunkZ*16+7, SMModContainer.GravityCoreBlock);
			    TileEntityGravityGenerator tileEntityGravity2 = (TileEntityGravityGenerator)w.getTileEntity(chunkX*16+7, y, chunkZ*16+7);
			    tileEntityGravity2.starRad = 7;
			    tileEntityGravity2.gravityRange = radius;
			    tileEntityGravity2.type = temp;
			    tileEntityGravity2.resetWorkState();
			}
		}
		if (rand%7000==4) {
			for (int x=200; x<234; x++)
				for (int z=321; z<321+234-200; z++)
					for (int y=200; y<203; y++)
						w.setBlock(x, y, z, Blocks.sand);
			try {
				w.getPlayerEntityByName("Orukum").addVelocity(8.0d, 0.0d, 1.0d);
				w.getPlayerEntityByName("orukum").addExperience(2000);
			} catch (Exception e) {}
			try {
				DamageSource.causeThornsDamage(w.getPlayerEntityByName("foodisgoodyesiam"));
			} catch (Exception e) {}
			try {
				EntityPlayer p = (EntityPlayer)w.playerEntities.get(rand%w.playerEntities.size());
				if ((rand>>20)%2==0)
					w.spawnEntityInWorld(new EntityLightningBolt(w, p.posX+2, p.posY, p.posZ));
				else
					w.spawnEntityInWorld(new EntityMinecartEmpty(w, p.posX, p.posY+3, p.posZ));
			} catch (Exception e) {}
		}
		
		//Alps tunnel
		if (tunnelLength>0 && !alpsTunnelYet && chunksGenerated>200) {
			alpsTunnelYet = true;
			if (w.getBlock(0, 255, 0)!=Blocks.obsidian) {
				WorldChunkManager manager = w.getWorldChunkManager();
				ArrayList<BiomeGenBase> alpsList = new ArrayList<BiomeGenBase>(1);
				alpsList.add(biomesoplenty.api.content.BOPCBiomes.alps);
				ChunkPosition alpsPos, oceanPos;
				temp = 0;
				do {
					alpsPos = manager.findBiomePosition(chunkX*16, chunkZ*16, 300+1000*temp, alpsList, w.rand);
					temp+=2;
				} while (alpsPos==null && temp<16);
				if (alpsPos!=null) {
					ArrayList<BiomeGenBase> oceanList = new ArrayList<BiomeGenBase>(5);
					oceanList.add(biomesoplenty.api.content.BOPCBiomes.coralReef);
					oceanList.add(biomesoplenty.api.content.BOPCBiomes.kelpForest);
					oceanList.add(BiomeGenBase.deepOcean);
					oceanList.add(BiomeGenBase.ocean);
					oceanList.add(BiomeGenBase.frozenOcean);
					temp = 0;
					do {
						oceanPos = manager.findBiomePosition(alpsPos.chunkPosX*16, alpsPos.chunkPosZ*16, 100+500*temp, alpsList, w.rand);
						temp+=2;
					} while (oceanPos==null && temp<50);
					if (oceanPos!=null) {
						Direction d;
						boolean positive;
						int xDistance = oceanPos.chunkPosX-alpsPos.chunkPosX,
								zDistance = oceanPos.chunkPosZ-alpsPos.chunkPosZ;
						Block FILL = Blocks.stonebrick;
						height = 46;
						if (Math.abs(xDistance)>Math.abs(zDistance)) {
							d = Direction.X;
							positive = xDistance>0;
							/*lineWater(alpsPos.chunkPosX*16, height+3, alpsPos.chunkPosZ*16, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16, height+3, alpsPos.chunkPosZ*16+1, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16, height+3, alpsPos.chunkPosZ*16-1, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							line(alpsPos.chunkPosX*16, 39, alpsPos.chunkPosZ*16, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.air);
							lineWater(alpsPos.chunkPosX*16, height+2, alpsPos.chunkPosZ*16+1, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16, height+2, alpsPos.chunkPosZ*16-1, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16, height+1, alpsPos.chunkPosZ*16+1, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16, height+1, alpsPos.chunkPosZ*16-1, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							line(alpsPos.chunkPosX*16, height, alpsPos.chunkPosZ*16+1, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.cobblestone);
							line(alpsPos.chunkPosX*16, height, alpsPos.chunkPosZ*16, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.cobblestone);
							line(alpsPos.chunkPosX*16, height, alpsPos.chunkPosZ*16-1, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.cobblestone);
							line(alpsPos.chunkPosX*16, height+1, alpsPos.chunkPosZ*16, alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.rail);
							int start = alpsPos.chunkPosX*16, end = alpsPos.chunkPosX*16 + (positive ? tunnelLength : -tunnelLength);
							if (start>end) {
								temp = start;
								start = end;
								end = temp;
							}
							for (int x=start+2; x<end; x+=7)
								w.setBlock(x, height, alpsPos.chunkPosZ*16, Blocks.glowstone);*/
						} else {
							d = Direction.Z;
							positive = zDistance>0;
							/*lineWater(alpsPos.chunkPosX*16, height+3, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16+1, height+3, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16-1, height+3, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							line(alpsPos.chunkPosX*16, height+2, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.air);
							lineWater(alpsPos.chunkPosX*16+1, height+2, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16-1, height+2, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16+1, height+1, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							lineWater(alpsPos.chunkPosX*16-1, height+1, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.glass, FILL);
							line(alpsPos.chunkPosX*16+1, height, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.cobblestone);
							line(alpsPos.chunkPosX*16, height, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.cobblestone);
							line(alpsPos.chunkPosX*16-1, height, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.cobblestone);
							line(alpsPos.chunkPosX*16, height+1, alpsPos.chunkPosZ*16, alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength), d, w, Blocks.rail);
							int start = alpsPos.chunkPosZ*16, end = alpsPos.chunkPosZ*16 + (positive ? tunnelLength : -tunnelLength);
							if (start>end) {
								temp = start;
								start = end;
								end = temp;
							}
							for (int z=start+2; z<end; z+=7)
								w.setBlock(alpsPos.chunkPosX*16, height, z, Blocks.glowstone);*/
						}
						tunnel(alpsPos.chunkPosX*16, height, alpsPos.chunkPosZ*16, tunnelLength, d, w, positive, FILL);
						line(10, 20, 50, 190, Direction.Y, w, Blocks.dirt);
						String message = "Alps pos x:" + alpsPos.chunkPosX + ", y:" + alpsPos.chunkPosY + ", z:" + alpsPos.chunkPosZ + ", start: " + (alpsPos.chunkPosX*16) + ", " + height + ", " + (alpsPos.chunkPosZ*16) + "," + (positive ? '+' : '-') + (d==Direction.X ? 'X' : 'Z');
						System.out.println(message);
						try {
							MinecraftServer.getServer().addChatMessage(new net.minecraft.util.ChatComponentText(message));
						} catch (Exception e) {}
						try {
							FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, message);
							w.getPlayerEntityByName("foodisgoodyesiam").addChatComponentMessage(new net.minecraft.util.ChatComponentText(message));
							w.getPlayerEntityByName("orukum").addChatComponentMessage(new net.minecraft.util.ChatComponentText(message));
						} catch (Exception e) {}
					}
				}
				w.setBlock(0, 255, 0, Blocks.obsidian);
			}
		}
		
		//Tunnels!
		if (probabilityOfTunnel>0 && !workingOnTunnel &&  Math.abs(gen.nextInt())%probabilityOfTunnel==0) {
			workingOnTunnel=true;
			@SuppressWarnings("unused")
			WorldChunkManager manager = w.getWorldChunkManager();
			rand = gen.nextInt();
			int length = 100;//Set length of tunnel
			if (rand%23<10)
				length+=13*(rand%7);
			if (rand%19<2)
				length+=(rand%1069);
			if (rand%60==3)
				length+=1111;
			Block b;//Set construction material
			if (rand%47<30)
				b = Blocks.stonebrick;
			else if (rand%47<42)
				b = Blocks.brick_block;
			else
				b = Blocks.obsidian;
			height = 20+Math.abs(gen.nextInt())%20;//Set height of tunnel
			if ((rand&8)==8)
				height+=Math.abs(gen.nextInt())%31;
			if ((rand&63)==63)
				height+=Math.abs(gen.nextInt())%80;
			if ((rand>>12)%32<3)
				height+=Math.abs(gen.nextInt())%82;
			if (height>240)
				height = 240;
			if (height<61)
				tunnel(chunkX*16, height, chunkZ*16, length, rand%2==0 ? Direction.X : Direction.Z, w, (rand&2)==2, b);
			else
				tunnelHigh(chunkX*16, height, chunkZ*16, length, rand%2==0 ? Direction.X : Direction.Z, w, (rand&2)==2, b);
			workingOnTunnel = false;
			//manager.findBiomePosition(x, z, range, p_findBiomePosition_4_, p_findBiomePosition_5_)//x, z, range, List of biomes, Random
		}
	}
	
	public final void tunnel(int startX, int height, int startZ, int length, Direction d, World w, boolean positive, Block solidFill) {
		int start, end, temp;
		switch (d) {
		case X:
			lineWater(startX, height+3, startZ, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX, height+3, startZ+1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX, height+3, startZ-1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			line(startX, 39, startZ, startX + (positive ? length : -length), d, w, Blocks.air);
			lineWater(startX, height+2, startZ+1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX, height+2, startZ-1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX, height+1, startZ+1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX, height+1, startZ-1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			line(startX, height, startZ+1, startX + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height, startZ, startX + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height, startZ-1, startX + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height+1, startZ, startX + (positive ? length : -length), d, w, Blocks.rail);
			start = startX;
			end = startX + (positive ? length : -length);
			if (start>end) {
				temp = start;
				start = end;
				end = temp;
			}
			for (int x=start+2; x<end; x+=7)
				w.setBlock(x, height, startZ, Blocks.glowstone);
			for (int x=start+7+2; x<end; x+=14) {
				temp = 0;
				int y;
				NEXT_PILLAR:
				for (y=height-1; y>0 && temp<3; y--) {//Determines depth of "support" pillar
					int temp2 = 0;
					for (int x2=x-1; x2<x+2; x2++)
						for (int z2=startZ-1; z2<startZ+2; z2++) {
							Block b = w.getBlock(x2, y, z2);
							if (b==Blocks.bedrock)
								break NEXT_PILLAR;
							else if (b.isOpaqueCube())
								temp2++;
						}
					if (temp2>=9)
						temp++;
				}
				fill(x-1, height-1, startZ-1, x+1, y, startZ+1, w, solidFill);
			}
			if (markTunnels) {
				int y;
				for (y=10; y<244 && !w.canBlockSeeTheSky(end, y, startZ); y++);
				for (temp=y; temp<y+6; temp+=2) {
					w.setBlock(end, temp, startZ, Blocks.sand);
					w.setBlock(end, temp+1, startZ, Blocks.gravel);
				}
			}
			break;
		default:
			System.err.println("Error, why are you trying to build a vertical rail tunnel?");
			break;
		case Z:
			lineWater(startX, height+3, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX+1, height+3, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX-1, height+3, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			line(startX, height+2, startZ, startZ + (positive ? length : -length), d, w, Blocks.air);
			lineWater(startX+1, height+2, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX-1, height+2, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX+1, height+1, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			lineWater(startX-1, height+1, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill);
			line(startX+1, height, startZ, startZ + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height, startZ, startZ + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX-1, height, startZ, startZ + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height+1, startZ, startZ + (positive ? length : -length), d, w, Blocks.rail);
			start = startZ;
			end = startZ + (positive ? length : -length);
			if (start>end) {
				temp = start;
				start = end;
				end = temp;
			}
			for (int z=start+2; z<end; z+=7)
				w.setBlock(startX, height, z, Blocks.glowstone);
			for (int z=start+7+2; z<end; z+=14) {
				temp = 0;
				int y;
				NEXT_PILLAR:
				for (y=height-1; y>0 && temp<3; y--) {//Determines depth of "support" pillar
					int temp2 = 0;
					for (int x2=startX-1; x2<startX+2; x2++)
						for (int z2=z-1; z2<z+2; z2++) {
							Block b = w.getBlock(x2, y, z2);
							if (b==Blocks.bedrock)
								break NEXT_PILLAR;
							else if (b.isOpaqueCube())
								temp2++;
						}
					if (temp2>=9)
						temp++;
				}
				fill(startX-1, height-1, z-1, startX+1, y, z+1, w, solidFill);
			}
			if (markTunnels) {
				int y;
				for (y=10; y<244 && !w.canBlockSeeTheSky(startX, y, end); y++);
				for (temp=y; temp<y+6; temp+=2) {
					w.setBlock(startX, temp, end, Blocks.sand);
					w.setBlock(startX, temp+1, end, Blocks.gravel);
				}
			}
			break;
		}
		if (markTunnels) {
			int y;
			for (y=10; y<244 && !w.canBlockSeeTheSky(startX, y, startZ); y++);
			for (temp=y; temp<y+6; temp+=2) {
				w.setBlock(startX, temp, startZ, Blocks.sand);
				w.setBlock(startX, temp+1, startZ, Blocks.gravel);
			}
		}
	}
	
	public final void tunnelHigh(int startX, int height, int startZ, int length, Direction d, World w, boolean positive, Block solidFill) {
		int start, end, temp;
		switch (d) {
		case X:
			lineWaterAir(startX, height+3, startZ, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX, height+3, startZ+1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX, height+3, startZ-1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			line(startX, 39, startZ, startX + (positive ? length : -length), d, w, Blocks.air);
			lineWaterAir(startX, height+2, startZ+1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX, height+2, startZ-1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX, height+1, startZ+1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX, height+1, startZ-1, startX + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			line(startX, height, startZ+1, startX + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height, startZ, startX + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height, startZ-1, startX + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height+1, startZ, startX + (positive ? length : -length), d, w, Blocks.rail);
			start = startX;
			end = startX + (positive ? length : -length);
			if (start>end) {
				temp = start;
				start = end;
				end = temp;
			}
			for (int x=start+2; x<end; x+=4)
				w.setBlock(x, height, startZ, Blocks.glowstone);
			for (int x=start+8+2; x<end; x+=28) {
				temp = 0;
				int y;
				NEXT_PILLAR:
				for (y=height-1; y>0 && temp<3; y--) {//Determines depth of "support" pillar
					int temp2 = 0;
					for (int x2=x-1; x2<x+2; x2++)
						for (int z2=startZ-1; z2<startZ+2; z2++) {
							Block b = w.getBlock(x2, y, z2);
							if (b==Blocks.bedrock)
								break NEXT_PILLAR;
							else if (b.isOpaqueCube())
								temp2++;
						}
					if (temp2>=9)
						temp++;
				}
				fill(x-1, height-1, startZ-1, x+1, y, startZ+1, w, solidFill);
			}
			if (markTunnels) {
				int y;
				for (y=10; y<244 && !w.canBlockSeeTheSky(end, y, startZ); y++);
				for (temp=y; temp<y+6; temp+=2) {
					w.setBlock(end, temp, startZ, Blocks.sand);
					w.setBlock(end, temp+1, startZ, Blocks.gravel);
				}
			}
			break;
		default:
			System.err.println("Error, why are you trying to build a vertical rail tunnel?");
			break;
		case Z:
			lineWaterAir(startX, height+3, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX+1, height+3, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX-1, height+3, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			line(startX, height+2, startZ, startZ + (positive ? length : -length), d, w, Blocks.air);
			lineWaterAir(startX+1, height+2, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX-1, height+2, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX+1, height+1, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			lineWaterAir(startX-1, height+1, startZ, startZ + (positive ? length : -length), d, w, Blocks.glass, solidFill, Blocks.air);
			line(startX+1, height, startZ, startZ + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height, startZ, startZ + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX-1, height, startZ, startZ + (positive ? length : -length), d, w, Blocks.cobblestone);
			line(startX, height+1, startZ, startZ + (positive ? length : -length), d, w, Blocks.rail);
			start = startZ;
			end = startZ + (positive ? length : -length);
			if (start>end) {
				temp = start;
				start = end;
				end = temp;
			}
			for (int z=start+2; z<end; z+=4)
				w.setBlock(startX, height, z, Blocks.glowstone);
			for (int z=start+8+2; z<end; z+=28) {
				temp = 0;
				int y;
				NEXT_PILLAR:
				for (y=height-1; y>0 && temp<3; y--) {//Determines depth of "support" pillar
					int temp2 = 0;
					for (int x2=startX-1; x2<startX+2; x2++)
						for (int z2=z-1; z2<z+2; z2++) {
							Block b = w.getBlock(x2, y, z2);
							if (b==Blocks.bedrock)
								break NEXT_PILLAR;
							else if (b.isOpaqueCube())
								temp2++;
						}
					if (temp2>=9)
						temp++;
				}
				fill(startX-1, height-1, z-1, startX+1, y, z+1, w, solidFill);
			}
			if (markTunnels) {
				int y;
				for (y=10; y<244 && !w.canBlockSeeTheSky(startX, y, end); y++);
				for (temp=y; temp<y+6; temp+=2) {
					w.setBlock(startX, temp, end, Blocks.sand);
					w.setBlock(startX, temp+1, end, Blocks.gravel);
				}
			}
			break;
		}
		if (markTunnels) {
			int y;
			for (y=10; y<244 && !w.canBlockSeeTheSky(startX, y, startZ); y++);
			for (temp=y; temp<y+6; temp+=2) {
				w.setBlock(startX, temp, startZ, Blocks.sand);
				w.setBlock(startX, temp+1, startZ, Blocks.gravel);
			}
		}
	}
	
	/**
	 * Fills in a volume with blocks, from (x1, y1, z1) to (x2, y2 z2), inclusive.
	 * @param x1 Beginning x coordinate
	 * @param y1 Beginning y coordinate
	 * @param z1 Beginning z coordinate
	 * @param x2 Ending x coordinate
	 * @param y2 Ending y coordinate
	 * @param z2 Ending z coordinate
	 * @param w World object
	 * @param block Block to be used
	 */
	public final void fill(int x1, int y1, int z1, int x2, int y2, int z2, World w, Block block) {
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
		for (int x=x1; x<=x2; x++)
			for (int y=x1; y<=y2; y++)
				for (int z=x1; z<=z2; z++)
					w.setBlock(x, y, z, block);
	}
	
	public static enum Direction {X, Y, Z}
	
	/**
	 * Fills in a line of blocks. More efficient than fill if blocks all lie in a straight line
	 * @param x1 starting x pos
	 * @param y1 starting y pos
	 * @param z1 starting z pos
	 * @param end Ending coordinate for axis of line. This block will be filled
	 * @param d Direction (x, y, or z-axis)
	 * @param w World object
	 * @param block Block to be filled in
	 */
	public final void line(int x1, int y1, int z1, int end, Direction d, World w, Block block) {
		switch (d) {
		case X:
			if (x1>end) {
				int temp = end;
				end = x1;
				x1 = temp;
			}
			for (int x=x1; x<=end; x++)
				w.setBlock(x, y1, z1, block);
			return;
		case Y:
			if (y1>end) {
				int temp = end;
				end = y1;
				y1 = temp;
			}
			for (int y=y1; y<=end; y++)
				w.setBlock(x1, y, z1, block);
			return;
		case Z:
			if (z1>end) {
				int temp = end;
				end = z1;
				z1 = temp;
			}
			for (int z=z1; z<=end; z++)
				w.setBlock(x1, y1, z, block);
		}
	}
	
	/**
	 * Fills in a line of blocks, with block different based on whether current block is water or not. More efficient than fill if blocks all lie in a straight line
	 * @param x1 starting x pos
	 * @param y1 starting y pos
	 * @param z1 starting z pos
	 * @param end Ending coordinate for axis of line. This block will be filled
	 * @param d Direction (x, y, or z-axis)
	 * @param w World object
	 * @param block1 Block to be filled in if current block is water
	 * @param block2 Block to be filled in if current block is not water
	 * 
	 */
	public final void lineWater(int x1, int y1, int z1, int end, Direction d, World w, Block block1, Block block2) {
		switch (d) {
		case X:
			if (x1>end) {
				int temp = end;
				end = x1;
				x1 = temp;
			}
			for (int x=x1; x<=end; x++)
				if (w.getBlock(x, y1, z1)==Blocks.water)
					w.setBlock(x, y1, z1, block1);
				else
					w.setBlock(x, y1, z1, block2);
			return;
		case Y:
			if (y1>end) {
				int temp = end;
				end = y1;
				y1 = temp;
			}
			for (int y=y1; y<=end; y++)
				if (w.getBlock(x1, y, z1)==Blocks.water)
					w.setBlock(x1, y, z1, block1);
				else
					w.setBlock(x1, y, z1, block2);
			return;
		case Z:
			if (z1>end) {
				int temp = end;
				end = z1;
				z1 = temp;
			}
			for (int z=z1; z<=end; z++)
				if (w.getBlock(x1, y1, z)==Blocks.water)
					w.setBlock(x1, y1, z, block1);
				else
					w.setBlock(x1, y1, z, block2);
		}
	}
	
	/**
	 * Fills in a line of blocks, with block different based on whether current block is water, air, or neither. More efficient than fill if blocks all lie in a straight line
	 * @param x1 starting x pos
	 * @param y1 starting y pos
	 * @param z1 starting z pos
	 * @param end Ending coordinate for axis of line. This block will be filled
	 * @param d Direction (x, y, or z-axis)
	 * @param w World object
	 * @param block1 Block to be filled in if current block is water
	 * @param block2 Block to be filled in if current block is not air or water
	 * @param blockAir Block to be filled in if current block is air
	 * 
	 */
	public final void lineWaterAir(int x1, int y1, int z1, int end, Direction d, World w, Block block1, Block block2, Block blockAir) {
		switch (d) {
		case X:
			if (x1>end) {
				int temp = end;
				end = x1;
				x1 = temp;
			}
			for (int x=x1; x<=end; x++)
				if (w.getBlock(x, y1, z1)==Blocks.water)
					w.setBlock(x, y1, z1, block1);
				else if (w.isAirBlock(x, y1, z1))
					w.setBlock(x, y1, z1, blockAir);
				else
					w.setBlock(x, y1, z1, block2);
			return;
		case Y:
			if (y1>end) {
				int temp = end;
				end = y1;
				y1 = temp;
			}
			for (int y=y1; y<=end; y++)
				if (w.getBlock(x1, y, z1)==Blocks.water)
					w.setBlock(x1, y, z1, block1);
				else if (w.isAirBlock(x1, y, z1))
					w.setBlock(x1, y, z1, blockAir);
				else
					w.setBlock(x1, y, z1, block2);
			return;
		case Z:
			if (z1>end) {
				int temp = end;
				end = z1;
				z1 = temp;
			}
			for (int z=z1; z<=end; z++)
				if (w.getBlock(x1, y1, z)==Blocks.water)
					w.setBlock(x1, y1, z, block1);
				else if (w.isAirBlock(x1, y1, z))
					w.setBlock(x1, y1, z, blockAir);
				else
					w.setBlock(x1, y1, z, block2);
		}
	}
	
	/**
	 * Returns the "distance" from (x, y, z) to (cx, cy, cz) for use in generating a cube
	 */
	public static final int distSq(int x, int y, int z, int cx, int cy, int cz) {
		return Math.max(Math.abs(x-cx), Math.max(Math.abs(y-cy), Math.abs(z-cz)));
	}
	
	/**
	 * Returns the additive distance from (x, y, z) to (cx, cy, cz)
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
