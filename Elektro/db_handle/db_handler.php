<?php

require __DIR__.'/db_connect.php';

class db_handler {

	private $mysqli_link;
	private $dbcon;
	function __construct() {
		$this->dbcon=new db_connect();
		// echo "connect";
		$this->mysqli_link=$this->dbcon->connect();

	}

	private function isUserPresent($data) {
		$stmt=$this->mysqli_link->prepare("SELECT * FROM users WHERE email=?");
		$stmt->bind_param('s',$data['email']);
		$stmt->execute();
		$stmt->store_result();
		return $stmt->num_rows>0;
	}

	public function newUser($data) {
		$rsp=array();
		$rsp['error']=true;
		$rsp['msg']="Unsuccessful attempt.";

		if ($this->isUserPresent($data)) {
			$rsp['msg']="Email exists";
			return $rsp;
		}
		if (isset($data['pass']))
			$data['pass']=password_hash($data['pass'], PASSWORD_BCRYPT);
		$data['u_id']=random_int(100000+7, 10000000+11);
		$stmt=$this->mysqli_link->prepare("INSERT INTO users VALUES(?,?,?,?,?)");
		$stmt->bind_param('isiss',$data['u_id'],$data['name'],$data['age'],$data['email'],$data['pass']);
		if ($stmt->execute()) {
			$rsp['error']=false;
			$rsp['msg']="Registered.";
			$rsp['u_id']=$data['u_id'];
		} else {
			$rsp['msg'] = "Error occurred";
		}

		$stmt->close();
		return $rsp;
	}

	private function isAlreadyFriend($data) {

		$stmt=$this->mysqli_link->prepare("SELECT * FROM has_a_friend WHERE user_id=? and friend_id=?");
		$stmt->bind_param('ii',$data['f_id'],$data['u_id']);
		$stmt->execute();
		$stmt->store_result();
		return $stmt->num_rows>0;
	}
	public function newFriend($data) {
		$rsp=array();
		$rsp['error']=true;
		$rsp['msg']="Unsuccessful attempt";
		
		if ($this->isAlreadyFriend($data)) {
			$rsp['msg']="Already friend";
			return $rsp;
		}
		$stmt=$this->mysqli_link->prepare("INSERT INTO has_a_friend VALUES(?,?)");
		$stmt->bind_param('ii',$data['u_id'],$data['f_id']);
		if ($stmt->execute()) {
			$rsp['error']=false;
			$rsp['msg']="You are now friends.";
		} else {
			$rsp['msg']="Error occurred";
		}

		$stmt->close();
		return $rsp;
	}

	public function newSong($data) {
		$rsp=array();
		$rsp['error']=true;
		$rsp['msg']="Unsuccessful attempt.";
		
		$stmt=$this->mysqli_link->prepare("INSERT INTO shared_songs VALUES(?,?)");
		$stmt->bind_param('is',$data['u_id'],$data['song_name']);
		if ($stmt->execute()) {
			$rsp['error']=false;
			$rsp['msg']="Song added.";
		} else {
			$rsp['msg']="Error occurred";
		}

		$stmt->close();
		return $rsp;
	}

	public function getFriends($id) {
		mysqli_report(MYSQLI_REPORT_ERROR);
		$resp = array(
			"error" => true,
			"msg" => "An error occurred"
			);

		$stmt=$this->mysqli_link->prepare("SELECT * FROM has_a_friend WHERE user_id=? OR friend_id=?");
		$stmt->bind_param('ii',$id,$id);
		$friend_id_list=array();
		if ($stmt->execute()) {
			$resp['error']=false;
			$resp['msg']="Request completed";
			$res=$stmt->get_result();

			while($row = $res->fetch_array(MYSQLI_ASSOC)) {
				echo $row['user_id'].',';
				if ($row['user_id']==$id) {
					array_push($friend_id_list, $row['friend_id']);
				} else {
					array_push($friend_id_list, $row['user_id']);
				}
			}
		} else {
			$resp['msg']="Error occurred";
		}
		$stmt->close();

		$frnds=array();
		$tmp=array();
		if (count($friend_id_list)>0) {
			
			foreach ($friend_id_list as $f_id) {
				$stmt=$this->mysqli_link->prepare("SELECT user_id,name FROM users WHERE user_id=?");
				$stmt->bind_param('i',$f_id);
				$stmt->execute();
				$stmt->bind_result($tmp['f_id'],$tmp['name']);
				$stmt->fetch();
				$stmt->close();

				array_push($frnds,$tmp);
			}
		}

		$resp['friends']=$frnds;
		return $resp;
	}

	public function getSongsOfFriend($id) {
		$resp = array(
			"error" => true,
			"msg" => "An error occurred"
			);

		$stmt=$this->mysqli_link->prepare("SELECT song_name FROM shared_songs WHERE user_id=?");
		$stmt->bind_param('i',$id);

		if ($stmt->execute()) {
			$resp['error']=false;
			$resp['msg']="Request completed";
			$res=$stmt->get_result();
			$resp['songs']=array();
			while($row = $res->fetch_array(MYSQLI_ASSOC)) {
				//echo $row;
				array_push($resp['songs'],$row['song_name']);
			}
		} else {
			$resp['msg']="Error occurred";
		}
		$stmt->close();
		return $resp;
	}

	public function userLogin($data) {
		$rsp['error']=true;
		$rsp['msg'] = "Unsuccessful login";
		$stmt=$this->mysqli_link->prepare("SELECT pass FROM users WHERE user_id=?");
		$stmt->bind_param('i',$data['u_id']);
		$stmt->execute();
		$stmt->store_result();
		if ($stmt->num_rows>0) {
			$stmt->bind_result($pHash);
			// $pHash = ($stmt->fetch_row())['pass'];
			$stmt->fetch();
			// echo "$pHash";
			$rsp['error']=false;
			if (password_verify($data['pass'], $pHash)) {
				$rsp['msg'] = "Login successful";
			} else $rsp['msg'] = "Incorrect Password";
		} else $rsp['msg'] = "User does not exist";

		$stmt->close();
		return $rsp;
	}
	
	function __destruct() {
		$this->dbcon->disconnect();
	}
}

// $db=new db_handler();
// print_r($db->newUser(array(
// "name" => "someone",
// "age" => 15,
// "email" => "afd@ad1.com",
// "pass" => "you ain't got no chill"
// 	)));

// print_r($db->newUser(array(
// "name" => "someone",
// "age" => 15,
// "email" => "afd@ad22.com",
// "pass" => "you ain't got no chill"
// 	)));

// print_r($db->newFriend(array(
// "u_id" => 4782369,
// "f_id" => "9475846"
// 	)));

// print_r($db->newSong(array(
// 'u_id' => "9475846",
// 'song_name' => "Closer"
// 	))) ;

// print_r($db->userLogin(array(
// 'u_id' => 9475846,
// 'pass' => "you ain't got no chill"
// 	)));
// print_r($db->getFriends(4782369));
// print_r($db->getSongsOfFriend(4782369));
