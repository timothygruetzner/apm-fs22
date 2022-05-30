package ch.fhnw.apm.keyvalstore.storage;

import org.springframework.cache.Cache;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedStorage implements Storage {

    private final int cacheSize;
    private final Storage storageToBeCached;

    private final Map<Integer, CacheEntry> localCache;

    public CachedStorage(int cacheSize, Storage storageToBeCached) {
        this.cacheSize = cacheSize;
        this.storageToBeCached = storageToBeCached;

        this.localCache = new ConcurrentHashMap<>(cacheSize);
    }

    @Override
    public boolean store(int key, String value) {
        storageToBeCached.store(key, value);

        if(this.localCache.size() > cacheSize) {
            cacheReplace(key, value);
        } else {
            this.localCache.put(key, new CacheEntry(System.currentTimeMillis(), value));
        }
        return true;
    }

    private void cacheReplace(int key, String value) {
        int toBeReplaced = this.localCache.entrySet().stream().min(Comparator.comparingLong(e -> e.getValue().lastUsed())).orElseThrow().getKey();
        this.localCache.remove(toBeReplaced);
        this.localCache.put(key, new CacheEntry(System.currentTimeMillis(), value));
    }

    @Override
    public String load(int key) {
        CacheEntry entry = this.localCache.get(key);
        if(entry == null) {
            entry = new CacheEntry(System.currentTimeMillis(), this.storageToBeCached.load(key));
            this.localCache.put(key, entry);
        } else {
            this.localCache.replace(key, new CacheEntry(System.currentTimeMillis(), entry.value()));
        }

        return entry.value();
    }
    private record CacheEntry(long lastUsed, String value) {}


}
