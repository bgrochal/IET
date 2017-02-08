var url = require('url');
var http = require('http');

var ps = require('ps-node');
var psjson = require('psjson');

var addon = require('bindings')('hello')
 
http.createServer(function(request, response) {
	
	var url_parts = url.parse(request.url, true);

	if(url_parts.pathname == '/uid') {
		console.log("UID request.");

		psjson.ps('ps -le | grep nodejs', function(err, uid) {
			response.writeHead(200, {'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*'});
			psjson.ps('ps -le | awk \'{print $4, $3}\' | grep ^' + url_parts.query.pid + '\' \'', function(err, puid) {
				if(puid) {
					if(puid.headers[1] == uid.headers[2])
						response.end(JSON.stringify({match: 1}));
					else
						response.end(JSON.stringify({match: 0}));
				}
				else
					response.end(JSON.stringify({match: 0}));
			});
		});
	}
	else if(url_parts.pathname == '/listing') {
		console.log("Listing request.");

		psjson.ps('ps -eo %p%P%c%U', function(err, data) {
			response.writeHead(200, {'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*'});
			response.end(JSON.stringify(data.rows));			
		});
	}
	else if(url_parts.pathname == '/killing') {
		console.log("Killing request.");
		ps.kill('\'' + url_parts.query.pid + '\'', function(err) {
			if(err) {
				throw new Error(err);
				response.writeHead(200, {'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*'});
				response.end(JSON.stringify({message: 'failed'}));
			}
			else {
				response.writeHead(200, {'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*'});
				response.end(JSON.stringify({message: 'killed'}));
			}
		});
	}
	else if(url_parts.pathname = '/addon') {
		if(url_parts.query.id == 'first') {
			response.writeHead(200, {'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*'});
			response.end(JSON.stringify({message: addon.hello()}));
		}
	}
	
}).listen(8080);