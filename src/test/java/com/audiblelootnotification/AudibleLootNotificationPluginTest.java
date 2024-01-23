package com.audiblelootnotification;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AudibleLootNotificationPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AudibleLootNotificationPlugin.class);
		RuneLite.main(args);
	}
}