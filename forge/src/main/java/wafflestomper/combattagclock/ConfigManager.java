package wafflestomper.combattagclock;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class ConfigManager {
	
	private Configuration config;
	private CombatTagClock combatTagClock;
			
	public ConfigManager(CombatTagClock combatTagClockRef){
		this.combatTagClock = combatTagClockRef;
	}
	
	public void init(FMLPreInitializationEvent event){
		this.config = new Configuration(event.getSuggestedConfigurationFile());
		this.config.load();
		
		//Server Settings
		String[] defaultServers = new String[] {"mc.civcraft.vg", "pvp.civcraft.vg"};
		this.combatTagClock.enabledServers = this.config.get("server settings", "enabled servers", defaultServers).getStringList();
		
		//Chat Suppression Settings
		this.config.addCustomCategoryComment("chat message supression", 
				"This section is used to hide combat tag messages in chat.\n" +
				"Setting these values to true will hide the applicable messages, false will make them visible.\n" + 
				"If you manually send a /ct, the reply should be unaffected by these settings.");
		this.combatTagClock.suppressLoginMessage = this.config.get("chat message supression","Tagged on login", false).getBoolean(false);
		this.combatTagClock.suppressCombatTimeMessage = this.config.get("chat message supression","In combat for x seconds", false).getBoolean(false);
		this.combatTagClock.suppressNotInCombatMessage = this.config.get("chat message supression","Not in combat", false).getBoolean(false);
		
		//Display Settings
		this.combatTagClock.textPosX = this.config.get("display options", "X Pos", 0).getInt(0);
		this.combatTagClock.textPosY = this.config.get("display options", "Y Pos", 0).getInt(0);
		this.combatTagClock.hideWhenNotTagged = this.config.get("display options", "Hide display when not tagged", false).getBoolean(false);
		
		this.config.save();
	}
}



