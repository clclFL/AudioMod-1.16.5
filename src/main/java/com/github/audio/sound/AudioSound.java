package com.github.audio.sound;

import com.github.audio.util.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/* sound with complete information */
public class AudioSound {

    protected static final DeferredRegister<SoundEvent> SOUND_REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Utils.MOD_ID);

    /* Default means song but not normal sound event. */
    protected static final long DEF_DURATION = 50;
    /* Default means the song's name has not been reinitialized yet. */
    static final String NON_NAMED = "non_named";
    /* Return the unique registryID for each auto-registry AudioSound. */
    private static int customRegistryID = 0;
    private static int registryID = 0;

    private final int id;
    private long duration;
    private String signedName;
    private String registryName;
    private final Supplier<SoundEvent> soundEvent;

    private AudioSound(int id, String registryName, String signedName,
                       Supplier<SoundEvent> soundEvent, long duration) {
        this.id = id;
        this.registryName = registryName;
        this.signedName = signedName;
        this.soundEvent = soundEvent;
        this.duration = duration;
    }

    /**
     * the song registry name should be the lower case and make sure they have been divided into part
     * by underscore such as "breath_of_a_serpent".
     */

    public String getRegistryName() {
        return registryName;
    }

    public String getSignedName() {
        return signedName;
    }

    public long getDuration() {
        return duration;
    }

    public SoundEvent getSoundEvent() {
        return soundEvent.get();
    }

    public String getDisplayName() {
        //TODO : implement the logic how signedName translated into DisplayName here.
        return null;
    }

    public int getID() {
        return this.id;
    }

    protected AudioSound into(final SoundChannel channel) {
        channel.getList().add(this);
        return this;
    }

    public static class AudioSoundBuilder{
        private long duration;
        private String registryName;
        private String displayName;
        private Supplier<SoundEvent> soundEvent;

        public AudioSoundBuilder() {
            this.init();
        }

        public AudioSoundBuilder tag(String registryName, String displayName) {
            this.registryName = registryName;
            this.displayName = displayName;
            return this;
        }

        /**
         *  This method should not be used to create song, as duration should be defined in the inner part, the method
         *  to get duration could be using AudioAPI method to read .ogg file or defining with *DEF_DURATION*.
         */
        public AudioSoundBuilder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public AudioSoundBuilder soundEvent(Supplier<SoundEvent> soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

        public AudioSoundBuilder display(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public AudioSound build() {

            if (registryName.equals(NON_NAMED)) {
                registryName = getCustomSoundRegistryID();
            }

            //TODO : gain the exact duration and implement here.
            if (duration == -1) {
                duration = DEF_DURATION;
            }

            if (soundEvent == null) {
                soundEvent = SOUND_REGISTER.register(registryName, () -> new SoundEvent(new ResourceLocation(Utils.MOD_ID, registryName)));
            }
            return new AudioSound(getRegistryID(), registryName, displayName, soundEvent, duration);
        }

        public void init() {
            duration = -1;
            registryName = NON_NAMED;
            displayName = NON_NAMED;
            soundEvent = null;
        }

        /**
         * get the registryID from steady static field.
         *
         * @return registry: this should be the unique mark to differ different auto-registry sound event.
         */
        private static String getCustomSoundRegistryID() {
            return "custom_" + (++customRegistryID);
        }

        private static int getRegistryID() {
            return ++registryID;
        }
    }

}


