This assignment focuses on creating a thread pool for processing messages received by clients. Although the server only
runs on one machine, it can easily support in excess of 100 client connections. As a default, the server will print
statistics every 20 sec.

### Server

Running the server using the command `Server <port> <thread pool size>`. This will create an instance of a Server running on the specific port. The thread pool size determines how many worker threads should be tasked with processing data. Ideally, this number will be equal to the number of cores on your machine but it can operate with more or less.

Internally, the server uses a distributed hash table to validate and respond to messages. The flow is as follows:

1. Server acknowledges a Client connection and adds it to the Java NIO selector.

2. The Client begins sending random messages at the rate specified by the Client's arguements.

3. When the Server receives a message, a worker thread will create the hashed version of that message and send it back to the client.

### Client

To connect a Client to the server simply invoke `Client <server hostname> <server port> <message rate>`. After connecting to the Server, the Client will begin sending random 8 KB messages and storing the hashed result to aid in varification. When the server responds, the returned hash is compared to the store hash from before sending.


### Package Explaination
client
  - Client: Client to connect to the server. Houses the client receiver.
  - ClientSender: Sends a message of raw bytes every N seconds where N is defined as an argument.
  - ClientThroughputChecker: Displays statistics based on total messages sent and successfully received.
  - HashList: Data structure to transfer data between the main thread and the sender thread.
  
exceptions
  - HashNotFound: Error message to handle a hash mismatch.
  - SocketClosedException: Error to handle if the socket has closed unexpectedly.
  
server
  - AllStatistics: Collates all the data for each client.
  - ConnectionProcessor: Collects messages and incoming connections. Messages get passed into the key buffer.
  - KeyBuffer: Buffer to hold keys ready for processing.
  - Server: Starts the ConnectionProcessor and ThreadPoolManager.
  - ServerThroughputChecker: Periodically prints statistics regarding message processing.
  - ThreadPoolManager: Assigns tasks as they arrive in the key buffer to worker threads.
  - Worker: Takes a key, preforms a read then sends back a hashed version of the message.
  
utils
  - ClientEntry: Container class to store client information.
  - ClientStatistics: Container class to store running client statistics.
  - HashCalculator: Calculates a hash value to be used by the client and server.
  - MessagingConstants: Shared constants for messaging.
  - ServerStatistics: Container class to store and process statistics from the server.
