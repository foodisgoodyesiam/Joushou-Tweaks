package foodisgood.mods.joushou_tweaks;

import java.io.File;

import org.apache.logging.log4j.Level;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.BiomeDictionary;
//import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.config.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.*;
/**
 * Copyright 2015, foodisgoodyesiam
 *
 * All rights reserved.
 *
 */
@Mod(name = JoushouTweaks.NAME, version = JoushouTweaks.VERSION, useMetadata = true, modid = JoushouTweaks.MODID, dependencies = "required-after:modJ_StarMiner;required-after:BiomesOPlenty;")
public class JoushouTweaks {
    public static final String NAME = "Joushou Tweaks", MODID = "JoushouTweaks";
    public static final String VERSION = "1.11";
    public static int pathWeight, pathMin = 0, pathMax;
    public Configuration config;
    
    public int getConfigValueSafe(String name, int min, String desc, int defaultVal) {
    	int ret = config.get(Configuration.CATEGORY_GENERAL, name, defaultVal, desc).getInt(defaultVal);
    	if (ret<min) {
    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: " + name + " as given in config outside bounds, reverting to default value of " + defaultVal);
    		StarGenerator.probabilityOfStar = 1111;
    		config.get(Configuration.CATEGORY_GENERAL, name, defaultVal).set(defaultVal);
    	}
    	return ret;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	try {
	    	config = new Configuration(new File(event.getModConfigurationDirectory(), "JoushouTweaks.cfg"));
	    	StarGenerator.probabilityOfStar = getConfigValueSafe("probabilityOfStar", 4, "X, where 4/X is the chance a given chunk will have a star of some kind. Must be greater than 3", 1111);
	    	StarGeneratorNether.probabilityOfStar = config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStarNether", 2000, "X, where 4/X is the chance a given chunk in the Nether will have a star of some kind in the Nether. Must be greater than 3, or else 0 for no stars in Nether.").getInt(2000);
	    	if (StarGeneratorNether.probabilityOfStar<4 && StarGeneratorNether.probabilityOfStar!=0) {
	    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: probabilityOfStarNether as given in config outside bounds, reverting to default value of 2000");
	    		StarGeneratorNether.probabilityOfStar = 2000;
	    		config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStarNether", 2000).set(2000);
	    	}
	    	StarGenerator.probabilityOfPillar = getConfigValueSafe("probabilityOfPillar", 0, "X, where 1/X is the chance a given chunk will have a pillar. Must be greater than 0, or else 0 for no pillars in OW.", 3000);
	    	StarGeneratorNether.probabilityOfPillar = getConfigValueSafe("probabilityOfPillarNether", 0, "X, where 1/X is the chance a given chunk in the Nether will have a pillar. Must be greater than 0, or else 0 for no pillars in Nether.", 5000);
	    	StarGenerator.probabilityOfTunnel = getConfigValueSafe("probabilityOfTunnel", 0, "X, where 1/X is the chance a given chunk will spawn a rail line.", StarGenerator.PROBABILITY_OF_TUNNEL_DEFAULT);
	    	StarGenerator.tunnelLength = getConfigValueSafe("tunnelLength", 10, "Length of tunnel radiating from Alps nearest to spawn. 0 for no tunnel. Must be greater than 10", StarGenerator.TUNNEL_LENGTH_DEFAULT);
	    	StarGenerator.markTunnels = config.get(Configuration.CATEGORY_GENERAL, "markTunnels", true, "Whether or not tunnels generated should have their endpoints marked above ground.").getBoolean(true);
	    	JTPath.maxLength = getConfigValueSafe("maxJTRoadLength", 3, "X, where X*7 is the maximum length of a JT-type road. Vanilla value is 5", 30);
	    	JTPath.villageSize = getConfigValueSafe("villageSize", 0, "In blocks, the maximum permissible \"radius\", or distance from central well, of village components for purposes of JT-road districts. Vanilla value is 112", 321);
	    	JTPath.maxBuildings = getConfigValueSafe("maxBuildings", 0, "Maximum number of buildings permissible to be spawned from a single JT road or its children roads, to catch runaway villages.", 20000);
	    	JTPath.recheckBiomes = config.get(Configuration.CATEGORY_GENERAL, "recheckBiomes", false, "Whether or not JT Roads should care if they are extending into a different terrain type, such as ocean, than they started in. (Vanilla roads do care)").getBoolean(false);
	    	pathWeight = getConfigValueSafe("pathComponentWeight", 0, "Weight for JTPath component when villages are being generated. If even a single JTPath is generated, the village will grow enormously.", 60);
	    	pathMax = getConfigValueSafe("pathMax", 0, "Maximum permissible number of JTPaths in a single village.", 20);
    	} catch (Exception e) {
            System.err.println("Problem loading Joushou Tweaks config (JoushouTweaks.cfg): " + e.getMessage());
            FMLRelaunchLog.log(JoushouTweaks.NAME, Level.FATAL, "Problem loading Joushou Tweaks config (JoushouTweaks.cfg): " + e.getMessage());
            e.printStackTrace();
            return;
    	}
    	try {
			config.save();
    	} catch (Exception e) {
    		System.err.println("Problem saving Joushou Tweaks config (JoushouTweaks.cfg): " + e.getMessage());
            FMLRelaunchLog.log(JoushouTweaks.NAME, Level.ERROR, "Problem saving Joushou Tweaks config (JoushouTweaks.cfg): " + e.getMessage());
    	}
    	MapGenStructureIO.func_143031_a(JTPath.class, "JoushouTweaks:JTPath");
    	VillagerRegistry.instance().registerVillageCreationHandler(new JTPath.JTPathHandler(pathWeight, pathMin, pathMax));
    	BiomeDictionary.registerBiomeType(biomesoplenty.api.content.BOPCBiomes.alps, BiomeDictionary.Type.FROZEN);
    	/*MapGenStructureIO.registerStructure(StarStructureStart.class, "JoushouStarStart");
        MapGenStructureIO.func_143031_a(StructureStar.class, "JoushouStar");*/
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	StarGenerator.initBlocks();
    	StarGeneratorNether.initBlocks();
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        System.out.println(NAME + ": load()");
        StarGenerator.alpsTunnelYet = false;
        StarGenerator.chunksGenerated = 0;
        GameRegistry.registerWorldGenerator(new StarGenerator(), 0);
        if (StarGeneratorNether.probabilityOfStar!=0)
        	GameRegistry.registerWorldGenerator(new StarGeneratorNether(), 0);
        System.out.println(NAME + ": loading successful");
    }
}
