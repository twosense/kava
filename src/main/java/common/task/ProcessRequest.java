package common.task;

import java.util.concurrent.Callable;

public class ProcessRequest<T> {

    private String _destination;
    private String _source;
    private T _data;

    public Callable<T> getTask() {
        return _task;
    }

    public void setTask(Callable<T> _task) {
        this._task = _task;
    }

    private Callable<T> _task;

    public ProcessRequest(String src, String dst, T data) {
        _destination = dst;
        _source = src;
        _data = data;
    }

    public String getDestination() {
        return _destination;
    }

    public String getSource() { return _source; }

    public T getData() {
        return _data;
    }
}
