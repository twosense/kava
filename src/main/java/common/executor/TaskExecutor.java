package common.executor;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executors;

public class TaskExecutor {

    private static final int MAX_THREAD_COUNT = 25;
    private static ListeningExecutorService THREAD_POOL = MoreExecutors.listeningDecorator(
        Executors.newFixedThreadPool(MAX_THREAD_COUNT)
    );

    public static ListeningExecutorService main() {
        return THREAD_POOL;
    }
}
