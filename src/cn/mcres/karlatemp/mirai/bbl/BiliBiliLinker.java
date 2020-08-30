/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/30 18:40:09
 *
 * MiraiPlugins/BiliBiliLinker/BiliBiliLinker.java
 */

package cn.mcres.karlatemp.mirai.bbl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.google.gson.*;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.event.internal.EventInternalJvmKt;
import net.mamoe.mirai.message.ContactMessage;
import net.mamoe.mirai.message.FriendMessage;
import net.mamoe.mirai.message.GroupMessage;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.RichMessage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BiliBiliLinker extends PluginBase {
    public static final Collection<Predicate<ContactMessage>> testers = new LinkedList<>();
    //匹配字符串(av和BV号)
    //匹配网址
    private static final Pattern p1 = Pattern.compile(
            "^https://(www\\.|)bilibili\\.com/video/([A-Za-z0-9]+)(/.*|\\?.*|)$"
    );
    private static final Pattern p2 = Pattern.compile(
            "^https://b23\\.tv/([0-9A-Za-z]+)(/.*|\\?.*|)$"
    );
    /*
    private static final Pattern p3 = Pattern.compile(
    		// https://www.bilibili.com/video/av62580762?p=3
    		"^AV([0-9]+)(/.*|\\?.*|)$"
    );
    */
    //匹配视频编号
    private static final Pattern p4 = Pattern.compile(
    		// https://www.bilibili.com/video/av62580762?p=3
    		"^av([0-9]+)(/.*|\\?.*|)$"
    );
    private static final Pattern p5 = Pattern.compile(
    		// https://www.bilibili.com/video/av62580762?p=3
    		"^BV([0-9A-Za-z]+)(/.*|\\?.*|)$"
    );
    /*
    private static final Pattern p6 = Pattern.compile(
    		// https://www.bilibili.com/video/av62580762?p=3
    		"^bv([[0-9A-Za-z]+)(/.*|\\?.*|)$"
    );
    */
    static BiliBiliLinker INSTANCE;
    static File tempFolder;


    public BiliBiliLinker() {
        INSTANCE = this;
    }

    public static boolean allow(ContactMessage packet) {
        synchronized (testers) {
            for (Predicate<ContactMessage> p : testers) {
                if (!p.test(packet)) return false;
            }
        }
        return true;
    }

    public static void callback(ByteArrayOutputStream stream, ContactMessage event) throws Throwable {
        final JsonObject bin = JsonParser
                .parseString(stream.toString("utf8")).getAsJsonObject();
        switch (bin.get("code").getAsInt()) {
            case 0: {
                final JsonObject data = bin.get("data").getAsJsonObject();
                String title = data.get("title").getAsString(),
                        image = data.get("pic").getAsString() + "@448w_252h_1c_100q.jpg";
                MessageChainBuilder builder = new MessageChainBuilder();
                File file = AsyncHttp.download(String.valueOf(data.get("bvid").getAsString()), image);
                if (file == null) builder.add("无法获取图片(下载错误).\n\n");
                else {
                    try {
                        builder.add(event.getSubject().uploadImage(file));
                    } catch (Throwable ignore) {
                        builder.add("上传图片失败，请等下再试\n");
                    }
                }
                builder.add(title + "\n");
                String desc;
                {
                    final JsonElement element = data.get("desc");
                    if (element == null) desc = "";
                    else desc = element.getAsString().trim();
                    if (desc.length() > 15) {
                        desc = desc.substring(0, 15) + "...";
                    }
                    if (desc.isEmpty()) desc = title;
                }
                //返回的json消息内容
                final JsonObject stat = data.get("stat").getAsJsonObject();
                //Up主
                builder.add("Up " + data.get("owner").getAsJsonObject().get("name").getAsString() + "\n");
                
                //AV号和BV号
                builder.add("av" + data.get("aid") + "\n");
                builder.add(data.get("bvid").getAsString() + "\n");
                /*
                builder.add("Aid " + data.get("aid") + "\n");
                builder.add("Bid " + data.get("bvid").getAsString() + "\n");
                */
                
                builder.add("播放 " + stat.get("view") + "\n");
                builder.add("弹幕 " + stat.get("danmaku") + "\n");
                builder.add("评论 " + stat.get("reply") + "\n");
                
                builder.add("点赞 " + stat.get("like") + " 硬币 " + stat.get("coin") +" 收藏 " + stat.get("favorite") + "\n");
                /*
                builder.add("点赞 " + stat.get("like") + "\n");
                builder.add("硬币 " + stat.get("coin") + "\n");
                builder.add("收藏 " + stat.get("favorite") + "\n");
                */
               
                //builder.add("分享>> " + stat.get("share") + "\n");
               // builder.add("不喜欢>> " + stat.get("dislike"));
                builder.add("基于 BilibiliLinker by Karlatemp \n" 
                		+ "二次开发 By 晓空" + " Based on Mirai 强力驱动");
                event.getSubject().sendMessageAsync(builder.asMessageChain());
                event.getSubject().sendMessageAsync("https://www.bilibili.com/video/" + data.get("bvid").getAsString());
                //禁用富文本发送链接
                /*
                event.getSubject().sendMessageAsync(
                        RichMessage.Templates.share("https://www.bilibili.com/video/" + data.get("bvid").getAsString(), title, desc, image)
                );
                */
                break;
            }
            default:
                event.getSubject().sendMessageAsync("BiliBili > " + "发生了一个意外的错误，请稍后再重试！\n" 
                		+ bin.get("message").getAsString());
        }
    }

    public static String make_check(String id) {
        if (id.startsWith("BV")) {
            return "https://api.bilibili.com/x/web-interface/view?bvid=" + id;
        }
        if (id.startsWith("av")) {
            return "https://api.bilibili.com/x/web-interface/view?aid=" + id.substring(2);
        }
        return "https://api.bilibili.com/x/web-interface/view?aid=" + id;
    }

    private void handle(ContactMessage event) {
        if (!allow(event)) return;
        final String match = event.getMessage().contentToString();
        {
        	//匹配的字符串 p1 p2 p4 p5
            final Matcher matcher = p1.matcher(match);
            if (matcher.find()) {
                AsyncHttp.run(make_check(matcher.group(2)), stream -> callback(stream, event));
            }
        }
        {
            final Matcher matcher = p2.matcher(match);
            if (matcher.find()) {
                AsyncHttp.run(make_check(matcher.group(1)), stream -> callback(stream, event));
            }
        }
        {
            final Matcher matcher = p4.matcher(match);
            if (matcher.find()) {
                AsyncHttp.run(make_check(matcher.group(1)), stream -> callback(stream, event));
            }
        }
        {
            final Matcher matcher = p5.matcher(match);
            if (matcher.find()) {
            	//event.getSubject().sendMessageAsync("");
                AsyncHttp.run(make_check(matcher.group(0)), stream -> callback(stream, event));
            }
        }
    }

    @SuppressWarnings("KotlinInternalInJava")
    @Override
    public void onEnable() {
        tempFolder = new File(getDataFolder(), "temp");
        ConfigurationLoader.reload();
        EventInternalJvmKt._subscribeEventForJaptOnly(GroupMessage.class, this, this::handle);
        EventInternalJvmKt._subscribeEventForJaptOnly(FriendMessage.class, this, this::handle);
    }

    @Override
    public void onDisable() {
        AsyncHttp.service.shutdownNow();
    }
}
