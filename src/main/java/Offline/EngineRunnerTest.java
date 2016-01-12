package Offline;


import ViewJson.ViewValue;
import WebController.EngineRunner;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EngineRunnerTest {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("offline-config.xml");
        EngineRunner runner = ctx.getBean(EngineRunner.class);

        for (int i = 0; i < 10; i++) {
            ViewValue vv = runner.getResult(33, 23);
            System.out.println(vv);
            Thread.sleep(30);
        }

        ctx.close();
    }
}
