-- liquibase formatted sql

-- changeset lc:1-1-block_history
CREATE TABLE "public"."block_history"
(
    "id"         INTEGER                     NOT NULL,
    "time"       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "person_id"  INTEGER                     NOT NULL,
    "post_id"    INTEGER,
    "comment_id" INTEGER,
    "action"     VARCHAR(25)                 NOT NULL,
    CONSTRAINT "block_history_pkey" PRIMARY KEY ("id")
);
COMMENT ON TABLE "public"."block_history" IS 'история блокировок пользователей за пост / комментарий';
COMMENT ON COLUMN "public"."block_history"."person_id" IS 'пользователь, которого заблокировали';
COMMENT ON COLUMN "public"."block_history"."action" IS 'тип действия: BLOCK (блокировка) или UNBLOCK (разблокировка)';