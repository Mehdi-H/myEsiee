<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-11 11:00:16
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-12 19:22:18
 */

try {
    $conn = new PDO('mysql:host=localhost;dbname=eroom;charset=UTF8', 'root', 'MyMVX2');
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch(PDOException $e) {
    echo 'ERROR: ' . $e->getMessage();
}

?>