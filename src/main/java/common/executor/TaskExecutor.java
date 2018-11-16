package common.executor;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import javax.annotation.Nullable;
import java.util.concurrent.Executors;

public class TaskExecutor {

  private static final int MAX_THREAD_COUNT = 25;
  private static final Object SINGLETON_LOCK = new Object();
  @Nullable
  private static volatile ListeningExecutorService sThreadPoolInstance = null;

  public static ListeningExecutorService main() {
    ListeningExecutorService instance = sThreadPoolInstance;
    if (instance == null) {
      synchronized (SINGLETON_LOCK) {
        instance = sThreadPoolInstance;
        if (instance == null) {
          sThreadPoolInstance = instance = MoreExecutors.listeningDecorator(
              Executors.newFixedThreadPool(MAX_THREAD_COUNT)
          );
        }
      }
    }
    return instance;
  }
}
