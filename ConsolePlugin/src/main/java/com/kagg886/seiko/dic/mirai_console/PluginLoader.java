package com.kagg886.seiko.dic.mirai_console;

import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.bridge.ErrorListener;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.session.impl.FriendMessageRuntime;
import com.kagg886.seiko.dic.session.impl.GroupMessageRuntime;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.dic.mirai_console
 * @className: PluginLoader
 * @author: kagg886
 * @description: 插件主类
 * @date: 2023/1/27 17:38
 * @version: 1.0
 */
public class PluginLoader extends JavaPlugin implements ErrorListener {

    public static final PluginLoader INSTANCE = new PluginLoader();

    private PluginLoader() {
        super(new JvmPluginDescriptionBuilder("com.kagg886.seiko.dic.mirai_console", "0.0.1")
                .name("Seiko Dictionary Plugin")
                .info("可以在PC上运行伪代码的插件\n项目地址:https://github.com/kagg886/Seiko")
                .author("kagg886 and All Seiko Contributors")
                .build());
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage api) {
        super.onLoad(api);
        File tmp = getConfigFolderPath().resolve("dic").toFile();
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        DictionaryEnvironment.getInstance().setDicRoot(tmp);
        tmp = getConfigFolderPath().resolve("dicData").toFile();
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        DictionaryEnvironment.getInstance().setErrorListener(this);
        DictionaryEnvironment.getInstance().setDicConfigPoint(getConfigFolderPath().resolve("dicList.json").toFile().getAbsolutePath());
        DictionaryEnvironment.getInstance().setDicData(tmp.toPath());
        getLogger().info("Environment初始化成功!");
    }

    @Override
    public void onEnable() {
        CommandManager.INSTANCE.registerCommand(CommandInstance.INSTANCE, false);
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            JSONObject dicConfigUnit;
            for (DictionaryFile dic : DICList.getInstance()) {
                dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().optJSONObject(dic.getName(), new JSONObject());
                if (dicConfigUnit.optBoolean("enabled", true)) {
                    GroupMessageRuntime runtime = new GroupMessageRuntime(dic, event);
                    runtime.invoke(event.getMessage().contentToString());
                }
            }
        });

        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            JSONObject dicConfigUnit;
            for (DictionaryFile dic : DICList.getInstance()) {
                if ((dicConfigUnit = DictionaryEnvironment.getInstance().getDicConfig().optJSONObject(dic.getName(), new JSONObject())) != null) {
                    if (dicConfigUnit.optBoolean("enabled", true)) {
                        FriendMessageRuntime runtime = new FriendMessageRuntime(dic, event);
                        runtime.invoke(event.getMessage().contentToString());
                    }
                }
            }
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onError(File p, Throwable e) {
        getLogger().error(p.getName() + "加载失败!", e);
    }
}
