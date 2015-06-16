package foodisgood.mods.joushou_tweaks;

import java.io.File;

import org.apache.logging.log4j.Level;
//import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.config.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
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
    public static final String VERSION = "1.9";
    public Configuration config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	try {
	    	config = new Configuration(new File(event.getModConfigurationDirectory(), "JoushouTweaks.cfg"));
	    	StarGenerator.probabilityOfStar = config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStar", 1111, "X, where 4/X is the chance a given chunk will have a star of some kind. Must be greater than 3").getInt(1111);
	    	if (StarGenerator.probabilityOfStar<4) {
	    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: probabilityOfStar as given in config outside bounds, reverting to default value of 1111");
	    		StarGenerator.probabilityOfStar = 1111;
	    		config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStar", 1111).set(1111);
	    	}
	    	StarGeneratorNether.probabilityOfStar = config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStarNether", 2000, "X, where 4/X is the chance a given chunk in the Nether will have a star of some kind in the Nether. Must be greater than 3, or else 0 for no stars in Nether.").getInt(2000);
	    	if (StarGeneratorNether.probabilityOfStar<4 && StarGeneratorNether.probabilityOfStar!=0) {
	    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: probabilityOfStarNether as given in config outside bounds, reverting to default value of 2000");
	    		StarGeneratorNether.probabilityOfStar = 2000;
	    		config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStarNether", 2000).set(2000);
	    	}
	    	StarGenerator.probabilityOfPillar = config.get(Configuration.CATEGORY_GENERAL, "probabilityOfPillar", 3000, "X, where 1/X is the chance a given chunk will have a pillar. Must be greater than 0, or else 0 for no pillars in OW.").getInt(3000);
	    	if (StarGenerator.probabilityOfPillar<0) {
	    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: probabilityOfPillar as given in config outside bounds, reverting to default value of 3000");
	    		StarGenerator.probabilityOfPillar = 3000;
	    		config.get(Configuration.CATEGORY_GENERAL, "probabilityOfPillar", 3000).set(3000);
	    	}
	    	StarGeneratorNether.probabilityOfPillar = config.get(Configuration.CATEGORY_GENERAL, "probabilityOfPillarNether", 5000, "X, where 1/X is the chance a given chunk in the Nether will have a pillar. Must be greater than 0, or else 0 for no pillars in Nether.").getInt(5000);
	    	if (StarGeneratorNether.probabilityOfPillar<0) {
	    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: probabilityOfPillarNether as given in config outside bounds, reverting to default value of 5000");
	    		StarGeneratorNether.probabilityOfPillar = 5000;
	    		config.get(Configuration.CATEGORY_GENERAL, "probabilityOfPillarNether", 5000).set(5000);
	    	}
	    	StarGenerator.probabilityOfTunnel = config.get(Configuration.CATEGORY_GENERAL, "probabilityOfTunnel", StarGenerator.PROBABILITY_OF_TUNNEL_DEFAULT, "TODO.").getInt(StarGenerator.PROBABILITY_OF_TUNNEL_DEFAULT);///TODO: Add description of how this works
	    	if (StarGenerator.probabilityOfTunnel<0) {
	    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: probabilityOfTunnel as given in config outside bounds, reverting to default value of " + StarGenerator.PROBABILITY_OF_TUNNEL_DEFAULT);
	    		StarGenerator.probabilityOfTunnel = StarGenerator.PROBABILITY_OF_TUNNEL_DEFAULT;
	    		config.get(Configuration.CATEGORY_GENERAL, "probabilityOfTunnel", StarGenerator.PROBABILITY_OF_TUNNEL_DEFAULT).set(StarGenerator.PROBABILITY_OF_TUNNEL_DEFAULT);
	    	}
	    	StarGenerator.tunnelLength = config.get(Configuration.CATEGORY_GENERAL, "tunnelLength", StarGenerator.TUNNEL_LENGTH_DEFAULT, "Length of tunnel radiating from Alps nearest to spawn. 0 for no tunnel.").getInt(StarGenerator.TUNNEL_LENGTH_DEFAULT);
	    	if (StarGenerator.tunnelLength<0) {
	    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: tunnelLength as given in config outside bounds, reverting to default value of " + StarGenerator.TUNNEL_LENGTH_DEFAULT);
	    		StarGenerator.tunnelLength = StarGenerator.TUNNEL_LENGTH_DEFAULT;
	    		config.get(Configuration.CATEGORY_GENERAL, "tunnelLength", StarGenerator.TUNNEL_LENGTH_DEFAULT).set(StarGenerator.TUNNEL_LENGTH_DEFAULT);
	    	}
	    	StarGenerator.markTunnels = config.get(Configuration.CATEGORY_GENERAL, "markTunnels", true, "Whether or not tunnels generated should have their endpoints marked above ground.").getBoolean(true);
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
