package com.wurmcraft.serveressentials.common.data;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;

public class DataLoader {

    public enum DataType {
        ACTION("api/action", "CRUD"),
        AUTORANK("api/autorank", "ID/PATCH"),
        BAN("api/ban", "ID/CRUD"),
        CURRENCY("api/currency", "ID/CRUD"),
        DONATOR("api/donator", "CRUD"),
        LOG_ENTRY("api/logging", "CRUD"),
        MARKET("api/market", "CRUD"),
        RANK("api/rank", "NAME/PATCH"),
        TRANSFER("api/transfer", "ID/PATCH"),
        ACCOUNT("api/user", "ID/PATCH");

        String path;
        String pathType;

        DataType(String path, String pathType) {
            this.path = path;
            this.pathType = pathType;
        }
    }

    private static NonBlockingHashMap<DataType, NonBlockingHashMap<String, Object[]>> storage = new NonBlockingHashMap<>();

    /**
     * Finds, caches and returns the requested data instance based on its key
     *
     * @param type type of data to look for
     * @param key  id of the data you are looking for
     */
    @Nullable
    public static Object get(DataType type, String key) {
        if (storage.containsKey(type)) {
            NonBlockingHashMap<String, Object[]> storedData = storage.get(type);
            if (storedData.containsKey(key)) {
                Object[] data = storedData.get(key);
                long timestamp = (long) data[0];
                Object foundData = data[1];
                // Check for data timeout
                if (timestamp > System.currentTimeMillis())
                    return foundData;
                else {
                    storage.get(type).remove(key);
                    return get(type, key);
                }
            } else {
                Object fetch = fetch(type, key);
                if (fetch != null) {
                    storage.get(type).put(key, new Object[]{System.currentTimeMillis() + getTimeout(type), fetch});
                    return fetch;
                }
            }
        } else {
            Object fetch = fetch(type, key);
            if (fetch != null) {
                NonBlockingHashMap<String, Object[]> newStorage = new NonBlockingHashMap<>();
                newStorage.put(key, new Object[]{System.currentTimeMillis() + getTimeout(type), fetch});
                storage.put(type, newStorage);
                return fetch;
            }
        }
        return null;
    }

    /**
     * Finds, caches and returns the requested data instance based on its key, automatically cast to the requested type
     *
     * @param type type of data you are looking for
     * @param key  key / id of the data  you are looking for
     * @param data instance to force cast the return into
     */
    @Nullable
    public static <T extends Object> T get(DataType type, String key, T data) {
        Object t = get(type, key);
        if (t != null)
            return (T) t;
        return null;
    }

    /**
     * Fetch / pull the requested data for the provided type and key / id
     *
     * @param type type of the data to look for
     * @param key  key / id of the object to look for
     */
    // TODO Implement
    @Nullable
    private static Object fetch(DataType type, String key) {
        return null;
    }

    /**
     * How many seconds to keep this type of data within storage before pulling it again
     *
     * @param type of data stored
     */
    // TODO Config
    private static long getTimeout(DataType type) {
        return 5 * 60 * 1000;    // 5m
    }
}
