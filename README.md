# ItsTimeAndIm


**Description**

The app consists in a single screen displaying a tweet whose message contains the exact time (hour / minutes) when the app is launched.

For example if it's 10:13 in the morning, the app will show a tweet of someone mentioning that time,

	"It's 10:13 and I just woke up. Today is the big day".

It's not necessary that the tweet was published at that specific date, only the content mentioning the hour is important. If several records are found, the app will select the most retweeted first and newer first if they have the same retweet count. 

Additionally the application automatically fetches a new tweet every minute.

**Libraries used**

* Twitter4J: http://twitter4j.org/en/index.html
* RxAndroid: https://github.com/ReactiveX/RxAndroid
* Joda-Time: http://www.joda.org/joda-time/
* Picasso: http://square.github.io/picasso/
* Butterknife: http://jakewharton.github.io/butterknife/


**Important**

Make sure to change the twitter consumer token/secret for the search API.

**Disclaimer**

This application has been developed by Jalal Souky as a request from Kerad Games.

http://www.keradgames.com/

Make sure to check their amazing football manager game

**Contact**

- Twitter: @callmesouky
- Email: jalalsouky@gmail.com
- Linkedin: https://www.linkedin.com/profile/view?id=145115290

