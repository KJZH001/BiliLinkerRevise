/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/30 19:22:15
 *
 * MiraiPlugins/BiliBiliLinker/ConfigurationLoader.java
 */

package cn.mcres.karlatemp.mirai.bbl;

import net.mamoe.mirai.console.plugins.YamlConfig;
import net.mamoe.mirai.message.ContactMessage;
import net.mamoe.mirai.message.FriendMessage;
import net.mamoe.mirai.message.GroupMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class ConfigurationLoader {
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static void reload() {
        final Collection<Predicate<ContactMessage>> testers = BiliBiliLinker.testers;
        synchronized (testers) {
            testers.removeIf(t -> t instanceof X);
            final BiliBiliLinker instance = BiliBiliLinker.INSTANCE;
            instance.getDataFolder().mkdirs();
            File f = new File(instance.getDataFolder(), "config.yml");
            if (!f.isFile()) {
                f.delete();
                try (InputStream is = instance.getResources("config.yml")) {
                    byte[] b = new byte[1024];
                    try (FileOutputStream fos = new FileOutputStream(f)) {
                        while (true) {
                            final int i = is.read(b);
                            if (i == -1) break;
                            fos.write(b, 0, i);
                        }
                    }
                } catch (IOException ioe) {
                    instance.getLogger().error(ioe);
                }
            }
            final YamlConfig config = new YamlConfig(f);
            final List<Long> denyGroups = config.getLongList("denyGroups");
            final List<Long> denyFriends = config.getLongList("denyFriends");
            final List<Long> onlyAllowsGroups = config.getLongList("onlyAllowsGroups");
            final List<Long> onlyAllowsFriends = config.getLongList("onlyAllowsFriends");
            testers.add((X) packet -> {
                if (packet instanceof GroupMessage)
                    return !denyGroups.contains(packet.getSubject().getId());
                return true;
            });
            testers.add((X) packet -> {
                if (packet instanceof FriendMessage)
                    return !denyFriends.contains(packet.getSubject().getId());
                return true;
            });
            if (!onlyAllowsFriends.isEmpty()) {
                testers.add((X) packet -> {
                    if (packet instanceof FriendMessage)
                        return onlyAllowsFriends.contains(packet.getSubject().getId());
                    return true;
                });
            }
            if (!onlyAllowsGroups.isEmpty()) {
                testers.add((X) packet -> {
                    if (packet instanceof GroupMessage)
                        return onlyAllowsGroups.contains(packet.getSubject().getId());
                    return true;
                });
            }
        }
    }

    private interface X extends Predicate<ContactMessage> {
    }
}
