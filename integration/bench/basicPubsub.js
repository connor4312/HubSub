var Benchmark = require('benchmark');
var Redis = require('ioredis');
var cryto = require('crypto');

var suite = new Benchmark.Suite;
var bytes = cryto.randomBytes(1024);
var util = require('../util');


var bench = suite
    .add('HubSub publish 1kb event', {
        defer: true,
        fn: function (deferred) {
            hClient1.once('message', function () {
                deferred.resolve();
            });
            hClient2.publish('chan:1', 'hello!');
        }
    })
    .add('Redis publish 1kb event', {
        defer: true,
        fn: function (deferred) {
            rClient1.once('message', function () {
                deferred.resolve();
            });
            rClient2.publish('chan:1', 'hello!');
        }
    })
    .on('cycle', function(event) {
        console.log(String(event.target));
    });

var hClient1, hClient2;
hClient1 = util.client();
hClient2 = util.client();
rClient1 = new Redis();
rClient2 = new Redis();

hClient1.subscribe('chan:1', function (err, count) {
    bench.run({ 'async': false });
});
rClient1.subscribe('chan:1');