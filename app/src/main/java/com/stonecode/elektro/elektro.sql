-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 21, 2017 at 05:58 PM
-- Server version: 5.7.17-0ubuntu0.16.04.1
-- PHP Version: 7.0.13-0ubuntu0.16.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `elektro`
--

-- --------------------------------------------------------

--
-- Table structure for table `has_a_friend`
--

CREATE TABLE `has_a_friend` (
  `user_id` int(11) NOT NULL,
  `friend_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `has_a_friend`
--

INSERT INTO `has_a_friend` (`user_id`, `friend_id`) VALUES
(7099191, 4782369),
(4782369, 9475846);

-- --------------------------------------------------------

--
-- Table structure for table `shared_songs`
--

CREATE TABLE `shared_songs` (
  `user_id` int(11) NOT NULL,
  `song_name` varchar(40) NOT NULL DEFAULT 'xyz'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `shared_songs`
--

INSERT INTO `shared_songs` (`user_id`, `song_name`) VALUES
(4782369, 'uncover'),
(9475846, 'Closer'),
(9475846, 'I took a pill in Ibiza');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `name` varchar(40) NOT NULL,
  `age` int(11) NOT NULL,
  `email` varchar(50) NOT NULL,
  `pass` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `age`, `email`, `pass`) VALUES
(2778072, 'someone2', 12, 'af@adf2.com', '$2y$10$37qO.qmkFt9mrMBBPz4LTeTAWexpS247CEhh1u5vzBjJR4w8g4ewy'),
(4782369, 'someone', 15, 'afd@ad22.com', '$2y$10$UIDhNK..Gr/fjm8mdfrFluMYlvQ9LGQ3q63ZUx3SyuGzrHCBSQgx6'),
(7099191, 'someone2', 11, 'af@adf.com', '$2y$10$N/duNgckdoJWFQts2T8za.c.t.EeqyNoIY/3G0BGpVlB6AWCfQAlu'),
(9475846, 'someone', 15, 'afd@ad1.com', '$2y$10$4eIXgyuM0cZ/DFcweKhI/uJUbpZSj4zbvpVE6nlW30.JTfL.P6zaS');

--
-- Triggers `users`
--
DELIMITER $$
CREATE TRIGGER `age_check` BEFORE INSERT ON `users` FOR EACH ROW BEGIN
    IF new.age<=10 THEN
    	SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Age cannot be less than 11';
    END IF;
    END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `has_a_friend`
--
ALTER TABLE `has_a_friend`
  ADD PRIMARY KEY (`user_id`,`friend_id`),
  ADD KEY `friend_id` (`friend_id`);

--
-- Indexes for table `shared_songs`
--
ALTER TABLE `shared_songs`
  ADD PRIMARY KEY (`user_id`,`song_name`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `email_2` (`email`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `has_a_friend`
--
ALTER TABLE `has_a_friend`
  ADD CONSTRAINT `has_a_friend_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `has_a_friend_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `shared_songs`
--
ALTER TABLE `shared_songs`
  ADD CONSTRAINT `shared_songs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
