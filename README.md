**<p align="center"> BSiUI </p>**
_________________________________
**<p align="center"> Wroclaw University of Science and Technology </p>**
**<p align="center"> Computer Science, Faculty of Electronics, 7 semester </p>**
<p align="center"> Radosław Lis, 241385 </p>

# Table of Contents
- [General info](#desc)
- [Documentation](#doc)
- [Prerequisites](#pre)
  * [JavaFX configuration in Java 11 or higher](#jav)
- [Run](#run)

<a name="desc"></a>
# General info
Simple client-server project with [Diffie–Hellman key exchange](https://en.wikipedia.org/wiki/Diffie%E2%80%93Hellman_key_exchange) implemented based on the [Java Sockets](https://docs.oracle.com/javase/tutorial/networking/sockets/index.html).

<a name="doc"></a>
# Documentation
In project there are two applications - **server** and **client**. You can run one server (which works on 6666 port) and as many client applications as you want (dynamic port allocation). To get the information about running apps see [this](#run) section.    

*Notation* - all parameters are marked as it is described [right here](https://en.wikipedia.org/wiki/Diffie%E2%80%93Hellman_key_exchange#Cryptographic_explanation).

1. Run server by pressing `Run server` button.
2. Press `Start connection` button on client apps to get `B` which now should be visible in appropriate text field.
3. Press `Request keys` to get `p` and `g` from the server.
4. Now you can draw `a` and calculate `A` by pressing `Draw a & and calc. A` button.
5. Now you have two options - send `A` to the server (to calculate secret `k` on the server side) or calculate secret 'k' on the client side. No matter which you will do first.
6. **Optional:** send encryption type - `NONE` (default), `CAESAR` (offset is youngest byte of `k` mod 26) or `XOR` (youngest byte of `k`) - to the server by choosing right type in the combobox and pressing `Send encryption` button.
7. Exchange messages with the server by typing and sending messages. On the server app you must choose client for the message by choosing right row in the table view. You can also change encryption type whenever you want, it should be instantly updated in the table view.  

![](https://s8.gifyu.com/images/gif0969b8eb377729df.gif)

<a name="pre"></a>
# Prerequisites
- [Java](https://www.oracle.com/java/technologies/javase-downloads.html) 8 (or 11 if you do some magic with JavaFX libs and VM options)
- Maven ([download](https://maven.apache.org/download.cgi) and [install](https://maven.apache.org/install.html))

<a name="jav"></a>
## JavaFX configuration in Java 11 or higher
1. Download [JavaFX](https://gluonhq.com/products/javafx/) and extract files on your computer.
2. Add all JARs from `...\javafx-sdk-11.0.2\lib` to the project in your IDE (e.g. IntelliJ IDEA, Eclipse).
3. In VM options add:
```
--module-path "xyz\javafx-sdk-11.0.2\lib" --add-modules=javafx.controls,javafx.fxml
```
Where `xyz` is path to `javafx-sdk` directory.


<a name="run"></a>
# Run
Clone repository and checkout to this branch:
```
$ git clone https://github.com/pwr-zak/241385.git
$ git checkout bsiui-lab1
$ cd bsiui
$ cd lab1
```
In the `lab1` folder (with pom.xml):
```
$ mvn install
$ cd target
```
To run client app:
```
$ java -jar ClientApplication.jar
```
And to run server app:
```
$ java -jar ServerApplication.jar
```

