var spawn = require('child_process').spawn;
var path = require('path');
var fs = require('fs');


var hubsub;
before(function (done) {
    function spawnHubSub() {
        var jar = getLatestFile('../build/libs');
        hubsub = spawn('java', ['-jar', jar]);
        hubsub.stderr.pipe(process.stderr);
        hubsub.stdout.pipe(process.stdout);

        var started = false;
        hubsub.stdout.on('data', function () {
            if (!started) {
                setTimeout(done, 1000);
                started = true;
            }
        });
    }

    if (process.argv.indexOf('--rebuild') !== -1) {
        var proc = spawn('gradle',  ['jar', '--project-dir=../']);
        proc.stdout.pipe(process.stdout);
        proc.stderr.pipe(process.stderr);
        proc.on('close', spawnHubSub);
    } else {
        spawnHubSub();
    }
});

after(function (done) {
    hubsub.kill('SIGINT');
    hubsub.on('exit', function () {
        done();
    });
});

beforeEach(function () {
    var clients = this._genClients = [];
    this.client = function () {
        var client = require('../util').client();
        clients.push(client);
        return client;
    };
});

afterEach(function (done) {
    var waiting = this._genClients.length;
    this._genClients.forEach(function (client) {
        client.quit();
        client.on('end', function () {
            if (--waiting === 0) {
                done();
            }
        });
    });
});

function getLatestFile (dir) {
    var files = fs.readdirSync(dir);

    var maxFile, maxTime = 0;
    for (var i = 0; i < files.length; i++) {
        files[i] = path.join(dir, files[i]);
        var time = fs.statSync(files[i]).mtime;
        if (time > maxTime) {
            maxFile = files[i];
            maxTime = time;
        }
    }

    return maxFile;
}