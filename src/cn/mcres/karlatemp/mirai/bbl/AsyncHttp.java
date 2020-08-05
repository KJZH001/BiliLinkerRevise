/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/30 18:46:03
 *
 * MiraiPlugins/BiliBiliLinker/AsyncHttp.java
 */

package cn.mcres.karlatemp.mirai.bbl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class AsyncHttp {
    public static final ThreadGroup group = new ThreadGroup("BiliBili Linker-Async Group");
    private static final AtomicInteger counter = new AtomicInteger();
    public static final ExecutorService service = Executors.newCachedThreadPool(task -> {
        Thread t = new Thread(group, task, "BiliBili Linker-Async Thread#" + counter.getAndIncrement());
        t.setDaemon(true);
        return t;
    });

    public static File download(String bv, String url) {
        BiliBiliLinker.tempFolder.mkdirs();
        File file = new File(BiliBiliLinker.tempFolder, bv + ".jpg");
        if (file.isFile()) {
            return file;
        }
        AtomicBoolean finished = new AtomicBoolean(false);
        runSync(url, stream -> {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                stream.writeTo(fos);
            }
            finished.set(true);
        }, null);
        return finished.get() ? file : null;
    }

    public static void runSync(String url, CallbackAction<ByteArrayOutputStream> action, Consumer<Throwable> onCatch) {
        try {
            URL url0 = new URL(url);
            final HttpURLConnection connection = (HttpURLConnection) url0.openConnection();
            connection.connect();
            try (final InputStream stream = connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream()) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
                while (true) {
                    final int read = stream.read(buffer);
                    if (read == -1) break;
                    os.write(buffer, 0, read);
                }
                action.action(os);
            }
        } catch (Throwable e) {
            if (onCatch != null) onCatch.accept(e);
            else BiliBiliLinker.INSTANCE.getLogger().error(e);
        }
    }

    public static void run(String url, CallbackAction<ByteArrayOutputStream> action) {
        service.execute(() -> runSync(url, action, null));
    }

    public interface CallbackAction<T> {
        void action(T val) throws Throwable;
    }
}
