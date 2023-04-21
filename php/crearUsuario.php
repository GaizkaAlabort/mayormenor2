<?php

$DB_SERVER="localhost";
$DB_USER="Xgalabort001";
$DB_PASS="pDFsJxS2q";
$DB_DATABASE="Xgalabort001_mayormenor";

$conexion = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);



if(mysqli_connect_errno()){
    echo'ERROR DE CONEXION: ' . mysqli_connect_error();
    exit();

} else {
    if(isset($_POST["nombre"]) && isset($_POST["contraseña"])){
        $nombre = $_POST['nombre'];
        $contraseña =  password_hash($_POST['contraseña'], PASSWORD_DEFAULT);

        $consulta = "SELECT * FROM Usuarios WHERE Nombre='{$nombre}'";
        $resultado = mysqli_query($conexion,$consulta);

        if($registro=mysqli_fetch_array($resultado)){
            $json = array('value' => false, 'mens' => 'registroNombreExiste');
        } else {
            $insert = "INSERT INTO Usuarios(Nombre,Contraseña) VALUES ('$nombre','$contraseña')";
            $resultado_insert=mysqli_query($conexion,$insert);

            if($resultado_insert){
                $consulta = "SELECT * FROM Usuarios WHERE Nombre='{$nombre}'";
                $resultado = mysqli_query($conexion,$consulta);

                if($registro=mysqli_fetch_array($resultado)){
                    $json = array('value' => true, 'mens' => 'Se ha generado correctamente');
                } else {
                    $json = array('value' => false, 'mens' => 'registroFallo');
                }
            }
        }
    } else {
        $json = array('value' => false, 'mens' => 'Introduzca nombre de usuario y contrasena para generar usuario');
    }

    echo json_encode($json);
}
?>