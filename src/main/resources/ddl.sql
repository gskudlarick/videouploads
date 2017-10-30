CREATE  TABLE users (
  username VARCHAR(45) NOT NULL ,
  password VARCHAR(45) NOT NULL ,
  enabled TINYINT NOT NULL DEFAULT 1 ,
  PRIMARY KEY (username));
CREATE TABLE user_roles (
  user_role_id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(45) NOT NULL,
  role varchar(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  UNIQUE KEY uni_username_role (role,username),
  KEY fk_username_idx (username),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username));
INSERT INTO users(username,password,enabled)
VALUES ('api','api100', true);
INSERT INTO users(username,password,enabled)
VALUES ('greg','greg100', true);
INSERT INTO user_roles (username, role)
VALUES ('api', 'ROLE_USER');
INSERT INTO user_roles (username, role)
VALUES ('greg', 'ROLE_ADMIN');


CREATE TABLE video
(
  media_id          INTEGER NOT NULL auto_increment,
  author            VARCHAR(255),
  author_ip_address VARCHAR(255),
  author_location   VARCHAR(255),
  create_date       DATETIME,
  description       VARCHAR(255),
  bit_rate          INTEGER NOT NULL,
  duration FLOAT NOT NULL,
  filename              VARCHAR(255),
  format_name           VARCHAR(255),
  size                  INTEGER NOT NULL,
  meta_data             VARCHAR(255),
  modify_date           DATETIME,
  NAME                  VARCHAR(255),
  upload_date           DATETIME,
  video_hls_url         VARCHAR(255),
  video_mpd_url         VARCHAR(255),
  video_poster_url      VARCHAR(255),
  video_sprite_url      VARCHAR(255),
  video_thumbs_json_url VARCHAR(255),
  video_url             VARCHAR(255),
  PRIMARY KEY (media_id))
