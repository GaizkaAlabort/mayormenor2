<?php

$DB_SERVER="localhost";
$DB_USER="Xgalabort001";
$DB_PASS="pDFsJxS2q";
$DB_DATABASE="Xgalabort001_mayormenor";

$conexion = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

$json = array();

if(mysqli_connect_errno()){
    echo'ERROR DE CONEXION: ' . mysqli_connect_error();
    exit();

} else {
    if(isset($_POST["nombre"])){
        $nombre = $_POST['nombre'];

        $consulta = "SELECT Contraseña FROM Usuarios WHERE Nombre='{$nombre}'";
        $resultado = mysqli_query($conexion,$consulta);

        if($registro=mysqli_fetch_array($resultado)){
           if(isset($_POST["contraseña"])){
                $contraseña = $_POST['contraseña'];
                if (password_verify($contraseña, $registro[0])) {
                    $json = array('value' => true, 'mens' => 'Coincide contrasena');
                } else {
                    $json = array('value' => false, 'mens' => 'conIncorrecta');
                }
            } else {
                $json = array('value' => false, 'mens' => 'Introduzca contrasena para comprobar');
            }
        } else {
            $json = array('value' => false, 'mens' => 'noExisteUsuario');
        }
    } else {
        $json = array('value' => false, 'mens' => 'Introduzca nombre de usuario para comprobar');
    }

    echo json_encode($json);
}
?>