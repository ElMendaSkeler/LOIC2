██╗      	 ██████╗ 	██╗ 	 ██████╗ 	   ██████╗ 
██║     	██╔═══██╗	██║	██╔════╝ 	   ╚════██╗
██║     	██║   	██║	██║	██║       	  	 █████╔╝
██║     	██║   	██║	██║	██║       	 	██╔═══╝ 
███████╗	╚██████╔╝	██║	╚██████╗ 	   ███████╗
╚══════╝  ╚═════╝ 	╚═╝ 	 ╚═════╝ 	   ╚══════╝		Por ElMendaXD

INDEX
1. LOIC
2. LOIC 2
3. Uso de LOIC 2
4. Dependencias
5. Objetivos de practica
==============================================================================================



1. LOIC (Fuente https://es.wikipedia.org/wiki/Low_Orbit_Ion_Cannon)

Low Orbit Ion Cannon (abreviado LOIC) es una aplicación diseñada para realizar un ataque de denegación de servicio durante el proyecto Chanology, desarrollada por «praetox» usando el lenguaje de programación C# (Existe también un fork en C++ y Qt llamado LOIQ).

La aplicación realiza un ataque de denegación de servicio al objetivo enviando una gran cantidad de paquetes TCP, paquetes UDP o peticiones HTTP con objeto de determinar cuál es la cantidad de peticiones por segundo que puede resolver la red objetivo antes de dejar de funcionar.



2. LOIC 2

Como su nombre indica, LOIC 2 (Low Orbital Ion Canon 2) consiste en una mejora sustancial de su sucesor, centrándose exclusivamente en la masificación mediante paquetes UDP, maximizando así su eficiencia.

Haciendo uso del mismo principio de ataque DDoS, mediante la masificación de datagramas UDP, se puede lograr saturar la banda de cualquier maquina. Sin embargo existe la limitación de la capacidad de emisión de paquetes, la cual viene limitada por la tarifa de internet.

Para hacer frente a este problema normalmente se optaba por aumentar la tarifa de internet, lo cual resulta costoso.
Sin embargo LOIC 2 hace uso del principio Master/Slave, mediante un terminal(cliente/master) se puede controlar a múltiples terminales(servidores/slaves), manteniendo una comunicación TCP entre el cliente y los servidores para el control operativo.

Este mecanismo permite el uso conjunto de múltiples terminales colaboradores, sumando sus respectivas bandas de ancho.

A su vez también permite la ocultación del cliente/master desde el punto de vista del objetivo.

LOIC 2 permite la asignación de objetivos deferentes a diferentes terminales, pudiendo gestionar no solo múltiples servidores, sino también múltiples ataques a múltiples objetivos, permitiendo definir que recursos se van a usar para cada objetivo.



3. Uso de LOIC 2

El paquete de distribución cuenta con archivos .exe para maquinas Windows y con archivos .jar para su uso en cualquier sistema operativo que cuente con java, tanto para los servidores como para el cliente.

Para su uso en Windows solo hay que ejecutar el archivo .exe correspondiente al rol que va a adoptar el terminal (SV para servidores y CL para el cliente) y seguir las instrucciones durante la ejecución.

Para su uso en cualquier sistema operativo los pasos son exactamente los mismos, para ejecutarlos simplemente use el comando java -jar <ruta el .jar>.

El sistema de comunicación es común, permitiendo conectar terminales sin importar el ejecutable que usen.

Se recomienda hacer uso de los puertos 65534 para mensajes y 65535 para el heartbeat para la comunicación con los servidores.

Para la selección de puertos del objetivo se recomienda usar Nmap con el comando nmap -p- -PN <IP del objetivo>



4. Dependencias

Únicamente es necesario contar con JDK 22, puede obtenerlo en https://www.oracle.com/es/java/technologies/downloads/



5. Objetivos de practica

164.132.206.237 en los puertos 3389,5085,8000,8080,8081,9600,9700,30120,40127