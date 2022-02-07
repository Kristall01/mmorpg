#!/bin/sh
print_usage() {
	echo "Usage: sh $0 [options] {frontend|backend|all}"
	echo "         (to start building the specific build target)"
	echo "   or  sh $0 --help"
	echo "         (to print this help dialog)"
	echo ""
	echo " where options include:"
	echo ""
	echo "    --docker  makes build process run in a docker container"
	echo ""
}
if [ $# -eq 0 ]
then
	print_usage
	exit
fi

docker=false
target=none

for var in "$@"
do
	case $var in
		--docker)
			docker=true
		;;
		all)
			target="all"
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

if [ $docker = true ]
then
	if [ $target = "all" ] || [ $target = "frontend" ]
	then
		printf "\033[32mbuilding frontend...\n"
		docker container run --rm -it -w /repo -v $(pwd):/repo node:16 /bin/sh ./build.sh frontend
	fi
	if [ $target = "all" ] || [ $target = "backend" ]
	then
		printf "\033[32mbuilding backend...\n"
		docker container run --rm -it -w /repo -v $(pwd):/repo openjdk:11 /bin/sh ./build.sh backend
	fi
else
	if [ $target = "all" ] || [ $target = "frontend" ]
	then
		printf "\033[32mbuilding frontend...\n"
		cd frontend
		npm install
		npm run build
		mkdir -p ../artifacts
		rm -rf ../artifacts/frontend
		cp -r build ../artifacts/frontend
		cd ..
	fi
	if [ $target = "all" ] || [ $target = "backend" ]
	then
		printf "\033[32mbuilding backend...\n"
		cd backend
		/bin/sh ./gradlew build
		mkdir -p ../artifacts
		rm -rf ../artifacts/backend.jar
		cp build/libs/server.jar ../artifacts/backend.jar
		cd ..
	fi
fi
