.PHONY: build
build:
	./gradlew build

.PHONY: test
test:
	./gradlew test

.PHONY: format
format:
	./gradlew --continue ktlintFormat

.PHONY: deploy
deploy:
	./gradlew build -x test -x check
	docker build apps/app/. -t agilogy/time-tracking-app:latest
	docker build apps/migrationsapp/. -t agilogy/time-tracking-migrations:latest
	heroku container:login
	docker tag agilogy/time-tracking-app:latest registry.heroku.com/agilogy-time-tracking/web
	docker tag agilogy/time-tracking-migrations:latest registry.heroku.com/agilogy-time-tracking/migrations
	docker push registry.heroku.com/agilogy-time-tracking/web
	docker push registry.heroku.com/agilogy-time-tracking/migrations
	heroku container:release migrations -a agilogy-time-tracking
	heroku run "migrations" -a agilogy-time-tracking --type=migrations
	heroku container:release web -a agilogy-time-tracking
