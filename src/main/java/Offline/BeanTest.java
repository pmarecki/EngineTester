package Offline;

import Domain.ResultRe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by pm on 1/6/16.
 */
public class BeanTest {
    private static Logger logger = LoggerFactory.getLogger(BeanTest.class);

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("offline-config.xml");
        ResultRe repo = ctx.getBean(ResultRe.class);
        System.out.println(repo.count());
        logger.warn("horrible!");
        ctx.close();
    }
}
