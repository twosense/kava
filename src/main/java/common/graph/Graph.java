package common.graph;

import common.task.AbstractTask;
import common.task.ProcessRequest;

import javax.annotation.Nonnull;

public interface Graph {

  /**
   * Links two Tasks by their tags.
   *
   * @param src, name of source Task.
   * @param dst, name of destination Task.
   * @return reference to Graph.
   * @throws Exception
   */
  Graph link(@Nonnull String src, @Nonnull String dst) throws Exception;

  /**
   * Links two Tasks in the current Graph.
   *
   * @param src, the source Task.
   * @param dst, the destination Task.
   * @return reference to Graph.
   * @throws Exception
   */
  Graph link(@Nonnull AbstractTask src, @Nonnull AbstractTask dst) throws Exception;

  /**
   * Links sequential Tasks by their tags.
   *
   * @param t, the list of Tasks in order.
   * @return reference to Graph.
   * @throws Exception
   */
  Graph chain(@Nonnull String... t) throws Exception;

  /**
   * Adds an untethered Task to the current Graph.
   *
   * @param t, the Task object to add.
   * @return reference to Graph.
   */
  Graph add(@Nonnull AbstractTask t) throws Exception;

  /**
   * Adds a list of untethered Tasks to the Graph.
   *
   * @param t, the list of Tasks to add.
   * @return reference to Graph.
   * @throws Exception
   */
  Graph add(@Nonnull AbstractTask... t) throws Exception;

  /**
   * Channels a data processing request to the graph.
   * @param request, the data request with a specified destination task.
   * @return reference to Graph.
   * @throws Exception
   */
  Graph in(@Nonnull ProcessRequest request) throws Exception;

  /**
   * Sets a completion callback for a particular output Task.
   * @param name, name of the output Task.
   * @param completionCallback, the completion callback.
   * @return reference to Graph.
   * @throws Exception
   */
  Graph out(@Nonnull String name, @Nonnull TaskGraphCallback completionCallback)  throws Exception;

  /**
   * Gets Task Node by name.
   *
   * @param name, the unique Task name.
   * @return a Node with a reference to the Task.
   */
  AbstractTask task(@Nonnull String name) throws Exception;

  /**
   * Turns the entire Graph on or off.
   *
   * @param on, state of the Graphh.
   * @return reference to Graph.
   */
  Graph turn(boolean on);

  /**
   * TaskGraphCallback
   */
  interface TaskGraphCallback {
    void onComplete(String name, Object out);
  }
}
