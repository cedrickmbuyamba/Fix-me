# Fix-me
This is a java maven project, to compile this project, use the command(make sure maven is installed):
->mvn clean package
How to run the project:
Under each component there is a target folder which contains a jar file.
Start with the router: java -jar <filename>.jar
Secondly, the market: java -jar <filename>.jar
thirdly, the broker: java -jar <filename>.jar <marketId> as you run the market component... every client that connects to the router gets an id
Lastly, watch the magic happen...
