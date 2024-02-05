CREATE TABLE timestamps(id bigint not null,
                        d DATE,
                        t_tz TIMESTAMP_TZ,
                        t_ntz TIMESTAMP_NTZ,
                        t_ltz TIMESTAMP_LTZ,
                        primary key (id));

CREATE sequence timestamps_seq start with 1 increment by 50;
