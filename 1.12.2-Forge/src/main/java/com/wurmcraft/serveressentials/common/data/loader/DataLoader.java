package com.wurmcraft.serveressentials.common.data.loader;

import com.wurmcraft.serveressentials.api.models.*;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

public class DataLoader implements IDataLoader {

    public enum DataType {
        ACTION("api/action", "relatedID;host;action;timestamp", null, Action.class, false),
        AUTORANK("api/autorank", "CRUD/PATCH", "autoRankID", AutoRank.class, true),
        BAN("api/ban", "CRUD", "banID", Ban.class, true),
        CURRENCY("api/currency", "CRUD", "currencyID", Currency.class, true),
        DONATOR("api/donator", "CRUD", "uuid", Donator.class, true),
        LOG_ENTRY("api/logging", "serverID;action;x;y;z;dim", null, LogEntry.class, false),
        MARKET("api/market", "CRUD", null, MarketEntry.class, false),
        RANK("api/rank", "CRUD/PATCH", "rankID", Rank.class, true),
        TRANSFER("api/transfer", "CRUD/PATCH", "transferID", TransferEntry.class, false),
        ACCOUNT("api/user", "CRUD/PATCH", "uuid", Account.class, true),
        LOCAL_ACCOUNT(null,null,"uuid", LocalAccount.class, true),
        LANGUAGE(null,null,"langKey", Language.class, true),
        CHANNEL(null,null,"name", Channel.class, true);

        String path;
        String pathType;
        Class<?> instanceType;
        boolean fileCache;
        String key;

        DataType(String path, String pathType, String key, Class<?> instanceType, boolean fileCache) {
            this.path = path;
            this.pathType = pathType;
            this.key = key;
            this.instanceType = instanceType;
            this.fileCache = fileCache;
        }
    }

    // key, [timestamp, data]
    protected final NonBlockingHashMap<DataType, NonBlockingHashMap<String, Object[]>> storage = new NonBlockingHashMap<>();

    /**
     * Finds the data within the cache and returns if possible, null if key does not exist, empty if the storage is empty or has expired
     *
     * @param key  type of data you are looking for
     * @param type cast the data to this type
     */
    @Override
    public <T> NonBlockingHashMap<String, T> getFromKey(DataType key, T type) {
        if (storage.containsKey(key)) {
            NonBlockingHashMap<String, Object[]> cachedData = storage.get(key);
            NonBlockingHashMap<String, T> data = new NonBlockingHashMap<>();
            for (String k : cachedData.keySet()) {
                Object[] d = cachedData.get(k);
                if (((long) d[0]) > System.currentTimeMillis())
                    data.put(k, (T) d[1]);
                else
                    storage.get(key).remove(k);
            }
            return data;
        }
        return null;
    }

    /**
     * Finds, caches and returns the requested data instance based on its key
     *
     * @param type type of data to look for
     * @param key  id of the data you are looking for
     */
    @Nullable
    public Object get(DataType type, String key) {
        if (storage.containsKey(type)) {
            NonBlockingHashMap<String, Object[]> storedData = storage.get(type);
            if (storedData.containsKey(key)) {
                Object[] data = storedData.get(key);
                long timestamp = (long) data[0];
                Object foundData = data[1];
                // Check for data timeout
                if (timestamp > System.currentTimeMillis())
                    return foundData;
                else
                    storage.get(type).remove(key);
            }
        }
        return null;
    }

    /**
     * Finds, caches and returns the requested data instance based on its key, casts the output based on the provided instance
     *
     * @param type type / category of this entry
     * @param key  key / ID of the entry
     * @param data instance of the data to cast the object into
     */
    @Override
    public <T> T get(DataType type, String key, T data) {
        try {
            return (T) get(type, key);
        } catch (Exception e) {
            LOG.error("Failed to get (cast) data '" + type.name() + "' @ '" + key + "' (" + e.getMessage() + ")");
        }
        return null;
    }

    /**
     * Adds the requested instance to the cache, if one does not exist
     *
     * @param type type / category of this entry
     * @param key  key / ID of the entry
     * @param data instance you want to be created / "registered"
     */
    @Override
    public boolean register(DataType type, String key, Object data) {
        if (storage.containsKey(type)) {
            if (!storage.get(type).containsKey(key)) {
                storage.get(type).put(key, new Object[]{System.currentTimeMillis(), data});
                LOG.trace("Entry on '" + type + "' has been cached with key '" + key + "'");
                return true;
            } else {
                LOG.warn("Tried to register a entry that already exists on '" + type + "' for key '" + key + "'");
                return false;
            }
        } else {
            NonBlockingHashMap<String, Object[]> newDataStorage = new NonBlockingHashMap<>();
            newDataStorage.put(key, new Object[]{System.currentTimeMillis(), data});
            storage.put(type, newDataStorage);
            LOG.debug("Creating new cached storage for '" + type.name() + "' with key '" + key + "'");
            return true;
        }
    }

    /**
     * Updates an existing entry in the cache
     *
     * @param type type / category of this entry
     * @param key  key / ID of the entry
     * @param data instance you want to be updated
     */
    @Override
    public boolean update(DataType type, String key, Object data) {
        if (storage.containsKey(type)) {
            if (storage.get(type).containsKey(key)) {
                storage.get(type).put(key, new Object[]{System.currentTimeMillis(), data});
                LOG.trace("Entry on '" + type + "' has been cached (update) with key ' " + key + "'");
                return true;
            } else {
                LOG.warn("Tried to update a entry that does not exist on '" + type + "' with key '" + key + "'");
                return false;
            }
        }
        return false;
    }

    /**
     * Removes the requested key from cache, cache-only is not used in this mode
     *
     * @param type      type / category of the entry
     * @param key       key / ID of the entry
     * @param cacheOnly remove from cache-only for 'false' for a full delete
     */
    @Override
    public boolean delete(DataType type, String key, boolean cacheOnly) {
        if (storage.containsKey(type) && storage.get(type).containsKey(key)) {
            storage.get(type).remove(key);
            LOG.trace("Entry on '" + type + "' has been removed with key '" + key + "'");
            return true;
        }
        return false;
    }

    /**
     * Removes the requested key from cache, cache-only is not used in this mode
     *
     * @param type type / category of the entry
     * @param key  key / ID of the entry
     * @see #delete(DataType, String, boolean)
     */
    @Override
    public boolean delete(DataType type, String key) {
        return delete(type, key, true);
    }

    /**
     * How many seconds to keep this type of data within storage before pulling it again
     *
     * @param type of data stored
     */
    // TODO Config
    protected long getTimeout(DataType type) {
        return 5 * 60 * 1000;    // 5m
    }
}
