/*
 * Segment tree in JS.
 *
 * thought: segment tree + devide and counque
 */

var maxn = 1000100;
var tmax = new Array([maxn*4]);
var tmin = new Array([maxn*4]);
var a = new Array([maxn]);
var n = parseInt(readline());
var x = readline().split(' ');

for(var i=0;i<n;i++) {
    a[i] = parseInt(x[i]);
}

function dfs(l, r, id) {
    if(l == r) {
        tmax[id] = a[l];
        tmin[id] = a[l];
        return;
    }
    var mid = ~~((l+r)/2);
    dfs(l, mid, id*2);
    dfs(mid+1, r, id*2+1);
    tmax[id] = Math.max(tmax[id*2] , tmax[id*2+1]);
    tmin[id] = Math.min(tmin[id*2] , tmin[id*2+1]);
    return;
}

dfs(0, n-1, 1);

function getMax(L, R, l, r, id) {
    if(L <= l && r <= R) return tmax[id];
    var mid = ~~((l+r)/2);
    var t1 = -1;
    var t2 = -1;
    if(mid >= L) t1 = getMax(L, R, l, mid, id*2);
    if(R > mid) t2 = getMax(L, R ,mid+1, r, id*2+1);
    if(t1 == -1) return t2;
    else if(t2 == -1) return t1;
    else return Math.max(t1, t2);
}

function getMin(L, R, l, r, id) {
    if(L <= l && r <= R) return tmin[id];
    var mid = ~~((l+r)/2);
    var t1 = -1;
    var t2 = -1;
    if(mid >= L) t1 = getMin(L, R, l, mid, id*2);
    if(R > mid) t2 = getMin(L, R ,mid+1, r, id*2+1);
    if(t1 == -1) return t2;
    else if(t2 == -1) return t1;
    else return Math.min(t1, t2);
}

// var test = getMax(0, 2, 0, n-1, 1);
// print(test);

function check(L, R) {
    var maxx = getMax(L, R, 0, n-1, 1);
    var minn = getMin(L, R, 0, n-1, 1);
    if(maxx - minn <= 1) return true;
    return false;
}

function get(start) {
    var l = start;
    var r = n-1;
    while(l <= r) {
        var mid = ~~((l+r)/2);
        if(check(start, mid) == true) l = mid+1;
        else r = mid - 1;
    }
    return l - start;
}

// for(var i = 0; i < n; i++) {
//     print(getMax(i,n-1, 0, n-1, 1), ",", getMin(i,n-1, 0, n-1, 1));
// }
// print(getMin(0, n-1, 0, n-1, 1));

var ans = 0;
for(var i = 0; i<n;i ++) {
    var t = get(i);
    if(t > ans) ans = t;
}
print(ans);