package cn.xiaozi0721.bpk.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "bpk")
public class BPKConfig implements ConfigData {
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean sprintBackward = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public double inertiaThreshold = 0.0001D;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean isBESneak = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean strafeAccelerateAllowed = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean ignoreCollidedHorizontally = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean oldCollisionOrder = true;
}
