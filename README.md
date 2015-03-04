CloseBy
=======
A Simple lib to place any element in android beside/above/below existing element with a lot of cool features

Version
=======
v.1.0.0

Usage (Maven)
=============
```XML
<dependency>
  <groupId>com.shehabic</groupId>
  <artifactId>closeby</artifactId>
  <version>1.0.0</version>
</dependency>
```

Usage (Gradle)
==============
```groovy
compile 'com.shehabic:closeby:1.0.0@aar'
```

How to use it
=======
        in an Activity context do the following

        CloseBy.Builder builder = new CloseBy.Builder(this)
                .setPosition(CloseBy.POSITION_TOP_RIGHT)
                .setSourceView(existing_view_of_any_type)
                .setCloseBy(R.layout.new_view_to_be_placed, this)
                .setMargin(5, -3, 0, 0);
        CloseBy cb = builder.build();
        
        cb.show();
        
        cb.hide();


Developed By
============

* Mohamed Shehab - <shehabic@gmail.com>


License
=======

    Copyright 2015 Mohamed Shehab

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
