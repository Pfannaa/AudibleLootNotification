package com.audiblelootnotification;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j

@Singleton
public class SoundManager
{
    @Inject
    private AudibleLootNotificationConfig config;

    public enum Sound {
        AcbSpec("/AcbSpec.wav"),
        CashRegister("/CashRegister.wav"),
        CatMeow("/CatMeow.wav"),
        Dolphin("/Dolphin.wav"),
        GuySayingMoney("/GuySayingMoney.wav"),
        HeyYouJustGotMoney("/HeyYouJustGotMoney.wav"),
        MinecraftVillager("/MinecraftVillager.wav"),
        MonkeyNoise("/MonkeyNoise.wav"),
        PigGrunt("/PigGrunt.wav"),
        SoMuchMoneyAutotune("/SoMuchMoneyAutotune.wav");

        private final String fileName;
        Sound(String filename) {
            fileName = filename;
        }

        String getFileName() {
            return fileName;
        }
    }

    private Clip clip = null;
    private static final long CLIP_MTIME_UNLOADED = -2;
    private long lastClipMTime = CLIP_MTIME_UNLOADED;

    // Source: c-engineer-completed plugin
    // https://github.com/m0bilebtw/c-engineer-completed
    private boolean loadClip(Sound sound) {
        try (InputStream s = getClass().getResourceAsStream(sound.getFileName());
             InputStream bufferedIn = new BufferedInputStream(s);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn)){
            clip.open(audioStream);
            return true;
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | NullPointerException e) {
            log.warn("Failed to load sound " + sound, e);
        }
        return false;
    }

    // Source: c-engineer-completed plugin
    // https://github.com/m0bilebtw/c-engineer-completed
    public void playClip(Sound sound) {
        long currentMTime = System.currentTimeMillis();

        if (clip == null || currentMTime != lastClipMTime || !clip.isOpen()) {
            if (clip != null && clip.isOpen()) {
                clip.close();
            }

            try {
                clip = AudioSystem.getClip();
            } catch (LineUnavailableException e) {
                lastClipMTime = CLIP_MTIME_UNLOADED;
                log.warn("Failed to get clip for sound " + sound, e);
                return;
            }

            lastClipMTime = currentMTime;
            if (!loadClip(sound)) {
                return;
            }
        }

        // User configurable volume
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float gain = 20f * (float) Math.log10(config.soundVolume() / 100f);
        gain = Math.min(gain, volume.getMaximum());
        gain = Math.max(gain, volume.getMinimum());
        volume.setValue(gain);

        clip.loop(0);
    }
}
