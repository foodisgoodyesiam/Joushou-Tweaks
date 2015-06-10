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
    public static final String VERSION = "1.4";
    public Configuration config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	try {
	    	config = new Configuration(new File(event.getModConfigurationDirectory(), "JoushouTweaks.cfg"));
	    	StarGenerator.probabilityOfStar = config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStar", 300, "X, where 4/X is the chance a given chunk will have a star of some kind. Must be greater than 3").getInt(300);
	    	if (StarGenerator.probabilityOfStar<4) {
	    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: probabilityOfStar as given in config outside bounds, reverting to default value of 300");
	    		StarGenerator.probabilityOfStar = 300;
	    		config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStar", 300).set(300);
	    	}
	    	StarGeneratorNether.probabilityOfStar = config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStarNether", 500, "X, where 4/X is the chance a given chunk in the Nether will have a star of some kind. Must be greater than 3, or else 0 for no stars in Nether.").getInt(500);
	    	if (StarGeneratorNether.probabilityOfStar<4 && StarGeneratorNether.probabilityOfStar!=0) {
	    		FMLRelaunchLog.log(JoushouTweaks.NAME, Level.INFO, "JoushouTweaks: probabilityOfStarNether as given in config outside bounds, reverting to default value of 500");
	    		StarGeneratorNether.probabilityOfStar = 500;
	    		config.get(Configuration.CATEGORY_GENERAL, "probabilityOfStarNether", 500).set(500);
	    	}
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
        GameRegistry.registerWorldGenerator(new StarGenerator(), 0);
        if (StarGeneratorNether.probabilityOfStar!=0)
        	GameRegistry.registerWorldGenerator(new StarGeneratorNether(), 0);
        System.out.println(NAME + ": loading successful");
    }
}
