FROM alpine:latest

WORKDIR /usr/local/pgbadger

RUN apk --no-cache add coreutils openssl perl make \
 && wget -qO- `wget -qO- https://api.github.com/repos/dalibo/pgbadger/releases/latest | grep 'tarball' | sed -E 's/^.*: +"([^"]+)".*$/\1/'` \
  | tar xz --strip 1 \
 && perl Makefile.PL \
 && make && make install \
 && echo '0 4 * * * pgbadger -I /var/log/postgresql/postgresql-$(date --date yesterday +"%Y-%m-%d").log -O /var/www/pg_reports/' >> /var/spool/cron/crontabs/root

CMD crond -l 2 -f
