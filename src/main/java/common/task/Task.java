package common.task;

import common.util.ImplementSynchronized;

import javax.annotation.Nonnull;

public interface Task<I, O> {

  /**
   * Sets the name of the Task.
   *
   * @param name, the name of the Task.
   * @return the Task object.
   */
  Task<I, O> name(@Nonnull final String name);

  /**
   * Runs a custom task defined by the user and returns the result immediately.
   * Must be implemented using the `synchronized` modifier.
   *
   * @return the output, O.
   */
  @ImplementSynchronized
  O execute();

  /**
   * Determines the link type.
   *
   * @return the link type, as a boolean Operator.
   */
  Operator operator();

  enum Operator {
    AND,
    OR
  }
}
