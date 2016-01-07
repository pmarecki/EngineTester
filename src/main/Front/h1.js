///////////////////////////////////////////////
// Main app module
///////////////////////////////////////////////


var myapp = angular.module('myApp', []);



myapp.controller('mainCtrl', function ($scope, $http, $filter) {
    $scope.M = {
        //cats: [],
        //dataSets: [],
        //engines: [],
        //data: [],
        //selCat: {},     //selected Category
        //selDS: {},      //selected DataSet
        //result: [[]],   //indexed by engineid, dataid;  --> .value, .status, .exectime
        //nFile:          //new file
        dataSource: 'http://localhost:8080/ET/'
    };

});


myapp.controller('enginetest', function ($scope, $http, $interval) {
    /**
     * HELPERS
     */
    var unselectAll = function (items) {
        if (items==undefined) return;
        items.forEach(function (item) {
            item.isSelected = false;
        });
    };


    /**
     * LOADERS
     */
    $scope.loadCategories = function () {
        $http.get($scope.M.dataSource + 'cat/list').success(
            function (data) {
                $scope.M.cats = data.result;
                if ($scope.M.cats.length>0)
                    $scope.M.selCat=$scope.M.cats[0];
            }
        );
    };

    //note: creates empty result arrays for each engine
    $scope.loadEngines = function () {
        //all engines for global selected category
        if ($scope.M.selCat==undefined) return;
        var catid = $scope.M.selCat.catid;
        $scope.M.result = [];

        $http.get($scope.M.dataSource + 'engine/list?catid='+catid).success(
            function (data) {
                $scope.M.engines = data.result;
                unselectAll($scope.M.engines);
                $scope.M.engines.forEach(function (en) {
                    $scope.M.result[en.engineid] = [];
                });
            }
        );
    };

    $scope.loadData = function () {
        if ($scope.M.selCat==undefined) return;
        var catid = $scope.M.selCat.catid;
        $http.get($scope.M.dataSource + 'data/list?catid=' + catid).success(
            function (data) {
                $scope.M.data = data.result;
                unselectAll($scope.M.data);
            }
        );
    };

    $scope.loadDatasets = function () {
        if ($scope.M.selCat==undefined) return;
        var catid = $scope.M.selCat.catid;
        $http.get($scope.M.dataSource + 'dataset/list?catid='+catid).success(
            function (data) {
                //all $http are async
                $scope.M.dataSets = data.result;
                if (!data.result) return;
                $scope.M.selDS=$scope.M.dataSets[0];
                $scope.loadAssigned();
            }
        );

    };


    $scope.loadAssigned = function() {
        var selDS = $scope.M.selDS;
        if (selDS===undefined) return;
        $http.get($scope.M.dataSource + 'assignment/list?datasetid=' + selDS.datasetid).success(
            function (data) {
                if (data.result===undefined) return;
                if ($scope.M.data===undefined) return;
                var selection = new Set();
                data.result.forEach(function (row) {
                    selection.add(row.dataid);
                });
                //mark selected data instances
                $scope.M.data.forEach(function (dt) {
                    if (selection.has(dt.dataid))
                        dt.isSelected = true;
                    else
                        dt.isSelected = false;
                });
            }
        );
    };


    ////// SAVE
    // only for system-created assignments;
    // engines and datas are uploaded, not saved

    $scope.saveAssigned = function () {
        var selDS = $scope.M.selDS;
        if (selDS === undefined) return;
        var markedids = "";
        var second = false;
        $scope.M.data.forEach(function (dataitem) {
            if (dataitem.isSelected) {
                if (second) markedids += ";";
                markedids += dataitem.dataid;
                second = true;
            }
        });
        $http.get($scope.M.dataSource + 'assignment/update?datasetid=' + selDS.datasetid +
            "&markedids=" + markedids).success(
            function (data) {
                console.log(data.result);
            });
    };

    $scope.savenDataSet = function (filename) {
        var catid = $scope.M.selCat.catid;
        $http.get($scope.M.dataSource + 'dataset/add?catid=' + catid +
            "&filename=" + filename).success(
            function (data) {
                $scope.loadDatasets();
            });
    };

    $scope.savenData = function (filename) {
        var catid = $scope.M.selCat.catid;
        $http.get($scope.M.dataSource + 'data/add?catid=' + catid +
            "&filename=" + filename).success(
            function (data) {
                $scope.loadData();
                $scope.loadAssigned();
            });
    };

    $scope.savenEngine = function (filename) {
        var catid = $scope.M.selCat.catid;
        $http.get($scope.M.dataSource + 'engine/add?catid=' + catid +
            "&filename=" + filename).success(
            function (data) {
                $scope.loadEngines();
            });
    };


    /**
     * Engine execution logic
     */

//@RequestMapping(value = {"/result/get"})
//    @ResponseBody
//    public Rest getResult(
//            @RequestParam(value = "engineid") Integer engineid,
//            @RequestParam(value = "dataid") Integer dataid
//
//    --->
//    @Data
//    public class ViewValue {
//        Double value;
//        Integer timems;
//        String status;
//        Integer nextReloadMs;   //when to ask for update
//    }
//
    $scope.loadEngineData = function (en, da) {
        $http.get($scope.M.dataSource + 'result/get?engineid=' + en.engineid +
            "&dataid=" + da.dataid).success(
            function (data) {
                var rapRes = data.result;
                if (rapRes==undefined) return;

                $scope.M.result[en.engineid][da.dataid] = {
                    value: rapRes.value,
                    timems: rapRes.timems,
                    status: rapRes.status
                };
                var delay = parseInt(rapRes.nextReloadMs);
                if (delay==undefined || delay==0) delay = 500;
                if (rapRes.status==='pending') {
                    console.log(rapRes);
                    if (rapRes.nextReloadMs==0) return;
                    $interval($scope.loadEngineData(en, da), delay);
                }
            });
    };




    $scope.runEngine = function (engine) {
        //loop over all selected data
        //run on all; backend will know if run or skip
        $scope.M.data.forEach(function (data) {
            if (!data.isSelected) return;   //continue
            $scope.loadEngineData(engine, data);
        });

    };

    $scope.loadEngineResults = function (engine) {
        $scope.M.data.forEach(function (data) {
            if (!data.isSelected) return;   //continue
            $scope.loadEngineData(engine, data);
        });
    };

    $scope.resetResultErrors = function () {
        $http.get($scope.M.dataSource + 'result/reseterrors');
        $scope.M.engines.forEach(function (en) {
            if (!en.isSelected) return;
            $scope.M.result[en.engineid].forEach( function(res){
                if (res.status=='OK' || res.status=='pending') return;
                res.value = '';
                res.timems = '';
                res.status = '';
            });
        });

    };


    ///// DELETE

    $scope.deleteEngineResults = function (en) {
        $http.get($scope.M.dataSource + 'result/delresults?engineid=' + en.engineid);
        console.log('delete for engineid=' + en.engineid);
        $scope.M.result[en.engineid].forEach( function(res){
            res.value = '';
            res.timems = '';
            res.status = '';
        });
    };

    $scope.delEn = function (en) {
        $http.get($scope.M.dataSource + 'engine/delete?engineid=' + en.engineid)
            .success(function(){
                $scope.loadEngines();
            });
    };

    $scope.delData = function (da) {
        $http.get($scope.M.dataSource + 'data/delete?dataid=' + da.dataid)
            .success(function(){
                $scope.loadData();
                $scope.loadAssigned();
            });
    };

    $scope.delDataSet = function (ds) {
        $http.get($scope.M.dataSource + 'dataset/delete?datasetid=' + ds.datasetid)
            .success(function(){
                $scope.loadDatasets();
            });
    };




    /**
     * Debug
     */

    $scope.getDirFiles = function () {
        $http.get($scope.M.dataSource + 'debug/dir').success(
            function (data) {
                $scope.M.dirFiles = data.result;
            }
        );
    };

    /**
     * Upload
     */
    $scope.uploadFile = function (file, filename) {
        var fd = new FormData();
        fd.append('file', file);
        fd.append('fname', filename);
        var url = $scope.M.dataSource + 'upload';
        $http.post(url, fd, {
            headers: {'Content-Type': undefined}
        });
    };

    $scope.uploadEngine = function() {
        var f = document.getElementById('fileEnUp').files[0];
        $scope.uploadFile(f, $scope.M.nFile);
        $scope.savenEngine($scope.M.nFile);
    };

    $scope.uploadData = function(){
        var f = document.getElementById('fileDataUp').files[0];
        $scope.uploadFile(f, $scope.M.nData);
        $scope.savenData($scope.M.nData);
    };




    /**
     * INIT
     */
    $scope.loadCategories();





    /**
     * WATCH
     */
    $scope.$watch('M.selCat', function() {
        if ($scope.M.selCat === undefined) return;
        $scope.loadEngines();
        $scope.loadData();
        $scope.loadDatasets();
    });

    $scope.$watch('M.selDS', function() {
        $scope.loadAssigned();
    })

});

