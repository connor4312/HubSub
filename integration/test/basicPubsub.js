var expect = require('chai').expect;
var assert = require('chai').assert;

describe('core pubsub', function () {
    describe('simple patterns', function () {
        it('works with basic subscription', function (done) {
            var client1 = this.client();
            var client2 = this.client();

            client1.on('message', function (channel, message) {
                expect(channel).to.equal('chan:1');
                expect(message).to.equal('hello!');
                done();
            });

            client1.subscribe('chan:1', function (err, count) {
                expect(err).to.be.null;
                client2.publish('chan:1', 'hello!');
            });
        });

        it('unsubscribes correctly', function (done) {
            var client1 = this.client();
            var client2 = this.client();

            client1.on('message', function () {
                assert.fail();
            });

            client1.subscribe('chan:1', function (err, count) {
                expect(err).to.be.null;
                client1.unsubscribe('chan:1', function (err, count) {
                    expect(err).to.be.null;
                    client2.publish('chan:1', 'hello!');
                    setTimeout(done, 200);
                });
            });
        });
    });
});