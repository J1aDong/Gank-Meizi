package com.j1adong.meizi;

/**
 * Created by J1aDong on 16/7/21.
 */
public class HttpResult<T> {
    private int count;
    private boolean error;
    private T results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}
