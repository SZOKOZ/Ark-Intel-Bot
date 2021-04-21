package arkintelbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

public class AllegianceIntelChannel 
{
	private static final Logger logger = LoggerFactory.getLogger(AllegianceIntelChannel.class);
	private ArkAllegiance allegiance;
	private TextChannel channel;
	private HashMap<String, Long> id64s;
	private MessageHistory history;
	private static Pattern id64Rgx = Pattern.compile("steamcommunity.com/profiles/\\d+", Pattern.CASE_INSENSITIVE);
	private Pattern id64numRgx = Pattern.compile("\\d+");
	private Consumer<Message> acceptPreviousMessage;
	private Consumer<List<Message>> acceptMessageHistory;
	
	public AllegianceIntelChannel()
	{
		id64s = new HashMap<String, Long>();
	}
	
	public AllegianceIntelChannel(TextChannel channel)
	{
		this.channel = channel;
		AllegianceIntelChannel();
		InitialiseFromChannel();
	}
	
	private void AllegianceIntelChannel()
	{
		if (channel == null)
		{
			logger.error("Error: Invalid channel provided for AllegianceIntelChannel %s. "
					+ "Please check if the channel id is in your config"
					+ " and exists.");
			return;
		}
		
		history = channel.getHistory();
		acceptPreviousMessage = new Consumer<Message>() {

			@Override
			public void accept(Message t) 
			{
				String content = t.getContentDisplay();
				Matcher matcher = id64Rgx.matcher(content);
				boolean potentialMatches = true;
				while(potentialMatches)
				{
					if (potentialMatches = matcher.find())
					{
						String url = matcher.group();
						matcher = id64numRgx.matcher(url);
						if (matcher.find())
						{
							id64s.put(t.getId(), Long.parseUnsignedLong(matcher.group()));
							return;
						}
						
						logger.info("Warning: Could not parse ID64 from Steam Profile URL, %s", url);
					}
					else
					{
						matcher = id64numRgx.matcher(content);
						if (potentialMatches = matcher.find())
						{
							id64s.put(t.getId(), Long.parseUnsignedLong(matcher.group()));
						}
						
						logger.info("Warning: Could not parse ID64 from custom input, %s", content);
					}
				}
			}
			
		};
		acceptMessageHistory = new Consumer<List<Message>>() {

			@Override
			public void accept(List<Message> arg0) 
			{
				if (arg0 == null )
				{
					return;
				}
				
				if (arg0.size() == 0)
				{
					return;
				}
				
				arg0.forEach(acceptPreviousMessage);
				InitialiseFromChannel();
			}
		};
		
		InitialiseFromChannel();
	}
	
	public boolean IsInAllegiance(Long id64)
	{
		return id64s.containsValue(id64);
	}
	
	public TextChannel GetChannel()
	{
		return channel;
	}
	
	public void SetChannel(TextChannel channel)
	{
		this.channel = channel;
		AllegianceIntelChannel();
	}
	
	public void AddId(String messageId, Long id64)
	{
		id64s.put(messageId, id64);
	}
	
	public void AddIdByUrl(String messageId, String url)
	{
		Matcher matcher = id64Rgx.matcher(url);
		if (matcher.find())
		{
			String protocolStrippedUrl = matcher.group();
			matcher = id64numRgx.matcher(protocolStrippedUrl);
			if (matcher.find())
			{
				id64s.put(messageId, Long.parseUnsignedLong(matcher.group()));
				return;
			}
			
			logger.info("Warning: Could not parse ID64 from Steam Profile URL, %s", url);
		}
	}
	
	public void RemoveIdByMsg(String messageId)
	{
		id64s.remove(messageId);
	}
	
	public void InitialiseAllegiancesFromChannel()
	{
		history = channel.getHistory();
		InitialiseFromChannel();
	}
	
	private void InitialiseFromChannel()
	{
		if (history == null)
		{
			logger.error("Error: AllegianceIntelChannel %s has not been configured with a text channel."
					+ " Please add the channel id to the config.", allegiance.toString());
			return;
		}
		
		history.retrievePast(100).queue(acceptMessageHistory);
		return;
	}
}
