var express = require('express');
var aws = require('aws-sdk');
const router = express.Router();
const pool = require('../config/db_pool');
var bodyParser = require('body-parser');
var FCM = require('fcm-push');

var serverKey = 'AAAAsj7w-60:APA91bGpiCxpOPUF7hqmle0syS0D2-OoNyNWZaXvkr2UpKDghFPwdqywpbXw7bo7HluA6loNBzCivSS-yithXR-uA-xjC4tUYQByxp9xd45jYeMuZrOrVn8klgCRFEDKp5jc-uV1HE4f';
var fcm = new FCM(serverKey);
var email;
var title;
var body;
var type;

//router.get('/', function(req, res){
router.get('/', function(req, res){
  pool.getConnection(function(err, connection){
    if(err) console.log('getConnection err: ',err);
    else {
      email = req.param('email');
      title = 'FloMate';
      type = req.param('type');

      if(type == 1) {
        body = '목이 말라요!'
      }
      else if(type == 2) {
        body = '너무 추워요!'
      }
      else if(type == 3) {
        body = '너무 어두워요...'
      }

      var query = 'SELECT token FROM notifications WHERE uid = (SELECT id FROM users WHERE email = ?)';
      connection.query(query, [email], function(err, rows, fields){
        if(err) {
          console.log('selecting query err: ',err);
        }
        else {
          token = rows[0].token;
          var message = {
            to: token,
            collapse_key: 'your_collapse_key',
            data: {
              your_custom_data_key: 'Updates Available'
            },
            notification: {
              title: title,
              body: body
            }
          };

          //callback style
          fcm.send(message, function(err, response){
            if (err) {
              console.log("Something has gone wrong!");
            } else {
              console.log("Successfully sent with response: ", response);
              res.redirect('/regquest?email='+email+'&body='+body+'&type='+type);
              connection.release();
            }
          });
        }
      });
    }
  });
});

module.exports = router;
