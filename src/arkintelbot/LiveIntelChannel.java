package arkintelbot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import arkintelbot.ArkAllegiance;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

public class LiveIntelChannel 
{
	enum IntelAlertType
	{
		ALERT_NONE,
		ALERT_LOW,
		ALERT_MEDIUM,
		ALERT_HIGH
	}
	
	private String channelAlias;
	private TextChannel channelServer;
	private Message messageIntel;
	private MessageEmbed embedIntel;
	private EmbedBuilder embedBuilderIntel;
	private int alertNotifs = 0;
	private long alertTime = System.currentTimeMillis();
	private IntelAlertType alertType = IntelAlertType.ALERT_NONE;
	public LiveIntelChannel(TextChannel textChannel, String alias)
	{
		channelServer = textChannel;
		/*if (channelServer != null)
		{
			System.out.println("HEH?");
			List<Message> messages = channelServer.getHistory().retrievePast(100).complete();
			channelServer.deleteMessages(messages).queue();
		}*/
		channelAlias = alias;
		embedBuilderIntel = new EmbedBuilder()
				.setAuthor(textChannel.getName())
				.setColor(Color.WHITE)
				.setDescription(String.format("Live Intel on %s", textChannel.getName()))
				.setTitle("Live Intelligence Board")
				.addField("Player Count", "Players", true)
				.addField("Status", "Not Connected", true)
				.addField("Intel List", "List", false)
				.addBlankField(false)
				.setFooter("Made by EXE KL#4645", null);
		List<Message> messages = channelServer.getPinnedMessages().complete();
		messages.forEach(new Consumer<Message>()
				{

					@Override
					public void accept(Message arg0) 
					{
						arg0.getEmbeds().forEach(new Consumer<MessageEmbed>()
								{

									@Override
									public void accept(MessageEmbed t) 
									{
										if (t.getAuthor().getName().equals(textChannel.getName()))
										{
											messageIntel = arg0;
											embedIntel = t;
										}
									}
							
								});
					}
			
				});
		
		if (messageIntel != null)
		{
			return;
		}
		
		textChannel.sendMessage(embedIntel = embedBuilderIntel.build()).queue(new Consumer<Message>()
				{

					@Override
					public void accept(Message t) 
					{
						messageIntel = t;
						t.pin().queue();	
					}
			
				});
	}

	public String GetAlias()
	{
		return channelAlias;
	}
	
	public TextChannel GetChannel() 
	{
		return channelServer;
	}

	public Message GetIntelMessage() 
	{
		return messageIntel;
	}

	public MessageEmbed GetIntelEmbed() 
	{
		return embedIntel;
	}

	public EmbedBuilder GetIntelEmbedBuilder() 
	{
		return embedBuilderIntel;
	}
	
	public void UpdateServerStatus(boolean status)
	{
		List<MessageEmbed.Field> fields = embedBuilderIntel.getFields();
		for (int i = 0; i < fields.size(); i++)
		{
			MessageEmbed.Field field = fields.get(i);
			if (!field.getName().equals("Status"))
			{
				continue;
			}
			
			fields.set(i, new MessageEmbed.Field("Status", status?"Connected ✅":"Not Connected ❌", true));
			break;
		}
		
		messageIntel.editMessage(embedBuilderIntel.build()).queue();
	}
	
	public void UpdatePlayerCount(int players)
	{
		List<MessageEmbed.Field> fields = embedBuilderIntel.getFields();
		for (int i = 0; i < fields.size(); i++)
		{
			MessageEmbed.Field field = fields.get(i);
			if (!field.getName().equals("Player Count"))
			{
				continue;
			}
			
			fields.set(i, new MessageEmbed.Field("Player Count", String.valueOf(players), true));
			break;
		}
		
		messageIntel.editMessage(embedBuilderIntel.build()).queue();
	}
	
	public void UpdatePlayerList(ArrayList<ArkPlayer> players)
	{
		int enemyCount = 0;
		ArrayList<String> listContent = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < players.size(); i++)
		{
			if (i != 0 && i % 10 == 0)
			{
				listContent.add(sb.toString());
				sb.delete(0, sb.length());
			}
			
			sb.append(String.format("https://steamcommunity.com/profiles/%d [%s]\n", 
					players.get(i).getId64(), players.get(i).getAllegiance().toString()));
			if (players.get(i).getAllegiance() == ArkAllegiance.ALLEGIANCE_ENEMY)
			{
				enemyCount++;
			}
			
		}
		listContent.add(sb.toString());
		
		if (enemyCount == 0)
		{
			if (alertType != IntelAlertType.ALERT_NONE)
			{
				alertType = IntelAlertType.ALERT_NONE;
				alertTime = System.currentTimeMillis();
				alertNotifs = 0;
			}
		}
		else if (1 <= enemyCount && enemyCount <= 2)
		{
			if (alertType != IntelAlertType.ALERT_LOW)
			{
				alertType = IntelAlertType.ALERT_LOW;
				alertTime = System.currentTimeMillis();
				alertNotifs = 0;
			}
		}
		else if (3 <= enemyCount && enemyCount <= 5)
		{
			if (alertType != IntelAlertType.ALERT_MEDIUM)
			{
				alertType = IntelAlertType.ALERT_MEDIUM;
				alertTime = System.currentTimeMillis();
				alertNotifs = 0;
			}
		}
		else if (6 < enemyCount)
		{
			if (alertType != IntelAlertType.ALERT_HIGH)
			{
				alertType = IntelAlertType.ALERT_HIGH;
				alertTime = System.currentTimeMillis();
				alertNotifs = 0;
			}
		}
		
		List<MessageEmbed.Field> fields = embedBuilderIntel.getFields();
		for (int i = 0; i < fields.size(); i++)
		{
			MessageEmbed.Field field = fields.get(i);
			if (!field.getName().contains("Intel List Page"))
			{
				continue;
			}
			
			fields.remove(i);
		}
		
		if (listContent.size() == 0)
		{
			return;
		}
		
		for (int i = 0; i < fields.size(); i++)
		{
			MessageEmbed.Field field = fields.get(i);
			if (!field.getName().equals("Intel List"))
			{
				continue;
			}
			
			fields.set(i, new MessageEmbed.Field("Intel List", listContent.get(0), false));
			for (int j = 1; j < listContent.size(); j++)
			{
				fields.add(i+j, new MessageEmbed.Field(String.format("Intel List Page %d", j+1), listContent.get(j), false));
			}
			
			break;
		}
		
		messageIntel.editMessage(embedBuilderIntel.build()).queue();
	}
	
	public String getChannelAlias() 
	{
		return channelAlias;
	}

	public long getAlertTime() 
	{
		return alertTime;
	}

	public IntelAlertType getAlertType() 
	{
		return alertType;
	}
	
	public int getAlertNotifs()
	{
		return alertNotifs;
	}
	
	public void incrementAlertNotifs()
	{
		alertNotifs++;
		alertTime = System.currentTimeMillis();
	}
}
