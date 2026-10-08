CREATE FUNCTION public.user_service_legacy_id_to_uuid(entity_table text, legacy_id bigint)
RETURNS uuid
LANGUAGE sql
IMMUTABLE
STRICT
AS $$
    SELECT md5('job-portal-user-service:' || entity_table || ':' || legacy_id::text)::uuid
$$;

CREATE TEMPORARY TABLE user_service_numeric_fk_columns
ON COMMIT DROP
AS
SELECT DISTINCT
    source_ns.nspname AS source_schema,
    source_table.relname AS source_table,
    source_column.attname AS source_column,
    target_table.relname AS target_table,
    constraint_info.conname AS constraint_name,
    pg_get_constraintdef(constraint_info.oid) AS constraint_definition
FROM pg_constraint constraint_info
JOIN pg_class source_table
    ON source_table.oid = constraint_info.conrelid
JOIN pg_namespace source_ns
    ON source_ns.oid = source_table.relnamespace
JOIN pg_class target_table
    ON target_table.oid = constraint_info.confrelid
JOIN pg_namespace target_ns
    ON target_ns.oid = target_table.relnamespace
CROSS JOIN LATERAL unnest(constraint_info.conkey) WITH ORDINALITY
    AS source_key(attnum, key_order)
JOIN LATERAL unnest(constraint_info.confkey) WITH ORDINALITY
    AS target_key(attnum, key_order)
    ON target_key.key_order = source_key.key_order
JOIN pg_attribute source_column
    ON source_column.attrelid = source_table.oid
    AND source_column.attnum = source_key.attnum
JOIN pg_attribute target_column
    ON target_column.attrelid = target_table.oid
    AND target_column.attnum = target_key.attnum
WHERE constraint_info.contype = 'f'
    AND source_ns.nspname = 'public'
    AND target_ns.nspname = 'public'
    AND target_table.relname = ANY (ARRAY[
        'users', 'roles', 'user_profiles', 'user_preferences', 'user_locations',
        'languages', 'skills', 'oauth_accounts', 'refresh_tokens',
        'password_reset_tokens', 'email_verification_tokens', 'educations',
        'experiences', 'certifications', 'projects'
    ])
    AND source_column.atttypid IN (
        'smallint'::regtype, 'integer'::regtype, 'bigint'::regtype
    )
    AND target_column.atttypid IN (
        'smallint'::regtype, 'integer'::regtype, 'bigint'::regtype
    );

DO $$
DECLARE
    foreign_key RECORD;
    entity_table text;
    column_type text;
BEGIN
    FOR foreign_key IN
        SELECT DISTINCT source_schema, source_table, constraint_name
        FROM user_service_numeric_fk_columns
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I DROP CONSTRAINT %I',
            foreign_key.source_schema,
            foreign_key.source_table,
            foreign_key.constraint_name
        );
    END LOOP;

    FOR foreign_key IN
        SELECT DISTINCT source_schema, source_table, source_column, target_table
        FROM user_service_numeric_fk_columns
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I ALTER COLUMN %I TYPE uuid USING public.user_service_legacy_id_to_uuid(%L, %I::bigint)',
            foreign_key.source_schema,
            foreign_key.source_table,
            foreign_key.source_column,
            foreign_key.target_table,
            foreign_key.source_column
        );
    END LOOP;

    FOREACH entity_table IN ARRAY ARRAY[
        'users', 'roles', 'user_profiles', 'user_preferences', 'user_locations',
        'languages', 'skills', 'oauth_accounts', 'refresh_tokens',
        'password_reset_tokens', 'email_verification_tokens', 'educations',
        'experiences', 'certifications', 'projects'
    ]
    LOOP
        SELECT data_type INTO column_type
        FROM information_schema.columns
        WHERE table_schema = 'public'
            AND table_name = entity_table
            AND column_name = 'id';

        IF column_type IN ('smallint', 'integer', 'bigint') THEN
            EXECUTE format(
                'ALTER TABLE public.%I ALTER COLUMN id DROP IDENTITY IF EXISTS',
                entity_table
            );
            EXECUTE format(
                'ALTER TABLE public.%I ALTER COLUMN id DROP DEFAULT',
                entity_table
            );
            EXECUTE format(
                'ALTER TABLE public.%I ALTER COLUMN id TYPE uuid USING public.user_service_legacy_id_to_uuid(%L, id::bigint)',
                entity_table,
                entity_table
            );
        END IF;
    END LOOP;

    FOREACH entity_table IN ARRAY ARRAY[
        'user_profiles', 'user_preferences', 'user_locations', 'oauth_accounts',
        'refresh_tokens', 'password_reset_tokens', 'email_verification_tokens',
        'educations', 'experiences', 'certifications', 'projects',
        'user_languages', 'user_skills'
    ]
    LOOP
        SELECT data_type INTO column_type
        FROM information_schema.columns
        WHERE table_schema = 'public'
            AND table_name = entity_table
            AND column_name = 'user_id';

        IF column_type IN ('smallint', 'integer', 'bigint') THEN
            EXECUTE format(
                'ALTER TABLE public.%I ALTER COLUMN user_id TYPE uuid USING public.user_service_legacy_id_to_uuid(''users'', user_id::bigint)',
                entity_table
            );
        END IF;
    END LOOP;

    FOR foreign_key IN
        SELECT *
        FROM (VALUES
            ('user_languages', 'language_id', 'languages'),
            ('user_skills', 'skill_id', 'skills')
        ) AS association_fk(table_name, column_name, target_table)
    LOOP
        SELECT data_type INTO column_type
        FROM information_schema.columns
        WHERE table_schema = 'public'
            AND table_name = foreign_key.table_name
            AND column_name = foreign_key.column_name;

        IF column_type IN ('smallint', 'integer', 'bigint') THEN
            EXECUTE format(
                'ALTER TABLE public.%I ALTER COLUMN %I TYPE uuid USING public.user_service_legacy_id_to_uuid(%L, %I::bigint)',
                foreign_key.table_name,
                foreign_key.column_name,
                foreign_key.target_table,
                foreign_key.column_name
            );
        END IF;
    END LOOP;

    FOR foreign_key IN
        SELECT DISTINCT source_schema, source_table, constraint_name, constraint_definition
        FROM user_service_numeric_fk_columns
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I ADD CONSTRAINT %I %s',
            foreign_key.source_schema,
            foreign_key.source_table,
            foreign_key.constraint_name,
            foreign_key.constraint_definition
        );
    END LOOP;
END
$$;

DROP FUNCTION public.user_service_legacy_id_to_uuid(text, bigint);
