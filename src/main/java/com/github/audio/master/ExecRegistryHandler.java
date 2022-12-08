package com.github.audio.master;

import com.github.audio.Audio;
import com.github.audio.Env;
import com.github.audio.api.annotation.Exec;
import com.github.audio.master.client.ClientExecutor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.util.*;

import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class ExecRegistryHandler {

    /* this path is for run client test. */
    private static final File CLIENT_FILE = new File("../src/main/java/com/github/audio/master/client/exec");
    private static final File SERVER_FILE = new File("../src/main/java/com/github/audio/master/exec");
    /* this path work properly ISwitchable think, probably the mod name */
    //TODO : you guys should figure out what if a player change the mod display name, in that case this path will not exist.
    private static final String CLIENT_JAR_PATH = "./mods/AudioMod-1.16.5-1.0.0.jar";
    private static final String SERVER_JAR_PATH = "./mods/AudioMod-1.16.5-1.0.0.jar";

    /* the default folder for store the audio executor, if the executor class is defined out of this folder,
     * then the method will not detect its existence, then it will not be added in to the event but. */
    private static final String CLIENT_PRE = "com.github.audio.master.client.exec";
    private static final String SERVER_PRE = "com.github.audio.master.exec";
    /* this path work in game environment. */
    private static final String CLIENT_JAR_PRE = "com/github/audio/master/client/exec/";
    private static final String SERVER_JAR_PRE = "com/github/audio/master/exec/";

    private static final boolean isServer = Env.isServer();
    private static final boolean isOnTest = Env.isOnTest();

    public static void registryExecutor(IEventBus eventBus) {
        try {
            Audio.info("Start registry Server Executor");
            getClientTarget(Type.SERVER).forEach(eventBus::register);

            if (isServer) return;
            Audio.info("Start registry Client Executor");
            getClientTarget(Type.CLIENT).forEach(eventBus::register);
        } catch (ClassNotFoundException | IOException | InvocationTargetException
                 | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Executor> getClientTarget(Enum<Type> side) throws ClassNotFoundException, IOException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        boolean flag = side.equals(Type.SERVER);
        Set<Class<?>> clientClass = isOnTest ? getFileClasses(flag ? Type.SERVER : Type.CLIENT) : getJarClasses(flag ? Type.SERVER : Type.CLIENT);
        Set<Class<?>> clazz2 = clientClass.stream().filter(clz -> clz.isAnnotationPresent(Exec.class)
                && clz.getAnnotation(Exec.class).value().equals(flag ? Dist.DEDICATED_SERVER : Dist.CLIENT)).collect(Collectors.toSet());

        ArrayList<Executor> addList = new ArrayList<>();
        for (Class<?> c : clazz2) {
            addList.add((Executor) c.getDeclaredMethod("getExecutor").invoke(null));
            Audio.info("successfully add " + c.getName() + " into eventbus");
        }
        return addList;
    }

    private static final Function<String, Class<?>> getClassProcess = s -> {
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("fail in loading executor");
    };

    private static Set<Class<?>> getFileClasses(Enum<Type> side) {
        boolean flag = side.equals(Type.SERVER);
        String pre = (flag ? SERVER_PRE : CLIENT_PRE);
        File[] targets = Objects.requireNonNull(flag ? SERVER_FILE.listFiles() : CLIENT_FILE.listFiles());
        return Arrays.stream(targets).filter(file -> !file.isDirectory())
                .map(file -> pre + "." + file.getName().substring(0, file.getName().length() - 5))
                .map(getClassProcess).collect(Collectors.toSet());
    }

    @SuppressWarnings("resource")
    private static Set<Class<?>> getJarClasses(Enum<Type> side) throws IOException {
        try {
            boolean flag = side.equals(Type.SERVER);
            Enumeration<JarEntry> entries = new JarFile(flag ? SERVER_JAR_PATH : CLIENT_JAR_PATH).entries();
            ArrayList<JarEntry> entryList = new ArrayList<>();
            while (entries.hasMoreElements()) entryList.add(entries.nextElement());
            return entryList.stream().map(ZipEntry::getName)
                    .filter(name -> name.startsWith(flag ? SERVER_JAR_PRE : CLIENT_JAR_PRE)
                            && name.endsWith(".class") && !name.contains("$"))
                    .map(s -> s.replace('/', '.').substring(0, s.length() - 6))
                    .map(getClassProcess).collect(Collectors.toSet());
        } catch (FileNotFoundException e) {
            if (isOnTest) return new HashSet<>();
            else Audio.warn("Cannot find the file through Jar path");
        }
        return new HashSet<>();
    }

    private enum Type {
        SERVER, CLIENT;
    }
}