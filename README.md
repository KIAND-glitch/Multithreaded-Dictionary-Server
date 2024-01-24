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
