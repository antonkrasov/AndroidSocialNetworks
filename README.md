### THIS PROJECT IS NO LONGER MAINTAINED, FEEL FREE TO FORK AND FIX IT FOR YOUR NEEDS

There is also an Android Library that is being maintained, [CloudRail Android SDK](https://github.com/CloudRail/cloudrail-si-android-sdk) that offers support for all the networks below as well as GitHub, Slack, Windows Live, and Slack.

Android Social Networks
=====================

<p>
<img src="https://raw.githubusercontent.com/antonkrasov/AndroidSocialNetworks/master/other/asn.png" width="200px" height="200px" align="left" hspace="15px" />
Android Social Networks is library which makes working with social networks easier.
If you sometime tried to work with social networks on android you should remember that this is a hell.
You should read documentation for every social network, download SDK or use some libraries for OAuth and make
http calls by yourself. This library should makes your life easier, it contains common interface for
Twitter, LinkedIn, Facebook and Google Plus, just build SocialNetworkManager and configure your AndroidManiferst and you can login users, or post messages or photos or add / remove friends.
</p>

**Library is still in development so more features will be added soon**

  - [Features](#features)
  - [Sample Application](#sample-application)
  - [Getting Started](#getting-started)
  - [Including in your project](#including-in-your-project)
  - [Important](#important)
  - [Dependencies](#dependencies)
  - [Developed By](#developed-by)
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

<a href="https://play.google.com/store/apps/details?id=com.github.androidsocialnetworks.apidemos">
  <img alt="Android app on Google Play"
       src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

### Getting started

  First of all, you need to register you application, please check this links: [Facebook](https://github.com/antonkrasov/AndroidSocialNetworks/wiki/Facebook), [Twitter](https://github.com/antonkrasov/AndroidSocialNetworks/wiki/Twitter), [LinkedIn](https://github.com/antonkrasov/AndroidSocialNetworks/wiki/LinkedIn), [GooglePlus](https://github.com/antonkrasov/AndroidSocialNetworks/wiki/Google-Plus)

  
  Next you need to initialize **mSocialNetworkManager**. Build it with **SocialNetworkManager.Builder** and
  add to fragment manager.

```java

    mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);

    if (mSocialNetworkManager == null) {
        mSocialNetworkManager = SocialNetworkManager.Builder.from(getActivity())
                .twitter(<< TWITTER  API TOKEN  >>, << TWITTER  API SECRET  >>)
                .linkedIn(<< LINKED_IN  API TOKEN  >>, << LINKED_IN API TOKEN  >>, "r_basicprofile+rw_nus+r_network+w_messages")
                .facebook()
                .googlePlus()
                .build();
        getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
    }

```  

  Now you can execute requests, for example login request:


```java
    mSocialNetworkManager.getTwitterSocialNetwork().requestLogin(new OnLoginCompleteListener() {
        @Override
        public void onLoginSuccess(int socialNetworkID) {

        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {

        }
    });
```  

### Including in your project

Library is still in development, so for now it's only available in staging repo.

```groovy
  compile('com.github.androidsocialnetworks:library:0.3.7@aar') {
      transitive = true
  }
```

### Important

- **Library don't manage state, you need to do it yourself.**
- If you use Google Plus login, please add this in your Activity:
```java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * This is required only if you are using Google Plus, the issue is that there SDK
         * require Activity to launch Auth, so library can't receive onActivityResult in fragment
         */
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BaseDemoFragment.SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
```

### Dependencies

- [support-v4](http://developer.android.com/tools/support-library/index.html)
- [Twitter4j](http://twitter4j.org/en)
- [Google Play Services](http://developer.android.com/google/play-services/index.html)
- [Facebook SDK](https://developers.facebook.com/docs/android/)
- [linkedin-j-android](https://code.google.com/p/linkedin-j/)
- [signpost-core](https://code.google.com/p/oauth-signpost/)
- [signpost-commonshttp4](https://code.google.com/p/oauth-signpost/)

##Developed By

  Anton Krasov - <anton.krasov@gmail.com>

<a href="https://twitter.com/ntnkrsv"><img src="https://raw.githubusercontent.com/antonkrasov/AndroidSocialNetworks/master/other/sn_icons/twitter.png" width="100px" height="81px" /></a><br/>

## License

Android Social Networks is made available under the [MIT license](http://opensource.org/licenses/MIT):

<pre>
The MIT License (MIT)

Copyright (c) 2014 Anton Krasov

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
