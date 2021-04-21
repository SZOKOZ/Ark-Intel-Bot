package arkintelbot.api;

import java.util.List;

import arkintelbot.ArkServer;
import arkintelbot.arkutilities.Resolvers;
import killzonebot.util.Bot;
import net.dv8tion.jda.core.entities.TextChannel;

public class LiveIntelServer
{
	long discordRelayId = 0;
	ArkServer server = null;
	Bot bot = null;
	
	public LiveIntelServer(Bot bot, String discordRelayName, ArkServer server)
	{
		this.server = server;
		this.bot = bot;
		
		if (bot != null)
		{
			discordRelayId = Resolvers.DiscordChannelNameToID(bot.getJDA(), discordRelayName);
		}
	}
	
	public LiveIntelServer(Bot bot, long discordRelayId, ArkServer server)
	{
		this.discordRelayId = discordRelayId;
		this.server = server;
		this.bot = bot;
	}
	
	public long GetDiscordRelayId()
	{
		return discordRelayId;
	}
	
	public String GetDiscordRelayIdAsString()
	{
		return String.valueOf(discordRelayId);
	}
	
	public String GetDiscordRelayName()
	{
		if (bot != null && discordRelayId != 0)
		{
			TextChannel relayChannel = bot.getJDA().getTextChannelById(discordRelayId);
			if (relayChannel != null)
			{
				return relayChannel.getName();
			}
		}
		
		return null;
	}
	
	public String GetIp() 
	{
		return server.getIp();
	}

	public int GetPort() 
	{
		return server.getPort();
	}

	public int GetQueryPort() 
	{
		return server.getQueryPort();
	}
	
	public long GetSteamId()
	{
		return server.getSteamId();
	}
	
	public String getAlias()
	{
		return server.getAlias();
	}
	
	public void setAlias(String alias)
	{
		server.setAlias(alias);
	}
}
