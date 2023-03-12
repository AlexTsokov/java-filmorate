DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users (
    USER_ID int generated by default as identity primary key,
    EMAIL varchar(100) not null,
    LOGIN varchar(100) not null,
    NAME varchar(100),
    BIRTHDAY date not null
    );

CREATE TABLE IF NOT EXISTS films (
    FILM_ID int generated by default as identity primary key,
    NAME varchar(100) not null,
    DESCRIPTION varchar(200) not null,
    RELEASE_DATE    date not null,
    DURATION        int not null
    );

CREATE TABLE IF NOT EXISTS friendship (
    FRIEND_ID int not null,
    OTHER_FRIEND_ID int not null,
    STATUS varchar(50) not null,
    CONSTRAINT fk_user_friendship
    FOREIGN KEY (FRIEND_ID)
    REFERENCES users(USER_ID),
    CONSTRAINT fk_friend_friendship
    FOREIGN KEY (OTHER_FRIEND_ID)
    REFERENCES USERS(USER_ID)
    );

CREATE TABLE IF NOT EXISTS likes (
    FILMS_ID int not null,
    USERS_ID int not null,
    CONSTRAINT fk_film_like
    FOREIGN KEY (FILMS_ID)
    REFERENCES films(FILM_ID),
    CONSTRAINT fk_user_like
    FOREIGN KEY (USERS_ID)
    REFERENCES users(USER_ID)
    );

CREATE TABLE IF NOT EXISTS genre (
    GENRE_ID int AUTO_INCREMENT PRIMARY KEY,
    NAME varchar(100) not null
    );

CREATE TABLE IF NOT EXISTS film_genre (
    FILM_ID int not null,
    GENRES_ID int not null,
    CONSTRAINT fk_film_genre
    FOREIGN KEY (FILM_ID)
    REFERENCES films(FILM_ID),
    CONSTRAINT fk_genre_id
    FOREIGN KEY (GENRES_ID)
    REFERENCES genre(GENRE_ID)
    );

CREATE TABLE IF NOT EXISTS mpa (
    MPA_ID int AUTO_INCREMENT PRIMARY KEY,
    NAME varchar(100) not null
    );

CREATE TABLE IF NOT EXISTS films_mpa (
    FILMS_ID int PRIMARY KEY,
    RATING_MPA_ID int not null,
    CONSTRAINT fk_mpa
    FOREIGN KEY (RATING_MPA_ID)
    REFERENCES mpa(MPA_ID),
    CONSTRAINT fk_film_mpa
    FOREIGN KEY (FILMS_ID)
    REFERENCES films(FILM_ID)
    );