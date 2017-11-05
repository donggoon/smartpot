var express = require('express');
var aws = require('aws-sdk');
const router = express.Router();
const pool = require('../config/db_pool');
var bodyParser = require('body-parser');
var app = express();

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

router.post('/', function(req, res){
  pool.getConnection(function(err, connection){
    if(err) console.log('getConnection err: ',err);
    else{
      var token = req.body.token;
      var email = req.body.email;

      var query = 'INSERT INTO notifications(uid, token) VALUES((SELECT id FROM users WHERE email = ?), ?) ON DUPLICATE KEY UPDATE token = ?';

      connection.query(query, [email, token, token], function(err, rows){
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

module.exports = router;
