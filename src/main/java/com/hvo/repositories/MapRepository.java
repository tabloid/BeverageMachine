package com.hvo.repositories;

import com.hvo.exceptions.RepositoryException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MapRepository<T> implements Repository<T> {
    private Logger logger = Logger.getLogger(MapRepository.class.getName());
    private static final String ADDED_TO_REPO__MESSAGE = " is added to repository.";
    private static final String REMOVED_FROM_REPO_MESSAGE = " is removed from repository.";

    private int capacity;
    private int size = 0;
    private Map<T, Integer> map;

    public MapRepository(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>(capacity);
    }

    @Override
    public void add(T item) throws RepositoryException {
        if (size < capacity) {
            map.merge(item, 1, (v1, v2) -> v1 + v2);
            size++;
            logger.info(item + ADDED_TO_REPO__MESSAGE);
        } else {
            throw new RepositoryException("Repository capacity is exceeded.");
        }

    }

    @Override
    public void remove(T item) throws RepositoryException {
        if (size > 0) {
            int quantity = getQuantity(item);
            if (quantity > 0) {
                map.merge(item, 1, (v1, v2) -> v1 - v2);
                size--;
                logger.info(item + REMOVED_FROM_REPO_MESSAGE);
            } else {
                map.remove(item);
                throw new RepositoryException("No such item in repository.");
            }
        } else {
            throw new RepositoryException("Repository is empty.");
        }
    }

    @Override
    public int getQuantity(T item) {
        return map.get(item);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isFull() {
        return size == capacity;
    }

    @Override
    public Set<T> itemSet() {
        return map.keySet().stream().filter(k -> map.get(k) > 0).collect(Collectors.toSet());
    }
}
