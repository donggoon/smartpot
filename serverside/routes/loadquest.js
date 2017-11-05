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
      var query = 'SELECT content, year, month, day, hour, minute, am_pm, type FROM quests WHERE uid = (SELECT id FROM users WHERE email = ?) ORDER BY id DESC';
      connection.query(query, [email], function(err, rows){
        if(err) {
      	   res.status(200).send("no rows");
      	   console.log('selecting query err: ',err);
      	}
        else {
           // var parse = JSON.stringify(rows);
           res.status(200).send(rows);
        }
        console.log(rows);
        connection.release();
      });
    }
  });
});

module.exports = router;
