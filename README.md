# Movie DB - sparse data and algorithm assigment

This is college's 2nd year project. Focused on sparse data and recomending movies to you by ratings of other users with similar taste to yours. Instead of regular GUI decided to do browser UI in semanticUI and communicate to server over websockets with JSON.

![first](/screenShoots/01.png)

# Features
* WebSocket server (**no** webserver included) tested on Linux.
* Semantic UI webclient (tested on Linux / Windows browsers).
* Instant responces thans to websockets.
* No 3rd party database server required, supported data storage:
  * Bytestream support
  * CSV files support
  * XML
  * JSON
* JavaDoc.
* JUint tests
* Search can lookup different fields, not just title
  * Can search content while you type (not needed to press enter/go button)
* Recomendations given depending on other users which are similar to your taste.
  * Some aspects of this algortihm are cached for better speed performance
* Own implementation of sorting / searching (no collection class used)
* Using library which fills gap between primitive arrays and object collections.


## Can display genres
![UI](/screenShoots/02.png)


## Can search genres
![UI](/screenShoots/03.png)

# Statistics

Number of            | Value   
:--------------------| ------:
Classes              |     15 
Methods              |    175 
Lines of server code |   1933 
Lines of client code |    400

## Displaying reccomendations
![UI](/screenShoots/04.png)


#How to run and use

* Server: **./runServer.sh** (location from where it's run is important, needs to access **data/database.dat**)
* Client: (open in browser) **./web-client/index.htm** 
Client has to have direct file access to HTML files because there is no webserver included.

Check ./data/movies-users.csv to see users logins and passwords, but you can use 
user pass:user to have regular user and admin pass:admin for administrator.

#Additional assigment related informations

Because of the fact that usually you see more pages than give ratings. And often
on these pages are reccomendations. This means that more often you will read
the reccomendation than you will write. So I decided to calculated recomendations
when change the data on write (giving rating) and keep stored results in cache.

Like de-normalization of data in mongoDB and other no-SQL databases I decided to
have some data redudant just to get faster responses. And like google or twitter
I decide to not give 100% of time 100% acurate results. Some results are cached
and some processes stopped after "good enough" results so in case of big 
database it doesn't have to proccess all users and movies each time. But will
returns acceptable results even they might be off from 100% acurate results.
One workable strategy could be to use cached in high load and high trafic situations,
but recalculate and populate caches with fresh data in off peak hours (maybe durning
night).

Ratings are kept in special format. Basicaly it's simple array of primitive types.

Instead of Class Integer primitive type would int be used. But now we can save even
more and insted of int, just use byte. Score doesn't have to be 32bit (because int 
is 4x bigger and not needed). Plus not objects overhead saves performance & memorry
 as well. And they are designed as sparse data so no wasting no matter how many movies
are in database.

Using libary meant for smaller footprint and
faster accessing. Ratings are kept as 2d vector format in almost empty matrix. So 
even if database is containing milion movies. If user rated just 10 movies, just 10 entries
are kept in memorry. Some arrayList implementations resize to contain much more 
reserved entries for future adds. This one still is still between  the smallest
ones, but the speed is the fastest. Benchmark with HashMaps is here:

http://cern.antonkrug.eu

Becuase I kept 2 separate lists as form of 2d vector and they have to be kept 
aligned. This means that I had to write my custom quick sort method. But then I 
could use cerns binary search methods. These classes support of multiple levels 
of dirtines. For little bit dirty list a merge sort could be used, for worst case
scenario quick sort is there.

As practice there is findGenreByName in DB class which is own binary search. 

I implemented cache which will stop after some threshold (so enough high marked 
matched users were found). So in case the database would be huge this would stop
and woudn't process everything. Then results are ordered and just the cache is 
populated with the best ones (again it's possible to change and tweek these thersholds).
Even this cache is keept for each user and it's limited in number, but populated with
good matches. This cache could be set dirty and purged it by admin. So you could
force to repopulate it from scratch maybe with new matches. Or just force existing
matches to update their matching score.

For future it's possible to be extended to contain very bad matches (negative 
ones, if there is user which has exactly oposite taste in movies, this can be used
and recomend exactly the movies he hates. And still yeld good results for your
reccomendation.

Some classes like Cache, Movie or MovieGenre have comparators, so you can sort them
by natural order, by usage, ratings or other factors.

Some classes have serialVersionUID. Generate new ID if you will change
anything in this class, if you change stuff often then comment it out and let
the compiler generate one for you which will change automaticly if you will do
any modifications to the class.

CSV, XML, bytestream and JSON are used for data. CSV is meant for import even export
is implemented (passwords encryption will lose plain text passwords).

XML works as well, but the object references can make it into big file
so bytestream is prefered and setup as default. JSON is used for API and supports 
API calls where it returns data in JSON, so other aplications can use this as data 
inport and export as well.

Read the README file in ./bake folder for all shell script tools lincense.
