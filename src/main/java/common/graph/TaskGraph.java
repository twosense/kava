package common.graph;

import common.task.AbstractTask;
import common.task.ProcessRequest;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;

@SuppressWarnings("WeakerAccess")
public class TaskGraph implements Graph {

  private boolean _on = false;
  protected WeakHashMap<String, TaskNode> _nodes;
  private HashMap<String, TaskGraphCallback> _completionCallbacks;

  /*****************
   Public Methods */

  public TaskGraph() {
    _nodes = new WeakHashMap<>();
    _completionCallbacks = new HashMap<>();
  }

  @Override
  public Graph add(@Nonnull AbstractTask t) throws Exception {
    if (t.getName() == null)
      throw new Exception("Null or nameless Task was added to Graph.");

    if (_nodes.containsKey(t.getName()))
      return this;

    TaskNode newNode = new TaskNode(t);
    newNode.setGraph(this);
    _nodes.put(t.getName(), newNode);

    return this;
  }

  @Override
  public Graph add(@Nonnull AbstractTask... t) throws Exception {
    if (t.length == 0)
      throw new Exception("Empty list of Tasks provided.");

    for (AbstractTask task : t)
      this.add(task);

    return this;
  }

  @Override
  public Graph link(@Nonnull String src, @Nonnull String dst) throws Exception {
    if (!_nodes.containsKey(src) || !_nodes.containsKey(dst))
      throw new Exception("Non-existent Task key provided.");

    _nodes.get(src).addSuccessor(_nodes.get(dst));

    return this;
  }

  @Override
  public Graph link(@Nonnull AbstractTask src, @Nonnull  AbstractTask dst) throws Exception {
    this.add(src, dst);
    this.link(src.getName(), dst.getName());

    return this;
  }

  @Override
  public Graph chain(@Nonnull String... t) throws Exception {
    if (t.length < 2)
      throw new Exception("Empty or invalid chain of Tasks provided.");

    for (int i = 0; i < t.length - 1; i++)
      this.link(t[i], t[i+1]);

    return this;
  }

  @Override
  public AbstractTask task(@Nonnull String name) throws Exception {
    if (name.isEmpty() || !_nodes.containsKey(name))
      throw new Exception("Non-existent Task key provided.");

    return _nodes.get(name).task();
  }

  @Override
  @SuppressWarnings("unchecked")
  public synchronized Graph in(@Nonnull ProcessRequest request) throws Exception {
    if (!_nodes.containsKey(request.getDestination()))
      throw new Exception("Invalid entry Task key in ProcessRequest.");

    if (!_on) {
      throw new Exception("Graph execution is turned off.");
    }

    TaskNode target = _nodes.get(request.getDestination());
    target.addLinker(request.getSource());
    target.on(request);

    return this;
  }

  @Override
  public Graph out(@Nonnull String name, @Nonnull TaskGraphCallback completionCallback) throws Exception {
    _completionCallbacks.put(name, completionCallback);

    return this;
  }

  @Override
  public Graph turn(boolean on) {
    _on = on;

    return this;
  }

  /*****************
   Hidden Methods */

  protected void executeCallback(String name, Object result) {
    _completionCallbacks.get(name).onComplete(name, result);
  }
}
