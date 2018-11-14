package common.task;

import com.google.common.reflect.TypeToken;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractTask<I, O> implements Task<I, O> {

  private final TypeToken<I> inTypeToken = new TypeToken<I>(getClass()) { };
  private final TypeToken<O> outTypeToken = new TypeToken<O>(getClass()) { };

  protected Map<String, BlockingQueue<Callable<I>>> _callableBuffer;
  protected Map<String, BlockingQueue<I>> _resultBuffer;
  protected String _name;
  protected List<Function> _subtasks;
  protected boolean running;

  public AbstractTask() {
    _subtasks = new LinkedList<>();
    _callableBuffer = new ConcurrentHashMap<>();
    _resultBuffer = new ConcurrentHashMap<>();
    running = false;
  }

  /*****************
   Consumer Methods */

  @Override
  public abstract O execute();

  @Override
  public abstract Operator operator();

  @Override
  public Task<I, O> name(@Nonnull String name) {
    _name = name;

    return this;
  }

  /*****************
   Public Methods */

  public String getName() {
    return _name;
  }

  public Class<?> getInType() {
    return inTypeToken.getRawType();
  }

  public Class<?> getOutType() {
    return outTypeToken.getRawType();
  }

  public void complete(@Nonnull String source, @Nonnull I result) throws InterruptedException {
    if (!_resultBuffer.containsKey(source))
      _resultBuffer.put(source, new LinkedBlockingQueue<>());
    _resultBuffer.get(source).put(result);
  }

  public Map<String, BlockingQueue<I>> results() {
    return _resultBuffer;
  }

  /*****************
   Hidden Methods */

  protected synchronized I result(String taskname) throws Exception {
    if (!_resultBuffer.containsKey(taskname)) {
      throw new Exception("Result for " + taskname + "has already been retrieved, " +
          "or does not exist");
    }
    return _resultBuffer.get(taskname).take();
  }
}
