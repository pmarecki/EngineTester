
//run: d8 --harmony_collections a.js


function gcd(a, b) {
    var r = a % b;
    while (r !== 0) {
        a = b;
        b = r;
        r = a % b;
    }
    return b;
}


print('Haha');
var arr = [1,2,3,8,23];
//loop over keys
for(i in a) {
    print('arr[' + i +']=' + a[i]);   //??
}



print('-----------Tablice/Mapa-----------------');


var map = [];
map['a'] = 12;
map['x'] = 11;
map['Abra'] = 33;       //insert ~c++
map['Abra'] = 34;
print('map[abra]: ' + map['Abra']); //access
print('#elems: ' + map.length);     //==0
print('#elems2:' + Object.keys(map).length);    //OK

delete map["Abra"];     //usuwanie

if (map["Abra"]) {
    print('Have "Abra"');
}
print('Map keys:' + Object.keys(map));  //klucze ~Java::keySet

print('----------Lista-------------')

var lista = [1,4,7,9];
print(lista);
print('idx:' + lista.indexOf(4));

// FILTERS
var listaBez7 = lista.filter(function (el) {
    return el!=7;
});
print('Lista bez 7:' + listaBez7);

print('----------Set-------------')

var _set = [];
lista.forEach(function (i) {
    _set[i]=1;
});

if (_set[4]) print("have 4");
if (_set[5]) print("have 5");
delete _set[4];
if (_set[4]) print("have 4");
    else print("dont have 4");

print("loop keys in _set:");
for(var key in _set){
    print(key);
}
print('Keys in _set:' + Object.keys(_set));
print(Object.keys(_set).join(','));



aa = 12;
bb = 6;
print(~~(aa / bb)); // int of division


/**
 * Sorting pairs
 */
print('----------Pair-------------')
A=[];
for(var i=0;i<10;++i){
    var score = 10-(i-3)*(i-3);
    A.push({id:i, val:score});
}
A.sort(function (a, b) {
    return a.val - b.val;
});

A.forEach(function (i) {
    //print(i.id + ' --> ' + i.val);
});

