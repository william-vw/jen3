# jen3  
The jen3 project is based on [Apache Jena](https://github.com/apache/jena) and is a Java framework for writing Semantic Web applications in [Notation3](https://w3c.github.io/N3/spec/).

Documentation for the original framework can be found at http://jena.apache.org/.

Although this framework has been in the works for some time, and used in several projects, it has not been made public until now. More detailed documentation on how to use the framework will be added soon.

Its main use to date is loading a N3 file and inspecting the resulting inferences. Although the main Jena API was adapted with N3-specific constructs, this has not been thoroughly tested.

A large set of test cases can be found under [testing/N3](https://github.com/william-vw/jen3/tree/master/testing/N3), and can be executed using [`N3TestRef.java`](https://github.com/william-vw/jen3/blob/master/src/test/java/org/apache/jen3/n3/N3TestRef.java).

For a simpler start with jen3, checkout the [`N3Test.java`](https://github.com/william-vw/jen3/blob/master/src/test/java/org/apache/jen3/n3/N3Test.java) class.
