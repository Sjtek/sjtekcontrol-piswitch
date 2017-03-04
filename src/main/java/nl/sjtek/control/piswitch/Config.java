package nl.sjtek.control.piswitch;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

/**
 * Created by wouter on 3-3-17.
 */
public class Config {

    private static Config instance;
    private final String host;
    private final String username;
    private final String password;
    private final String systemCode;
    private final String rcSwitchPath;
    private final String temperatureCommand;
    private final int targets[];

    private Config(String host, String username, String password, String systemCode, String rcSwitchPath, String temperatureCommand, int[] targets) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.systemCode = systemCode;
        this.rcSwitchPath = rcSwitchPath;
        this.temperatureCommand = temperatureCommand;
        this.targets = targets;
    }

    public static synchronized Config get() {
        return instance;
    }

    public static synchronized void init(String path) throws IOException {
        String json = Files.toString(new File(path), Charsets.UTF_8);
        instance = new Gson().fromJson(json, Config.class);
        if (instance == null) throw new NullPointerException("Error while reading the config");
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public int[] getTargets() {
        return targets;
    }

    public boolean hasTarget(int target) {
        for (int includedTarget : targets) {
            if (includedTarget == target) return true;
        }
        return false;
    }

    public String getRcSwitchPath() {
        return rcSwitchPath;
    }

    public String getTemperatureCommand() {
        return temperatureCommand;
    }
}
