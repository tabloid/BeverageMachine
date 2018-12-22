package com.hvo.repositories;

import com.hvo.exceptions.RepositoryException;

import java.util.Set;

public interface Repository<T> {
    public void add(T item) throws RepositoryException;

    public void remove(T item) throws RepositoryException;

    public int getQuantity(T item);

    public int size();

    public boolean isFull();

    public Set<T> itemSet();

}
