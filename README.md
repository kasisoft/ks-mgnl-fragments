Purpose
=======

This library provides a simple mechanism allowing to render HTML fragments without the use of a corresponding component node.
It's mainly useful to render dynamically generated code.


Contact
=======

* daniel.kasmeroglu@kasisoft.net


Requirements
============

 * Java 8
 * Magnolia 5.5.3


Jira
====

* https://kasisoft.com/jira/projects/MGNLFRAG/issues


Maven
=====

Release
-------

     <dependency>
         <groupId>com.kasisoft.mgnl</groupId>
         <artifactId>com.kasisoft.mgnl.fragments</artifactId>
         <version>0.1</version>
     </dependency>
     
     
Snapshot
--------

     <dependency>
         <groupId>com.kasisoft.mgnl</groupId>
         <artifactId>com.kasisoft.mgnl.fragments</artifactId>
         <version>0.2-SNAPSHOT</version>
     </dependency>
     
     <repositories>
         <repository>
             <id>libs-kasisoft</id>
             <url>https://kasisoft.com/artifactory/libs-kasisoft</url>
             <releases>
                 <enabled>true</enabled>
             </releases>
             <snapshots>
                 <enabled>true</enabled>
             </snapshots>
         </repository>
     </repositories>
     

License
=======

MIT License

Copyright (c) 2017 Daniel Kasmeroglu (Kasisoft)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
