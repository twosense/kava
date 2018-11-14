# Kava
## A graph-based executor for async and sequential tasks.

```Java
// tasks are defined using the `AbstractTask` class
private AbstractTask<Integer, Double> task1, task2, task3;

// example definition of `task2`, which is dependent upon the result of `task1`
task2 = new AbstractTask<Integer, Double>() {

    @Override
    public Operator operator() {
        // denotes whether the Task waits on all predecessor results (AND) or takes one result at a time (OR)
        return Operator.AND;
    }
        
    @Override
    public synchronized Double execute()  {
        // variable to hold input value
        Integer result;
        try {
            // result of the previous task is fetch using this.result(String taskName)
            result = this.result("task1");
            // variable to hold output value
            Double modifiedResult = result += 1.0;
            
            Thread.sleep(1000);
            return modifiedResult;
        } catch (Exception e) {
            return null;
        }
    }
};

// each AbstractTask must be assigned a unique name
task2.name("task2");

// example definition of TaskGraph
private TaskGraph taskGraph = new TaskGraph();

taskGraph.link(inputTask1, someTask)
         .link(inputTask2, someTask)
         .link(inputTask3, someTask)
         .link(someTask, outputTask)
         .link(inputTask4, outputTask);
         
Graph.TaskGraphCallback completionCallback = (Object finalResult) -> System.out.println(finalResult.toString());
taskGraph.out("outputTask", completionCallback);

// data is passed to the TaskGraph using a ProcessRequest:

ProcessRequest<Integer> request1 = new ProcessRequest<>("data1", "inputTask1", 1);
ProcessRequest<Integer> request2 = new ProcessRequest<>("data2", "inputTask2", 2);
ProcessRequest<Integer> request3 = new ProcessRequest<>("data3", "inputTask3", 0);

taskGraph.in(request1);
taskGraph.in(request2);
taskGraph.in(request3);

// in the abstract execute() method of an input task (the entry point node), the data from a ProcessRequest is retrieved using `this.result(String processRequestLabel)`

```

