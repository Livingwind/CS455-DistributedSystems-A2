This assignment focuses on creating a thread pool for processing messages received by clients. Although the server only
runs on one machine, it can easily support in excess of 100 client connections. As a default, the server will print
statistics every 20 sec.

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