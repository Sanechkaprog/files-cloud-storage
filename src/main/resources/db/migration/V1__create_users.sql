CREATE TABLE users (
                       user_id UUID DEFAULT gen_random_uuid(),
                       username VARCHAR(50) NOT NULL,
                       password VARCHAR(150) NOT NULL,

                       CONSTRAINT users_pk PRIMARY KEY (user_id),
                       CONSTRAINT username_unique UNIQUE (username)
)
