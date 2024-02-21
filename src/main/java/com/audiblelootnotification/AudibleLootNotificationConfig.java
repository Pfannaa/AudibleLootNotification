package com.audiblelootnotification;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.util.ArrayList;
import java.util.List;

@ConfigGroup("AudibleLootNotification")
public interface AudibleLootNotificationConfig extends Config
{
	@ConfigItem(
			keyName = "minValue",
			name = "Minimum value",
			description = "Minimum value at which to play the sound"
	)
	default int minValue() {
		return 500000;
	}

	@ConfigItem(
			keyName = "soundVolume",
			name = "Sound volume",
			description = "Adjust the volume of the soundclip",
			position = 2
	)
	default int soundVolume() {
		return 75;
	}

	@ConfigItem(
			keyName = "soundToPlay",
			name = "Select sound",
			description = "Select a sound which shall notify you on valuable drops",
			position = 3
	)
	default SoundManager.Sound getSelectedSound()
	{
		return SoundManager.Sound.MonkeyNoise;
	}

}
