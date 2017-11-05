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
      var email = req.body.email;
      var name = req.body.name;
      var birthYear = req.body.birthYear;
      var birthMonth = req.body.birthMonth;
      var birthDay = req.body.birthDay;
      var type = req.body.type;
      var level = req.body.level;

      var query = 'INSERT INTO plants(uid, name, year_of_birth, month_of_birth, day_of_birth, type, level)' +
                  'VALUES((SELECT id FROM users WHERE email = ?), ?, ?, ?, ?, ?, ?)';
      connection.query(query, [email, name, birthYear, birthMonth, birthDay, type, level], function(err, rows){
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
