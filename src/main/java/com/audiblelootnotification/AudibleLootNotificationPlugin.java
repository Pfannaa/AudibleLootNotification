package com.audiblelootnotification;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.http.api.loottracker.LootRecordType;

import java.util.Collection;

@Slf4j
@PluginDescriptor(
	name = "AudibleLootNotification",
	description = "Notifies you with preselected sounds once you received loot which exceeds a defined value threshold"

)
public class AudibleLootNotificationPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SoundManager soundManager;
	@Inject
	private AudibleLootNotificationConfig config;

	@Inject
	private ItemManager itemManager;

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived npcLootReceived){
		handleReceivedLoot(npcLootReceived.getItems(), npcLootReceived.getNpc().getName());
	}

	@Subscribe
	public void onPlayerLootReceived(PlayerLootReceived playerLootReceived){
		handleReceivedLoot(playerLootReceived.getItems(), playerLootReceived.getPlayer().getName());
	}

	@Subscribe
	public void onLootReceived(LootReceived lootReceived) {
		if (lootReceived.getType() != LootRecordType.EVENT && lootReceived.getType() != LootRecordType.PICKPOCKET) {
			return;
		}
		handleReceivedLoot(lootReceived.getItems(), lootReceived.getName());
	}

	private void handleReceivedLoot(Collection<ItemStack> items, String name) {
		for (ItemStack stack : items) {
			int value = itemManager.getItemPrice(stack.getId()) * stack.getQuantity();

			if (value >= config.minValue())
			{
				new Thread(() -> {
					soundManager.playClip(config.getSelectedSound());
				}).start();
				return;
			}
		}
	}

	@Provides
	AudibleLootNotificationConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AudibleLootNotificationConfig.class);
	}
}
