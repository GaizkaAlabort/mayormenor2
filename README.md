<em> Mayor o Menor </em>

![logo_mayormenor](https://user-images.githubusercontent.com/70901676/226133522-c4f833af-0fb8-43a3-aed9-d552b1b55d23.png)

<h1 align="center"> Indice </h1>

[Título e imagen de portada](#Título-e-imagen-de-portada)

[Índice](#índice)

[Instrucción Juego](#instruccion-juego)

[Explicaciones de clases](#explicacion-clases)



<h1 align="center"> Instrucción Juego: </h1>

El juego cuenta con dos opciones de búsquedas que se han hecho, una de ellas cuenta con las vistas realizadas y en la segunda opción se debe elegir si tiene más o menos 
visualizaciones en google. Si aciertas sumas un punto y si no pierdes. En este juego no se tienen vidas.  Estas visualizaciones se tienen en cuenta respecto a las 
realizadas en España como media en un mes.

<h1 align="center"> Explicaciones de clases: </h1>

<h2> Clases de las pantallas principales: </h2>

- Login: Es la clase de acceso al juego mediante usuario y contraseña, es necesario para entrar a jugar y cambiar el idioma de la app.
- Registro: Clase para crear el usuario a utilizar en la aplicación. Sin cuenta no se puede acceder a la aplicación
- Usuario: Tras acceder con el usuario se puede observar en esta pantalla el ranking o acceder a jugar. Página principal después de acceso.
- Juego: Clase del juego. Recoge los datos del archivo de texto y compara ambas opciones, si tiene más o menos visualizaciones.

<h2> Clases de los fragmentos de las listas: </h2>

- Ranking_global_fragment: Se recarga y rellena el ranking global del fragmento.
- AdaptadorLitView: Enlaza cada artículo del fragmento de ranking global, para que la lista sea personalizada.
- Ranking_personal_fragment: Espera a seleccionar un artículo del fragmento de ranking global, para recargar y llenar el ranking personal.
- RankingPersonal: Actividad para el caso de que el móvil esté vertical y se pulse un artículo del fragmento de ranking global. En vez de actualizar el fragmento de la clase usuario se genera esta actividad.

<h2> Clases de los dialogos: </h2>

- abandonarJuego: Diálogo para el juego, al pulsar en puntos o dar al botón de ir hacia atrás aparece este diálogo. Si aceptas cancelas la partida y si no lo puedes cerrar y continuar el juego. 
- idiomaDialogo: Dialogo en el acceso a la aplicación, para poder cambiar el idioma al pulsar el boton de “Idioma”.

<h2> Clase de base de datos: </h2>

- BD: Clase para crear las tablas de la base de datos y poder insertar, consultar o borrar.

<h1 align="center"> Requisitos realizados: </h1>

<h2> Obligatorios: </h2>

- Se usa listview tanto en ranking personal como para ranking global. En este último caso son personalizados con dos variables e imágenes.
- Se cuenta con dos tablas de una base de datos local, una es utilizada para crear usuario (está solo tiene para añadir) y otra se utiliza para la puntuación de las partidas (se añaden después de cada partida, se listan en los rankings y se borra al mantener seleccionado un artículo en ranking personal).
- Se usa diálogos en cambiar idioma y para aceptar cancelar la partida del juego.
- Se usan notificaciones al terminar el juego para indicar los puntos obtenidos en el.

<h2> Extras: </h2>

- En la pantalla del usuario, tras loguearse, se comporta de manera distinta al estar en vertical y en horizontal. En este se usan fragmentos para mostrar los rankings.
- La aplicación está habilitada para Español e Inglés. (Multiidioma)
- Para almacenar las visualizaciones y a que pertenecen, se utilizan ficheros de texto.
- Se cuenta con registro y login para acceder a la aplicación.
- Se cuenta con un intent que te lleva a la app de twitter en caso de tenerla y sino a la página web.
