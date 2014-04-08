package wafflestomper.combattagclock;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@Mod(modid = CombatTagClock.MODID, version = CombatTagClock.VERSION, name = CombatTagClock.NAME)
public class CombatTagClock extends GuiScreen{
    public static final String MODID = "CombatTagClock";
    public static final String VERSION = "1.7.2-1.1.8";
    public static final String NAME = "CombatTag Clock";
	
	private Minecraft mc = Minecraft.getMinecraft();
	
	private CTEventHandler ctEventHandler = new CTEventHandler(this);
	private ConfigManager config = new ConfigManager(this);
	
	public boolean pluginEnabled = false;
	public boolean shownDisabledMessage = true;
	
	public String[] enabledServers;
	
	public int textPosX = 0;
	public int textPosY = 0;
	private String displayText = "DISABLED";
	private String displayTextColour = "0000FF";
	public boolean hideWhenNotTagged = false;
	
	private boolean currentlyTagged = false;
	
	private long lastCTMessageSent = 0;
	private long finishTime = 0;
	
	public boolean lazyTagCheckRequested = false;
	private long lazyTagCheckMinTime = 10000;
	
	public boolean suppressLoginMessage = false;
	public boolean suppressCombatTimeMessage = true;
	public boolean suppressNotInCombatMessage = true;
	public boolean suppressNextReply = false;
	
	/**
	 * Call to start or update the clock
	 */
	public void setClockTime(int timeLeft){
		this.finishTime = System.currentTimeMillis() + (timeLeft * 1000);
		this.currentlyTagged = true;
		this.displayTextColour = "FF0000";
	}
	
	/**
	 * Call when we're not in combat
	 */
	public void setClockOff(){
		this.currentlyTagged = false;
		this.displayText = "NOT TAGGED";
		this.displayTextColour = "00FF00";
	}
	
	/**
	 * Sends /ct to the server
	 */
	public void checkTimeLeft(){
		this.mc.thePlayer.sendChatMessage("/ct");
		this.suppressNextReply = true;
		this.lastCTMessageSent = System.currentTimeMillis();
		this.lazyTagCheckRequested = false;
	}
	
	public void onRenderTick(){
		if (!this.pluginEnabled){
			if (!this.shownDisabledMessage && this.mc.theWorld != null){
				this.mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + 
						"Combat Tag Clock is not enabled on this server ("+ this.mc.func_147104_D().serverIP + ")"));
				this.shownDisabledMessage = true;
			}
			return;
		}
		if (this.currentlyTagged){
			long timeLeft = this.finishTime - System.currentTimeMillis();
			if (timeLeft < 0){timeLeft = 0;}
			this.displayText = String.format("%.1f seconds left in combat",timeLeft/1000.0);
			long timeSinceLastCheck = System.currentTimeMillis() - this.lastCTMessageSent;
			if ((timeLeft == 0 && timeSinceLastCheck >= 1000)){
				checkTimeLeft();
			}
		}
		if (this.currentlyTagged || !this.hideWhenNotTagged){
			if (this.mc.theWorld != null && !this.mc.gameSettings.showDebugInfo)
		    {
		        Gui.drawRect(this.textPosX, this.textPosY, this.textPosX + this.mc.fontRenderer.getStringWidth(this.displayText) + 5, 
		        		this.textPosY + 12, -2147483648);
		        this.mc.fontRenderer.drawStringWithShadow(this.displayText, this.textPosX + 3, this.textPosY + 2, 
		        		Integer.parseInt(this.displayTextColour, 16));
		    }
		}
		if (this.lazyTagCheckRequested && (System.currentTimeMillis() - this.lastCTMessageSent) > this.lazyTagCheckMinTime){
			checkTimeLeft();
		}
	}
	
	@EventHandler
	public void init(FMLPreInitializationEvent event){
		this.config.init(event);
	}
}
