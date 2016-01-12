package Offline;

import Proto.ETO;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by pm on 12/28/15.
 */
public class ProtobufTest {
    public static void main(String[] args) throws Exception {
        String outPath = "outfile.res";

        // Modification of data (done somewhere else)
        ETO.TaskResult.Builder bbb = ETO.TaskResult.newBuilder();
        bbb.setStatus("OK");
        bbb.setExecutionTime(124);
        bbb.setValue(3.1415);

        // Write data back to disk.
        FileOutputStream output = new FileOutputStream(outPath);
        bbb.build().writeTo(output);
        output.close();


        ETO.TaskResult out = ETO.TaskResult.parseFrom(new FileInputStream(outPath));
        System.out.println(out);


        /**
         * todo: Offline tests of the methods
         */


    }
}
