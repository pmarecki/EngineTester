package WebController;

import Domain.*;
import ViewJson.ViewValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.ServletContext;
import java.io.*;

@Controller
@EnableWebMvc
public class MainRest implements InitializingBean {
    @Autowired
    CategoryRe categoryRe;
    @Autowired
    EngineRe engineRe;
    @Autowired
    ADataRe dataRe;
    @Autowired
    DataSetRe dataSetRe;
    @Autowired
    AssignDataRe assignRe;
    @Autowired
    ResultRe resultRe;
    @Autowired
    EngineRunner engineRunner;

    @Autowired
    ServletContext servletContext;

    String rootPath = "";

    private static Logger logger = LoggerFactory.getLogger(MainRest.class);


    @Override
    public void afterPropertiesSet() throws Exception {
        rootPath = servletContext.getRealPath("Files") + "/";
        engineRunner.setRootPath(rootPath);
    }

    ///////////// CATEGORIES

    @RequestMapping(value = {"/cat/list"})
    @ResponseBody
    public Rest getCatList() {
        Rest resp = new Rest();
        resp.result = categoryRe.findAll();
        return resp;
    }
    @RequestMapping(value = {"/cat/add"})
    @ResponseBody
    public Rest addCat(@RequestParam(value = "name") String name) {
        Category new_ = new Category();
        new_.setName(name);
        categoryRe.save(new_);
        return new Rest();
    }

    ///////////// ENGINES
    @RequestMapping(value = {"/engine/list"})
    @ResponseBody
    public Rest getEngineList(@RequestParam(value = "catid", defaultValue = "-1") Integer catid) {
        Rest resp = new Rest();
        if (catid==-1) resp.result = engineRe.findAll();
        else resp.result = engineRe.findByCatid(catid);
        return resp;
    }
    @RequestMapping(value = {"/engine/add"})
    @ResponseBody
    public Rest addEngine(@RequestParam(value="catid") Integer catid,
                          @RequestParam(value = "filename") String filename) {
        if (!categoryRe.exists(catid)) return new Rest("Wrong catid");
        Engine new_ = new Engine();
        new_.setCatid(catid);
        new_.setFilename(filename);
        engineRe.save(new_);
        //Make sure engine's file is executable
        String fullpath = engineRunner.getRootPath() + filename;
        Utils.setExecutable(fullpath);
        return new Rest();
    }

    ///////////// DATASETS
    @RequestMapping(value = {"/dataset/list"})
    @ResponseBody
    public Rest getDatasetList(@RequestParam(value = "catid", defaultValue = "-1") Integer catid)
    {
        Rest resp = new Rest();
        if (catid==-1) resp.result = dataSetRe.findAll();
        else resp.result = dataSetRe.findByCatid(catid);
        return resp;
    }
    @RequestMapping(value = {"/dataset/add"})
    @ResponseBody
    public Rest addDataset(@RequestParam(value="catid") Integer catid,
                           @RequestParam(value = "filename") String name) {
        if (!categoryRe.exists(catid)) return new Rest("Wrong catid");
        DataSet new_ = new DataSet();
        new_.setName(name);
        new_.setCatid(catid);
        dataSetRe.save(new_);
        return new Rest();
    }


    ///////////// DATA
    @RequestMapping(value = {"/data/list"})
    @ResponseBody
    public Rest getDataList(@RequestParam(value = "catid") Integer catid) {
        Rest resp = new Rest();
        resp.result = dataRe.findByCatid(catid);
        return resp;
    }

    @RequestMapping(value = {"/data/add"})
    @ResponseBody
    public Rest addData(@RequestParam(value="catid") Integer catid,
                        @RequestParam(value = "filename") String filename) {
        if (!categoryRe.exists(catid)) return new Rest("Wrong catid");
        AData new_ = new AData();
        new_.setCatid(catid);
        new_.setFilename(filename);
        dataRe.save(new_);
        return new Rest();
    }



    @RequestMapping(value = {"/data/writefile"})
    @ResponseBody
    public Rest writeFromString(@RequestParam(value = "dataid") Integer dataid,
                             @RequestParam(value = "content") String content) {
        AData data = dataRe.findOne(dataid);
        if (data==null) return new Rest("No such dataid");

        String fileName = this.rootPath + data.getFilename();

        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(content);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            return new Rest("Writing file " + data.getFilename() + " failed");
        }
        return new Rest();

    }


    @RequestMapping(value = {"/data/readfile"})
    @ResponseBody
    public Rest readAsString(@RequestParam(value = "dataid") Integer dataid) {
        AData data = dataRe.findOne(dataid);
        if (data==null) return new Rest("No such dataid");
        StringBuilder b = new StringBuilder();
        String fileName = this.rootPath + data.getFilename();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            while(true) {
                String s = reader.readLine();
                if (s==null) break;
                b.append(s + "\n");
            }
        } catch (Exception e) {
            return new Rest("Reading file " + data.getFilename() + "failed");
        }
        Rest ans = new Rest();
        ans.setResult(b.toString());

        return ans;

    }





    /////////////  ASSIGNMENTS  (data <--> dataset)
    @RequestMapping(value = {"/assignment/list"})
    @ResponseBody
    public Rest getAssignList(@RequestParam(value = "datasetid") Integer datasetid) {
        Rest resp = new Rest();
        resp.result = assignRe.findByDatasetid(datasetid);
        return resp;
    }

    @RequestMapping(value = {"/assignment/update"})
    @ResponseBody
    public Rest getAssignList(
            @RequestParam(value = "datasetid") Integer datasetid,
            @RequestParam(value = "markedids", defaultValue = "-") String markedids
            ) {
        if (markedids.equals("-")) {
            assignRe.removeByDatasetid(datasetid);
            Rest rr = new Rest();
            rr.setStatus("OK; cleared all markedid's for datasetid: " + datasetid);
            return rr;
        }
        String[] ids = markedids.split(";");
        int n = ids.length;
        Integer[] active = new Integer[n];
        for (int i = 0; i < n; i++) {
            try {
                active[i] = Integer.parseInt(ids[i]);
            } catch (Exception e) {
                return new Rest("Wrong format of markedids:`" + markedids +"`");
            }
        }
        assignRe.removeByDatasetid(datasetid);
//        List<AssignData> aa = assignRe.findByDatasetid(datasetid);
//        for(AssignData a : aa) {
//            assignRe.delete(a);
//        }

        for (int i = 0; i < n; i++) {
            AssignData _new = new AssignData();
            _new.setDatasetid(datasetid);
            _new.setDataid(active[i]);
            assignRe.save(_new);
        }
        return new Rest();
    }


    /////////////RESULTS
    @RequestMapping(value = {"/result/get"})
    @ResponseBody
    public Rest getResult(
            @RequestParam(value = "engineid") Integer engineid,
            @RequestParam(value = "dataid") Integer dataid
    ) {
        ViewValue vv  = engineRunner.getResult(engineid, dataid);
        Rest rest = new Rest();
        rest.setResult(vv);
        return rest;
    }

    @RequestMapping(value = {"/result/reseterrors"})
    @ResponseBody
    public Rest resetErrors(){
        engineRunner.cleanErrorStatuses();
        return new Rest();
    }

    @RequestMapping(value = {"/result/delresults"})
    @ResponseBody
    public Rest deleteResults(
            @RequestParam(value = "engineid") Integer engineid){
        resultRe.removeByEngineid(engineid);    //delete from DB
        this.deleteResultFilesByEngineid(engineid); //delete from Files
        engineRunner.deleteStatusesByEngineid(engineid); //delete Statuses !pending todo: Q: pending (long compute)
        return new Rest();
    }

    //------------------delete logic-------
    @RequestMapping(value = {"/engine/delete"})
    @ResponseBody
    public Rest deleteEngine(
            @RequestParam(value = "engineid") Integer engineid){
        Engine en = engineRe.findOne(engineid);
        if (en == null) {
            return new Rest("No engine with engineid=" + engineid + " exists.");
        }
        resultRe.removeByEngineid(engineid);
        engineRe.removeByEngineid(engineid);
        String path = engineRunner.getRootPath();
        Utils.deleteFile(path, en.getFilename());
        this.deleteResultFilesByEngineid(engineid);
        return new Rest();
    }

    @RequestMapping(value = {"/data/delete"})
    @ResponseBody
    public Rest deleteData(
            @RequestParam(value = "dataid") Integer dataid){
        AData da = dataRe.findOne(dataid);
        if (da == null) {
            return new Rest("No data with dataid=" + dataid + " exists.");
        }
        assignRe.removeByDataid(dataid);
        resultRe.removeByDataid(dataid);
        dataRe.removeByDataid(dataid);

        String path = engineRunner.getRootPath();
        Utils.deleteFile(path, da.getFilename());
        String mask = "_d" + dataid + ".res";
        Utils.deleteFile(path, mask);
        return new Rest();
    }

    @RequestMapping(value = {"/dataset/delete"})
    @ResponseBody
    public Rest deleteDataSet(
            @RequestParam(value = "datasetid") Integer datasetid){
        DataSet ds = dataSetRe.findOne(datasetid);
        if (ds == null) {
            return new Rest("No dataset with datasetid=" + datasetid + " exists.");
        }
        assignRe.removeByDatasetid(datasetid);
        dataSetRe.removeByDatasetid(datasetid);

        return new Rest();
    }




    ////// UTILS
    @RequestMapping(value = {"/debug/dir"})
    @ResponseBody
    public Rest getDirFiles() {
        Rest rest = new Rest();
        rest.setResult(engineRunner.getDirectoryListing());
        return rest;
    }



    @RequestMapping(value="/upload", method= RequestMethod.POST)
    public @ResponseBody Rest handleFileUpload(
                @RequestParam("fname") String name,
                @RequestParam("file") MultipartFile file)
    {
        String path = engineRunner.getRootPath();
        String fname = path + name;
        Rest response = new Rest();
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(
                        new File(fname)));
                stream.write(bytes);
                stream.close();
                response.setResult("You successfully uploaded " + name + "");

            } catch (Exception e) {
                response.setResult("You failed to upload " + name + " => " + e.getMessage());
            }
        } else {
            response.setResult("File was empty.");
        }
        logger.warn("File uploaded:" + fname);
        //result must be of json type (object); no basic string allowed [else: angular error]
        return response;
    }




    /////////////////////////////////////////////////////////////////////////////


    ////// PRIVATE
    private void deleteResultFilesByEngineid(int engineid) {
        String path = engineRunner.getRootPath();
        String mask = "_e" + engineid + "_";
        Utils.deleteFile(path, mask);
    }




}

