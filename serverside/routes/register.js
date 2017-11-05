var express = require('express');
var aws = require('aws-sdk');
const router = express.Router();
const pool = require('../config/db_pool');
var bodyParser = require('body-parser');
var crypto = require('crypto');
var uniqid = require('uniqid');
var sha1 = require('sha1');
// var random = require('random');

var app = express();

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

router.post('/', function(req, res){
  pool.getConnection(function(err, connection){
    if(err) console.log('getConnection err: ',err);
    else{
      var name = req.body.name;
      var email = req.body.email;
      var password = req.body.password;

      var uuid = uniqid();
      var salt = sha1(Math.random());
      salt = salt.substr(0, 10);
      var encrypted_password = ssha(password, salt);

      var query = 'INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())';
      connection.query(query, [uuid, name, email, encrypted_password, salt], function(err, rows){
        if(err) console.log('selecting query err: ',err);
        else {
           // var parse = JSON.stringify(rows);
	   res.write('success');
           res.send();
        }
        console.log(rows);
        connection.release();
      });
    }
  });
});

function ssha(cleartext, salt) {
  var sum = crypto.createHash('sha1');
  ( typeof(salt) == 'undefined') ? salt = new Buffer(crypto.randomBytes(20)).toString('base64') : salt = salt;
  sum.update(cleartext);
  sum.update(salt);
  var digest = sum.digest();
  var ssha = '{SSHA}' + new Buffer(digest+salt,'binary').toString('base64');
  return ssha;
}

module.exports = router;
