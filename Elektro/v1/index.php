<?php

error_reporting(-1);

require __DIR__.'/../vendor/autoload.php';
require __DIR__.'/../db_handle/db_handler.php';

use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

//$config=['settings'=>['displayErrorDetails'=>true],];

$app=new \Slim\App(/*$config*/);

$app->post('/register', function (Request $req, Response $res) {
	$dbh=new db_handler();
	$body = $req->getParsedBody();
	$body=$dbh->newUser($body);
	echo $body;
	return $res->withJson($body);
});

$app->post('/new-friend' , function (Request $req, Response $res) {
	// $id=$req->getAttribute('id');
	$dbh=new db_handler();
	$body=$req->getParsedBody();

	$body=$dbh->newFriend($body);
	return $res->withJson($body);
});

$app->post('/new-song' , function (Request $req, Response $res) {
	// $id=$req->getAttribute('id');
	$dbh=new db_handler();
	$body=$req->getParsedBody();

	$body=$dbh->newSong($body);
	return $res->withJson($body);
});

$app->get('/my-friends/{u_id}' , function (Request $req, Response $res) {
	$id=$req->getAttribute('u_id');
	$dbh=new db_handler();
	$body=$dbh->getFriends($id);
	return $res->withJson($body);//body is json_encoded
});

$app->get('/friend-song/{f_id}' , function (Request $req, Response $res) {
	$id=$req->getAttribute('f_id');
	$dbh=new db_handler();
	$body=$dbh->getSongsOfFriend($id);
	return $res->withJson($body);//body is json_encoded
});

$app->post('/login' , function (Request $req, Response $res) {
	// $id=$req->getAttribute('id');
	$dbh=new db_handler();
	$body=$req->getParsedBody();
	
	$body=$dbh->login($body);
	return $res->withJson($body);
});

$app->run();