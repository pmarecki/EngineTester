package WebController;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


class ComputeTask implements Runnable {
    String engineFile;
    String dataFile;
    String resultFile;
    String rootPath;

    ConcurrentMap<TaskKey, String> statusTable;
    final TaskKey taskKey;

    public ComputeTask(String engineFile, String dataFile, String resultFile, String rootPath,
                       ConcurrentMap<TaskKey, String> statusTable, TaskKey taskKey) {
        this.engineFile = engineFile;
        this.dataFile = dataFile;
        this.resultFile = resultFile;
        this.rootPath = rootPath;
        this.statusTable = statusTable;
        this.taskKey = taskKey;
    }

    /**
     * Attempt evaluation.
     * Status stored in taskStatus.
     * {OK, pending, error}
     * - On OK result written into resultFile (by Engine).
     * - Executor reads from this file output values.
     */

    @Override
    public void run() {
        //Do work; results will be saved in `resultFile` by the engine
        try {
            Process tr = Runtime.getRuntime().exec( new String[]{
                    rootPath + engineFile,
                    rootPath + dataFile,
                    rootPath + resultFile} );
            boolean exitValue = tr.waitFor(10000, TimeUnit.MILLISECONDS);
            if (exitValue!=true) throw new RuntimeException(" .exec error");
            //else: result in file
        } catch (Exception e) {
            statusTable.put(taskKey, "ERROR: " + e);
            return;
        }
        statusTable.put(taskKey, "OK");
    }
}
