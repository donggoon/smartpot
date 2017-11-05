var express = require('express');
var aws = require('aws-sdk');
const router = express.Router();
const pool = require('../config/db_pool');
var bodyParser = require('body-parser');
var app = express();

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// router.get('/', function(req, res){
router.post('/', function(req, res){
  pool.getConnection(function(err, connection){
    if(err) console.log('getConnection err: ',err);
    else{
      var humi = req.body.humi;
      var light = req.body.light;
      var temp = req.body.temp;

      var type = 1;
      var email = 'dkkim04@naver.com';

      var query = 'UPDATE monitering SET humi=?, light=?, temp=?, email=? WHERE id = 1';
      connection.query(query, [humi, light, temp, email], function(err, rows){
        if(err) console.log('selecting query err: ',err);
        else {
          // res.redirect('/loadsensor?email='+email+'&humi='+humi+'&light='+light+'&temp'+temp);
          if(humi < 400) {
            type = 1;
            res.redirect('/push?email='+email+'&type='+type);
          }
          else if(light > 500) {
            type = 2;
            res.redirect('/push?email='+email+'&type='+type);
          }
          else if(temp < 30) {
            type = 3;
            res.redirect('/push?email='+email+'&type='+type);
          }
          else {
            res.write('ok');
            res.send();
          }
        }
        connection.release();
      });
    }
  });
});

module.exports = router;
