# Blockchain Project
A static blockchain project that implements the funtionality of blockchain. User chooses from an interactive block chain menu from 0 – 6 to simulate a blockchain operation, illustrating several important nonfunctional characteristics of distributed systems. Some extra things done based on this stand-alone blockchain: build web services to enhance interoperability, considering various styles of API design. A stand-alone blockchain and remote clients that interact with a blockchain API. E.x. SOAP based API, single argument style JAX-WS web service, resource based or REST web service.

run:
> MENU
  0. View basic blockchain status.
  1. Add a transaction to the blockchain.
  2. Verify the blockchain.
  3. View the blockchain.
  4. Corrupt the chain.
  5. Hide the Corruption by repairing the chain.
  6. Exit

Some basic functions:
- Is made up of blocks that store data (transaction text, timestamp). 
- Has a digital signature that chains your blocks together.
- Requires proof of work mining to validate new blocks (difficulty entered by user).
- Can corrupt the chain.
- Can be check to see if data in it is valid and unchanged (loop the entire blockchain).

More functional and non-functional details are explained in class documentations. Enjoy!
