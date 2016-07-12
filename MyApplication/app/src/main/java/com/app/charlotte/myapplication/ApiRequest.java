package com.app.charlotte.myapplication;

/**
 * Created by charlotte on 05.07.16.
 */
public class ApiRequest<T> {
    public ApiRequest(Class<T> clazz) {
        this.clazz = clazz;
    }

    Class<T> clazz;
    public boolean isHasToFetch() {
        return hasToFetch;
    }

    public void setHasToFetch(boolean hasToFetch) {
        this.hasToFetch = hasToFetch;
    }

    public boolean isHasFetched() {
        return hasFetched;
    }

    public void setHasFetched(boolean hasFetched) {
        this.hasFetched = hasFetched;
    }

    public Class returnType()
    {
        return  clazz;
    }


    boolean hasToFetch, hasFetched;
}
