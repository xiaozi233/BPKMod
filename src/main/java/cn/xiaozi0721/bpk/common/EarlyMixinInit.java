package cn.xiaozi0721.bpk.common;

import cn.xiaozi0721.bpk.Tags;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.libraries.ModList;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name(Tags.MOD_ID)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class EarlyMixinInit implements IFMLLoadingPlugin, IEarlyMixinLoader {
    //这个才是真正的unused(
    public static final boolean isClient = FMLLaunchHandler.side() == Side.CLIENT;
    public static boolean hasAquaAcrobatics = false;
    //与 ILateMixinLoader 同理 只不过这里给出的config文件是用来描述mixin原版或者forge类的
    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.bpk.json");
    }

    //下方 IFMLLoadingPlugin和IEarlyMixinLoader给出了大量你需要实现的方法 但是如果你只需要mixin通常让这些方法留空就行
    //具体使用请参考LoliASM
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        List<ITweaker> coremodList = (List<ITweaker>) data.get("coremodList");
        for (ITweaker coremod : coremodList) {
            if (coremod.toString().contains("Aqua Acrobatics Transformer")){
                hasAquaAcrobatics = true;
                break;
            }
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}