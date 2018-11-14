package common.graph;

import javax.annotation.Nonnull;

public interface Node {

    /**
     * Link Task with name to predecessor.
     *
     * @param name, the name of the Task to link.
     * @return reference to Node.
     * @throws Exception
     */
    Node then(@Nonnull String name) throws Exception;
}
