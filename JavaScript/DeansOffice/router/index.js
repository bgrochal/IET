/* Opening SQLite3 Database. */
var sqlite3 = require("sqlite3").verbose();
var fs = require("fs");

var file = "public/office.db3";
var db = new sqlite3.Database(file);

/* Handling routes. */

module.exports=function(app)
{
	app.get('/', function(req, res) {
		db.all('SELECT Name FROM Groups', function(error, rows) {
			if(error != null)
				res.status(500).send('Sorry, an error has occurred. Stacktrace below. \n' + error);
			else {
				var responseArgs = "";
				for(var i=0; i<rows.length; i++)
					responseArgs += (rows[i].Name + '\n');
				res.render('index', {groupNames: responseArgs.trim()});
			}
		});
	});
	
	app.get('/student', findStudent, findGroup, findClasses, findGrades, findStudentsAndLectures, findAvailableLectures, renderStudent);
	
	function findStudent(req, res, next) {
		var dbRequest = 'SELECT * FROM Students WHERE IDCard = \'' + req.query['id'] + '\' AND FirstName = \'' + req.query['firstname'] + '\' AND LastName = \'' + req.query['lastname'] + '\' AND GroupID = \'' + req.query['group'] + '\'';
		
		db.all(dbRequest, function(error, rows) {
			if(rows.length !== 0) {
				req.students = rows;
				return next();
			}
			res.render('incorrect_student');
		});
	}
	
	function findGroup(req, res, next) {
		dbRequest = 'SELECT * FROM Groups WHERE Name = \'' + req.query['group'] + '\''; 
			
		db.all(dbRequest, function(error, rows) {
			req.groups = rows;
			next();
		});
	}
	
	function findClasses(req, res, next) {
		dbRequest = 'SELECT * FROM Classes WHERE GroupName = \'' + req.query['group'] + '\' ORDER BY Date';
		
		db.all(dbRequest, function(error, rows) {
			req.classes = rows;
			next();
		});
	}
	
	function findGrades(req, res, next) {
		if(typeof req.query['order'] !== 'undefined') {
			dbRequest = 'SELECT * FROM Grades WHERE StudentID = \'' + req.query['id'] + '\' AND GroupID = \'' + req.query['group'] + '\' ORDER BY ' + req.query['order'];
		}
		else {
			dbRequest = 'SELECT * FROM Grades WHERE StudentID = \'' + req.query['id'] + '\' AND GroupID = \'' + req.query['group'] + '\' ORDER BY Date';
		}
		
		db.all(dbRequest, function(error, rows) {
			req.grades = rows;
			next();
		});
	}
	
	function findStudentsAndLectures(req, res, next) {
		dbRequest = 'SELECT * FROM Students LEFT JOIN Lectures ON Students.IDCard = Lectures.StudentID AND Students.GroupID = Lectures.GroupID WHERE Students.GroupID = \'' + req.query['group'] + '\''
		
		db.all(dbRequest, function(error, rows) {
			req.studentsList = rows;
			next();
		});
	}
	
	function findAvailableLectures(req, res, next) {
		dbRequest = 'SELECT * FROM Lectures WHERE StudentID IS NULL AND GroupID = \'' + req.query['group'] + '\'';
		
		db.all(dbRequest, function(error, rows) {
			req.lectures = rows;
			next();
		});
	}
	
	function renderStudent(req, res) {
		res.render('student', {IDCard: req.students[0].IDCard, group: req.groups[0].Name, firstName: req.students[0].FirstName, lastName: req.students[0].LastName, lecture: req.students[0].LectureID, studentNews: req.students[0].News, startHour: req.groups[0].StartHour, endHour: req.groups[0].EndHour, professor: req.groups[0].Professor, status: req.groups[0].LectureStatus, groupNews: req.groups[0].News, classes: req.classes, grades: req.grades, list: req.studentsList, lectures: req.lectures, url_addr: req.originalUrl});
	}

}