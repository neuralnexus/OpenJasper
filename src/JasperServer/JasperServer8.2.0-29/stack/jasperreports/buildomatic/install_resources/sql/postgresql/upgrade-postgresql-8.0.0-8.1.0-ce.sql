--
--
-- 8.0.0 to 8.1.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

    create table JIFavoriteResource
    (
        id int8 not null,
        user_id int8 not null,
        resource_id int8 not null,
        creation_date timestamp not null,
        primary key (id)
    );

    alter table JIFavoriteResource
        add constraint UKrj25jnmtcmddlfp23n7duhpv unique (user_id, resource_id);

    alter table JIFavoriteResource
        add constraint FKe3ak4arnheeorbrsc4u8m8pi0
        foreign key (user_id)
        references JIUser
        on delete cascade;

    alter table JIFavoriteResource
        add constraint FK63man3dkmekfr2hgfifi3ne8c
        foreign key (resource_id)
        references JIResource
        on delete cascade;

    create index JIFavoriteResource_resource_id_idx on JIFavoriteResource (resource_id);
