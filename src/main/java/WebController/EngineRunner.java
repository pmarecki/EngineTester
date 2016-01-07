package WebController;

import Domain.*;
import Proto.ETO;
import ViewJson.ViewValue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Logic of running tasks.
 * 1) Others ask for results only from this bean
 * 2) Keeps list && logic of running tasks
 * 3) Loads filenames from DB, and updatas DB with results.
 *
 */

@Service
public class EngineRunner implements InitializingBean {

    @Autowired
    ADataRe dataRe;
    @Autowired
    EngineRe engineRe;
    @Autowired
    ResultRe resultRe;
    @Autowired
    ThreadPoolTaskExecutor executor;
    @Autowired
    ServletContext servletContext;

    private String rootPath;

    /**
     * Maps controlling execution:
     * - taskStatus     : "OK", "pending", "error: ..."; shared with tasks
     * - nextReload     : 50, 100, 200, x2 ms
     *
     * Key = engineid * 1e5 + dataid
     */
    Map<TaskKey, Integer> nextReload = new HashMap<>();
    ConcurrentMap<TaskKey, String> taskStatus = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        rootPath = servletContext.getRealPath("Files") + "/";
    }




    ////// MAIN METHOD

    /**
     * Values obtained:
     * (1) if Result object in DB -->[FILE DOES EXIST], get from file
     * (2) if taskStatus=="OK" -->[FILE DOES EXIST], write filename (Result class) to DB, and get from file
     * (3) if "pending" --> answer with "wait"; double next wait time
     * (4) if "error" --> return error code
     * (5) no status, not in DB --> new task
     *
     * (3), (4): file can be absent, or corrupted
     */
    public ViewValue getResult(Integer engineid, Integer dataid) {
        Result res = resultRe.findByEngineidAndDataid(engineid, dataid);

        if (res!=null) return getViewValueFromFile(engineid, dataid); //just report to web-client

        TaskKey key = new TaskKey(engineid, dataid);
        String status = taskStatus.get(key);

        ViewValue vv = new ViewValue();
        if (status == null) {
            runNewTask(engineid, dataid);   //RUN!
            taskStatus.put(key, "pending");
            nextReload.put(key, 20);
            vv.setStatus("pending");    //for web-client
            vv.setNextReloadMs(20);
            return vv;
        } else if (status.equals("OK")) {
            writeToDb(engineid, dataid);
            return getViewValueFromFile(engineid, dataid);
        } else if (status.equals("pending")) {
            vv.setStatus("pending");
            int delay = Math.max(nextReload.get(key), 20);
            vv.setNextReloadMs(delay);
            nextReload.put(key, delay * 2);
            return vv;
        } else {
            //ERROR
            vv.setStatus("ERROR: " + taskStatus.get(key));
        }
        return vv;
    }

    //All !pending results will be eligible for re-run.
    public void cleanErrorStatuses() {
        for(TaskKey key : taskStatus.keySet()) {
            String status = taskStatus.get(key);
            if (!status.equals("pending")) {
                nextReload.remove(key);
                taskStatus.remove(key);
            }
        }
    }

    public void deleteStatusesByEngineid(int engineid) {
        for(TaskKey key : taskStatus.keySet()) {
            if (key.getEngineid()!=engineid) continue;
            if (!taskStatus.get(key).equals("pending")) {
                //todo: concurrent??
                taskStatus.remove(key);
            }
        }
    }


    public List<String> getDirectoryListing() {
        List<String> res = new ArrayList<>();

        try {
            Process tr = Runtime.getRuntime().exec( new String[]{"ls", "-l", "--sort=time", rootPath} );
            BufferedReader rd = new BufferedReader( new InputStreamReader( tr.getInputStream() ) );
            String s;
            s = rd.readLine();
            while( s!=null){
                res.add(s);
                s = rd.readLine();
            }
        } catch (IOException e) {
            res.add(e.toString());
        }

        return res;
    }


    public String getResultFilename(int engineid, int dataid) {
        return "res_e" + engineid + "_d" + dataid + ".res";
    }



    ////// PRIVATE

    //Only place where new processes (tasks) are launched (on new therad)
    private void runNewTask(int engineid, int dataid) {
        //no path in the file names (task needs to prefix them)
        String e = engineRe.findOne(engineid).getFilename();
        String d = dataRe.findOne(dataid).getFilename();
        String r = this.getResultFilename(engineid, dataid);

        //All tasks update ConcurrentHashMap taskStatus.
        //TaskKey = unique task id
        ComputeTask task = new ComputeTask(e, d, r, getRootPath(),
                taskStatus, new TaskKey(engineid, dataid));
        executor.execute(task);
    }


    //Assumes: result present in file
    private void writeToDb(int engineid, int dataid) {
        Engine e = engineRe.findOne(engineid);
        Result r = new Result();
        r.setDataid(dataid);
        r.setEngineid(engineid);
        r.setCatid(e.getCatid());
        r.setFilename(this.getResultFilename(engineid, dataid));
        resultRe.save(r);
    }


    // Reports results using ETO.proto
    private ViewValue getViewValueFromFile(int engineid, int dataid) {

        ViewValue vv = new ViewValue();
        try {
            String filePath = this.rootPath + getResultFilename(engineid, dataid);
            ETO.TaskResult resultProtobuf = ETO.TaskResult.parseFrom(new FileInputStream(filePath));
            vv.setStatus(resultProtobuf.getStatus());
            vv.setValue(resultProtobuf.getValue());
            vv.setTimems(resultProtobuf.getExecutionTime());
            return vv;
        } catch (Exception e) {
            vv.setStatus("ERROR parsing result: " + e.getMessage());
            return vv;
        }
    }


//    Getters


    public String getRootPath() {
        return rootPath;
    }
}
