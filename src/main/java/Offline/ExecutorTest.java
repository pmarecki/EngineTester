package Offline;

import Domain.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Offline-owy tester repozytoriów.
 */

class MTask implements Runnable {
    int i;
    AtomicInteger cnt;
    public MTask(int i, AtomicInteger cnt) {
        this.i = i;
        this.cnt = cnt;
    }

    @Override
    public void run() {
        System.out.println("cnt=" + cnt.incrementAndGet() + "from " + i + " thread:" + Thread.currentThread());
    }
}


public class ExecutorTest {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("offline-config.xml");

//        CategoryRe catre = ctx.getBean(CategoryRe.class);
//        Category coco = new Category();
//        coco.setName("MulDiv test");
//        catre.save(coco);

//        ADataRe re = ctx.getBean(ADataRe.class);
//        for(AData d : re.findByCatid(1))
//            System.out.println(d);

//        //REPO REMOVAL TEST
//        AssignDataRe re = ctx.getBean(AssignDataRe.class);
//        re.save(new AssignData(1, 20));
//        re.save(new AssignData(1, 21));
//        re.removeByDatasetid(1);
//        for (AssignData ids : re.findByDatasetid(1)) {
//            System.out.println(ids);
//        }

        AtomicInteger cnt = new AtomicInteger();
        ThreadPoolTaskExecutor executor = ctx.getBean(ThreadPoolTaskExecutor.class);
        Future[] ff = new Future[120];
        for (int i = 0; i < 100; i++) {
            ff[i] = executor.submit(new MTask(i, cnt));     //throws
        }
        for (int i = 0; i < 100; i++) {
            System.out.print(ff[i]==null?"n ":(ff[i].isDone()?"1":"0"));
        }
        System.out.println("Main finishing: " + cnt.get());
        executor.shutdown();
        System.out.println("After shutdown: " + cnt.get());
        for (int i = 0; i < 100; i++) {
            System.out.print(ff[i]==null?"n ":(ff[i].isDone()?"1":"0"));
        }
        ctx.close();
        System.out.println(cnt.get());

    }
}
