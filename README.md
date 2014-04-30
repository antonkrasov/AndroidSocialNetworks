Android Social Networks
=====================

Android Social Networks is library which makes working with social networks easier.
If you sometime tried to work with social networks on android you should remember that this is a hell.
You should read documentation for every social network, download SDK or use some libraries for OAuth and make
http calls by yourself. This library should makes your life easier, it contains common interface for 
Twitter, LinkedIn, Facebook and Google Plus, just build SocialNetworkManager and configure your AndroidManiferst and you can login users, or post messages or photos or add / remove friends. 
**Library is still in development so more features will be added soon**

  - [Features](#features)
  - [Sample Application](#sample-application)
  - [Getting Started](#getting-started)
  - [Including in your project](#cncluding-in-your-project)
  - [Dependencies](#dependencies)
  - [License](#license)

### Features

  - Login (Twitter, LinkedIn, Facebook, Google Plus)
  - Get person info (Twitter, LinkedIn, Facebook, Google Plus)
  - Post message (Twitter, LinkedIn, Facebook)
  - Post photo (Twitter, LinekdIn)
  - Check is user in your friends list (Twitter, LinkedIn)
  - Add to friends (Twitter, LinkedIn)
  - Remove from friends (Twitter)

### Sample Application

  [Download](https://dl.dropboxusercontent.com/u/80518668/ASN%20Demo.apk)

### Getting started

  TODO Getting started

### Including in your project

  repositories {
      maven {
          url "https://oss.sonatype.org/content/repositories/snapshots"
      }
  }
  
  compile('com.github.androidsocialnetworks:library:0.1.8-SNAPSHOT@aar') {
      transitive = true
  }

### Dependencies

- [support-v4](http://developer.android.com/tools/support-library/index.html)
- [Twitter4j](http://twitter4j.org/en)
- [Google Play Services](http://developer.android.com/google/play-services/index.html)
- [Facebook SDK](https://developers.facebook.com/docs/android/)
- [linkedin-j-android](https://code.google.com/p/linkedin-j/)
- [signpost-core](https://code.google.com/p/oauth-signpost/)
- [signpost-commonshttp4](https://code.google.com/p/oauth-signpost/)

## License

Android Social Networks is made available under the [MIT license](http://opensource.org/licenses/MIT):

<pre>
The MIT License (MIT)

Copyright (c) 2013 Path, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
</pre>

