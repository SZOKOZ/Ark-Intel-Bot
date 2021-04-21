package arkintelbot.arkutilities;

import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;

public class Resolvers 
{
	public static Long DiscordChannelNameToID(JDA jda, String name)
	{
		if (jda != null)
		{
			List<TextChannel> channel = jda.getTextChannelsByName(name, true);
			if (channel.size() > 0)
			{
				return channel.get(0).getIdLong();
			}
		}
		
		return null;
	}
	
	public static HashMap<String, String> BattleMetricsToServer(String url)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
	public static HashMap<String, String> SteamToServer(String url)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}
}
