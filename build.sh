#!/bin/sh
bold=$(tput bold)
normal=$(tput sgr0)

print_usage() {
	echo "Usage: sh $0 [options] {build target}"
	echo "         (to start building the specific build target)"
	echo "   or  sh $0 --help"
	echo "         (to print this help dialog)"
	echo ""
	echo "Build targets:"
	echo "\t${bold}frontend   ${normal}build frontend component and copies files to 'artifacts/frontend' directory"
	echo "\t${bold}backend    ${normal}build backend component and copy jar to 'artifacts/backend.jar' file"
	echo "\t${bold}all        ${normal}executes both 'frontend' and 'backend' build targets, frontend first, backend second"
	echo "\t${bold}combined   ${normal}executes 'all' build targets, then copies 'artifacts/frontend' contend to 'artifacts/backend.jar'"
}
if [ $# -eq 0 ]
then
	print_usage
	exit
fi

target=none

for var in "$@"
do
	case $var in
		all)
			target="all"
		;;
		combined)
			target="combined"
		;;
		frontend)
			target="frontend"
		;;
		backend)
			target="backend"
		;;
		--help|-help|help|--h|-h|h)
			print_usage
			exit
		;;
	esac
done



if [ $target = "none" ]
then
	echo "invalid target"
	print_usage
	exit
fi

if [ $target = "all" ] || [ $target = "combined" ] || [ $target = "frontend" ]
then
	printf "\033[32mbuilding frontend...\n\033[0m"
	cd frontend
	npm install
	npm run build
	mkdir -p ../artifacts
	rm -rf ../artifacts/frontend
	cp -r build ../artifacts/frontend
	cd ..
fi
if [ $target = "all" ] || [ $target = "combined" ] || [ $target = "backend" ]
then
	printf "\033[32mbuilding backend...\n\033[0m"
	cd backend
	/bin/sh ./gradlew build
	mkdir -p ../artifacts
	rm -rf ../artifacts/backend.jar
	cp build/libs/server.jar ../artifacts/backend.jar
	cd ..
fi

if [ $target = "combined" ]
then
	java -jar tools/zipmerger.jar artifacts/frontend artifacts/backend.jar frontend
fi