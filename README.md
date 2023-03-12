# java-filmorate
Data-base diagram:
<img src="C:\Users\Alex\Downloads\Untitled.png" title="Diagrqam" width="800"/>
Samples of queries:

All users query:
SELECT * FROM users;

All films query:
SELECT * FROM films;

Film likes query:
SELECT users_id FROM likes WHERE films_id=film_id;