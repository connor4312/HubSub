var Redis = require('ioredis');

exports.client = function () {
    return new Redis(3221, { enableReadyCheck: false });
};