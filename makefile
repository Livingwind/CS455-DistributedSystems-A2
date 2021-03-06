all: compile
	@echo -e "[INFO] Done!"

pkg: all
	@echo -e "[INFO] Packaging submission"
	@tar cvf Westerman-Christopher-HW2-PC.tar cs455/**/**/*.java \
		README.txt makefile

clean:
	@echo -e "[INFO] Cleaning Up.."
	@-rm -rf cs455/**/**/*.class

compile:
	@echo -e "[INFO] Compiling the Source.."
	@javac -d . cs455/**/**/*.java
