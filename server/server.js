//Lets require/import the HTTP module
var http = require('http');
var dispatcher = require('httpdispatcher');

var express = require('express');
var fs = require('fs');
var bodyParser = require('body-parser');
var app = express();

app.use(bodyParser.urlencoded({
	extended: true
}));

app.use(bodyParser.json());
const PORT=8080; 


//mongodb
var mongodb = require('mongodb');
var MongoClient = mongodb.MongoClient;
var mongodb_url = 'mongodb://localhost:27017/database3';

// MongoClient.connect(mongodb_url, function (err, db) {
//   if (err) {
//     console.log('Unable to connect to the mongoDB server. Error:', err);
//   } else {
//     console.log('Connection established to', mongodb_url);
//     var calculator= db.collection('calculator');
//     db.close();
//   }
// });


app.get('/home', function(req, res){
	res.setHeader('Content-Type', 'application/json');
	res.send(JSON.stringify({"name": "Lucky Sahani"}));
})

app.post('/add', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var num2 = parseFloat(req.body.num2);
	var add_result ;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var add= db.collection('add');
			add.find({"num1": num1 , "num2": num2} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in add database with ',num1,' + ',num2,' = ', result[0].num3);
					add_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "add": add_result}));
					db.close();
				} 
				else {
					add_result = num1+num2;
					console.log('No match found !! Inserting into Add collection: ',num1,' + ',num2,' = ',add_result);
					add.insert({"num1": num1 , "num2": num2 , "num3": add_result });
					res.send(JSON.stringify({"status": "true", "add": add_result}));
					db.close();
				}
			});
		}
	});
});

app.post('/multiply', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var num2 = parseFloat(req.body.num2);
	var multiply_result;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var multiply= db.collection('multiply');
			multiply.find({"num1": num1 , "num2": num2} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in add database with ',num1,' * ',num2,' = ', result[0].num3);
					multiply_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "multiply": multiply_result}));	
					db.close();
				} 
				else {
					multiply_result = num1*num2;
					console.log('No match found !! Inserting into Multiply collection: ',num1,' * ',num2,' = ',multiply_result);
					multiply.insert({"num1": num1 , "num2": num2 , "num3": multiply_result });
					res.send(JSON.stringify({"status": "true", "multiply": multiply_result}));	
					db.close();
				}
			});
			
		}
		
	});
});

app.post('/divide', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var num2 = parseFloat(req.body.num2);
	var divide_result;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var divide= db.collection('divide');
			divide.find({"num1": num1 , "num2": num2} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in Divide database with ',num1,' / ',num2,' = ', result[0].num3);
					divide_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "divide": divide_result}))
					db.close();
				} 
				else {
					if (num2 == 0) {
						divide_result = "undefined";
					}
					else{
						divide_result = num1/num2;
					}
					console.log('No match found !! Inserting into Divide collection: ',num1,' / ',num2,' = ',divide_result);
					divide.insert({"num1": num1 , "num2": num2 , "num3": divide_result });
					res.send(JSON.stringify({"status": "true", "divide": divide_result}))
					db.close();
				}
			});
		}
	});
});

app.post('/subtract', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var num2 = parseFloat(req.body.num2);
	var subtract_result;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var subtract= db.collection('subtract');
			subtract.find({"num1": num1 , "num2": num2} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in Subtract database with ',num1,' - ',num2,' = ', result[0].num3);
					subtract_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "subtract": subtract_result}));	
					db.close();
				} 
				else {
					subtract_result = num1 - num2;
					console.log('No match found !! Inserting into Subtract collection: ',num1,' - ',num2,' = ',subtract_result);
					subtract.insert({"num1": num1 , "num2": num2 , "num3": subtract_result });
					res.send(JSON.stringify({"status": "true", "subtract": subtract_result}));	
					db.close();
				}
			});		
		}	
	});
});

app.post('/sin', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var sin_result;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var sin= db.collection('sin');
			sin.find({"num1": num1} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in sin database with sin(',num1,') = ', result[0].num3);
					sin_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "sin": sin_result}));	
					db.close();
				} 
				else {
					sin_result = Math.sin(num1) ;
					console.log('No match found !! Inserting into sin collection: sin(',num1,') = ', sin_result);
					sin.insert({"num1": num1 , "num3": sin_result });
					res.send(JSON.stringify({"status": "true", "sin": sin_result}));	
					db.close();
				}
			});		
		}	
	});
});

app.post('/cos', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var cos_result;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var cos= db.collection('cos');
			cos.find({"num1": num1} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in cos database with cos(',num1,') = ', result[0].num3);
					cos_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "cos": cos_result}));	
					db.close();
				} 
				else {
					cos_result = Math.cos(num1) ;
					console.log('No match found !! Inserting into cos collection: cos(',num1,') = ', cos_result);
					cos.insert({"num1": num1 , "num3": cos_result });
					res.send(JSON.stringify({"status": "true", "cos": cos_result}));	
					db.close();
				}
			});		
		}	
	});
});

app.post('/tan', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var tan_result;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var tan= db.collection('tan');
			tan.find({"num1": num1} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in tan database with tan(',num1,') = ', result[0].num3);
					tan_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "tan": tan_result}));	
					db.close();
				} 
				else {
					tan_result = Math.tan(num1) ;
					console.log('No match found !! Inserting into tan collection: tan(',num1,') = ', tan_result);
					tan.insert({"num1": num1 , "num3": tan_result });
					res.send(JSON.stringify({"status": "true", "tan": tan_result}));	
					db.close();
				}
			});		
		}	
	});
});

app.post('/log', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var log_result;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var log= db.collection('log');
			log.find({"num1": num1} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in log database with log(',num1,') = ', result[0].num3);
					log_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "log": log_result}));	
					db.close();
				} 
				else {
					log_result = Math.log(num1) ;
					console.log('No match found !! Inserting into log collection: log(',num1,') = ', log_result);
					log.insert({"num1": num1 , "num3": log_result });
					res.send(JSON.stringify({"status": "true", "log": log_result}));	
					db.close();
				}
			});		
		}	
	});
});

app.post('/exp', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var exp_result;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var exp= db.collection('exp');
			exp.find({"num1": num1} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in exp database with exp(',num1,') = ', result[0].num3);
					exp_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "exp": exp_result}));	
					db.close();
				} 
				else {
					exp_result = Math.exp(num1) ;
					console.log('No match found !! Inserting into exp collection: exp(',num1,') = ', exp_result);
					exp.insert({"num1": num1 , "num3": exp_result });
					res.send(JSON.stringify({"status": "true", "exp": exp_result}));	
					db.close();
				}
			});		
		}	
	});
});

app.post('/sqrt', function(req, res){
	var num1 = parseFloat(req.body.num1);
	var sqrt_result;
	res.setHeader('Content-Type', 'application/json');

	MongoClient.connect(mongodb_url, function (err, db) {
		if (err) {
			console.log('Unable to connect to the mongoDB server. Error:', err);
		} 
		else {
			// console.log('Connection established to', mongodb_url);
			var sqrt= db.collection('sqrt');
			sqrt.find({"num1": num1} ).toArray(function (err, result) {
				if (err) {
					console.log(err);
				} 
				else if (result.length) {
					console.log('Found result in sqrt database with sqrt(',num1,') = ', result[0].num3);
					sqrt_result =result[0].num3;
					res.send(JSON.stringify({"status": "true", "sqrt": sqrt_result}));	
					db.close();
				} 
				else {
					sqrt_result = Math.sqrt(num1) ;
					console.log('No match found !! Inserting into sqrt collection: sqrt(',num1,') = ', sqrt_result);
					sqrt.insert({"num1": num1 , "num3": sqrt_result });
					res.send(JSON.stringify({"status": "true", "sqrt": sqrt_result}));	
					db.close();
				}
			});		
		}	
	});
});

app.listen(PORT);
console.log('Listening at http://localhost:' + PORT);