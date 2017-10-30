
--- [main] org.hibernate.SQ
create table video (media_id integer not null auto_increment, author varchar(255), author_ip_address varchar(255), author_location varchar(255), create_date datetime, description varchar(255), bit_rate integer not null, duration float not null, filename varchar(255), format_name varchar(255), size integer not null, meta_data varchar(255), modify_date datetime, name varchar(255), upload_date datetime, video_hls_url varchar(255), video_mpd_url varchar(255), video_poster_url varchar(255), video_sprite_url varchar(255), video_thumbs_json_url varchar(255), video_url varchar(255), primary key (media_id))
