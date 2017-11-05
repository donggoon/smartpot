var express = require('express');
const router = express.Router();

var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var aws = require('./routes/aws');
var index = require('./routes/index');
var users = require('./routes/users');

var regplant = require('./routes/regplant');
var loadplant = require('./routes/loadplant');
var loadquest = require('./routes/loadquest');
var regtoken = require('./routes/regtoken');
var regquest = require('./routes/regquest');
var regemail = require('./routes/regemail');
var register = require('./routes/register');
var login = require('./routes/login');
var regsensor = require('./routes/regsensor');
var push = require('./routes/push');
var loadsensor = require('./routes/loadsensor');
var modplant = require('./routes/modplant');

var app = express();

// view engine setup
// app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', index);
app.use('/aws', aws);
app.use('/loadplant', loadplant);
app.use('/loadquest', loadquest);
app.use('/regplant', regplant);
app.use('/regtoken', regtoken);
app.use('/regquest', regquest);
app.use('/regemail', regemail);
app.use('/register', register);
app.use('/login', login);
app.use('/regsensor', regsensor);
app.use('/push', push);
app.use('/loadsensor', loadsensor);
app.use('/modplant', modplant);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});
module.exports = router;
module.exports = app;

app.listen(3000, function(){
  console.log('Connected 3000 Port!');
});
