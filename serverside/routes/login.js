var express = require('express');
var aws = require('aws-sdk');
const router = express.Router();
const pool = require('../config/db_pool');
var bodyParser = require('body-parser');
var crypto = require('crypto');
var uniqid = require('uniqid');

var app = express();

app.use(bodyParser.urlencoded({ extended: false }));
// parse application/json
app.use(bodyParser.json());

router.post('/', function(req, res){
  pool.getConnection(function(err, connection){
    if(err) console.log('getConnection err: ',err);
    else{
      var email = req.body.email;
      var password = req.body.password;

      var query = 'SELECT * FROM users WHERE email = ?';
      connection.query(query, [email], function(err, rows, fields){
	if(err) {
           console.log('selecting query err: ',err);
        }
        else {
           // var parse = JSON.stringify(rows);
	   // console.log(rows);
	   var encrypted = ssha(password, rows[0].salt);
           var check = checkssha(password, encrypted);
	   if(check) {
		res.status(200).send(rows);
	   }
	   else {
		res.write('failed');
		res.send();
	   }
        }
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

function checkssha(cleartext, hash) {
  var sum = crypto.createHash('sha1');
  if (hash.substr(0,6) != '{SSHA}') {
    console.error("Not a SSHA hash");
    return false;
  }
  var bhash = new Buffer(hash.substr(6),'base64');
  var salt  = bhash.toString('binary',20);
  var newssha = ssha(cleartext, salt);
  return (hash == newssha);
}

module.exports = router;
