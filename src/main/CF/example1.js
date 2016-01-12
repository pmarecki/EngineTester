var x = readline().split(' ');
var n = parseInt(x[0]);
var bx = parseInt(x[1]);
x = readline().split(' ');
var a = [];
for(var i = 0; i < n; i++) {
    a[i] = parseInt(x[i]);
}
x = readline().split(' ');
var m = parseInt(x[0]);
var by = parseInt(x[1]);
x = readline().split(' ');
var b = [];
for(var i = 0; i<m;i ++) {
    b[i] = parseInt(x[i]);
}

if(a1 < a2) print("<");
else if(a1 == a2) print("=");
else print(">");

var solve = function(a, n, m) {
    var ans = 0;
    for(var i=0;i<n;i++) {
        for(var j=0;j<m;j++) {
            if(a[i][2*j] == 1 || a[i][2*j+1] == 1) {
                ans ++;
            }
        }
    }
    return ans;
};

function gcd(a, b) {
    var r = a % b;
    while (r !== 0) {
        a = b;
        b = r;
        r = a % b;
    }
    return b;
}


var a = [];
a.push(b);

var count, firstLetter, i, length, letter, map, name, origin, ref, ref1, ref2, ref3, secondLetter;

ref = readline().split(' '), length = ref[0], count = ref[1];

name = readline();

map = [];

for (letter = i = ref1 = 'a'.charCodeAt(), ref2 = 'z'.charCodeAt(); ref1 <= ref2 ? i <= ref2 : i >= ref2; letter = ref1 <= ref2 ? ++i : --i) {
    map[String.fromCharCode(letter)] = String.fromCharCode(letter);
}

while (count--) {
    ref3 = readline().split(' '), firstLetter = ref3[0], secondLetter = ref3[1];
    for (origin in map) {
        if (map[origin] === firstLetter) {
            map[origin] = secondLetter;
        } else if (map[origin] === secondLetter) {
            map[origin] = firstLetter;
        }
    }
}

print(((function() {
    var j, len, results;
    results = [];
    for (j = 0, len = name.length; j < len; j++) {
        letter = name[j];
        results.push(map[letter]);
    }
    return results;
})()).join(''));

