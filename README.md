# ProgRSX_TP1

## TCP Server

Run ServerMultiThreaded class to start the server.  
Requested parameters are :
* Host's address (String) - ex : localhost,
* Host's port (int) - ex : 80.

Then run ClientMultiThreadedChat class for each client you want to connect to the server.  
Requested parameters are :
* Host's address (String) - ex : localhost,
* Host's port (int) - ex : 80.

The server implements multiple channels.  
You can navigate trough channel with the **/channel _<channel_number>_** command.  
There are infinite channel.  
Default channel number is 0.  

The server also implements a persistant chat historic by channel.

Everytime a user join a channel, a message is sent to other connected clients.  
Same is done when a user leave a channel.

## MultiCast Server

Run ClientMultiCastChat for each client you want to connect to the server.  
Requested parameters are :
* Host's address (String) - ex : 228.5.6.7,
* Host's port (int) - ex : 6789.

The client can handle server switching using the **/channel _<channel_number>_** command.  
There are only 3 different channels : 0, 1 and 2.  

Everytime a user join a channel, a message is sent to other connected clients.  
Same is done when a user leave a channel.
