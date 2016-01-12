var x = readline().split(' ');
var n = parseInt(x[0]);
var m = parseInt(x[1]);

var ans = 0;
for(var i=0;i<n;++i) {
    x = readline().split(' ');
    for(var j=0;j<m; ++j) {
        if (x[j*2]=='1' || x[j*2+1]=='1') ++ans;
    }
}
print(ans);