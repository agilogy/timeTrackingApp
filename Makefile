.PHONY: build
build:
	./gradlew build


.PHONY: deploy
deploy:
	./gradlew build -x test -x lintKotlin
	docker build app/. -t agilogy/time-tracking-app:latest
	heroku container:login
	docker tag agilogy/time-tracking-app:latest registry.heroku.com/agilogy-time-tracking/web
	docker push registry.heroku.com/agilogy-time-tracking/web
	heroku container:release web -a agilogy-time-tracking
