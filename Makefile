setup:
	make -C app wrapper --gradle-version 8.5
	make -C app build

app:
	make -C app bootRun --args='--spring.profiles.active=dev'

clean:
	make -C app clean

build:
	make -C app clean build

dev: app

reload-classes:
	make -C app -t classes

install:
	make -C app installDist

test:
	make -C app test

report:
	make -C app jacocoTestReport

check-java-deps:
	make -C app dependencyUpdates -Drevision=release

start:
	make -C app run

