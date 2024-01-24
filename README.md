# Multi-threaded Dictionary Server

A multi-threaded dictionary client-server has been designed and implemented in this project,
which forms the core of a distributed dictionary system. It enables concurrent clients to access
and manage word definitions seamlessly using a thread pool architecture, with communication
carried out via sockets. This client-server architecture ensures efficient communication, error
resilience, simple encryption and data integrity.

### Key Aspects:
**Concurrency and Communication:** The server handles multiple client requests simultaneously
using a thread pool architecture approach. This optimizes resource utilization and
responsiveness, enhancing the user experience.

**Network Protocol and Messaging:** TCP protocol ensures reliable data transmission between
the server and clients. A JSON-based messaging system serves as a universal language for
information exchange, facilitating smooth communication.

**Error Handling:** The server effectively manages I/O and network errors, maintaining system
stability even in challenging scenarios. Parameter validation prevents invalid inputs,
safeguarding data integrity.

**Request Validation:** Robust validation prevents clients from making illegal requests. The server
identifies and addresses issues like duplicate word addition, non-existent word removal, and
adding words without meanings.

**Concurrent Data Manipulation:** Synchronized operations guarantee data consistency in
multi-threaded environments. This eliminates conflicts when multiple clients modify shared
resources, ensuring accurate updates.

**Scalability:** The architecture is built to accommodate numerous clients, adapting to varying
demand levels. Thread management optimizes performance, even under heavy loads.

### System Components:
**Dictionary Server:** The DictionaryServer class acts as the heart of the server-side application.
It initializes, listens for incoming client connections, and assigns a dedicated ClientHandler
thread for each client. It manages the dictionary data, facilitating operations such as searching
for word meanings, adding new words, removing words, and updating meanings based on
incoming client requests.

**ClientHandler:** ClientHandler threads, running independently, are responsible for handling
individual client connections. Each thread processes client requests by interacting with the
Dictionary class, ensuring that multiple clients can interact with the server concurrently without
blocking.

**Dictionary:** The Dictionary class serves as the storage and management component for
dictionary data. It reads the initial dictionary data from a JSON file and maintains it in memory
using a JSONObject. This class offers synchronized methods to safely handle concurrent
access to the dictionary data.

**Dictionary Client:** The DictionaryClient class represents the client-side application. It
establishes socket connections with the server, enabling communication.
This class provides methods for sending client requests and receiving server responses.
Additionally, it hosts the main method, initiating the client application by accepting server IP and
port as command-line inputs.

**DictionaryClientGUI:** The DictionaryClientGUI class delivers a user-friendly graphical interface
for the client application, created using Java's Swing library. The interface features input fields
for various dictionary operations (add, remove, update, search) and displays responses from the
server. Interaction with the DictionaryClient class enables request submission and response
reception.

**DictionaryServerGUI:** The DictionaryServerGUI class offers an intuitive graphical interface for
the server application. It opens a window presenting a log area that records critical server
events, including client connections and responses. The GUI tracks connected clients and
showcases their logs in the log area, fostering efficient server monitoring and management.

**CustomThreadPool:** The CustomThreadPool class introduces a custom thread pool
mechanism for managing worker threads efficiently. It allows for the parallel processing of
incoming client requests by assigning tasks to available worker threads, improving the server's
responsiveness.

**CaesarCipher:** The CaesarCipher class handles text encryption and decryption, ensuring the
security and integrity of messages exchanged between the server and clients. It employs a
Caesar cipher algorithm with a predefined shift value to encode and decode messages.
