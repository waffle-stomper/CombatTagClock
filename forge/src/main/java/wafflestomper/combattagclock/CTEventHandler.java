package wafflestomper.combattagclock;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CTEventHandler {
	
	private CombatTagClock combatTagClock;
	private Minecraft mc = Minecraft.getMinecraft();
	
	private Pattern patternTimeLeft = Pattern.compile(".*you are in combat for (\\d+) seconds\\.");
	
	public CTEventHandler(CombatTagClock combatTagClockRef){
		this.combatTagClock = combatTagClockRef;
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void chatReceived(ClientChatReceivedEvent event){
		if (!this.combatTagClock.pluginEnabled){return;}
		String message = event.message.getUnformattedText().toLowerCase();
		if (message.contains("you have been combat tagged on login") ||
				message.contains("type /ct to check your remaining tag time")){
			if (message.contains("tagged on login") && this.combatTagClock.suppressLoginMessage &&
					this.combatTagClock.pluginEnabled){
				event.setCanceled(true);
			}
			this.combatTagClock.checkTimeLeft();
		}
		else if (message.contains("you are not currently in combat!")){
			if (this.combatTagClock.suppressNotInCombatMessage && this.combatTagClock.suppressNextReply &&
					this.combatTagClock.pluginEnabled){
				event.setCanceled(true);
				this.combatTagClock.suppressNextReply = false;
			}
			combatTagClock.setClockOff();
		}
		else{
			Matcher matcher = this.patternTimeLeft.matcher(message);
			if (matcher.find()){
				int timeLeft = Integer.parseInt(matcher.group(1));
				if (this.combatTagClock.suppressCombatTimeMessage && this.combatTagClock.suppressNextReply &&
						this.combatTagClock.pluginEnabled){
					event.setCanceled(true);
					this.combatTagClock.suppressNextReply = false;
				}
				this.combatTagClock.setClockTime(timeLeft+1);
			}
		}
    }
	
	/**
	 * This is used to detect when a player is hurt.
	 * It triggers a /ct check some time in the next 10 seconds.
	 */
	@SubscribeEvent 
	public void livingUpdate(LivingUpdateEvent event){
		if (event.entityLiving.hurtTime == 10 &&
				event.entity instanceof EntityPlayer &&
				this.combatTagClock.pluginEnabled){
			this.combatTagClock.lazyTagCheckRequested = true;
		}
	}
	
	/**
	 * Called when the client (re)connects to a server.
	 * Enables the plugin iff the game is multiplayer and the server is in the 
	 *   list of enabled servers in the config file.
	 */
	@SubscribeEvent
	public void connectedToServer(ClientConnectedToServerEvent event){
		if (this.mc.isSingleplayer()){
			this.combatTagClock.pluginEnabled = false;
			return;
		}
		String hostname = this.mc.func_147104_D().serverIP.toLowerCase();
		for (String server : this.combatTagClock.enabledServers){
			if (hostname.equals(server.toLowerCase())){
				this.combatTagClock.pluginEnabled = true;
				return;
			}
		}
		this.combatTagClock.pluginEnabled = false;
		this.combatTagClock.shownDisabledMessage = false;
	}
	
	@SubscribeEvent
    public void renderTick(RenderTickEvent event){
    	if (event.phase == Phase.END){
    		this.combatTagClock.onRenderTick();
    	}
    }
	
}