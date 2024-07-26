setup:
	make -C app setup

app:
	make -C app app

clean:
	make -C app clean

build:
	make -C app build

dev: app

reload-classes:
	make -C app reload-classes

install:
	make -C app install

test:
	make -C app test

report:
	make -C app report

check-java-deps:
	make -C app check-java-deps

start:
	make -C app start

