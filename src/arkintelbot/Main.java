package arkintelbot;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.koraktor.steamcondenser.servers.SourceServer;

import arkintelbot.LiveIntelChannel.IntelAlertType;
import arkintelbot.arkutilities.ClientPref;
import killzonebot.util.Bot;
import killzonebot.util.BotFactory;
import killzonebot.util.CommandInterface;
import killzonebot.util.Feature;
import killzonebot.util.ICommandAction;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import steambot.SteamBotJNI;
public class Main 
{
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	@SuppressWarnings("unchecked")
	/*
	 * This main is huge. Thankfully it isnt't meant to be read by anyone but me.
	 */
	public static void main(String arg[])
	{
		boolean dev = false;
		//arg = new String[1];
		//arg[0] = "--master";
		boolean master = false;
		boolean spawnSlaveConfig = false;
		if (!dev)
		{
			if (arg.length == 0)
			{
				logger.info("Needs to be run with --master or --slave args.");
				return;
			}
			
			if ((arg[0].equalsIgnoreCase("--master") || arg[0].equalsIgnoreCase("--slave")))
			{
				master = arg[0].equalsIgnoreCase("--master");
				if (arg.length == 3 && arg[1].equalsIgnoreCase("createslave"))
				{
					spawnSlaveConfig = true;
				}
			}
			else
			{
				logger.info("Invalid args. Needs to be run with --master or --slave.");
				return;
			}
		}
		SteamBotJNI steamInterface = new SteamBotJNI();
		ConfigLoader config = null;
		CommandInterface battleMetricCommands = new CommandInterface();
		BattleMetrics bm = new BattleMetrics();
		ArrayList<Long> handledBmRequests = new ArrayList<Long>();
		ArrayList<LiveIntelChannel> liveIntelChannels = new ArrayList<LiveIntelChannel>();
		AllegianceIntelChannel tribeIntelChannel = new AllegianceIntelChannel();
		AllegianceIntelChannel allyIntelChannel = new AllegianceIntelChannel();
		AllegianceIntelChannel enemyIntelChannel = new AllegianceIntelChannel();
		ArrayList<ArkServer> connectedArkServers = new ArrayList<ArkServer>();
		
		int serverQueryPorts[] = {27015, 27016, 27017, 27018, 27019, 27020};
		String optionEmotes[] = {"\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3", "\u0034\u20E3", "\u0035\u20E3", "\u0036\u20E3"};
		try 
		{
			config = new ConfigLoader(master||dev);
			if (spawnSlaveConfig)
			{
				config.SpawnSlaveConfig(arg[2]);
				logger.info("Slave config generated with license "+arg[2]);
				return;
			}
		} 
		catch (IOException e) 
		{
			logger.error("Failed to initialise bot configuration. Reason:"+e.getLocalizedMessage());
			return;
		}
		
		if (master && config.IsBlankConfig())
		{
			logger.info("A new configuration file has been created. Please configure the bot and restart.");
			return;
		}
		else if (!master && config.IsBlankConfig())
		{
			logger.error("Slave configuration is missing... Please ask the bot owner to generate one for you.");
			return;
		}
		
		if (!steamInterface.IsInitialised())
		{
			logger.error("You need to be logged in to Steam in order for the bot to work."
					+ " If you are logged in but still encountering this error, make sure you have ARK:Survival Evolved in your library."
					+ " Else contact the developer if you are still stuck. EXE KL#4645");
			if (!master && !dev)
			{
				return;
			}
		}
		
		Bot arkDiscord = new BotFactory().setToken((String)config.GetAnyConfigValue("botToken")).build();
		if (arkDiscord.getJDA() == null)
		{
			logger.error("Failed to log in to discord with provided token.");
			return;
		}
		
		VerificationChannel verificationChannel = new VerificationChannel(arkDiscord.getJDA()
				.getTextChannelById((String)config.GetAnyConfigValue("licensingChannel")));
		String configuredLicense = (String)config.GetAnyConfigValue("license");
		
		if (!verificationChannel.IsGuildValid())
		{
			System.out.println("Exit Code: K3pp0");
			return;
		}
		
		if (!verificationChannel.IsLicenseValid(configuredLicense))
		{
			System.out.println("Exit Code: Kappa");
			return;
		}
		
		if (configuredLicense.contains("MASTER") && !master)
		{
			logger.info("License is a master type but you are attempting to run bot as a slave."
					+ " If this was accidental, re-run the bot with the args --master.");
		}
		else if (!configuredLicense.contains("MASTER") && master)
		{
			logger.error("License is not a master type but you attempted to run the bot as a master."
					+ " Will now run as a slave...");
			master = false;
		}
		
		//has to be final ):
		final String bmChannel = (String)config.GetAnyConfigValue("battlemetricsChannel");;
		if (master)
		{
			tribeIntelChannel.SetChannel(arkDiscord.getJDA().getTextChannelById((String)config.GetAnyConfigValue("tribeInputChannel")));
			allyIntelChannel.SetChannel(arkDiscord.getJDA().getTextChannelById((String)config.GetAnyConfigValue("allyInputChannel")));
			enemyIntelChannel.SetChannel(arkDiscord.getJDA().getTextChannelById((String)config.GetAnyConfigValue("enemyInputChannel")));
		}
		TextChannel enemyAlertChannel = arkDiscord.getJDA()
				.getTextChannelById((String)config.GetAnyConfigValue("enemyalertchannel"));
		
		HashMap<String, Object> mapServers = (HashMap<String, Object>) config.GetAnyConfigValue("servers");
		HashMap<String, Object> mapIntelChannels = (HashMap<String, Object>) config.GetAnyConfigValue("liveIntelChannels");
		int serverCount = mapServers.size();
		if (serverCount > 0)
		{
			mapIntelChannels.forEach(new BiConsumer<String, Object>()
					{

						@Override
						public void accept(String arg0, Object arg1) 
						{
							TextChannel channelServer = arkDiscord.getJDA().getTextChannelById((String)arg1);
							liveIntelChannels.add(new LiveIntelChannel(channelServer, arg0));
						}
				
					});
			
			mapServers.forEach(new BiConsumer<String, Object>()
					{

						@Override
						public void accept(String t, Object u) 
						{
							String serverAddress = (String)u;
							String addressComponents[] = serverAddress.split(":");
							Supplier findAndQuery = new Supplier()
									{

										@Override
										public Object get() 
										{
											for (int i = 0; i < serverQueryPorts.length; i++)
											{
												HashMap<String, Object> info = null;
												try
												{
													InetAddress arkServerIp = InetAddress.getByName(addressComponents[0]);
													SourceServer arkServer = new SourceServer(arkServerIp, serverQueryPorts[i]);
													info = arkServer.getServerInfo();
												}
												catch(Exception e)
												{
													logger.error(e.getLocalizedMessage());
												}
												
												if (info == null)
												{
													continue;
												}
												
												int players = steamInterface.GetPlayerCountByServerId((long)info.get("serverId"));
												if (players == 0)
												{
													logger.info(String.format("Warning: Not connected to %s @ %s:%s.", 
															t, addressComponents[0], addressComponents[1]));
													continue;
												}
												
												connectedArkServers.add(
														new ArkServer(addressComponents[0], Integer.parseInt(addressComponents[1]),
																serverQueryPorts[i], info, t));
												return null;
											}
											
											return null;
										}
								
									};
							if (addressComponents.length == 2)
							{
								findAndQuery.get();
							}
							else if (addressComponents.length == 3)
							{
								HashMap<String, Object> info = null;
								try
								{
									InetAddress arkServerIp = InetAddress.getByName(addressComponents[0]);
									SourceServer arkServer = new SourceServer(arkServerIp, Integer.parseInt(addressComponents[2]));
									info = arkServer.getServerInfo();
								}
								catch(Exception e)
								{
									logger.error(e.getLocalizedMessage());
									findAndQuery.get();
									return;
								}
								
								if (info == null)
								{
									logger.info("Warning: Unable to retrieve info on %s @ %s:%s. Is the server up?"
											+ " Is the rcon port specified in the config correct?",
											t, addressComponents[0], addressComponents[1]);
									return;
								}
								
								int players = steamInterface.GetPlayerCountByServerId((long)info.get("serverId"));
								if (players == 0)
								{
									logger.info(String.format("Warning: Not connected to %s @ %s:%s.", 
											t, addressComponents[0], addressComponents[1]));
									return;
								}
								
								connectedArkServers.add(new ArkServer(addressComponents[0], Integer.parseInt(addressComponents[1]),
										Integer.parseInt(addressComponents[2]), info, t));
							}
						}
				
					});
		}

			if (master)
			{
				arkDiscord.getJDA().addEventListener(new ListenerAdapter() {
					@Override
					public void onMessageDelete(MessageDeleteEvent deletedMsg)
					{
						TextChannel channel = deletedMsg.getTextChannel();
						if (channel == null)
						{
							return;
						}
						
						if (allyIntelChannel.GetChannel() != null && channel.getIdLong() == allyIntelChannel.GetChannel().getIdLong())
						{
							allyIntelChannel.RemoveIdByMsg(deletedMsg.getMessageId());
						}
						else if (enemyIntelChannel.GetChannel() != null && channel.getIdLong() == enemyIntelChannel.GetChannel().getIdLong())
						{
							enemyIntelChannel.RemoveIdByMsg(deletedMsg.getMessageId());
						}
						else if (tribeIntelChannel.GetChannel() != null && channel.getIdLong() == tribeIntelChannel.GetChannel().getIdLong())
						{
							tribeIntelChannel.RemoveIdByMsg(deletedMsg.getMessageId());
						}
					}
					
					@Override
					public void onMessageReceived(MessageReceivedEvent receivedMsg)
					{
						if (receivedMsg.getAuthor().getIdLong() == arkDiscord.getJDA().getSelfUser().getIdLong())
						{
							return;
						}
						
						TextChannel channel = receivedMsg.getTextChannel();
						if (channel == null)
						{
							return;
						}
						
						if (allyIntelChannel.GetChannel() != null && channel.getIdLong() == allyIntelChannel.GetChannel().getIdLong())
						{
							allyIntelChannel.InitialiseAllegiancesFromChannel();
						}
						else if (enemyIntelChannel.GetChannel() != null && channel.getIdLong() == enemyIntelChannel.GetChannel().getIdLong())
						{
							enemyIntelChannel.InitialiseAllegiancesFromChannel();
						}
						else if (tribeIntelChannel.GetChannel() != null && channel.getIdLong() == tribeIntelChannel.GetChannel().getIdLong())
						{
							tribeIntelChannel.InitialiseAllegiancesFromChannel();
						}
					}
					
					@Override
					public void onMessageUpdate(MessageUpdateEvent updatedMsg)
					{	
						TextChannel channel = updatedMsg.getTextChannel();
						if (channel == null)
						{
							return;
						}
						
						if (allyIntelChannel.GetChannel() != null && channel.getIdLong() == allyIntelChannel.GetChannel().getIdLong())
						{
							allyIntelChannel.InitialiseAllegiancesFromChannel();
						}
						else if (enemyIntelChannel.GetChannel() != null && channel.getIdLong() == enemyIntelChannel.GetChannel().getIdLong())
						{
							enemyIntelChannel.InitialiseAllegiancesFromChannel();
						}
						else if (tribeIntelChannel.GetChannel() != null && channel.getIdLong() == tribeIntelChannel.GetChannel().getIdLong())
						{
							tribeIntelChannel.InitialiseAllegiancesFromChannel();
						}
					}
					
					@Override
					public void onMessageReactionAdd(MessageReactionAddEvent reaction)
					{
						if (reaction.getUser().getIdLong() == arkDiscord.getJDA().getSelfUser().getIdLong())
						{
							return;
						}
						
						TextChannel channel = reaction.getTextChannel();
						if (channel == null)
						{
							return;
						}
						
						if (!channel.getId().equals(bmChannel))
						{
							return;
						}
						
						Message message = channel.getMessageById(reaction.getMessageId()).complete();
						if (handledBmRequests.contains(message.getIdLong()))
						{
							return;
						}
						
						String content = message.getEmbeds().get(0).getDescription();
						Pattern linkPatt= Pattern.compile("https://www.battlemetrics.com/servers/ark/\\d+");
						Matcher matcher = linkPatt.matcher(content);
						String bmLinks[] = new String[3];
						for (int i = 0; (i < bmLinks.length) && matcher.find(); i++)
						{
							bmLinks[i] = matcher.group();
						}
						
						int option = 0;
						if (reaction.getReactionEmote().getName().equals(optionEmotes[0]))
						{
							option = 0;
						}
						else if (reaction.getReactionEmote().getName().equals(optionEmotes[1]))
						{
							option = 1;
						}
						else if (reaction.getReactionEmote().getName().equals(optionEmotes[2]))
						{
							option = 2;
						}
						else
						{
							reaction.getReaction().removeReaction().queue();
							return;
						}
						message.editMessage(createBMStatsEmbed(bm, bmLinks[option])).queue((m) -> m.clearReactions().queue());
						handledBmRequests.add(message.getIdLong());
					}
				});
			}
			
		if (master)
		{
			battleMetricCommands.addCommand("server", new ICommandAction() {

				@Override
				public void CommandAction(String[] command, int args, Object any) 
				{
					MessageReceivedEvent msgEvent = (MessageReceivedEvent) any;
					if (args != 2)
					{
						String commandfmt = "Command format: !server number";
						msgEvent.getChannel().sendMessage(commandfmt).queue();
						return;
					}
					
					Object url = null;
					if ((url = ClientPref.GetClientPref(msgEvent.getAuthor()
							.getId(), "server"+command[1])) != null && !((String)url).equals("-1"))
					{
						msgEvent.getChannel().sendMessage(createBMStatsEmbed(bm, (String)url)).queue();
						return;
					}
					
					
					msgEvent.getChannel().sendMessage(createBMMenuEmbed(bm, 
							command[1])).queue(new Consumer<Message>() {

						@Override
						public void accept(Message arg0) 
						{
							arg0.addReaction(optionEmotes[0]).queue();
							arg0.addReaction(optionEmotes[1]).queue();
							arg0.addReaction(optionEmotes[2]).queue();
						}
						
					});
				}
				
			});
			
			battleMetricCommands.addCommand("set", new ICommandAction() {

				@Override
				public void CommandAction(String[] command, int args, Object any) 
				{
					MessageReceivedEvent msgEvent = (MessageReceivedEvent) any;
					if (args != 3)
					{
						String commandfmt = "Command format: !set number url";
						msgEvent.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(commandfmt).queue());
						return;
					}
					
					ClientPref.AddPref(msgEvent.getAuthor().getId(), "server"+command[1], command[2]);
					msgEvent.getChannel().sendMessage(String.format("Preference for server %s "
							+ "has been set for you.", command[1])).queue();
				}
				
			});
			
			battleMetricCommands.addCommand("clearset", new ICommandAction() {

				@Override
				public void CommandAction(String[] command, int args, Object any) 
				{
					MessageReceivedEvent msgEvent = (MessageReceivedEvent) any;
					if (args != 2)
					{
						String commandfmt = "Command format: !clearset number";
						msgEvent.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(commandfmt).queue());
						return;
					}
					
					ClientPref.AddPref(msgEvent.getAuthor().getId(), "server"+command[1], String.valueOf(-1));
					msgEvent.getChannel().sendMessage(String.format("Your preference for server %s "
							+ "has been cleared.", "server"+command[1])).queue();
				}
				
			});
			
			battleMetricCommands.addCommand("search", new ICommandAction() {

				@Override
				public void CommandAction(String[] command, int args, Object any) 
				{
					MessageReceivedEvent msgEvent = (MessageReceivedEvent) any;
					if (args != 2)
					{
						String commandfmt = "Command format: !search number";
						msgEvent.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(commandfmt).queue());
						return;
					}
					
					msgEvent.getChannel().sendMessage(createBMMenuEmbed(bm, 
							command[1])).queue(new Consumer<Message>() {

						@Override
						public void accept(Message arg0) 
						{
							arg0.addReaction(optionEmotes[0]).queue();
							arg0.addReaction(optionEmotes[1]).queue();
							arg0.addReaction(optionEmotes[2]).queue();
						}
						
					});
				}
				
			});
			
			battleMetricCommands.addCommand("help", new ICommandAction() {
				
				@Override
				public void CommandAction(String[] command, int args, Object any) 
				{
					MessageReceivedEvent msgEvent = (MessageReceivedEvent) any;
					String help = "!server NUMBER (Gives you a list of the top 3 results)\n"+
					"!set NUMBER URL (Sets the default battlemetric result of !server "
					+ "for the server number)\n"
					+ "!search NUMBER (Gives you a list of the top 3 results regardless "
					+ "of the preference you set for a server number)\n"
					+ "!clearset NUMBER (Clears the preference of !server for the server number)";
					msgEvent.getChannel().sendMessage(help).queue();
				}
				
			});
		}
		
		arkDiscord.addFeature(new Feature("BattleMetrics", battleMetricCommands, new String[] {"server",
				"set", "clearset", "search", "help"}));
		try 
		{
			Thread.sleep(1000);
		} 
		catch (InterruptedException e) 
		{
			logger.debug(e.getLocalizedMessage());
		}
		
		
		Timer pollPlayerList = new Timer();
		pollPlayerList.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() 
			{
				for (int i = 0; i < connectedArkServers.size(); i++)
				{
					ArkServer server = connectedArkServers.get(i);
					int players = steamInterface.GetPlayerCountByServerId(server.getSteamId());
					if (players == 0)
					{
						logger.error("SteamBot has been disconnected from to %s @ %s:%s.\n"
								+ "Will check later to see if it is back up.", 
								server.getAlias(), server.getIp(), server.getPort());
						connectedArkServers.remove(i);
						liveIntelChannels.forEach(new Consumer<LiveIntelChannel>()
								{

									@Override
									public void accept(LiveIntelChannel t) 
									{
										if (!t.GetAlias().equals(server.getAlias()))
										{
											return;
										}
										
										t.UpdateServerStatus(false);
									}
							
								});
						continue;
					}
					
					liveIntelChannels.forEach(new Consumer<LiveIntelChannel>()
					{

						@Override
						public void accept(LiveIntelChannel t) 
						{
							if (!t.GetAlias().equals(server.getAlias()))
							{
								return;
							}
							
							t.UpdateServerStatus(true);
							t.UpdatePlayerCount(players);
						}
					});
					
					SteamBotJNI.SteamPlayer[] onlinePlayers = steamInterface.GetPlayersByServerId(server.getSteamId());
					liveIntelChannels.forEach(new Consumer<LiveIntelChannel>()
					{

						@Override
						public void accept(LiveIntelChannel t) 
						{
							if (!t.GetAlias().equals(server.getAlias()))
							{
								return;
							}
							
							System.out.println("omg");
							
							ArrayList<ArkPlayer> arkPlayers = new ArrayList<ArkPlayer>();
							for (int onlinePlayer = 0; onlinePlayer < onlinePlayers.length; onlinePlayer++)
							{
								SteamBotJNI.SteamPlayer player = onlinePlayers[onlinePlayer];
								ArkAllegiance allegiance = ArkAllegiance.ALLEGIANCE_UNKNOWN;
								if (allyIntelChannel.IsInAllegiance(player.id64()))
								{
									allegiance = ArkAllegiance.ALLEGIANCE_ALLY;
								}
								else if (enemyIntelChannel.IsInAllegiance(player.id64()))
								{
									allegiance = ArkAllegiance.ALLEGIANCE_ENEMY;
								}
								else if (tribeIntelChannel.IsInAllegiance(player.id64()))
								{
									allegiance = ArkAllegiance.ALLEGIANCE_TRIBE;
								}
								arkPlayers.add(new ArkPlayer(player.getPlayerName(), player.id64(), allegiance));
							}
							t.UpdatePlayerList(arkPlayers);
							if (enemyAlertChannel != null)
							{
								EmbedBuilder eb = new EmbedBuilder();
								eb.setTitle(String.format("%s Enemy Alerts", server.getAlias()));
								eb.setDescription(String.format("[Connect to %s](steam://connect/%s:%d)", server.getAlias(),
										server.getIp(), server.getQueryPort()));
								
								List<Message> messages = enemyAlertChannel.getHistory().retrievePast(100).complete();
								Message embedMsg = null;
								MessageEmbed currentEmbed = null;
								for(Message m:messages)
								{
									List<MessageEmbed> embeds = m.getEmbeds();
									if (embeds == null)
									{
										continue;
									}
									
									if (embeds.size() == 0)
									{
										continue;
									}
									
									MessageEmbed embed = embeds.get(0);
									if (embed.getTitle().contains(server.getAlias()))
									{
										embedMsg = m;
										currentEmbed = embed;
										break;
									}
								}
								
								if (embedMsg == null)
								{
									switch (t.getAlertType())
									{
									case ALERT_HIGH:
										eb.addField(IntelAlertType.ALERT_HIGH.toString(), "@here Over 6 enemies have been detected on the server!.", false);
										t.incrementAlertNotifs();
										break;
									case ALERT_LOW:
										eb.addField(IntelAlertType.ALERT_LOW.toString(), "@here 1 or 2 enemies have been detected on the server!.", false);
										t.incrementAlertNotifs();
										break;
									case ALERT_MEDIUM:
										eb.addField(IntelAlertType.ALERT_MEDIUM.toString(), "@here 3 to 5 enemies have been detected on the server!.", false);
										t.incrementAlertNotifs();
										break;
									case ALERT_NONE:
										eb.addField(IntelAlertType.ALERT_NONE.toString(), "No enemies present so far.", false);
										break;
									default:
										break;
									
									}
									enemyAlertChannel.sendMessage(eb.build()).queue();
									return;
								}
								
								long millisElapsed = System.currentTimeMillis() - t.getAlertTime();
								if (t.getAlertType() == IntelAlertType.ALERT_NONE)
								{
									Field field = currentEmbed.getFields().get(0);
									if (field.getName().isEmpty() ||
											field.getName().equals(IntelAlertType.ALERT_NONE.toString()))
									{
										return;
									}
									
									eb.addField(IntelAlertType.ALERT_NONE.toString(), "No enemies present so far.", false);
								}
								else if (t.getAlertType() == IntelAlertType.ALERT_LOW)
								{
									Field field = currentEmbed.getFields().get(0);
									if (field.getName().equals(IntelAlertType.ALERT_LOW.toString()) && t.getAlertNotifs() > 1)
									{
										return;
									}
									
									if (t.getAlertNotifs() > 0 && millisElapsed < 5*1000*60)
									{
										return;
									}
									
									eb.addField(IntelAlertType.ALERT_LOW.toString(), "@here 1 or 2 enemies have been detected on the server!.", false);
									t.incrementAlertNotifs();
								}
								else if (t.getAlertType() == IntelAlertType.ALERT_MEDIUM)
								{
									Field field = currentEmbed.getFields().get(0);
									if (field.getName().equals(IntelAlertType.ALERT_LOW.toString()) && t.getAlertNotifs() > 3)
									{
										return;
									}
									
									if (t.getAlertNotifs() > 2 && millisElapsed < 10*1000*60)
									{
										return;
									}
									
									if (t.getAlertNotifs() > 0 && millisElapsed < 5*1000*60)
									{
										return;
									}
									
									eb.addField(IntelAlertType.ALERT_MEDIUM.toString(), "@here 3 to 5 enemies have been detected on the server!.", false);
									t.incrementAlertNotifs();
								}
								else if (t.getAlertType() == IntelAlertType.ALERT_HIGH)
								{
									if (t.getAlertNotifs() > 3 && System.currentTimeMillis() - t.getAlertTime() < 10*1000*60)
									{
										return;
									}
									
									if (t.getAlertNotifs() > 0 && millisElapsed < 5*1000*60)
									{
										return;
									}
									
									eb.addField(IntelAlertType.ALERT_HIGH.toString(), "@here Over 6 enemies have been detected on the server!.", false);
									t.incrementAlertNotifs();
								}
								
								embedMsg.editMessage(eb.build()).queue();
							}
						}
				
					});
				}
			}
			
		}, 0, 60000);
		
	}
	
	@SuppressWarnings("unchecked")
	private static MessageEmbed createBMStatsEmbed(BattleMetrics bm, String urlOrId)
	{
		HashMap<String, Object> info = (HashMap<String, Object>)bm.GetServerInfo(urlOrId);
		if (info == null)
		{
			return null;
		}
		HashMap<String, Object> infoDetails = (HashMap<String, Object>)info.get("details");
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle((String)info.get("name"));
		embed.setDescription(String.format("[View On Battlemetrics](%s)", urlOrId));
		embed.addField("Players", String.format("%s/%s", info.get("players"), 
				info.get("maxPlayers")), true);
		embed.addField("Map", (String)infoDetails.get("map"), true);
		embed.addField("Mode", (boolean)infoDetails.get("pve")?"PVE":"PVP", true);
		embed.addField("Status", "✅Online", true);
		embed.addField("Official", (boolean)infoDetails.get("official")?"✅Yes":"❌No", true);
		embed.addField("Mods", String.format("%d", ((Object[])infoDetails.get("modsNames")) != null?
				((Object[])infoDetails.get("modsNames")).length:0), true);
		
		return embed.build();
	}
	
	@SuppressWarnings("unchecked")
	private static MessageEmbed createBMMenuEmbed(BattleMetrics bm, String serverNo)
	{
		List<String> top3 = bm.GetTop3(Integer.parseInt(serverNo));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < top3.size(); i++)
		{
			String emoji = null;
			switch (i)
			{
			case 0:
			{
				emoji = ":one:";
				break;
			}
			case 1:
			{
				emoji = ":two:";
				break;
			}
			case 2:
			{ 
				emoji = ":three:";
				break;
			}
			}
			HashMap<String, Object> info = (HashMap<String, Object>)bm.GetServerInfo(top3.get(i));
			if (info == null)
			{
				sb.append(String.format("%s %s\n", emoji, top3.get(i)));
			}
			else
			{
				sb.append(String.format("%s [%s](%s) \n", emoji, info.get("name"), top3.get(i)));
			}
		}
		
		if (sb.length() == 0)
		{
			sb.append(String.format("There are no results for server %s", serverNo));
		}
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(String.format("Top 3 Results for Server %s", serverNo));
		embed.setDescription(sb.toString());
		
		return embed.build();
	}
}
