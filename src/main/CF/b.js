var res = [[]];

var e1 = {id: 5, f: 'alpha'};
var d1 = {id: 1, f: 'theta'};


res[0][1] = 12;
print(res[0][2]);

print(e1.id);
var a = [];

a['aaa'] = [];  //initialize for each engine
a['aaa']['bbb'] = 12;


a[e1.id] = [];
a[e1.id][d1.id] = 333;

a[e1.id] = [];

print(a[e1.id][d1.id]);