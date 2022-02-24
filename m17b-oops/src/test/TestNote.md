When running JUnit Test, all the string PATH, must be begin with bin instead of src.
Eg: "bin/test/test_province.json" instead of "src/test/test_province.json" 

Places where to change PATH:
in src/test
* FactionTest.java
* MovementTest.java

in src/unsw/gloriaromanus
* movement/Graph.java
* units/UnitFactory.java
* Game.java

Also Gradle fails every alternative time, need to modify any file by adding a line/ comment or space " " and retype the gradle test command

