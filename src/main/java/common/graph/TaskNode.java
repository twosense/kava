package common.graph;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import common.executor.TaskExecutor;
import common.task.AbstractTask;
import common.task.ProcessRequest;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings({"WeakerAccess", "unchecked"})
public class TaskNode implements Node {

  private AbstractTask _task;
  private Class<?> _inType;
  private Class<?> _outType;
  private Set<TaskNode> _successors;
  private Set<String> _linkers;
  private WeakReference<TaskGraph> _graph;
  /*****************
   Public Methods */

  public TaskNode(AbstractTask t) {
    _successors = new HashSet<>();
    _linkers = new HashSet<>();
    _task = t;
    _inType = t.getInType();
    _outType = t.getOutType();
  }

  public AbstractTask task() {
    return _task;
  }

  public TaskGraph graph() {
    return _graph.get();
  }

  @Override
  public Node then(@Nonnull String name) throws Exception {
    if (this.graph() == null)
      throw new Exception("Current Task is disconnected from graph.");

    this.graph().link(_task.getName(), name);

    return this;
  }

  /*****************
   Hidden Methods */

  protected void on(ProcessRequest request) throws IllegalArgumentException, InterruptedException, ExecutionException {
    if (request.getData() != null && request.getData().getClass() != _inType)
      throw new IllegalArgumentException("Invalid input type.");

    // Add to results queue
    _task.complete(request.getSource(), request.getData());

    switch (_task.operator()) {
      case AND: {
        this.and();
        break;
      }
      case OR: {
        this.or();
        break;
      }
    }
  }

  protected void and() throws InterruptedException {
    // Make decision if ready to execute current task
    boolean allResultsAvailableForCurrentTask = _task.results().keySet().equals(_linkers);
    if (!allResultsAvailableForCurrentTask) {
      return;
    }
    boolean allQueuesHaveResults = true;
    for (Object q : _task.results().keySet()) {
      BlockingQueue<Callable> queue = (BlockingQueue<Callable>) _task.results().get(q);
      if (queue.isEmpty()) {
        allQueuesHaveResults = false;
        break;
      }
    }
    if (!allQueuesHaveResults) {
      return;
    }
    this.scheduleThisTask();
  }

  protected void or() {
    this.scheduleThisTask();
  }

  protected void scheduleThisTask() {
    // Schedule current task and add callback for its completion
    Callable thisTask = _task::execute;
    ListenableFuture<Object> asyncTask = TaskExecutor.main().submit(thisTask);
    Futures.addCallback(asyncTask, new FutureCallback<Object>() {
      @Override
      @ParametersAreNonnullByDefault
      public void onFailure(Throwable t) {
        System.out.println(t.getMessage());
      }

      @Override
      @ParametersAreNonnullByDefault
      public void onSuccess(Object result) {
        if (result.getClass() != _outType) {
          this.onFailure(new Exception("Incorrect type of result for " + _task.getName()));
          return;
        }
        if (_successors.size() == 0) {
          try {
            _graph.get().executeCallback(_task.getName(), result);
          } catch (NullPointerException e) {
            System.out.println("\n!!!Warning: no output callback for task " + _task.getName() + "!!!\n");
          }
          return;
        }
        // Notify successors that we've completed execution
        for (TaskNode n : _successors) {
          try {
            n.on(new ProcessRequest(_task.getName(), n._task.getName(), result));
          } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
          }
        }
      }
    }, TaskExecutor.main());
  }

  protected void setGraph(TaskGraph g) {
    if (_graph == null)
      _graph = new WeakReference<>(g);
  }

  protected void addSuccessor(@Nonnull TaskNode node) throws Exception {
    if (node._task == null || node._task.getName() == null)
      throw new Exception("Null or nameless Task was added to \""
          + _task.getName()
          + "\" successors set. Tasks cannot be null and must have a name.");

    if (_outType != node._task.getInType())
      throw new Exception("New successor for Task \"" + task().getName() + "\" has push type " +
          "that does not match the output type of \"" + task().getName() + "\" (" + node._task.getInType().getName() + "vs." + _outType.getName() + ").");

    node.addLinker(this._task.getName());
    _successors.add(node);
  }

  protected void addLinker(@Nonnull String linker) throws Exception {
    _linkers.add(linker);
  }
}