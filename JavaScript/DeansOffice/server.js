/* Using extensions from dependencies. */
var express = require('express');
var app = express();

require('./router/index')(app);

/* Adding EmbeddedJS views. */
app.set('views',__dirname + '/views');
app.set('view engine', 'ejs');

app.engine('html', require('ejs').renderFile);

/* Adding ./public directory for other files. */
app.use(express.static(__dirname + '/public'));

/* Listening on a port. */
var server=app.listen(3000,function(){
	console.log("Express is running on port 3000");
});

/* Server is working. Routed to: './router/index.js'. */