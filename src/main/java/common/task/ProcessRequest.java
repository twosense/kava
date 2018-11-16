package common.task;

public class ProcessRequest<T> {

    private String _destination;
    private String _source;
    private T _data;

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
