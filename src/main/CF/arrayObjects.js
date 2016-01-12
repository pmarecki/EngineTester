
a = [];
a[0] = { a:1, b:'aa'};
a[1] = { a:2, b:'bb'};
a[2] = { a:1, b:'aa'};

//Good for @Data objects
print(JSON.stringify(a[0]) == JSON.stringify(a[2]));

print(a[0].a);
var w = a[0];
delete(a[a.indexOf(w)]);        //usuwanie elementów po referencji do obiektu


print(a.length);  //3
print(Object.keys(a).length);//OK

a.forEach(function (i) {
    print(i.b);
});