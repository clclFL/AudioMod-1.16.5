package com.github.audio.sound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SoundChannel {

    protected final List<AudioSound> channelSoundList;

    /* ChannelSoundList Define*/
    private static final LinkedList<AudioSound> KATANA_ZERO = new LinkedList<>();
    private static final LinkedList<AudioSound> CUSTOM_SOUND = new LinkedList<>();
    private static final ArrayList<AudioSound> MUSIC_BOX_CLEW_TONE = new ArrayList<>();

    /* Channel */
    public static final SoundChannel MUSIC_BOX_CHANNEL = new SoundChannel(MUSIC_BOX_CLEW_TONE);
    public static final SoundChannel KATANA_ZERO_CHANNEL = new SoundChannel(KATANA_ZERO);
    /**
     * @Description: This channel is for storing custom sound that registry into the game.
     */
    public static final SoundChannel CUSTOM_SOUND_CHANNEL = new SoundChannel(CUSTOM_SOUND);

    private SoundChannel(List<AudioSound> channelSoundList) {
        this.channelSoundList = channelSoundList;
    }

    public AudioSound getFromRegistryName(String registryName) {
        List<String> collect = this.channelSoundList.stream().map(AudioSound::getRegistryName).collect(Collectors.toList());
        return this.channelSoundList.get(collect.contains(registryName) ? collect.indexOf(registryName) : 0);
    }

    public void add(AudioSound... audioSounds) {
        this.channelSoundList.addAll(Arrays.asList(audioSounds));
    }

    public void delete(AudioSound audioSound) {
        this.channelSoundList.remove(audioSound);
    }

    public AudioSound get(int index) {
        return channelSoundList.get(index);
    }

    public int getSize() {
        return this.channelSoundList.size();
    }

    public boolean isEmpty() {
        return this.channelSoundList.isEmpty();
    }

    public final List<AudioSound> getList() {
        return this.channelSoundList;
    }
}
