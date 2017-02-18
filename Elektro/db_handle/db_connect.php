<?php

require __DIR__.'/db_config.php';
class db_connect {
	private $conn;

	function __construct() {

	}
	public function connect() {
		$this->conn=new mysqli(DB_HOST,USER,PASSWORD,DB_NAME);
		if ($this->conn->connect_errno) {
			die('Connect Error: '.$this->conn->connect_errno);
		}
		return $this->conn;
	}

	public function disconnect() {
		$this->conn->close();
	}

	function __destruct() {

	}
}
