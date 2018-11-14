package common.graph;

import common.task.AbstractTask;
import common.task.ProcessRequest;
import common.task.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskGraphTest {

  private TaskGraph taskGraph = new TaskGraph();
  private AbstractTask<Integer, Double> inputTask1, inputTask2, inputTask3, inputTask4, inputTask5;
  private AbstractTask<Double, Integer> additionAndConversion, passThroughConversion;
  private AbstractTask<Integer, Integer> outputTask;
  private AbstractTask<Double, Double> outputTask1;
  private Object output;

  private CountDownLatch latch;

  @Before
  public void setUp() throws Exception {
    inputTask1 = new AbstractTask<Integer, Double>() {
      @Override
      public Operator operator() {
        return Operator.AND;
      }

      @Override
      public synchronized Double execute()  {
        Double result;
        try {
          System.out.println("inside " + _name + " execute()");
          result = this.result("touch") + 3.0;
          //Thread.sleep(3000);
          System.out.println("complete " + _name + " execute()");
          return result;
        } catch (Exception e) {
          return null;
        }
      }
    };

    inputTask2 = new AbstractTask<Integer, Double>() {
      @Override
      public Operator operator() {
        return Operator.AND;
      }

      @Override
      public synchronized Double execute() {
        Double result = null;
        try {
          System.out.println("inside " + _name + " execute()");
          result = this.result("gait") + 4.0;
          Thread.sleep(1000);
          System.out.println("complete " + _name + " execute()");
          return result;
        } catch (Exception e) {
          return null;
        }
      }
    };

    inputTask3 = new AbstractTask<Integer, Double>() {
      @Override
      public Operator operator() {
        return Operator.AND;
      }

      @Override
      public synchronized Double execute() {
        Double result = null;
        try {
          System.out.println("inside " + _name + " execute()");
          result = this.result("pickup") + 6.0;
          Thread.sleep(100);
          System.out.println("complete " + _name + " execute()");
          return result;
        } catch (Exception e) {
          return null;
        }
      }
    };

    inputTask4 = new AbstractTask<Integer, Double>() {
      @Override
      public Operator operator() {
        return Operator.AND;
      }

      @Override
      public synchronized Double execute() {
        Double result;
        try {
          System.out.println("inside " + _name + " execute()");
          result = this.result("factor4") + 0.0;
          Thread.sleep(500);
          System.out.println("complete " + _name + " execute()");
          return result;
        } catch (Exception e) {
          return null;
        }
      }
    };

    inputTask5 = new AbstractTask<Integer, Double>() {
      @Override
      public Operator operator() {
        return Operator.AND;
      }

      @Override
      public synchronized Double execute() {
        Double result;
        try {
          System.out.println("inside " + _name + " execute()");
          result = this.result("factor5") + 0.0;
          Thread.sleep(1000);
          System.out.println("complete " + _name + " execute()");
          return result;
        } catch (Exception e) {
          return null;
        }
      }
    };

    passThroughConversion = new AbstractTask<Double, Integer>() {
      @Override
      public Operator operator() {
        return Operator.OR;
      }

      @Override
      public synchronized Integer execute() {
        Integer a = 5;
        Integer resultFromTask4 = null, resultFromTask5 = null;
        System.out.println("inside " + _name + " execute()");
        try {
          resultFromTask4 = this.result("inputTask4").intValue();
        } catch (Exception e) {
          System.out.println("result from inputTask4 not ready!");
        }
        try {
          resultFromTask5 = this.result("inputTask5").intValue();
        } catch (Exception e) {
          System.out.println("result from inputTask5 not ready!");
        }

        if (resultFromTask4 != null) {
          a += resultFromTask4;
        }
        if (resultFromTask5 != null) {
          a += resultFromTask4;
        }
        System.out.println("complete " + _name + " execute()");
        return a;
      }
    };

    additionAndConversion = new AbstractTask<Double, Integer>() {
      @Override
      public Operator operator() {
        return Operator.AND;
      }

      @Override
      public synchronized Integer execute() {
        Integer result = null;
        try {
          System.out.println("inside " + _name + " execute()");
          result = (new Double(this.result("inputTask1") +
              this.result("inputTask3"))).intValue();
          System.out.println("complete " + _name + " execute()");
          return result;
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }
    };

    outputTask = new AbstractTask<Integer, Integer>() {
      @Override
      public Operator operator() {
        return Operator.AND;
      }

      @Override
      public synchronized Integer execute() {
        Integer result;
        try {
          System.out.println("inside " + _name + " execute()");
          result = this.result("additionAndConversion") + this.result("passThroughConversion");
          System.out.println("complete " + _name + " execute()");
          Thread.sleep(3000);
          return result;
        } catch (Exception e) {
          latch.countDown();
          e.printStackTrace();
          return null;
        }
      }
    };

    outputTask1 = new AbstractTask<Double, Double>() {
      @Override
      public Operator operator() {
        return Operator.AND;
      }

      @Override
      public synchronized Double execute() {
        try {
          System.out.println("inside " + _name + " execute()");
          return this.result("inputTask1");
        } catch (Exception e) {
          return null;
        }
      }
    };
  }

  @Test
  public void testTaskGraph_FullIntegrationTest() throws Exception {
    latch = new CountDownLatch(1);

    inputTask1.name("inputTask1");
    inputTask2.name("inputTask2");
    inputTask3.name("inputTask3");
    inputTask4.name("inputTask4");
    inputTask5.name("inputTask5");
    outputTask1.name("outputTask1");
    additionAndConversion.name("additionAndConversion");
    passThroughConversion.name("passThroughConversion");
    outputTask.name("outputTask");

    Graph.TaskGraphCallback completionCallback = (String name, Object o) -> {
      System.out.println("result for " + name + ": " + o);
      latch.countDown();
      output = o;
    };

    Graph.TaskGraphCallback completionCallback1 = (String name, Object o) -> {
      System.out.println("result for " + name + ": " + o);
    };

    taskGraph
        .turn(true)
        .link(inputTask1, additionAndConversion)
        .link(inputTask1, outputTask1)
        .link(inputTask2, additionAndConversion)
        .link(inputTask3, additionAndConversion)
        .link(inputTask4, passThroughConversion)
        .link(inputTask5, passThroughConversion)
        .link(additionAndConversion, outputTask)
        .link(passThroughConversion, outputTask)
        .out("outputTask", completionCallback)
        .out("outputTask1", completionCallback1);

    ProcessRequest<Integer> request1 = new ProcessRequest<>("touch", "inputTask1", 1);
    ProcessRequest<Integer> request2 = new ProcessRequest<>("gait", "inputTask2", 2);
    ProcessRequest<Integer> request3 = new ProcessRequest<>("pickup", "inputTask3", 0);
    ProcessRequest<Integer> request4 = new ProcessRequest<>("factor4", "inputTask4", 7);
    ProcessRequest<Integer> request5 = new ProcessRequest<>("factor5", "inputTask5", 8);

    ExecutorService service = Executors.newFixedThreadPool(10);
    long startTime = System.currentTimeMillis();
    
    taskGraph.in(request2);

    service.submit(() -> {
      try {
        //taskGraph.in(request3);
        taskGraph.in(request3);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        Assert.fail(e.getMessage());
      }
    });
    service.submit(() -> {
      try {
        taskGraph.in(request1);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        Assert.fail(e.getMessage());
      }
    });
    service.submit(() -> {
      try {
        taskGraph.in(request4);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        Assert.fail(e.getMessage());
      }
    });
    service.submit(() -> {
      try {
        taskGraph.in(request1);
        //taskGraph.in(request1);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        Assert.fail(e.getMessage());
      }
    });
    service.submit(() -> {
      try {
        taskGraph.in(request5);
        //taskGraph.in(request5);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        Assert.fail(e.getMessage());
      }
    });
     // for multi-threaded execution

    latch.await();

    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    System.out.println(elapsedTime + "\n");

    Assert.assertEquals("output of graph is correct", 22, output);
  }
}