package com.poker.reader.cache;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Cache<ID, T> {
    private static final int MAX_SIZE = 10000;
    private final Map<ID, T> cacheMap = new HashMap<>();

    public boolean contains(ID id) {
        return cacheMap.containsKey(id);
    }

    public T get(ID id) {
        return cacheMap.get(id);
    }

    public T put(ID id, T tournament) {
        //free space
        if(cacheMap.size() == MAX_SIZE) {
            List<ID> keyList = new ArrayList<>(cacheMap.keySet());
            Collections.shuffle(keyList);
            cacheMap.remove(keyList.get(0));
        }
        return cacheMap.put(id, tournament);
    }
}
