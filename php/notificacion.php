<?php

if(isset($_POST["usuario"]) && isset($_POST["puntos"])){
    
    $api_key="AAAAGNxdvb4:APA91bHeF4nS9X4HX6mp5-eQhXIVenuFpnlw-eeAg8ybvzZC2EY17Y4VOObSsx9TW-csDx29jbdvXf_4XKdxPukgn_qmmOQ1PVhDTngafCwYsIgZXcp-gwMYprgx1AQqcUmX_SqnMk8S";
    
    $usuario= $_POST["usuario"];
    $puntos= $_POST["puntos"];

    $data = array (
        'title' => 'Nuevo record!!',
        'body' => 'El usuario ' . $usuario . ' ha conseguido ' . $puntos . ' puntos.'
        );
    $to= '/topics/ranking';
    $fields=json_encode(array('to'=>$to,'notification'=>$data));

    $ch = curl_init(); #inicializar el handler de curl
    #indicar el destino de la petición, el servicio FCM de google
    curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
    #indicar que la conexión es de tipo POST
    curl_setopt( $ch, CURLOPT_POST, true );
    #agregar las cabeceras
    $cabecera = array();
    $cabecera[] = 'Authorization: key ='. $api_key;
    $cabecera[] = 'Content-Type: application/json';
    curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
    #Indicar que se desea recibir la respuesta a la conexión en forma de string
    curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
    #agregar los datos de la petición en formato JSON
    curl_setopt( $ch, CURLOPT_POSTFIELDS, $fields );
    #ejecutar la llamada
    $resultado= curl_exec( $ch );

    if (curl_errno($ch)){
        $json = array('value' => false, 'mens' => 'ERROR: ' . curl_error($ch));
        curl_close( $ch );
    } else {
       #cerrar el handler de curl
        curl_close( $ch );
        $json = array('value' => true, 'mens' => 'ENVIADO');
    }

}else {
    $json = array('value' => false, 'mens' => 'Introduzca nombre de usuario y puntos para generar notificacion.');
}

echo json_encode($json);

?>