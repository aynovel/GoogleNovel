package life.forever.cf.interfaces;

import java.io.File;
import java.lang.reflect.Type;


public abstract class Cache {

    private File mCacheDir;

    private int mCacheAmount = 3;

    public Cache(File cacheDir) {
        checkIsDirectory(cacheDir);
        this.mCacheDir = cacheDir;
    }

    private void checkIsDirectory(File cacheDir) {
        if (!cacheDir.isDirectory()) {
            throw new IllegalArgumentException("cacheDir must be a directory!");
        }
    }

    public abstract void put(String key, String value);

    public abstract <T> void put(String key, T t);

    public abstract String get(String key);

    public abstract <T> T get(String key, Type clazz);

    public abstract boolean remove(String key);

    public abstract boolean removeAll();

    /**
     * 指定key是否已经有缓存
     *
     * @param key 缓存key
     * @return 已经有缓存返回true，否则返回false
     */
    public abstract boolean isCached(String key);

    public File getCacheDir() {
        return mCacheDir;
    }

    public void setCacheDir(File cacheDir) {
        checkIsDirectory(cacheDir);
        this.mCacheDir = cacheDir;
    }

    public int getCacheAmount() {
        return mCacheAmount;
    }

    public void setCacheAmount(int cacheAmount) {
        mCacheAmount = cacheAmount;
    }
}
