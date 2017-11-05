var express = require('express');
var aws = require('aws-sdk');
const router = express.Router();
const pool = require('../config/db_pool');
var bodyParser = require('body-parser');
var datetime = require('node-datetime');
var app = express();

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

var dt = datetime.create();
var year = dt.format('y');
var month = dt.format('m');
var date = dt.format('d');
var hour24 = dt.format('H');
var hour12 = dt.format('I');
var minute = dt.format('M');
var second = dt.format('S');
var am_pm;

if(hour24 > 12) {
  am_pm = 0;
} else {
  am_pm = 1;
}

router.get('/', function(req, res) {
  pool.getConnection(function(err, connection) {
    if(err) console.log('getConnection err: ',err);
    else {
      var email = req.param('email');
      var body = req.param('body');
      var type = req.param('type');

      var query = 'INSERT INTO quests(uid, content, year, month, day, hour, minute, am_pm, type) VALUES((SELECT id FROM users WHERE email = ?), ?, ?, ?, ?, ?, ?, ?, ?)';
      connection.query(query, [email, body, year, month, date, hour12, minute, am_pm, type], function(err, rows, fields) {
        if(err) {
          console.log('selecting query err: ',err);
        }
        else {
          res.write('success');
          res.send();
        }
      });
      connection.release();
    }
  });
});

module.exports = router;
