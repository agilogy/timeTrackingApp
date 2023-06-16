.PHONY: build
build:
	./gradlew build
	docker build app/. -t agilogy/time-tracking-app:latest

.PHONY: deploy
deploy:
	make build
	heroku container:login
	docker tag agilogy/time-tracking-app:latest registry.heroku.com/agilogy-time-tracking/web
	docker push registry.heroku.com/agilogy-time-tracking/web
	heroku container:release web -a agilogy-time-tracking
